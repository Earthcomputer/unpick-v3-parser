package net.earthcomputer.unpickv3parser.writer;

import org.junit.jupiter.api.Test;

import net.earthcomputer.unpickv3parser.tree.TargetAnnotation;

public class TestTargetAnnotation {
    @Test
    public void testTargetAnnotation() {
        TestWriter.testTargetAnnotation(
                "target_annotation foo.Bar baz",
                new TargetAnnotation("foo.Bar", "baz")
        );
    }
}
