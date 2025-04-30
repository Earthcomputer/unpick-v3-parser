package net.earthcomputer.unpickv3parser.writer;

import java.util.Collections;

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
                new GroupDefinition(Collections.emptyList(), false, false, DataType.INT, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testPackageScope() {
        TestWriter.testGroupDefinition(
                "group int\n\t@scope package foo.bar",
                new GroupDefinition(Collections.singletonList(new GroupScope.Package("foo.bar")), false, false, DataType.INT, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testClassScope() {
        TestWriter.testGroupDefinition(
                "group int\n\t@scope class foo.Bar",
                new GroupDefinition(Collections.singletonList(new GroupScope.Class("foo.Bar")), false, false, DataType.INT, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testMethodScope() {
        TestWriter.testGroupDefinition(
                "group int\n\t@scope method foo.Bar baz ()V",
                new GroupDefinition(Collections.singletonList(new GroupScope.Method("foo.Bar", "baz", "()V")), false, false, DataType.INT, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testStrict() {
        TestWriter.testGroupDefinition(
                "group int\n\t@strict",
                new GroupDefinition(Collections.emptyList(), false, true, DataType.INT, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testStrictWithScope() {
        TestWriter.testGroupDefinition(
                "group int\n\t@scope package foo.bar\n\t@strict",
                new GroupDefinition(Collections.singletonList(new GroupScope.Package("foo.bar")), false, true, DataType.INT, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testFlag() {
        TestWriter.testGroupDefinition(
                "group int\n\t@flags",
                new GroupDefinition(Collections.emptyList(), true, false, DataType.INT, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testLong() {
        TestWriter.testGroupDefinition(
                "group long",
                new GroupDefinition(Collections.emptyList(), false, false, DataType.LONG, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testFloat() {
        TestWriter.testGroupDefinition(
                "group float",
                new GroupDefinition(Collections.emptyList(), false, false, DataType.FLOAT, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testDouble() {
        TestWriter.testGroupDefinition(
                "group double",
                new GroupDefinition(Collections.emptyList(), false, false, DataType.DOUBLE, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testString() {
        TestWriter.testGroupDefinition(
                "group String",
                new GroupDefinition(Collections.emptyList(), false, false, DataType.STRING, null, Collections.emptyList(), null)
        );
    }

    @Test
    public void testNamed() {
        TestWriter.testGroupDefinition(
                "group int g",
                new GroupDefinition(Collections.emptyList(), false, false, DataType.INT, "g", Collections.emptyList(), null)
        );
    }

    @Test
    public void testFormatDecimal() {
        TestWriter.testGroupDefinition(
                "group int\n\t@format decimal",
                new GroupDefinition(Collections.emptyList(), false, false, DataType.INT, null, Collections.emptyList(), GroupFormat.DECIMAL)
        );
    }

    @Test
    public void testFormatHex() {
        TestWriter.testGroupDefinition(
                "group int\n\t@format hex",
                new GroupDefinition(Collections.emptyList(), false, false, DataType.INT, null, Collections.emptyList(), GroupFormat.HEX)
        );
    }

    @Test
    public void testFormatBinary() {
        TestWriter.testGroupDefinition(
                "group int\n\t@format binary",
                new GroupDefinition(Collections.emptyList(), false, false, DataType.INT, null, Collections.emptyList(), GroupFormat.BINARY)
        );
    }

    @Test
    public void testFormatOctal() {
        TestWriter.testGroupDefinition(
                "group int\n\t@format octal",
                new GroupDefinition(Collections.emptyList(), false, false, DataType.INT, null, Collections.emptyList(), GroupFormat.OCTAL)
        );
    }

    @Test
    public void testFormatChar() {
        TestWriter.testGroupDefinition(
                "group int\n\t@format char",
                new GroupDefinition(Collections.emptyList(), false, false, DataType.INT, null, Collections.emptyList(), GroupFormat.CHAR)
        );
    }

    @Test
    public void testSimpleConstant() {
        Expression constant = new LiteralExpression(new Literal.Integer(0));
        TestWriter.testGroupDefinition(
                "group int\n\t0",
                new GroupDefinition(Collections.emptyList(), false, false, DataType.INT, null, Collections.singletonList(constant), null)
        );
    }

    @Test
    public void testConstantWithFormat() {
        Expression constant = new LiteralExpression(new Literal.Integer(0));
        TestWriter.testGroupDefinition(
                "group int\n\t@format hex\n\t0",
                new GroupDefinition(Collections.emptyList(), false, false, DataType.INT, null, Collections.singletonList(constant), GroupFormat.HEX)
        );
    }

    @Test
    public void testLongConstant() {
        Expression constant = new LiteralExpression(new Literal.Long(Long.MAX_VALUE));
        TestWriter.testGroupDefinition(
                "group long\n\t9223372036854775807L",
                new GroupDefinition(Collections.emptyList(), false, false, DataType.LONG, null, Collections.singletonList(constant), null)
        );
    }

    @Test
    public void testNegativeLongConstant() {
        Expression constant = new UnaryExpression(new LiteralExpression(new Literal.Long(Long.MIN_VALUE)), UnaryExpression.Operator.NEGATE);
        TestWriter.testGroupDefinition(
                "group long\n\t-9223372036854775808L",
                new GroupDefinition(Collections.emptyList(), false, false, DataType.LONG, null, Collections.singletonList(constant), null)
        );
    }

    @Test
    public void testHexLongConstant() {
        Expression constant = new LiteralExpression(new Literal.Long(-1, 16));
        TestWriter.testGroupDefinition(
                "group long\n\t0xffffffffffffffffL",
                new GroupDefinition(Collections.emptyList(), false, false, DataType.LONG, null, Collections.singletonList(constant), null)
        );
    }

    @Test
    public void testDecimalConstant() {
        Expression constant = new LiteralExpression(new Literal.Integer(10, 2));
        TestWriter.testGroupDefinition(
                "group int\n\t0b1010",
                new GroupDefinition(Collections.emptyList(), false, false, DataType.INT, null, Collections.singletonList(constant), null)
        );
    }

    @Test
    public void testOctalConstant() {
        Expression constant = new LiteralExpression(new Literal.Integer(511, 8));
        TestWriter.testGroupDefinition(
                "group int\n\t0777",
                new GroupDefinition(Collections.emptyList(), false, false, DataType.INT, null, Collections.singletonList(constant), null)
        );
    }

    @Test
    public void testDoubleConstant() {
        Expression constant = new LiteralExpression(new Literal.Double(1.5));
        TestWriter.testGroupDefinition(
                "group double\n\t1.5",
                new GroupDefinition(Collections.emptyList(), false, false, DataType.DOUBLE, null, Collections.singletonList(constant), null)
        );
    }

    @Test
    public void testStringConstant() {
        Expression constant = new LiteralExpression(new Literal.String(""));
        TestWriter.testGroupDefinition(
                "group String\n\t\"\"",
                new GroupDefinition(Collections.emptyList(), false, false, DataType.STRING, null, Collections.singletonList(constant), null)
        );
    }
}
