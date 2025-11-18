package org.vineflower.unpick.parser.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.opentest4j.AssertionFailedError;

import org.vineflower.unpick.parser.UnpickParseException;
import org.vineflower.unpick.parser.UnpickV3Reader;
import org.vineflower.unpick.parser.tree.UnpickV3Visitor;

public final class TestReader {
    private TestReader() {
    }

    public static void assertThrowsParseError(String file, int line, int column, String message) throws IOException {
        try {
            test(file);
            throw new AssertionFailedError("Did not throw parse error");
        } catch (UnpickParseException e) {
            assertEquals(line + ":" + column + ": " + message, e.getMessage());
            assertEquals(line, e.line, "Mismatching line");
            assertEquals(column, e.column, "Mismatching column");
        }
    }

    public static void test(String file) throws IOException {
        test(file, new UnpickV3Visitor() {
        });
    }

    public static void test(String file, UnpickV3Visitor visitor) throws IOException {
        InputStream in = TestReader.class.getResourceAsStream("/" + file + ".unpick");
        if (in == null) {
            throw new AssertionFailedError("Unable to find resource " + file);
        }
        try (UnpickV3Reader reader = new UnpickV3Reader(new BufferedReader(new InputStreamReader(in)))) {
            reader.accept(visitor);
        }
    }
}
