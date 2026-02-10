package org.autoflex.factory;

import java.util.UUID;

public final class TestData {
    private TestData() {
    }

    public static String unique(String prefix) {
        return prefix + "-" + UUID.randomUUID();
    }
}

