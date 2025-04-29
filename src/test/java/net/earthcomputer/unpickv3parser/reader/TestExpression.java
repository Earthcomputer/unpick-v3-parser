package net.earthcomputer.unpickv3parser.reader;

import net.earthcomputer.unpickv3parser.tree.DataType;
import net.earthcomputer.unpickv3parser.tree.GroupDefinition;
import net.earthcomputer.unpickv3parser.tree.Literal;
import net.earthcomputer.unpickv3parser.tree.UnpickV3Visitor;
import net.earthcomputer.unpickv3parser.tree.expr.BinaryExpression;
import net.earthcomputer.unpickv3parser.tree.expr.CastExpression;
import net.earthcomputer.unpickv3parser.tree.expr.Expression;
import net.earthcomputer.unpickv3parser.tree.expr.FieldExpression;
import net.earthcomputer.unpickv3parser.tree.expr.LiteralExpression;
import net.earthcomputer.unpickv3parser.tree.expr.ParenExpression;
import net.earthcomputer.unpickv3parser.tree.expr.UnaryExpression;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public final class TestExpression {
    @Test
    public void testExpression() throws IOException {
        List<Expression> expressions = new ArrayList<>();
        TestReader.test("expression/expression", new UnpickV3Visitor() {
            @Override
            public void visitGroupDefinition(GroupDefinition groupDefinition) {
                expressions.addAll(groupDefinition.constants);
            }
        });
        assertEquals(20, expressions.size());
        checkExpr0(expressions.get(0));
        checkExpr1(expressions.get(1));
        checkExpr2(expressions.get(2));
        checkExpr3(expressions.get(3));
        checkExpr4(expressions.get(4));
        checkExpr5(expressions.get(5));
        checkExpr6(expressions.get(6));
        checkExpr7(expressions.get(7));
        checkExpr8(expressions.get(8));
        checkExpr9(expressions.get(9));
        checkExpr10(expressions.get(10));
        checkExpr11(expressions.get(11));
        checkExpr12(expressions.get(12));
        checkExpr13(expressions.get(13));
        checkExpr14(expressions.get(14));
        checkExpr15(expressions.get(15));
        checkExpr16(expressions.get(16));
        checkExpr17(expressions.get(17));
        checkExpr18(expressions.get(18));
        checkExpr19(expressions.get(19));
    }

    private static void checkExpr0(Expression expr) {
        BinaryExpression add = assertBinary(BinaryExpression.Operator.ADD, expr);
        BinaryExpression subtract = assertBinary(BinaryExpression.Operator.SUBTRACT, add.lhs);
        assertLiteral(1, subtract.lhs);
        assertLiteral(2, subtract.rhs);
        assertLiteral(3, add.rhs);
    }

    private static void checkExpr1(Expression expr) {
        BinaryExpression bitOr = assertBinary(BinaryExpression.Operator.BIT_OR, expr);
        assertLiteral(0, bitOr.lhs);
        BinaryExpression bitXor = assertBinary(BinaryExpression.Operator.BIT_XOR, bitOr.rhs);
        BinaryExpression bitShiftRight = assertBinary(BinaryExpression.Operator.BIT_SHIFT_RIGHT, bitXor.lhs);
        BinaryExpression bitShiftRightUnsigned = assertBinary(BinaryExpression.Operator.BIT_SHIFT_RIGHT_UNSIGNED, bitShiftRight.lhs);
        BinaryExpression bitShiftLeft = assertBinary(BinaryExpression.Operator.BIT_SHIFT_LEFT, bitShiftRightUnsigned.lhs);
        assertLiteral(1, bitShiftLeft.lhs);
        BinaryExpression add = assertBinary(BinaryExpression.Operator.ADD, bitShiftLeft.rhs);
        BinaryExpression modulo = assertBinary(BinaryExpression.Operator.MODULO, add.lhs);
        BinaryExpression divide = assertBinary(BinaryExpression.Operator.DIVIDE, modulo.lhs);
        assertLiteral(2, divide.lhs);
        assertLiteral(3, divide.rhs);
        assertLiteral(4, modulo.rhs);
        assertLiteral(5, add.rhs);
        assertLiteral(6, bitShiftRightUnsigned.rhs);
        assertLiteral(7, bitShiftRight.rhs);
        BinaryExpression bitAnd = assertBinary(BinaryExpression.Operator.BIT_AND, bitXor.rhs);
        BinaryExpression multiply = assertBinary(BinaryExpression.Operator.MULTIPLY, bitAnd.lhs);
        assertLiteral(8, multiply.lhs);
        assertLiteral(9, multiply.rhs);
        BinaryExpression subtract = assertBinary(BinaryExpression.Operator.SUBTRACT, bitAnd.rhs);
        assertLiteral(10, subtract.lhs);
        assertLiteral(11, subtract.rhs);
    }

    private static void checkExpr2(Expression expr) {
        UnaryExpression negate = assertUnary(UnaryExpression.Operator.NEGATE, expr);
        assertLiteral(0, negate.operand);
    }

    private static void checkExpr3(Expression expr) {
        UnaryExpression bitNot = assertUnary(UnaryExpression.Operator.BIT_NOT, expr);
        assertLiteral(0, bitNot.operand);
    }

    private static void checkExpr4(Expression expr) {
        BinaryExpression add = assertBinary(BinaryExpression.Operator.ADD, expr);
        assertLiteral(1, add.lhs);
        UnaryExpression negate = assertUnary(UnaryExpression.Operator.NEGATE, add.rhs);
        assertLiteral(2, negate.operand);
    }

    private static void checkExpr5(Expression expr) {
        CastExpression cast = assertCast(DataType.INT, expr);
        assertLiteral(1, cast.operand);
    }

    private static void checkExpr6(Expression expr) {
        ParenExpression paren = assertInstanceOf(ParenExpression.class, expr);
        assertLiteral(1, paren.expression);
    }

    private static void checkExpr7(Expression expr) {
        ParenExpression paren = assertInstanceOf(ParenExpression.class, expr);
        BinaryExpression add = assertBinary(BinaryExpression.Operator.ADD, paren.expression);
        assertLiteral(1, add.lhs);
        assertLiteral(2, add.rhs);
    }

    private static void checkExpr8(Expression expr) {
        BinaryExpression divide = assertBinary(BinaryExpression.Operator.DIVIDE, expr);
        BinaryExpression multiply = assertBinary(BinaryExpression.Operator.MULTIPLY, divide.lhs);
        assertLiteral(1, multiply.lhs);
        ParenExpression paren = assertInstanceOf(ParenExpression.class, multiply.rhs);
        BinaryExpression add = assertBinary(BinaryExpression.Operator.ADD, paren.expression);
        assertLiteral(2, add.lhs);
        assertLiteral(3, add.rhs);
        assertLiteral(4, divide.rhs);
    }

    private static void checkExpr9(Expression expr) {
        BinaryExpression add = assertBinary(BinaryExpression.Operator.ADD, expr);
        CastExpression cast = assertCast(DataType.INT, add.lhs);
        assertLiteral(1, cast.operand);
        assertLiteral(2, add.rhs);
    }

    private static void checkExpr10(Expression expr) {
        FieldExpression fieldExpr = assertInstanceOf(FieldExpression.class, expr);
        assertEquals("foo.Bar", fieldExpr.className);
        assertEquals("baz", fieldExpr.fieldName);
        assertNull(fieldExpr.fieldType);
        assertTrue(fieldExpr.isStatic);
    }

    private static void checkExpr11(Expression expr) {
        FieldExpression fieldExpr = assertInstanceOf(FieldExpression.class, expr);
        assertEquals("foo.Bar", fieldExpr.className);
        assertEquals("baz", fieldExpr.fieldName);
        assertNull(fieldExpr.fieldType);
        assertFalse(fieldExpr.isStatic);
    }

    private static void checkExpr12(Expression expr) {
        FieldExpression fieldExpr = assertInstanceOf(FieldExpression.class, expr);
        assertEquals("foo.Bar", fieldExpr.className);
        assertEquals("baz", fieldExpr.fieldName);
        assertEquals(DataType.BYTE, fieldExpr.fieldType);
        assertTrue(fieldExpr.isStatic);
    }

    private static void checkExpr13(Expression expr) {
        FieldExpression fieldExpr = assertInstanceOf(FieldExpression.class, expr);
        assertEquals("foo.Bar", fieldExpr.className);
        assertEquals("baz", fieldExpr.fieldName);
        assertEquals(DataType.BYTE, fieldExpr.fieldType);
        assertFalse(fieldExpr.isStatic);
    }

    private static void checkExpr14(Expression expr) {
        ParenExpression paren = assertInstanceOf(ParenExpression.class, expr);
        assertInstanceOf(FieldExpression.class, paren.expression);
    }

    private static void checkExpr15(Expression expr) {
        FieldExpression fieldExpr = assertInstanceOf(FieldExpression.class, expr);
        assertEquals("Foo", fieldExpr.className);
        assertEquals("bar", fieldExpr.fieldName);
    }

    private static void checkExpr16(Expression expr) {
        FieldExpression fieldExpr = assertInstanceOf(FieldExpression.class, expr);
        assertEquals("foo.Bar", fieldExpr.className);
        assertNull(fieldExpr.fieldName);
        assertNull(fieldExpr.fieldType);
        assertTrue(fieldExpr.isStatic);
    }

    private static void checkExpr17(Expression expr) {
        FieldExpression fieldExpr = assertInstanceOf(FieldExpression.class, expr);
        assertEquals("foo.Bar", fieldExpr.className);
        assertNull(fieldExpr.fieldName);
        assertNull(fieldExpr.fieldType);
        assertFalse(fieldExpr.isStatic);
    }

    private static void checkExpr18(Expression expr) {
        FieldExpression fieldExpr = assertInstanceOf(FieldExpression.class, expr);
        assertEquals("foo.Bar", fieldExpr.className);
        assertNull(fieldExpr.fieldName);
        assertEquals(DataType.BYTE, fieldExpr.fieldType);
        assertTrue(fieldExpr.isStatic);
    }

    private static void checkExpr19(Expression expr) {
        FieldExpression fieldExpr = assertInstanceOf(FieldExpression.class, expr);
        assertEquals("foo.Bar", fieldExpr.className);
        assertNull(fieldExpr.fieldName);
        assertEquals(DataType.BYTE, fieldExpr.fieldType);
        assertFalse(fieldExpr.isStatic);
    }

    private static void assertLiteral(int value, Expression expr) {
        LiteralExpression literalExpr = assertInstanceOf(LiteralExpression.class, expr);
        Literal.Integer literal = assertInstanceOf(Literal.Integer.class, literalExpr.literal);
        assertEquals(value, literal.value);
    }

    private static UnaryExpression assertUnary(UnaryExpression.Operator operator, Expression expr) {
        UnaryExpression unary = assertInstanceOf(UnaryExpression.class, expr);
        assertEquals(operator, unary.operator);
        return unary;
    }

    private static BinaryExpression assertBinary(BinaryExpression.Operator operator, Expression expr) {
        BinaryExpression binary = assertInstanceOf(BinaryExpression.class, expr);
        assertEquals(operator, binary.operator);
        return binary;
    }

    private static CastExpression assertCast(DataType castType, Expression expr) {
        CastExpression cast = assertInstanceOf(CastExpression.class, expr);
        assertEquals(castType, cast.castType);
        return cast;
    }

    @Test
    public void testIdentifier() throws IOException {
        TestReader.assertThrowsParseError("expression/invalid/identifier", 3, 8, "Expected '.' before '\\n' token");
    }

    @Test
    public void testIdentifierParenthesized() throws IOException {
        TestReader.assertThrowsParseError("expression/invalid/identifier_parenthesized", 3, 6, "Expected data type before 'foo' token");
    }

    @Test
    public void testBinaryExpressionUnterminated() throws IOException {
        TestReader.assertThrowsParseError("expression/invalid/binary_expression_unterminated", 3, 8, "Expected expression before '\\n' token");
    }

    @Test
    public void testUnaryExpressionIncomplete() throws IOException {
        TestReader.assertThrowsParseError("expression/invalid/unary_expression_incomplete", 3, 6, "Expected expression before '\\n' token");
    }

    @Test
    public void testUnclosedParentheses() throws IOException {
        TestReader.assertThrowsParseError("expression/invalid/unclosed_parentheses", 3, 7, "Expected ')' before '\\n' token");
    }

    @Test
    public void testNonExistentOperator() throws IOException {
        TestReader.assertThrowsParseError("expression/invalid/nonexistent_operator", 3, 7, "Expected '\\n' before '@' token");
    }
}
