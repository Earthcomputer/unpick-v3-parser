package net.earthcomputer.unpickv3parser.writer;

import org.junit.jupiter.api.Test;

import net.earthcomputer.unpickv3parser.tree.DataType;
import net.earthcomputer.unpickv3parser.tree.Literal;
import net.earthcomputer.unpickv3parser.tree.expr.BinaryExpression;
import net.earthcomputer.unpickv3parser.tree.expr.CastExpression;
import net.earthcomputer.unpickv3parser.tree.expr.FieldExpression;
import net.earthcomputer.unpickv3parser.tree.expr.LiteralExpression;
import net.earthcomputer.unpickv3parser.tree.expr.ParenExpression;
import net.earthcomputer.unpickv3parser.tree.expr.UnaryExpression;

public final class TestExpression {
    @Test
    public void testIntegerLiteral() {
        TestWriter.testExpression("0", new LiteralExpression(new Literal.Integer(0)));
    }

    @Test
    public void testLongLiteral() {
        TestWriter.testExpression("0L", new LiteralExpression(new Literal.Long(0L)));
    }

    @Test
    public void testFloatLiteral() {
        TestWriter.testExpression("0.0F", new LiteralExpression(new Literal.Float(0f)));
    }

    @Test
    public void testDoubleLiteral() {
        TestWriter.testExpression("0.0", new LiteralExpression(new Literal.Double(0d)));
    }

    @Test
    public void testSimpleCharLiteral() {
        TestWriter.testExpression("'a'", new LiteralExpression(new Literal.Character('a')));
    }

    @Test
    public void testQuoteCharLiteral() {
        TestWriter.testExpression("'\"'", new LiteralExpression(new Literal.Character('"')));
    }

    @Test
    public void testApostropheCharLiteral() {
        TestWriter.testExpression("'\\''", new LiteralExpression(new Literal.Character('\'')));
    }

    @Test
    public void testBackspaceCharLiteral() {
        TestWriter.testExpression("'\\b'", new LiteralExpression(new Literal.Character('\b')));
    }

    @Test
    public void testTabCharLiteral() {
        TestWriter.testExpression("'\\t'", new LiteralExpression(new Literal.Character('\t')));
    }

    @Test
    public void testNewlineCharLiteral() {
        TestWriter.testExpression("'\\n'", new LiteralExpression(new Literal.Character('\n')));
    }

    @Test
    public void testFormFeedCharLiteral() {
        TestWriter.testExpression("'\\f'", new LiteralExpression(new Literal.Character('\f')));
    }

    @Test
    public void testCarriageReturnCharLiteral() {
        TestWriter.testExpression("'\\r'", new LiteralExpression(new Literal.Character('\r')));
    }

    @Test
    public void testBackslashCharLiteral() {
        TestWriter.testExpression("'\\\\'", new LiteralExpression(new Literal.Character('\\')));
    }

    @Test
    public void testNullCharLiteral() {
        TestWriter.testExpression("'\\0'", new LiteralExpression(new Literal.Character('\0')));
    }

    @Test
    public void testVerticalTabCharLiteral() {
        TestWriter.testExpression("'\\13'", new LiteralExpression(new Literal.Character('\13')));
    }

    @Test
    public void testZeroWidthSpaceLiteral() {
        TestWriter.testExpression("'\\u200b'", new LiteralExpression(new Literal.Character('\u200b')));
    }

    @Test
    public void testAmogusCharLiteral() {
        TestWriter.testExpression("'ඞ'", new LiteralExpression(new Literal.Character('ඞ')));
    }

    @Test
    public void testEmptyStringLiteral() {
        TestWriter.testExpression("\"\"", new LiteralExpression(new Literal.String("")));
    }

    @Test
    public void testSimpleStringLiteral() {
        TestWriter.testExpression("\"Hello, World!\"", new LiteralExpression(new Literal.String("Hello, World!")));
    }

    @Test
    public void testEscapedStringLiteral() {
        TestWriter.testExpression("\"'\\b\\t\\n\\f\\r\\\\\\\"\\0\\13\\u200bඞ\"", new LiteralExpression(new Literal.String("'\b\t\n\f\r\\\"\0\13\u200bඞ")));
    }

    @Test
    public void testBitOrExpression() {
        TestWriter.testExpression(
                "1 | 2",
                new BinaryExpression(
                        new LiteralExpression(new Literal.Integer(1)),
                        new LiteralExpression(new Literal.Integer(2)),
                        BinaryExpression.Operator.BIT_OR
                )
        );
    }

    @Test
    public void testBitXorExpression() {
        TestWriter.testExpression(
                "1 ^ 2",
                new BinaryExpression(
                        new LiteralExpression(new Literal.Integer(1)),
                        new LiteralExpression(new Literal.Integer(2)),
                        BinaryExpression.Operator.BIT_XOR
                )
        );
    }

    @Test
    public void testBitAndExpression() {
        TestWriter.testExpression(
                "1 & 2",
                new BinaryExpression(
                        new LiteralExpression(new Literal.Integer(1)),
                        new LiteralExpression(new Literal.Integer(2)),
                        BinaryExpression.Operator.BIT_AND
                )
        );
    }

