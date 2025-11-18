package org.vineflower.unpick.parser.writer;

import org.junit.jupiter.api.Test;

import org.vineflower.unpick.parser.tree.DataType;
import org.vineflower.unpick.parser.tree.GroupDefinition;

public final class TestDocs {
    @Test
    public void testDocs() {
        TestWriter.testGroupDefinition(
                "#: \n#: boo\n#: and\n#: \n#: foo\n#: \ngroup int",
                GroupDefinition.Builder.global(DataType.INT).docs("\nboo\nand\n\nfoo\n").build()
        );
    }
}
