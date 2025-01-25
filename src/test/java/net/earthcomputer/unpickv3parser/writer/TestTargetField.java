package net.earthcomputer.unpickv3parser.writer;

import net.earthcomputer.unpickv3parser.tree.TargetField;
import org.junit.jupiter.api.Test;

public final class TestTargetField {
    @Test
    public void testTargetField() {
        TestWriter.testTargetField(
            "target_field foo.Bar baz I g",
            new TargetField("foo.Bar", "baz", "I", "g")
        );
    }
}
