package sandbox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class JsonConverter4 {
    public static void convertToCsv(String jsonPath, String outputDir) throws IOException {
        File jsonFile = new File(jsonPath);

        new CsvConverter()
                .readFile(jsonFile) // ファイルから読み込む事を型安全にするためFile型を渡す
                .writeCsvFile(outputDir, "sender") // 出力先が変更し易いようにoutputPathを渡してます
                .writeCsvFile(outputDir, "receiver")
                .writeCsvFile(outputDir, "documents");
    }


    private static class CsvConverter {
        private static JsonObject jsonObject;
        private static final Iterator<Integer> rowIndex = Stream.iterate(1, i -> ++i).iterator();

        public CsvConverter readFile(File file) throws IOException {
            // try-with-resources文を使うことで自動closeしてくれます
            try (FileInputStream is = new FileInputStream(file);
                    JsonReader jsonReader = Json.createReader(is)) {
                jsonObject = jsonReader.readObject();
            }
            return this;
        }

        public CsvConverter writeCsvFile(String outputPath, String jsonPath) throws IOException {
            File file = new File(outputPath + "/" + jsonPath + ".csv");
            JsonValue jsonValue = jsonObject.getValue("/" + jsonPath);
            // JsonObjecはJsonArrayに変換し必ずJsonArrayとして取得することでCsvに変換しやすくします
            Optional<JsonArray> jsonArray = getJsonArray(jsonValue);
            if (jsonArray.isPresent()) {
                new CsvMapper(jsonArray.get())
                        .map(this::numbering)
                        .withHeader() // ヘッダをつけることも簡単に出来ます
                        .writeValue(file);
            }
            return this;
        }

        private Optional<JsonArray> getJsonArray(JsonValue jsonValue) {
            // JsonValueのgetValueType()を使う事でObjectかArrayを判定できます
            switch (jsonValue.getValueType()) {
                case OBJECT:
                    return Optional.of(Json.createArrayBuilder().add(jsonValue).build());
                case ARRAY:
                    return Optional.of(jsonValue.asJsonArray());
                default:
                    return Optional.empty();
            }
        }

        // JsonObjectにid列を追加して採番する事でCsvに変換した後で採番する必要を無くしています
        private JsonObject numbering(JsonValue jsonValue) {
            // JsonPatchObjectは後ろにしか値を追加できないためJsonObjectBuilderを使って前に追加しています
            JsonObject obj = jsonValue.asJsonObject();
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("id", rowIndex.next().toString());
            for (String k : obj.keySet()) {
                builder.add(k, obj.getJsonString(k));
            }
            return builder.build();
        }
    }

    public static class CsvMapper {
        private JsonArray jsonArray;
        private Optional<String> headers = Optional.empty();

        public CsvMapper(JsonArray jsonArray) {
            this.jsonArray = jsonArray;
        }

        public CsvMapper map(Function<JsonValue, JsonObject> func) {
            JsonArrayBuilder builder = Json.createArrayBuilder();
            for (JsonValue value : jsonArray) {
                builder.add(func.apply(value));
            }
            return new CsvMapper(builder.build());
        }

        public void writeValue(File file) throws IOException {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                // ヘッダが生成されている場合のみ出力する
                if (headers.isPresent()) {
                    bw.write(headers.get());
                    bw.newLine();
                }

                // StreamはExceptionを外に投げることができない為iteratorにキャストしてforで処理します
                for (String line : (Iterable<String>) jsonArray.stream().map(this::toCsv)::iterator) {
                    bw.write(line);
                    bw.newLine();
                }
            }
        }

        public CsvMapper withHeader() {
            headers = Optional.of(jsonArray
                    .getJsonObject(0)
                    .keySet()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(",")));
            return this;
        }

        private String toCsv(JsonValue jsonValue) {
            return jsonValue
                    .asJsonObject()
                    .values()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
        }
    }
}
