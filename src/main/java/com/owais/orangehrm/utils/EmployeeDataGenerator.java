package com.owais.orangehrm.utils;

import java.security.SecureRandom;

/**
 * Generates unique, letters-only employee names. The OrangeHRM demo is a
 * shared environment, so re-using a fixed name ("John Doe") collides with
 * earlier runs and makes search assertions ambiguous. A short random suffix
 * keeps every run independent.
 */
public final class EmployeeDataGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String LETTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ";

    private EmployeeDataGenerator() {
    }

    public static String randomSuffix(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(LETTERS.charAt(RANDOM.nextInt(LETTERS.length())));
        }
        return sb.toString();
    }

    /** e.g. firstName "AutoKQX", lastName "TestMZB" */
    public static String[] uniqueEmployeeName(String firstNamePrefix, String lastNamePrefix) {
        return new String[]{
                firstNamePrefix + randomSuffix(3),
                lastNamePrefix + randomSuffix(3)
        };
    }

    public static String fullName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }
}
