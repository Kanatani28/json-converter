package sandbox;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

public class JsonConverter2 {
    private static int counter = 0;

    public static void convertToCsv(String jsonPath, String outputDir) throws IOException {
        String jsonString = Files.readString(Paths.get(jsonPath));
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonStructure json = jsonReader.read();

        String[] keys = { "sender", "receiver", "documents" };
        for (String key : keys) {
            String result = getValueByKey(json, key);
            Files.writeString(Paths.get(outputDir + key + ".csv"), result);
        }
    }

    private static String getValueByKey(JsonStructure json, String key) {
        Function<JsonObject, String> objectToString = new Function<JsonObject, String>() {
            @Override
            public String apply(JsonObject t) {
                counter++;
                String id = "\"" + counter + "\"";
                Stream<String> jsonElements = t.entrySet().stream().map(entry -> {
                    return entry.getValue().toString();
                });
                List<String> elements = Stream.concat(Stream.of(id), jsonElements).toList();
                return String.join(",", elements);
            }
        };

        Function<JsonArray, String> arrayToString = new Function<JsonArray, String>() {
            @Override
            public String apply(JsonArray t) {
                List<String> lines = t.stream().map(json -> {
                    String line = switch (json.getValueType()) {
                        case OBJECT -> objectToString.apply(json.asJsonObject());
                        case ARRAY -> this.apply(json.asJsonArray());
                        default -> throw new IllegalStateException("Invalid JsonValue: " + json.toString());
                    };
                    return line;
                }).toList();
                return String.join(System.lineSeparator(), lines);
            }
        };

        JsonValue value = json.getValue("/" + key);
        ValueType valueType = value.getValueType();

        String lines = switch (valueType) {
            case OBJECT -> objectToString.apply(value.asJsonObject());
            case ARRAY -> arrayToString.apply(value.asJsonArray());
            default -> throw new IllegalStateException("Invalid JsonValue: " + key);
        };

        return lines;
    }
}