    @Test
    public void testBitShiftLeftExpression() {
        TestWriter.testExpression(
                "1 << 2",
                new BinaryExpression(
                        new LiteralExpression(new Literal.Integer(1)),
                        new LiteralExpression(new Literal.Integer(2)),
                        BinaryExpression.Operator.BIT_SHIFT_LEFT
                )
        );
    }

    @Test
    public void testBitShiftRightExpression() {
        TestWriter.testExpression(
                "1 >> 2",
                new BinaryExpression(
                        new LiteralExpression(new Literal.Integer(1)),
                        new LiteralExpression(new Literal.Integer(2)),
                        BinaryExpression.Operator.BIT_SHIFT_RIGHT
                )
        );
    }

    @Test
    public void testBitShiftRightUnsignedExpression() {
        TestWriter.testExpression(
                "1 >>> 2",
                new BinaryExpression(
                        new LiteralExpression(new Literal.Integer(1)),
                        new LiteralExpression(new Literal.Integer(2)),
                        BinaryExpression.Operator.BIT_SHIFT_RIGHT_UNSIGNED
                )
        );
    }

    @Test
    public void testAddExpression() {
        TestWriter.testExpression(
                "1 + 2",
                new BinaryExpression(
                        new LiteralExpression(new Literal.Integer(1)),
                        new LiteralExpression(new Literal.Integer(2)),
                        BinaryExpression.Operator.ADD
                )
        );
    }

    @Test
    public void testSubtractExpression() {
        TestWriter.testExpression(
                "1 - 2",
                new BinaryExpression(
                        new LiteralExpression(new Literal.Integer(1)),
                        new LiteralExpression(new Literal.Integer(2)),
                        BinaryExpression.Operator.SUBTRACT
                )
        );
    }

    @Test
    public void testMultiplyExpression() {
        TestWriter.testExpression(
                "1 * 2",
                new BinaryExpression(
                        new LiteralExpression(new Literal.Integer(1)),
                        new LiteralExpression(new Literal.Integer(2)),
                        BinaryExpression.Operator.MULTIPLY
                )
        );
    }

    @Test
    public void testDivideExpression() {
        TestWriter.testExpression(
                "1 / 2",
                new BinaryExpression(
                        new LiteralExpression(new Literal.Integer(1)),
                        new LiteralExpression(new Literal.Integer(2)),
                        BinaryExpression.Operator.DIVIDE
                )
        );
    }

    @Test
    public void testModuloExpression() {
        TestWriter.testExpression(
                "1 % 2",
                new BinaryExpression(
                        new LiteralExpression(new Literal.Integer(1)),
                        new LiteralExpression(new Literal.Integer(2)),
                        BinaryExpression.Operator.MODULO
                )
        );
    }

    @Test
    public void testNegateExpression() {
        TestWriter.testExpression(
                "-42",
                new UnaryExpression(
                        new LiteralExpression(new Literal.Integer(42)),
                        UnaryExpression.Operator.NEGATE
                )
        );
    }

    @Test
    public void testBitNotExpression() {
        TestWriter.testExpression(
                "~42",
                new UnaryExpression(
                        new LiteralExpression(new Literal.Integer(42)),
                        UnaryExpression.Operator.BIT_NOT
                )
        );
    }

    @Test
    public void testCastExpression() {
        TestWriter.testExpression(
                "(byte) 42",
                new CastExpression(
                        DataType.BYTE,
                        new LiteralExpression(new Literal.Integer(42))
                )
        );
    }

    @Test
    public void testParenExpression() {
        TestWriter.testExpression(
                "(42)",
                new ParenExpression(new LiteralExpression(new Literal.Integer(42)))
        );
    }

    @Test
    public void testFieldExpression() {
        TestWriter.testExpression(
                "foo.Bar.baz",
                new FieldExpression("foo.Bar", "baz", null, true)
        );
    }

    @Test
    public void testInstanceFieldExpression() {
        TestWriter.testExpression(
                "foo.Bar.baz:instance",
                new FieldExpression("foo.Bar", "baz", null, false)
        );
    }

    @Test
    public void testTypedFieldExpression() {
        TestWriter.testExpression(
                "foo.Bar.baz:byte",
                new FieldExpression("foo.Bar", "baz", DataType.BYTE, true)
        );
    }

    @Test
    public void testTypedInstanceFieldExpression() {
        TestWriter.testExpression(
                "foo.Bar.baz:instance:byte",
                new FieldExpression("foo.Bar", "baz", DataType.BYTE, false)
        );
    }

    @Test
    public void testComplexExpression() {
        TestWriter.testExpression(
                "(1 + 2) * 3 / foo.Bar.baz",
                new BinaryExpression(
                        new BinaryExpression(
                                new ParenExpression(
                                        new BinaryExpression(
                                                new LiteralExpression(new Literal.Integer(1)),
                                                new LiteralExpression(new Literal.Integer(2)),
                                                BinaryExpression.Operator.ADD
                                        )
                                ),
                                new LiteralExpression(new Literal.Integer(3)),
                                BinaryExpression.Operator.MULTIPLY
                        ),
                        new FieldExpression("foo.Bar", "baz", null, true),
                        BinaryExpression.Operator.DIVIDE
                )
        );
    }
}
