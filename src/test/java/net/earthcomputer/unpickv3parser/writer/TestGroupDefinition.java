package net.earthcomputer.unpickv3parser.writer;

import org.junit.jupiter.api.Test;

import net.earthcomputer.unpickv3parser.tree.DataType;
import net.earthcomputer.unpickv3parser.tree.GroupDefinition;
import net.earthcomputer.unpickv3parser.tree.GroupFormat;
import net.earthcomputer.unpickv3parser.tree.GroupScope;
import net.earthcomputer.unpickv3parser.tree.Literal;
import net.earthcomputer.unpickv3parser.tree.expr.Expression;
import net.earthcomputer.unpickv3parser.tree.expr.LiteralExpression;
import net.earthcomputer.unpickv3parser.tree.expr.UnaryExpression;

public final class TestGroupDefinition {
    @Test
    public void testSimpleGroupDefinition() {
        TestWriter.testGroupDefinition(
                "group int",
                GroupDefinition.Builder.global(DataType.INT).build()
        );
    }

    @Test
    public void testPackageScope() {
        TestWriter.testGroupDefinition(
                "group int\n\t@scope package foo.bar",
                GroupDefinition.Builder.global(DataType.INT).scope(new GroupScope.Package("foo.bar")).build()
        );
    }

    @Test
    public void testClassScope() {
        TestWriter.testGroupDefinition(
                "group int\n\t@scope class foo.Bar",
                GroupDefinition.Builder.global(DataType.INT).scope(new GroupScope.Class("foo.Bar")).build()
        );
    }

    @Test
    public void testMethodScope() {
        TestWriter.testGroupDefinition(
                "group int\n\t@scope method foo.Bar baz ()V",
                GroupDefinition.Builder.global(DataType.INT).scope(new GroupScope.Method("foo.Bar", "baz", "()V")).build()
        );
    }

    @Test
    public void testStrict() {
        TestWriter.testGroupDefinition(
                "group int\n\t@strict",
                GroupDefinition.Builder.global(DataType.INT).strict().build()
        );
    }

    @Test
    public void testStrictWithScope() {
        TestWriter.testGroupDefinition(
                "group int\n\t@scope package foo.bar\n\t@strict",
                GroupDefinition.Builder.global(DataType.INT).scope(new GroupScope.Package("foo.bar")).strict().build()
        );
    }

    @Test
    public void testFlag() {
        TestWriter.testGroupDefinition(
                "group int\n\t@flags",
                GroupDefinition.Builder.global(DataType.INT).flags().build()
        );
    }

    @Test
    public void testLong() {
        TestWriter.testGroupDefinition(
                "group long",
                GroupDefinition.Builder.global(DataType.LONG).build()
        );
    }

    @Test
    public void testFloat() {
        TestWriter.testGroupDefinition(
                "group float",
                GroupDefinition.Builder.global(DataType.FLOAT).build()
        );
    }

    @Test
    public void testDouble() {
        TestWriter.testGroupDefinition(
                "group double",
                GroupDefinition.Builder.global(DataType.DOUBLE).build()
        );
    }

    @Test
    public void testString() {
        TestWriter.testGroupDefinition(
                "group String",
                GroupDefinition.Builder.global(DataType.STRING).build()
        );
    }

    @Test
    public void testNamed() {
        TestWriter.testGroupDefinition(
                "group int g",
                GroupDefinition.Builder.named(DataType.INT, "g").build()
        );
    }

    @Test
    public void testFormatDecimal() {
        TestWriter.testGroupDefinition(
                "group int\n\t@format decimal",
                GroupDefinition.Builder.global(DataType.INT).format(GroupFormat.DECIMAL).build()
        );
    }

    @Test
    public void testFormatHex() {
        TestWriter.testGroupDefinition(
                "group int\n\t@format hex",
                GroupDefinition.Builder.global(DataType.INT).format(GroupFormat.HEX).build()
        );
    }

    @Test
    public void testFormatBinary() {
        TestWriter.testGroupDefinition(
                "group int\n\t@format binary",
                GroupDefinition.Builder.global(DataType.INT).format(GroupFormat.BINARY).build()
        );
    }

    @Test
    public void testFormatOctal() {
        TestWriter.testGroupDefinition(
                "group int\n\t@format octal",
                GroupDefinition.Builder.global(DataType.INT).format(GroupFormat.OCTAL).build()
        );
    }

    @Test
    public void testFormatChar() {
        TestWriter.testGroupDefinition(
                "group int\n\t@format char",
                GroupDefinition.Builder.global(DataType.INT).format(GroupFormat.CHAR).build()
        );
    }

    @Test
    public void testSimpleConstant() {
        Expression constant = new LiteralExpression(new Literal.Integer(0));
        TestWriter.testGroupDefinition(
                "group int\n\t0",
                GroupDefinition.Builder.global(DataType.INT).constant(constant).build()
        );
    }

    @Test
    public void testConstantWithFormat() {
        Expression constant = new LiteralExpression(new Literal.Integer(0));
        TestWriter.testGroupDefinition(
                "group int\n\t@format hex\n\t0",
                GroupDefinition.Builder.global(DataType.INT).format(GroupFormat.HEX).constant(constant).build()
        );
    }

    @Test
    public void testLongConstant() {
        Expression constant = new LiteralExpression(new Literal.Long(Long.MAX_VALUE));
        TestWriter.testGroupDefinition(
                "group long\n\t9223372036854775807L",
                GroupDefinition.Builder.global(DataType.LONG).constant(constant).build()
        );
    }

    @Test
    public void testNegativeLongConstant() {
        Expression constant = new UnaryExpression(new LiteralExpression(new Literal.Long(Long.MIN_VALUE)), UnaryExpression.Operator.NEGATE);
        TestWriter.testGroupDefinition(
                "group long\n\t-9223372036854775808L",
                GroupDefinition.Builder.global(DataType.LONG).constant(constant).build()
        );
    }

    @Test
    public void testHexLongConstant() {
        Expression constant = new LiteralExpression(new Literal.Long(-1, 16));
        TestWriter.testGroupDefinition(
                "group long\n\t0xffffffffffffffffL",
                GroupDefinition.Builder.global(DataType.LONG).constant(constant).build()
        );
    }

    @Test
    public void testDecimalConstant() {
        Expression constant = new LiteralExpression(new Literal.Integer(10, 2));
        TestWriter.testGroupDefinition(
                "group int\n\t0b1010",
                GroupDefinition.Builder.global(DataType.INT).constant(constant).build()
        );
    }

    @Test
    public void testOctalConstant() {
        Expression constant = new LiteralExpression(new Literal.Integer(511, 8));
        TestWriter.testGroupDefinition(
                "group int\n\t0777",
                GroupDefinition.Builder.global(DataType.INT).constant(constant).build()
        );
    }

    @Test
    public void testDoubleConstant() {
        Expression constant = new LiteralExpression(new Literal.Double(1.5));
        TestWriter.testGroupDefinition(
                "group double\n\t1.5",
                GroupDefinition.Builder.global(DataType.DOUBLE).constant(constant).build()
        );
    }

    @Test
    public void testStringConstant() {
        Expression constant = new LiteralExpression(new Literal.String(""));
        TestWriter.testGroupDefinition(
                "group String\n\t\"\"",
                GroupDefinition.Builder.global(DataType.STRING).constant(constant).build()
        );
    }
}
