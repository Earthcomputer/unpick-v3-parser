package net.earthcomputer.unpickv3parser.writer;

import org.junit.jupiter.api.Test;

import net.earthcomputer.unpickv3parser.tree.DataType;
import net.earthcomputer.unpickv3parser.tree.GroupDefinition;

public final class TestDocs {
    @Test
    public void testDocs() {
        TestWriter.testGroupDefinition(
                "#: \n#: boo\n#: and\n#: \n#: foo\n#: \ngroup int",
                GroupDefinition.Builder.global(DataType.INT).docs("\nboo\nand\n\nfoo\n").build()
        );
    }
}
