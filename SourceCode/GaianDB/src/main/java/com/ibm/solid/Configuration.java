package com.ibm.solid;

/**
 * @author Reza Moosaei
 * Configuration of Logical Table
 */
public class Configuration {
    public static void config2 (String fileName)
    {
                /////
        String logicalTableName2 = "SOLID_SPARQL";

        PropertiesManagement.getInstance(fileName).addProperty(
                "LT" + logicalTableName2 + "_DS0_ARGS",
                "<GAIAN_WORKSPACE>/csvtestfiles/solid_sparql.csv");
        PropertiesManagement.getInstance(fileName).addProperty(
                "LT" + logicalTableName2 + "_CONSTANTS",
                "");
        PropertiesManagement.getInstance(fileName).addProperty(
                "LT" + logicalTableName2 + "_DS0_OPTIONS",
                "MAP_COLUMNS_BY_POSITION");
        PropertiesManagement.getInstance(fileName).addProperty(
                "LT" + logicalTableName2 + "_DEF",
                "PATIENT VARCHAR(255), SURGERY VARCHAR(255), STATUS VARCHAR(255), SURGERYDATE VARCHAR(255), ACTIVITYDATE VARCHAR(255), STEPS VARCHAR(255)");
        PropertiesManagement.getInstance(fileName).addProperty(
                "LT" + logicalTableName2 + "_DS0_VTI",
                "com.ibm.db2j.FileImport");

        System.out.println("The SOLID SPARQL properties has been configured...");

    }

    public static void config(String fileName) {

        String logicalTableName = "SOLID";
        PropertiesManagement.getInstance(fileName).addProperty(
                "LT" + logicalTableName + "_DS0_ARGS",
                "<GAIAN_WORKSPACE>/csvtestfiles/solid.csv");
        PropertiesManagement.getInstance(fileName).addProperty(
                "LT" + logicalTableName + "_CONSTANTS",
                "");
        PropertiesManagement.getInstance(fileName).addProperty(
                "LT" + logicalTableName + "_DS0_OPTIONS",
                "MAP_COLUMNS_BY_POSITION");
        PropertiesManagement.getInstance(fileName).addProperty(
                "LT" + logicalTableName + "_DEF",
                "Search_Parameters VARCHAR(255), Document_URL VARCHAR(255), RELEVANCE VARCHAR(3)");
        PropertiesManagement.getInstance(fileName).addProperty(
                "LT" + logicalTableName + "_DS0_VTI",
                "com.ibm.db2j.FileImport");
        System.out.println("The SOLID properties has been configured...");

    }
}