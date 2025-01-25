package net.earthcomputer.unpickv3parser.writer;

import net.earthcomputer.unpickv3parser.UnpickV3Writer;
import net.earthcomputer.unpickv3parser.tree.DataType;
import net.earthcomputer.unpickv3parser.tree.GroupConstant;
import net.earthcomputer.unpickv3parser.tree.GroupDefinition;
import net.earthcomputer.unpickv3parser.tree.GroupScope;
import net.earthcomputer.unpickv3parser.tree.GroupType;
import net.earthcomputer.unpickv3parser.tree.Literal;
import net.earthcomputer.unpickv3parser.tree.TargetField;
import net.earthcomputer.unpickv3parser.tree.TargetMethod;
import net.earthcomputer.unpickv3parser.tree.UnpickV3Visitor;
import net.earthcomputer.unpickv3parser.tree.expr.Expression;

import java.util.Collections;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

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

    public static void testExpression(String expected, Expression expression) {
        GroupConstant constant = new GroupConstant(new Literal.Long(0), expression);
        GroupDefinition groupDefinition = new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.INT, null, Collections.singletonList(constant), null);
        testGroupDefinition("const int\n\t0 = " + expected, groupDefinition);
    }

    public static void test(String expected, Consumer<UnpickV3Visitor> visitorConsumer) {
        UnpickV3Writer writer = new UnpickV3Writer();
        visitorConsumer.accept(writer);
        String actual = writer.getOutput().replace(System.lineSeparator(), "\n");
        assertEquals(expected, actual);
    }
}
