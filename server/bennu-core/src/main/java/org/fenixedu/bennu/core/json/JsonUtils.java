package org.fenixedu.bennu.core.json;

import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtils {
    public static String getString(JsonObject json, String property) {
        if (!json.has(property)) {
            return null;
        }
        JsonElement value = json.get(property);
        if (!value.isJsonPrimitive()) {
            throw BennuCoreDomainException.wrongJsonFormat(value, "primitive");
        }
        return value.getAsString();
    }

    public static <T extends Object> T get(JsonObject json, String property, JsonBuilder ctx, Class<T> type) {
        if (!json.has(property)) {
            return null;
        }
        return ctx.create(json.get(property), type);
    }

    public static void put(JsonObject json, String property, String value) {
        if (value != null) {
            json.addProperty(property, value);
        }
    }

    public static void put(JsonObject json, String property, JsonElement value) {
        if (value != null) {
            json.add(property, value);
        }
    }
}
