package com.owais.orangehrm.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/** Reads JSON test data from src/test/resources/testdata. */
public final class JsonTestData {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonTestData() {
    }

    public static List<Map<String, String>> readEmployees() {
        try (InputStream in = JsonTestData.class.getClassLoader()
                .getResourceAsStream("testdata/employees.json")) {
            if (in == null) {
                throw new IllegalStateException("testdata/employees.json not found");
            }
            return MAPPER.readValue(in, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read employees.json", e);
        }
    }
}
