package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class JsonToCsvConverter {
    static boolean validFile = false;
    static String jsonFilePath;
    static File jsonFile;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("The current working directory is " + System.getProperty("user.dir"));

        while (!validFile) {
            // Prompt the user for the JSON file path and name
            System.out.println("Enter the path and filename of the JSON file:");
            jsonFilePath = scanner.nextLine();
            System.out.println("jasonFilePath = " + jsonFilePath);

            // Check if the JSON file exists
            jsonFile = new File(jsonFilePath);
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
        String csvFilePath = scanner.nextLine();

        // Create parent directories if they don't exist
        File csvFile = new File(csvFilePath);
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
            JsonNode itemsNode = rootNode.get("Items");

            // TreeMap to store data sorted by date
            Map<String, String> dataByDate = new TreeMap<>();

            // Iterate through JSON objects
            for (JsonNode itemNode : itemsNode) {
                String date = itemNode.get("sampleFolder").get("S").asText();
                String sampleId = itemNode.get("sampleId").get("S").asText();
                dataByDate.put(date, sampleId);
            }

            // Prepare CSV file
            FileWriter csvWriter = new FileWriter(csvFilePath, true); // Open in append mode
            if (new File(csvFilePath).length() == 0) { // Check if CSV file is empty
                csvWriter.append("Date,Sample Code\n"); // Add header only if the file is empty
            }

            // Write sorted data to CSV file
            for (Map.Entry<String, String> entry : dataByDate.entrySet()) {
                csvWriter.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
            }

            // Close CSV writer
            csvWriter.flush();
            csvWriter.close();

            System.out.println("CSV file generated successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
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
