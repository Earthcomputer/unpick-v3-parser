package net.earthcomputer.unpickv3parser.writer;

import org.junit.jupiter.api.Test;

import net.earthcomputer.unpickv3parser.tree.TargetField;

public final class TestTargetField {
    @Test
    public void testTargetField() {
        TestWriter.testTargetField(
                "target_field foo.Bar baz I g",
                new TargetField("foo.Bar", "baz", "I", "g")
        );
    }
}
