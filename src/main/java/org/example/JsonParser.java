package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonParser {
    static boolean validFile = false;
    static String jsonFilePath;
    static File jsonFile;
    private static final Logger LOGGER = Logger.getLogger(JsonParser.class.getName());

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Set the DynamoDB table name
        String tableName = "codes_by_date";

        DynamoDBHelper dynamoDBHelper = new DynamoDBHelper(tableName);

        while (true) {
            // Prompt the user for the desired action
            System.out.println("What do you want to do? (read/write/delete/quit):");
            String action = scanner.nextLine().toLowerCase();

            switch (action) {
                case "write":
                    writeData(scanner, dynamoDBHelper);
                    break;
                case "read":
                    readData(scanner, dynamoDBHelper);
                    break;
                case "delete":
                    deleteData(scanner, dynamoDBHelper);
                    break;
                case "quit":
                    System.out.println("Exiting the program.");
                    return;
                default:
                    System.out.println("Invalid action. Please enter 'read', 'write', 'delete', or 'quit'.");
            }
        }
    }

    private static void writeData(Scanner scanner, DynamoDBHelper dynamoDBHelper) {
        while (!validFile) {
            // Prompt the user for the JSON file path and name
            System.out.println("Enter the path and filename of the JSON file:");
            jsonFilePath = scanner.nextLine();
            jsonFile = new File(jsonFilePath);

            // Check if the JSON file exists
            if (!jsonFile.exists()) {
                System.out.println("Error: JSON file does not exist.");
                validFile = false;
                System.out.println("Please try again or press control^c to exit.");
                continue;
            }

            // Check if the JSON file is valid
            if (!isValidJson(jsonFile)) {
                System.out.println("Error: Invalid JSON file.");
                validFile = false;
                System.out.println("Please try again or press control^c to exit.");
                continue;
            }
            validFile = true;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonFile);

            // Convert JSON data to a map with date as key and list of sampleIds as value
            Map<String, List<String>> dataMap = new TreeMap<>(Collections.reverseOrder());
            for (JsonNode itemNode : rootNode.get("Items")) {
                String date = itemNode.get("sampleFolder").get("S").asText();
                String sampleId = itemNode.get("sampleId").get("S").asText();
                dataMap.computeIfAbsent(date, _ -> new ArrayList<>()).add(sampleId);
            }

            // Use DynamoDBHelper to write data
            dynamoDBHelper.writeData(dataMap);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while processing the JSON file", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred while interacting with DynamoDB", e);
        }
    }

    private static void readData(Scanner scanner, DynamoDBHelper dynamoDBHelper) {
        // Prompt the user for a date to retrieve sampleIds
        System.out.println("Enter the date to retrieve sample IDs:");
        String queryDate = scanner.nextLine();
        List<String> sampleIds = dynamoDBHelper.getSampleIdsByDate(queryDate);

        if (sampleIds != null) {
            System.out.println("Sample IDs for date " + queryDate + ":");
            for (String id : sampleIds) {
                System.out.println(id + ",");
            }
        } else {
            System.out.println("No sample IDs found for date " + queryDate);
        }
    }

    private static void deleteData(Scanner scanner, DynamoDBHelper dynamoDBHelper) {
        // Prompt the user for a date to delete
        System.out.println("Enter the date to delete:");
        String deleteDate = scanner.nextLine();
        dynamoDBHelper.deleteDataByDate(deleteDate);
    }

    // Method to check if the given file is a valid JSON file
    private static boolean isValidJson(File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.readTree(file);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
