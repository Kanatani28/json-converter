package sandbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class JsonConverter3 {
    public static void convertToCsv(String jsonPath, String outputDir) throws IOException {
        JsonObject jsonObject = createJsonObject(jsonPath);
        
        CsvMaker csvMaker = new CsvMaker(jsonObject, outputDir);
        csvMaker.makeSenderCsvFile();
        csvMaker.makeReceiverCsvFile();
        csvMaker.makeDocumentsCsvFile();
    }

    private static JsonObject createJsonObject(String jsonPath) throws FileNotFoundException {
        File jsonFile = new File(jsonPath);
        JsonReader jsonReader = Json.createReader(new FileInputStream(jsonFile));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();
        return jsonObject;
    }

    private static class CsvMaker {
        private final JsonObject jsonObject;
        private final String outputPath;
        private int rowIndex = 1;

        public CsvMaker(JsonObject jsonObject, String outputPath) {
            this.jsonObject = jsonObject;
            this.outputPath = outputPath;
        }

        public void makeSenderCsvFile() throws IOException {

            JsonArray senderJsonArray = jsonObject.getJsonArray("sender");
            String senderCsvString = "";
            for (int i = 0; i < senderJsonArray.size(); i++) {
                JsonObject senderJsonObj = (JsonObject) senderJsonArray.get(i);
                senderCsvString += createCsvLine(senderJsonObj) + System.lineSeparator();
            }
            createCsvFile(senderCsvString, "sender.csv", outputPath);
        }

        public void makeReceiverCsvFile() throws IOException {
            JsonArray receiverJsonArray = jsonObject.getJsonArray("receiver");
            String receiverCsvString = "";
            for (int i = 0; i < receiverJsonArray.size(); i++) {
                JsonObject receiverJsonObj = (JsonObject) receiverJsonArray.get(i);
                receiverCsvString += createCsvLine(receiverJsonObj) + System.lineSeparator();
            }
            createCsvFile(receiverCsvString, "receiver.csv", outputPath);
        }

        public void makeDocumentsCsvFile() throws IOException {
            JsonObject documentsJsonObj = jsonObject.getJsonObject("documents");
            String documentString = createCsvLine(documentsJsonObj);
            createCsvFile(documentString, "documents.csv", outputPath);
        }

        private String createCsvLine(JsonObject jsonObject) {
            String csvLine = "\"" + this.rowIndex++ + "\",";
            for (JsonValue value : jsonObject.values()) {
                csvLine += value.toString() + ",";
            }
            csvLine = csvLine.substring(0, csvLine.length() - 1);
            return csvLine;
        }

        private void createCsvFile(String csvString, String fileName, String outputPath) throws IOException {
            File csvFile = new File(this.outputPath + "/" + fileName);
            csvFile.createNewFile();
            FileWriter fw = new FileWriter(csvFile);
            fw.write(csvString);
            fw.close();
        }
    }
}
