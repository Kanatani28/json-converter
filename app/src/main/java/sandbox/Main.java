package sandbox;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String jsonPath1 = "src/main/resources/sample.json";
        String jsonPath2 = "src/main/resources/sample2.json";
        String jsonPath3 = "src/main/resources/sample3.json";

        String outputDir = "src/main/resources/";

        JsonConverter.convertToCsv(jsonPath1, outputDir);

        JsonConverter.convertToCsv(jsonPath2, outputDir);
        // JsonConverter2.convertToCsv(jsonPath1, outputDir);  
        // JsonConverter3.convertToCsv(jsonPath1, outputDir);  
        // JsonConverter4.convertToCsv(jsonPath1, outputDir);  
    }
}
