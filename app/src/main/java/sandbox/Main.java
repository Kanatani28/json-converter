package sandbox;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String jsonPath1 = "src/main/resources/sample.json";
        String jsonPath2 = "src/main/resources/sample2.json";
        String[] jsonPath3 = {"src/main/resources/sample3/data1.json", "src/main/resources/sample3/data2.json"};

        String outputDir = "src/main/resources/sample3/";

        for(String jsonPath: jsonPath3) {
            JsonConverter.convertToCsv(jsonPath, outputDir);
            // JsonConverter2.convertToCsv(jsonPath, outputDir);  
            // JsonConverter3.convertToCsv(jsonPath, outputDir);  
            // JsonConverter4.convertToCsv(jsonPath, outputDir);  
        }
    }
}
