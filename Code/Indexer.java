package index.index;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.*;

public class Index {
    
    
   
    public  String extractServerNumber(String input) {
        Pattern pattern = Pattern.compile("srv(\\d{5})");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1); // Return the captured number
        }
        return null; // Return null if no match is found
    }

    public static void main(String[] args) {
        
        if (args.length != 3) {
            System.out.println("Usage: java Index <dictionaryJsonFilePath> <sourceDir> <outputDir>");
            System.exit(1);
        }

        try {
            // Parse command-line arguments
            String dictionaryJsonFilePath = args[0];
            String sourceDir = args[1];
            String outputDir = args[2];

         // Read the dictionary JSON from the file
            ObjectMapper objectMapper = new ObjectMapper();
            File dictionaryFile = new File(dictionaryJsonFilePath);

            // Use TypeReference to ensure proper deserialization of the nested structure
            Map<String, Map<String, Map<String, List<String>>>> dictionary = objectMapper.readValue(
                dictionaryFile,
                new TypeReference<Map<String, Map<String, Map<String, List<String>>>>>() {}
            );

            // Perform indexing with parallelization
            Index indexer = new Index();
            indexer.indexData(dictionary, sourceDir, outputDir);
            System.out.println("Indexing completed successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    private String sanitizePath(String input) {
        return input.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
    }

    public void indexData(Map<String, Map<String, Map<String, List<String>>>> dictionary,
                          String sourceDir, String outputDir) throws IOException {

        ExecutorService executor = Executors.newFixedThreadPool(10); // Pool size of 4 (adjust based on system)

        try {
            for (String webId : dictionary.keySet()) {
                Map<String, Map<String, List<String>>> servers = dictionary.get(webId);

                // Create directories
                Path webIdRoot = Paths.get(outputDir, webId);
                Files.createDirectories(webIdRoot);
                Path fileLevelDir = Paths.get(webIdRoot.toString(), "file-level");
                Path podLevelDir = Paths.get(webIdRoot.toString(), "pod-level");
                Path serverLevelDir = Paths.get(webIdRoot.toString(), "server-level");
                Files.createDirectories(fileLevelDir);
                Files.createDirectories(podLevelDir);
                Files.createDirectories(serverLevelDir);

                // Submit file-level indexing tasks
                for (String server : servers.keySet()) {
                    Path fileLevelServerDir = Paths.get(fileLevelDir.toString(), sanitizePath(server));
                    Files.createDirectories(fileLevelServerDir);

                    Map<String, List<String>> pods = servers.get(server);
                    for (String pod : pods.keySet()) {
                        executor.submit(() -> {
                            try {
                                Path fileLevelServerPodDir = Paths.get(fileLevelServerDir.toString(), sanitizePath(pod));
                                Files.createDirectories(fileLevelServerPodDir);
                                Path fileLevelPodDir = Paths.get(fileLevelServerPodDir.toString(), sanitizePath(webId) + ".zip");
                               
                                
                                Path tempPodDir = Files.createTempDirectory("tempPodIndex");
                                try {
                                    List<String> files = getFilesForWebIdAndPod(dictionary, webId, server, pod);
                                    indexFileLevel(webId, server, pod, files, sourceDir, tempPodDir);
                                    zipDirectory(tempPodDir, fileLevelPodDir);
                                } finally {
                                    deleteDirectory(tempPodDir);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }

                // Submit pod-level indexing tasks
                for (String server : servers.keySet()) {
                    executor.submit(() -> {
                        try {
                            
                            Path PodLevelServerDir = Paths.get(podLevelDir.toString(), sanitizePath(server));
                            Files.createDirectories(PodLevelServerDir);
                            
                            Path podLevelServerZip = Paths.get(PodLevelServerDir.toString(), sanitizePath(webId) + "-pods.zip");
                            Path tempPodIndexDir = Files.createTempDirectory("tempPodIndex");
                            try {
                                indexPodLevel(webId, server, servers.get(server), sourceDir, tempPodIndexDir);
                                zipDirectory(tempPodIndexDir, podLevelServerZip);
                            } finally {
                                deleteDirectory(tempPodIndexDir);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }

                // Submit server-level indexing tasks
                    executor.submit(() -> {
                        try {
                            
                            Path ServerLevelServerDir = Paths.get(serverLevelDir.toString(), sanitizePath(selectRandomServer(servers.keySet())));
                            Files.createDirectories(ServerLevelServerDir);
                            
                            Path serverLevelIndexDir = Paths.get(ServerLevelServerDir.toString(),  sanitizePath(webId) + "-servers.zip");
                            Path tempServerDir = Files.createTempDirectory("tempServerIndex");
                            try {
                                indexServerLevel(webId, servers, sourceDir, tempServerDir);
                                zipDirectory(tempServerDir, serverLevelIndexDir);
                            } finally {
                                deleteDirectory(tempServerDir);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            }

            // Shutdown the executor service after all tasks are submitted
            executor.shutdown();
            if (!executor.awaitTermination(60, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            e.printStackTrace();
        }
    }

    private void indexFileLevel(String webId, String server, String pod, List<String> fileList, String sourceDir,
                                Path outputDir) throws IOException {

        Files.createDirectories(outputDir);
        try (Directory directory = FSDirectory.open(outputDir);
             IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()))) {

            for (String fileName : fileList) {
                Path filePath = Paths.get(sourceDir, fileName);
                Document doc = new Document();
                doc.add(new StringField("Id", fileName, Field.Store.YES));
                doc.add(new TextField("content", Files.readString(filePath), Field.Store.YES));
                writer.addDocument(doc);
            }
        }
    }

    private void indexPodLevel(String webId, String server, Map<String, List<String>> pods,
                               String sourceDir, Path outputDir) throws IOException {

        Files.createDirectories(outputDir);
        try (Directory directory = FSDirectory.open(outputDir);
             IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()))) {

            for (String pod : pods.keySet()) {
                StringBuilder concatenatedContent = new StringBuilder();

                for (String fileName : pods.get(pod)) {
                    Path filePath = Paths.get(sourceDir, fileName);
                    concatenatedContent.append(Files.readString(filePath)).append("\n");
                }

                Document doc = new Document();
                doc.add(new StringField("Id", pod, Field.Store.YES));
                doc.add(new TextField("content", concatenatedContent.toString(), Field.Store.YES));
                writer.addDocument(doc);
            }
        }
    }

    private void indexServerLevel(String webId, Map<String, Map<String, List<String>>> servers,
                                  String sourceDir, Path outputDir) throws IOException {

        Files.createDirectories(outputDir);
        try (Directory directory = FSDirectory.open(outputDir);
             IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()))) {

            for (String server : servers.keySet()) {
                StringBuilder concatenatedContent = new StringBuilder();

                Map<String, List<String>> pods = servers.get(server);
                for (String pod : pods.keySet()) {
                    for (String fileName : pods.get(pod)) {
                        Path filePath = Paths.get(sourceDir, fileName);
                        concatenatedContent.append(Files.readString(filePath)).append("\n");
                    }
                }

                Document doc = new Document();
                doc.add(new StringField("Id", server, Field.Store.YES));
                doc.add(new TextField("content", concatenatedContent.toString(), Field.Store.YES));
                writer.addDocument(doc);
            }
        }
    }

    private void zipDirectory(Path sourceDir, Path zipFilePath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
            Files.walk(sourceDir)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString());
                        try {
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException("Error zipping directory", e);
                        }
                    });
        }
    }

    private void deleteDirectory(Path directory) throws IOException {
        Files.walk(directory)
                .sorted((path1, path2) -> path2.compareTo(path1)) // Reverse order to delete children first
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        throw new RuntimeException("Error deleting directory", e);
                    }
                });
    }
    public static String selectRandomServer(Set<String> servers) {
        // If the set is empty, return null or handle it as needed
        if (servers.isEmpty()) {
            return null;
        }

        // Convert Set to List to randomly access an index
        List<String> serverList = new ArrayList<>(servers);

        // Use Random to select a random index
        Random random = new Random();
        int randomIndex = random.nextInt(serverList.size());  // random index within the size of the list

        return serverList.get(randomIndex);  // Return the randomly selected server
    }

    private static List<String> getFilesForWebIdAndPod(
        Map<String, Map<String, Map<String, List<String>>>> dictionary,
        String webId,
        String serverUrl,
        String podUrl
    ) {
        return dictionary.getOrDefault(webId, Collections.emptyMap())
                         .getOrDefault(serverUrl, Collections.emptyMap())
                         .getOrDefault(podUrl, Collections.emptyList());
    }
}

