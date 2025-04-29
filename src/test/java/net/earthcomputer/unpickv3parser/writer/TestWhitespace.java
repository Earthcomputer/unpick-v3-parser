package net.earthcomputer.unpickv3parser.writer;

import net.earthcomputer.unpickv3parser.UnpickV3Writer;
import net.earthcomputer.unpickv3parser.tree.DataType;
import net.earthcomputer.unpickv3parser.tree.GroupDefinition;
import net.earthcomputer.unpickv3parser.tree.Literal;
import net.earthcomputer.unpickv3parser.tree.TargetField;
import net.earthcomputer.unpickv3parser.tree.TargetMethod;
import net.earthcomputer.unpickv3parser.tree.expr.LiteralExpression;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public final class TestWhitespace {
    @Test
    public void testEmptyFile() {
        TestWriter.test("unpick v3\n", visitor -> {});
    }

    @Test
    public void testMultipleEmptyGroups() {
        TestWriter.test("unpick v3\n\ngroup int\n\ngroup int\n", visitor -> {
            visitor.visitGroupDefinition(new GroupDefinition(Collections.emptyList(), false, false, DataType.INT, null, Collections.emptyList(), null));
            visitor.visitGroupDefinition(new GroupDefinition(Collections.emptyList(), false, false, DataType.INT, null, Collections.emptyList(), null));
        });
    }

    @Test
    public void testMultipleGroups() {
        TestWriter.test("unpick v3\n\ngroup int g\n\t0\n\ngroup int\n", visitor -> {
            visitor.visitGroupDefinition(new GroupDefinition(Collections.emptyList(), false, false, DataType.INT, "g", Collections.singletonList(new LiteralExpression(new Literal.Integer(0))), null));
            visitor.visitGroupDefinition(new GroupDefinition(Collections.emptyList(), false, false, DataType.INT, null, Collections.emptyList(), null));
        });
    }

    @Test
    public void testMultipleTargetFields() {
        TestWriter.test("unpick v3\n\ntarget_field foo.Bar baz I g\n\ntarget_field foo.Bar baz I g\n", visitor -> {
            visitor.visitTargetField(new TargetField("foo.Bar", "baz", "I", "g"));
            visitor.visitTargetField(new TargetField("foo.Bar", "baz", "I", "g"));
        });
    }

    @Test
    public void testMultipleEmptyTargetMethods() {
        TestWriter.test("unpick v3\n\ntarget_method foo.Bar baz ()V\n\ntarget_method foo.Bar baz ()V\n", visitor -> {
            visitor.visitTargetMethod(new TargetMethod("foo.Bar", "baz", "()V", Collections.emptyMap(), null));
            visitor.visitTargetMethod(new TargetMethod("foo.Bar", "baz", "()V", Collections.emptyMap(), null));
        });
    }

    @Test
    public void testMultipleTargetMethods() {
        TestWriter.test("unpick v3\n\ntarget_method foo.Bar baz ()V\n\treturn g\n\ntarget_method foo.Bar baz ()V\n", visitor -> {
            visitor.visitTargetMethod(new TargetMethod("foo.Bar", "baz", "()V", Collections.emptyMap(), "g"));
            visitor.visitTargetMethod(new TargetMethod("foo.Bar", "baz", "()V", Collections.emptyMap(), null));
        });
    }

    @Test
    public void testMultipleTypes() {
        TestWriter.test("unpick v3\n\ntarget_field foo.Bar baz I g\n\ntarget_method foo.Bar baz ()V\n", visitor -> {
            visitor.visitTargetField(new TargetField("foo.Bar", "baz", "I", "g"));
            visitor.visitTargetMethod(new TargetMethod("foo.Bar", "baz", "()V", Collections.emptyMap(), null));
        });
    }

    @Test
    public void testCustomIndent() {
        UnpickV3Writer writer = new UnpickV3Writer(" ");
        writer.visitTargetMethod(new TargetMethod("foo.Bar", "baz", "()V", Collections.emptyMap(), "g"));
        String output = writer.getOutput().replace(System.lineSeparator(), "\n");
        assertEquals("unpick v3\n\ntarget_method foo.Bar baz ()V\n return g\n", output);
    }
}
