package gdd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for loading CSV files into arrays and maps
 * Based on Baeldung's CSV reading guide
 */
public class CSVLoader {
    
    /**
     * Loads a CSV file and returns a list of string arrays
     * Each array represents a row, with each element being a column value
     */
    public static List<String[]> loadCSVToArray(String filePath) throws IOException {
        List<String[]> records = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Split by comma, handling quoted values
                String[] values = parseCSVLine(line);
                records.add(values);
            }
        }
        
        return records;
    }
    
    /**
     * Loads a CSV file with headers and returns a list of maps
     * Each map represents a row with column names as keys
     */
    public static List<Map<String, String>> loadCSVToMapList(String filePath) throws IOException {
        List<Map<String, String>> records = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                return records; // Empty file
            }
            
            String[] headers = parseCSVLine(headerLine);
            
            String line;
            while ((line = br.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                String[] values = parseCSVLine(line);
                Map<String, String> record = new HashMap<>();
                
                // Map values to headers
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    record.put(headers[i].trim(), values[i].trim());
                }
                
                records.add(record);
            }
        }
        
        return records;
    }
    
    /**
     * Loads spawn details from CSV file into a map keyed by frame number
     */
    public static Map<Integer, SpawnDetails> loadSpawnDetailsFromCSV(String filePath) throws IOException {
        Map<Integer, SpawnDetails> spawnMap = new HashMap<>();
        
        List<Map<String, String>> records = loadCSVToMapList(filePath);
        
        for (Map<String, String> record : records) {
            try {
                int frame = Integer.parseInt(record.get("Frame"));
                String type = record.get("Type");
                int x = Integer.parseInt(record.get("X"));
                int y = Integer.parseInt(record.get("Y"));
                
                SpawnDetails details = new SpawnDetails(type, x, y);
                
                // Add optional fields if they exist
                if (record.containsKey("Health")) {
                    details.setHealth(Integer.parseInt(record.get("Health")));
                }
                if (record.containsKey("MovementPattern")) {
                    details.setMovementPattern(record.get("MovementPattern"));
                }
                if (record.containsKey("AttackPattern")) {
                    details.setAttackPattern(record.get("AttackPattern"));
                }
                if (record.containsKey("PowerUpDrop")) {
                    details.setPowerUpDrop(record.get("PowerUpDrop"));
                }
                
                spawnMap.put(frame, details);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing spawn details: " + e.getMessage());
                // Skip invalid records
            }
        }
        
        return spawnMap;
    }
    
    /**
     * Parse a single CSV line, handling quoted values and commas within quotes
     */
    private static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        
        // Add the last field
        result.add(currentField.toString());
        
        return result.toArray(new String[0]);
    }
}

