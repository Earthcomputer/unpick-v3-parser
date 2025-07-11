package net.earthcomputer.unpickv3parser.reader;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public final class TestOther {
    @Test
    public void testSpec() throws IOException {
        TestReader.test("other/spec");
    }

    @Test
    public void testMissingHeader() throws IOException {
        TestReader.assertThrowsParseError("other/invalid/missing_header", 1, 1, "Missing version marker");
    }
}
