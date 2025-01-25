package net.earthcomputer.unpickv3parser.writer;

import net.earthcomputer.unpickv3parser.tree.TargetMethod;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TestTargetMethod {
    @Test
    public void testEmptyTargetMethod() {
        TestWriter.testTargetMethod(
            "target_method foo.Bar baz ()V",
            new TargetMethod("foo.Bar", "baz", "()V", Collections.emptyMap(), null)
        );
    }

    @Test
    public void testTargetMethodReturn() {
        TestWriter.testTargetMethod(
            "target_method foo.Bar baz ()V\n\treturn g",
            new TargetMethod("foo.Bar", "baz", "()V", Collections.emptyMap(), "g")
        );
    }

    @Test
    public void testTargetMethodParameters() {
        // use a linked hash map to make sure that the parameters are sorted in the output
        Map<Integer, String> paramGroups = new LinkedHashMap<>();
        paramGroups.put(69, "h");
        paramGroups.put(0, "g");
        TestWriter.testTargetMethod(
            "target_method foo.Bar baz ()V\n\tparam 0 g\n\tparam 69 h",
            new TargetMethod("foo.Bar", "baz", "()V", paramGroups, null)
        );
    }

    @Test
    public void testTargetMethodParametersAndReturn() {
        Map<Integer, String> paramGroups = new HashMap<>();
        paramGroups.put(0, "g");
        paramGroups.put(69, "h");
        TestWriter.testTargetMethod(
            "target_method foo.Bar baz ()V\n\tparam 0 g\n\tparam 69 h\n\treturn i",
            new TargetMethod("foo.Bar", "baz", "()V", paramGroups, "i")
        );
    }
}
