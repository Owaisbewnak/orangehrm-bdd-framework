package com.owais.orangehrm.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Thread-safe scratchpad for sharing data between step-definition classes
 * inside one scenario (e.g. the employee name created in the PIM steps and
 * then searched for in a later step). Backed by a ThreadLocal map so
 * parallel scenarios never see each other's data. Cleared in Hooks @After.
 */
public final class TestContext {

    private static final ThreadLocal<Map<String, Object>> CONTEXT =
            ThreadLocal.withInitial(HashMap::new);

    private TestContext() {
    }

    public static void put(String key, Object value) {
        CONTEXT.get().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        return (T) CONTEXT.get().get(key);
    }

    public static String getString(String key) {
        Object value = CONTEXT.get().get(key);
        return value == null ? null : value.toString();
    }

    public static void clear() {
        CONTEXT.get().clear();
    }
}
