package sandbox;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String jsonPath = "src/main/resources/sample.json";
        // String jsonPath = "src/main/resources/sample2.json";
        String outputDir = "src/main/resources/";

        JsonConverter.convertToCsv(jsonPath, outputDir);
        // JsonConverter2.convertToCsv(jsonPath, outputDir);  
        // JsonConverter3.convertToCsv(jsonPath, outputDir);  
        // JsonConverter4.convertToCsv(jsonPath, outputDir);  
    }
}
