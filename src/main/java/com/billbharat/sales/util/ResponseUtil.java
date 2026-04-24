package com.billbharat.sales.util;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for building standardized API responses.
 */
@UtilityClass
public class ResponseUtil {

    public static Map<String, Object> success(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    public static Map<String, Object> success(Object data) {
        return success("Operation successful", data);
    }

    public static Map<String, Object> paginated(List<?> data, long total, int page, int limit) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("page", page);
        response.put("limit", limit);
        response.put("total", total);
        response.put("totalPages", (int) Math.ceil((double) total / limit));
        return response;
    }

    public static Map<String, Object> error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }

    public static Map<String, Object> error(String message, Object details) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("error", details);
        return response;
    }
}
