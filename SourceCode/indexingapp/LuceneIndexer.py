import subprocess
import json

# Sample dictionary (use your actual data structure)
"""dictionary = {
    "webid1": {
        "srv03812": {
            "pod1": ["AlecFile.txt", "AlphaFile.txt"],
            "pod2": ["Jamal.txt"]
        },
        "srv03813": {
            "pod4": ["Toer.txt"]
        }
    },
    "webid2": {
        "srv03812": {
            "pod3": ["Mo.txt", "Helen.txt"]
        }
    }
}"""

dictionary = {
    "webid1": {
        "srv03812": {
            "pod1": ["AlecFile.txt", "AlphaFile.txt"],
            "pod2": ["Jamal.txt"]
        }
    },
    "webid2": {
        "srv03812": {
            "pod3": ["Mo.txt", "Helen.txt"]
        }
    }
}

# Convert dictionary to JSON string
dictionary_json = json.dumps(dictionary)

# Specify source and output directories
source_dir = "lucenetestsource"
output_dir = "lucenetestsink"

# Path to the JAR file
jar_file = "Index.jar"

# Run the Java program with the JAR file
result = subprocess.run([
    "java", "-jar", jar_file, dictionary_json, source_dir, output_dir
], capture_output=True, text=True)

# Print the output from the Java program
print(result.stdout)
print(result.stderr)  # If there were any errors