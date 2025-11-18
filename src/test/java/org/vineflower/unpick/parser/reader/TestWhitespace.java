package org.vineflower.unpick.parser.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import org.vineflower.unpick.parser.tree.TargetField;
import org.vineflower.unpick.parser.tree.UnpickV3Visitor;

public final class TestWhitespace {
    @Test
    public void testBlankLines() throws IOException {
        checkTargetFields("whitespace/blank_lines", 1);
    }

    @Test
    public void testComments() throws IOException {
        checkTargetFields("whitespace/comments", 2);
    }

    @Test
    public void testIndent() throws IOException {
        TestReader.test("whitespace/indent");
    }

    @Test
    public void testMinimalSpaces() throws IOException {
        TestReader.test("whitespace/minimal_spaces");
    }

    @Test
    public void testCommentOnFirstLine() throws IOException {
        TestReader.assertThrowsParseError("whitespace/invalid/comment_on_first_line", 1, 1, "Missing version marker");
    }

    @Test
    public void testWhitespaceBeforeFirstLine() throws IOException {
        TestReader.assertThrowsParseError("whitespace/invalid/whitespace_before_first_line", 1, 1, "Missing version marker");
    }

    @Test
    public void testInvalidIndent() throws IOException {
        TestReader.assertThrowsParseError("whitespace/invalid/indent", 3, 1, "Expected unpick item before '    ' token");
    }

    private static void checkTargetFields(String file, int expectedCount) throws IOException {
        int[] targetFieldCount = {0};
        TestReader.test(file, new UnpickV3Visitor() {
            @Override
            public void visitTargetField(TargetField targetField) {
                assertEquals("foo.Bar", targetField.className());
                assertEquals("baz", targetField.fieldName());
                assertEquals("I", targetField.fieldDesc());
                assertEquals("g", targetField.groupName());
                targetFieldCount[0]++;
            }
        });
        assertEquals(expectedCount, targetFieldCount[0]);
    }
}
