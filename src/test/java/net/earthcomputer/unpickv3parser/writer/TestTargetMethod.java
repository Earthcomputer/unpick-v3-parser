package net.earthcomputer.unpickv3parser.writer;

import org.junit.jupiter.api.Test;

import net.earthcomputer.unpickv3parser.tree.TargetMethod;

public final class TestTargetMethod {
    @Test
    public void testEmptyTargetMethod() {
        TestWriter.testTargetMethod(
                "target_method foo.Bar baz ()V",
                TargetMethod.Builder.builder("foo.Bar", "baz", "()V").build()
        );
    }

    @Test
    public void testTargetMethodReturn() {
        TestWriter.testTargetMethod(
                "target_method foo.Bar baz ()V\n\treturn g",
                TargetMethod.Builder.builder("foo.Bar", "baz", "()V").returnGroup("g").build()
        );
    }

    @Test
    public void testTargetMethodParameters() {
        // use a linked hash map to make sure that the parameters are sorted in the output
        TestWriter.testTargetMethod(
                "target_method foo.Bar baz ()V\n\tparam 0 g\n\tparam 69 h",
                TargetMethod.Builder.builder("foo.Bar", "baz", "()V")
                        .paramGroup(69, "h")
                        .paramGroup(0, "g")
                        .build()
        );
    }

    @Test
    public void testTargetMethodParametersAndReturn() {
        TestWriter.testTargetMethod(
                "target_method foo.Bar baz ()V\n\tparam 0 g\n\tparam 69 h\n\treturn i",
                TargetMethod.Builder.builder("foo.Bar", "baz", "()V")
                        .paramGroup(0, "g")
                        .paramGroup(69, "h")
                        .returnGroup("i")
                        .build()
        );
    }
}
