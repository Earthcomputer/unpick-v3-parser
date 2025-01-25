package net.earthcomputer.unpickv3parser.writer;

import net.earthcomputer.unpickv3parser.tree.DataType;
import net.earthcomputer.unpickv3parser.tree.GroupConstant;
import net.earthcomputer.unpickv3parser.tree.GroupDefinition;
import net.earthcomputer.unpickv3parser.tree.GroupFormat;
import net.earthcomputer.unpickv3parser.tree.GroupScope;
import net.earthcomputer.unpickv3parser.tree.GroupType;
import net.earthcomputer.unpickv3parser.tree.Literal;
import net.earthcomputer.unpickv3parser.tree.expr.LiteralExpression;
import net.earthcomputer.unpickv3parser.tree.expr.UnaryExpression;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public final class TestGroupDefinition {
    @Test
    public void testSimpleGroupDefinition() {
        TestWriter.testGroupDefinition(
            "const int",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.INT, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testPackageScope() {
        TestWriter.testGroupDefinition(
            "scoped package foo.bar const int",
            new GroupDefinition(new GroupScope.Package("foo.bar"), GroupType.CONST, false, DataType.INT, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testClassScope() {
        TestWriter.testGroupDefinition(
            "scoped class foo.Bar const int",
            new GroupDefinition(new GroupScope.Class("foo.Bar"), GroupType.CONST, false, DataType.INT, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testMethodScope() {
        TestWriter.testGroupDefinition(
            "scoped method foo.Bar baz ()V const int",
            new GroupDefinition(new GroupScope.Method("foo.Bar", "baz", "()V"), GroupType.CONST, false, DataType.INT, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testStrict() {
        TestWriter.testGroupDefinition(
            "const strict int",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, true, DataType.INT, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testStrictWithScope() {
        TestWriter.testGroupDefinition(
            "scoped package foo.bar const strict int",
            new GroupDefinition(new GroupScope.Package("foo.bar"), GroupType.CONST, true, DataType.INT, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testFlag() {
        TestWriter.testGroupDefinition(
            "flag int",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.FLAG, false, DataType.INT, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testLong() {
        TestWriter.testGroupDefinition(
            "const long",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.LONG, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testFloat() {
        TestWriter.testGroupDefinition(
            "const float",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.FLOAT, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testDouble() {
        TestWriter.testGroupDefinition(
            "const double",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.DOUBLE, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testString() {
        TestWriter.testGroupDefinition(
            "const String",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.STRING, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testNamed() {
        TestWriter.testGroupDefinition(
            "const int g",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.INT, "g", Collections.emptyList(), null)
        );
    }

    @Test
    public void testFormatDecimal() {
        TestWriter.testGroupDefinition(
            "const int\n\tformat = decimal",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.INT, null, Collections.emptyList(), GroupFormat.DECIMAL)
        );
    }

    @Test
    public void testFormatHex() {
        TestWriter.testGroupDefinition(
            "const int\n\tformat = hex",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.INT, null, Collections.emptyList(), GroupFormat.HEX)
        );
    }

    @Test
    public void testFormatBinary() {
        TestWriter.testGroupDefinition(
            "const int\n\tformat = binary",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.INT, null, Collections.emptyList(), GroupFormat.BINARY)
        );
    }

    @Test
    public void testFormatOctal() {
        TestWriter.testGroupDefinition(
            "const int\n\tformat = octal",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.INT, null, Collections.emptyList(), GroupFormat.OCTAL)
        );
    }

    @Test
    public void testFormatChar() {
        TestWriter.testGroupDefinition(
            "const int\n\tformat = char",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.INT, null, Collections.emptyList(), GroupFormat.CHAR)
        );
    }

    @Test
    public void testSimpleConstant() {
        GroupConstant constant = new GroupConstant(new Literal.Long(0), new LiteralExpression(new Literal.Integer(0)));
        TestWriter.testGroupDefinition(
            "const int\n\t0 = 0",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.INT, null, Collections.singletonList(constant), null)
        );
    }

    @Test
    public void testConstantWithFormat() {
        GroupConstant constant = new GroupConstant(new Literal.Long(0), new LiteralExpression(new Literal.Integer(0)));
        TestWriter.testGroupDefinition(
            "const int\n\tformat = hex\n\t0 = 0",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.INT, null, Collections.singletonList(constant), GroupFormat.HEX)
        );
    }

    @Test
    public void testLongConstant() {
        GroupConstant constant = new GroupConstant(new Literal.Long(Long.MAX_VALUE), new LiteralExpression(new Literal.Long(Long.MAX_VALUE)));
        TestWriter.testGroupDefinition(
            "const long\n\t9223372036854775807 = 9223372036854775807L",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.LONG, null, Collections.singletonList(constant), null)
        );
    }

    @Test
    public void testNegativeLongConstant() {
        GroupConstant constant = new GroupConstant(new Literal.Long(Long.MIN_VALUE), new UnaryExpression(new LiteralExpression(new Literal.Long(Long.MIN_VALUE)), UnaryExpression.Operator.NEGATE));
        TestWriter.testGroupDefinition(
            "const long\n\t-9223372036854775808 = -9223372036854775808L",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.LONG, null, Collections.singletonList(constant), null)
        );
    }

    @Test
    public void testHexLongConstant() {
        GroupConstant constant = new GroupConstant(new Literal.Long(-1, 16), new LiteralExpression(new Literal.Long(-1, 16)));
        TestWriter.testGroupDefinition(
            "const long\n\t0xffffffffffffffff = 0xffffffffffffffffL",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.LONG, null, Collections.singletonList(constant), null)
        );
    }

    @Test
    public void testDecimalConstant() {
        GroupConstant constant = new GroupConstant(new Literal.Long(10, 2), new LiteralExpression(new Literal.Integer(10, 2)));
        TestWriter.testGroupDefinition(
            "const int\n\t0b1010 = 0b1010",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.INT, null, Collections.singletonList(constant), null)
        );
    }

    @Test
    public void testOctalConstant() {
        GroupConstant constant = new GroupConstant(new Literal.Long(511, 8), new LiteralExpression(new Literal.Integer(511, 8)));
        TestWriter.testGroupDefinition(
            "const int\n\t0777 = 0777",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.INT, null, Collections.singletonList(constant), null)
        );
    }

    @Test
    public void testDoubleConstant() {
        GroupConstant constant = new GroupConstant(new Literal.Double(1.5), new LiteralExpression(new Literal.Double(1.5)));
        TestWriter.testGroupDefinition(
            "const double\n\t1.5 = 1.5",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.DOUBLE, null, Collections.singletonList(constant), null)
        );
    }

    @Test
    public void testStringConstant() {
        GroupConstant constant = new GroupConstant(new Literal.String(""), new LiteralExpression(new Literal.String("")));
        TestWriter.testGroupDefinition(
            "const String\n\t\"\" = \"\"",
            new GroupDefinition(GroupScope.Global.INSTANCE, GroupType.CONST, false, DataType.STRING, null, Collections.singletonList(constant), null)
        );
    }
}
