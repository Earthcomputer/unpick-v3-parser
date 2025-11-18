package org.vineflower.unpick.parser.writer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Consumer;

import org.vineflower.unpick.parser.UnpickV3Writer;
import org.vineflower.unpick.parser.tree.DataType;
import org.vineflower.unpick.parser.tree.GroupDefinition;
import org.vineflower.unpick.parser.tree.TargetAnnotation;
import org.vineflower.unpick.parser.tree.TargetField;
import org.vineflower.unpick.parser.tree.TargetMethod;
import org.vineflower.unpick.parser.tree.UnpickV3Visitor;
import org.vineflower.unpick.parser.tree.expr.Expression;

public final class TestWriter {
    public static void testGroupDefinition(String expected, GroupDefinition groupDefinition) {
        test("unpick v3\n\n" + expected + "\n", visitor -> visitor.visitGroupDefinition(groupDefinition));
    }

    public static void testTargetField(String expected, TargetField targetField) {
        test("unpick v3\n\n" + expected + "\n", visitor -> visitor.visitTargetField(targetField));
    }

    public static void testTargetMethod(String expected, TargetMethod targetMethod) {
        test("unpick v3\n\n" + expected + "\n", visitor -> visitor.visitTargetMethod(targetMethod));
    }

    public static void testTargetAnnotation(String expected, TargetAnnotation targetAnnotation) {
        test("unpick v4\n\n" + expected + "\n", visitor -> {
            visitor.visitHeader(4);
            visitor.visitTargetAnnotation(targetAnnotation);
        });
    }

    public static void testExpression(String expected, Expression expression) {
        GroupDefinition groupDefinition = GroupDefinition.Builder.global(DataType.INT).constant(expression).build();
        testGroupDefinition("group int\n\t" + expected, groupDefinition);
    }

    public static void test(String expected, Consumer<UnpickV3Visitor> visitorConsumer) {
        UnpickV3Writer writer = new UnpickV3Writer();
        visitorConsumer.accept(writer);
        String actual = writer.getOutput().replace(System.lineSeparator(), "\n");
        assertEquals(expected, actual);
    }
}
