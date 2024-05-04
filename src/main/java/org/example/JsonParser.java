package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
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
        System.out.println("The current working directory is " + System.getProperty("user.dir"));

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

        // Prompt the user for the CSV file path and name
        System.out.println("Enter the path and filename to save the CSV file:");
        String newFilePath = scanner.nextLine();

        // Create parent directories if they don't exist
        File csvFile = new File(newFilePath);
        File parentDirectory = csvFile.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists()) {
            boolean directoriesCreated = parentDirectory.mkdirs();
            if (!directoriesCreated) {
                System.out.println("Error: Failed to create directories for CSV file.");
                return;
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonFile);

            // Convert JSON data to a hierarchical map
            Map<String, Map<String, String>> dataMap = new TreeMap<>(Collections.reverseOrder());
            for (JsonNode itemNode : rootNode.get("Items")) {
                String date = itemNode.get("sampleFolder").get("S").asText();
                String sampleId = itemNode.get("sampleId").get("S").asText();
                dataMap.computeIfAbsent(date, _ -> new HashMap<>()).put(sampleId, null);
            }

            // Write hierarchical map to JSON file
            FileWriter writer = new FileWriter(newFilePath);
            objectMapper.writeValue(writer, dataMap);
            writer.close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while processing the JSON file", e);
        }
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
