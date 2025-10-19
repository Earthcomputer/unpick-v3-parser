package net.earthcomputer.unpickv3parser.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import net.earthcomputer.unpickv3parser.tree.UnpickV3Visitor;

public final class TestOther {
    @Test
    public void testSpec() throws IOException {
        int[] headerVersion = new int[1];
        TestReader.test("other/spec", new UnpickV3Visitor() {
            @Override
            public void visitHeader(int version) {
                headerVersion[0] = version;
            }
        });
        assertEquals(4, headerVersion[0]);
    }

    @Test
    public void testMissingHeader() throws IOException {
        TestReader.assertThrowsParseError("other/invalid/missing_header", 1, 1, "Missing version marker");
    }
}
