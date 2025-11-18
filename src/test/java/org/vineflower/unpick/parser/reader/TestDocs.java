package org.vineflower.unpick.parser.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import org.vineflower.unpick.parser.tree.GroupDefinition;
import org.vineflower.unpick.parser.tree.UnpickV3Visitor;

public final class TestDocs {
    @Test
    public void testDocs() throws IOException {
        List<@Nullable String> docs = new ArrayList<>();
        TestReader.test("docs/docs", new UnpickV3Visitor() {
            @Override
            public void visitGroupDefinition(GroupDefinition groupDefinition) {
                docs.add(groupDefinition.docs());
            }
        });
        assertEquals(Arrays.asList("foo", "bar", "boo\nand\nfoo", null, "\nsome\n\nblank\n\n\nlines\n", null), docs);
    }
}
