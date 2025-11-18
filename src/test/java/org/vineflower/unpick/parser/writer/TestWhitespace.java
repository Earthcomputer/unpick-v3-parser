package org.vineflower.unpick.parser.writer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.vineflower.unpick.parser.UnpickV3Writer;
import org.vineflower.unpick.parser.tree.DataType;
import org.vineflower.unpick.parser.tree.GroupDefinition;
import org.vineflower.unpick.parser.tree.Literal;
import org.vineflower.unpick.parser.tree.TargetField;
import org.vineflower.unpick.parser.tree.TargetMethod;
import org.vineflower.unpick.parser.tree.expr.LiteralExpression;

public final class TestWhitespace {
    @Test
    public void testEmptyFile() {
        TestWriter.test("unpick v3\n", visitor -> {
        });
    }

    @Test
    public void testMultipleEmptyGroups() {
        TestWriter.test("unpick v3\n\ngroup int\n\ngroup int\n", visitor -> {
            visitor.visitGroupDefinition(GroupDefinition.Builder.global(DataType.INT).build());
            visitor.visitGroupDefinition(GroupDefinition.Builder.global(DataType.INT).build());
        });
    }

    @Test
    public void testMultipleGroups() {
        TestWriter.test("unpick v3\n\ngroup int g\n\t0\n\ngroup int\n", visitor -> {
            visitor.visitGroupDefinition(GroupDefinition.Builder.named(DataType.INT, "g").constant(new LiteralExpression(new Literal.Integer(0))).build());
            visitor.visitGroupDefinition(GroupDefinition.Builder.global(DataType.INT).build());
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
            visitor.visitTargetMethod(TargetMethod.Builder.builder("foo.Bar", "baz", "()V").build());
            visitor.visitTargetMethod(TargetMethod.Builder.builder("foo.Bar", "baz", "()V").build());
        });
    }

    @Test
    public void testMultipleTargetMethods() {
        TestWriter.test("unpick v3\n\ntarget_method foo.Bar baz ()V\n\treturn g\n\ntarget_method foo.Bar baz ()V\n", visitor -> {
            visitor.visitTargetMethod(TargetMethod.Builder.builder("foo.Bar", "baz", "()V").returnGroup("g").build());
            visitor.visitTargetMethod(TargetMethod.Builder.builder("foo.Bar", "baz", "()V").build());
        });
    }

    @Test
    public void testMultipleTypes() {
        TestWriter.test("unpick v3\n\ntarget_field foo.Bar baz I g\n\ntarget_method foo.Bar baz ()V\n", visitor -> {
            visitor.visitTargetField(new TargetField("foo.Bar", "baz", "I", "g"));
            visitor.visitTargetMethod(TargetMethod.Builder.builder("foo.Bar", "baz", "()V").build());
        });
    }

    @Test
    public void testCustomIndent() {
        UnpickV3Writer writer = new UnpickV3Writer(" ");
        writer.visitTargetMethod(TargetMethod.Builder.builder("foo.Bar", "baz", "()V").returnGroup("g").build());
        String output = writer.getOutput().replace(System.lineSeparator(), "\n");
        assertEquals("unpick v3\n\ntarget_method foo.Bar baz ()V\n return g\n", output);
    }
}
