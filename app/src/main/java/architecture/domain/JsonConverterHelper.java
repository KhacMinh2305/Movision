package architecture.domain;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Map;

public class JsonConverterHelper {
    private final Gson gson;

    public JsonConverterHelper() {
        this.gson = new Gson();
    }

    public JsonObject convertStringToJsonObject(String jsonString) {
        return gson.fromJson(jsonString, JsonObject.class);
    }

    public String convertJsonObjectToString(JsonObject jsonObject) {
        return gson.toJson(jsonObject);
    }

    public JsonObject createJsonObject(Map<String, String> content) {
        if (content.isEmpty()) {
            return null;
        }
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, String> entry : content.entrySet()) {
            jsonObject.addProperty(entry.getKey(), entry.getValue());
        }
        return jsonObject;
    }
}
