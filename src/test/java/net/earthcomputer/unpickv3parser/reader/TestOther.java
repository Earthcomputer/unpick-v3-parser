package net.earthcomputer.unpickv3parser.reader;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class TestOther {
    @Test
    public void testSpec() throws IOException {
        TestReader.test("other/spec");
    }

    @Test
    public void testMissingHeader() throws IOException {
        TestReader.assertThrowsParseError("other/invalid/missing_header", 1, 1);
    }
}
