package net.earthcomputer.unpickv3parser.reader;

import net.earthcomputer.unpickv3parser.tree.GroupConstant;
import net.earthcomputer.unpickv3parser.tree.GroupDefinition;
import net.earthcomputer.unpickv3parser.tree.Literal;
import net.earthcomputer.unpickv3parser.tree.TargetField;
import net.earthcomputer.unpickv3parser.tree.TargetMethod;
import net.earthcomputer.unpickv3parser.tree.UnpickV3Visitor;
import net.earthcomputer.unpickv3parser.tree.expr.ExpressionVisitor;
import net.earthcomputer.unpickv3parser.tree.expr.LiteralExpression;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public final class TestTokens {
    @Test
    public void testIdentifier() throws IOException {
        List<String> groupNames = new ArrayList<>();
        TestReader.test("tokens/identifier", new UnpickV3Visitor() {
            @Override
            public void visitGroupDefinition(GroupDefinition groupDefinition) {
                assertNotNull(groupDefinition.name);
                groupNames.add(groupDefinition.name);
            }
        });
        assertEquals(Arrays.asList("a", "A", "_", "$", "az0_$G"), groupNames);
    }

    @Test
    public void testDouble() throws IOException {
        List<Double> constantKeys = new ArrayList<>();
        List<Double> exprDoubles = new ArrayList<>();
        TestReader.test("tokens/double", new UnpickV3Visitor() {
            @Override
            public void visitGroupDefinition(GroupDefinition groupDefinition) {
                for (GroupConstant constant : groupDefinition.constants) {
                    constantKeys.add(((Literal.Double) constant.key).value);
                    constant.value.accept(new ExpressionVisitor() {
                        @Override
                        public void visitLiteralExpression(LiteralExpression literalExpression) {
                            exprDoubles.add(((Literal.Double) literalExpression.literal).value);
                        }
                    });
                }
            }
        });
        assertEquals(Arrays.asList(0.0, -2.0, 4.5, 1.0e5, 1.0e5), constantKeys);
        assertEquals(Arrays.asList(1.0, 3.0, 1.0e5, 1.0e-5, 1.0e10), exprDoubles);
    }

    @Test
    public void testFloat() throws IOException {
        List<Float> exprFloats = new ArrayList<>();
        TestReader.test("tokens/float", new UnpickV3Visitor() {
            @Override
            public void visitGroupDefinition(GroupDefinition groupDefinition) {
                for (GroupConstant constant : groupDefinition.constants) {
                    constant.value.accept(new ExpressionVisitor() {
                        @Override
                        public void visitLiteralExpression(LiteralExpression literalExpression) {
                            exprFloats.add(((Literal.Float) literalExpression.literal).value);
                        }
                    });
                }
            }
        });
        assertEquals(Arrays.asList(1.0f, 3.0f, 1.0e5f, 1.0e-5f, 1.0e10f), exprFloats);
    }

    @Test
    public void testInteger() throws IOException {
        List<Integer> exprInts = new ArrayList<>();
        List<Integer> exprRadixes = new ArrayList<>();
        TestReader.test("tokens/integer", new UnpickV3Visitor() {
            @Override
            public void visitGroupDefinition(GroupDefinition groupDefinition) {
                for (GroupConstant constant : groupDefinition.constants) {
                    constant.value.accept(new ExpressionVisitor() {
                        @Override
                        public void visitLiteralExpression(LiteralExpression literalExpression) {
                            Literal.Integer literal = (Literal.Integer) literalExpression.literal;
                            exprInts.add(literal.value);
                            exprRadixes.add(literal.radix);
                        }
                    });
                }
            }
        });
        assertEquals(Arrays.asList(0, 1, 2, Integer.MAX_VALUE, Integer.MIN_VALUE, 0x9ff, 0xffffffff, 0b1010, 0xffffffff, 511, 0xffffffff), exprInts);
        assertEquals(Arrays.asList(10, 10, 10, 10, 10, 16, 16, 2, 2, 8, 8), exprRadixes);
    }

    @Test
    public void testLong() throws IOException {
        List<Long> constantKeys = new ArrayList<>();
        List<Integer> constantRadixes = new ArrayList<>();
        List<Long> exprLongs = new ArrayList<>();
        List<Integer> exprRadixes = new ArrayList<>();
        TestReader.test("tokens/long", new UnpickV3Visitor() {
            @Override
            public void visitGroupDefinition(GroupDefinition groupDefinition) {
                for (GroupConstant constant : groupDefinition.constants) {
                    Literal.Long constantLiteral = (Literal.Long) constant.key;
                    constantKeys.add(constantLiteral.value);
                    constantRadixes.add(constantLiteral.radix);
                    constant.value.accept(new ExpressionVisitor() {
                        @Override
                        public void visitLiteralExpression(LiteralExpression literalExpression) {
                            Literal.Long literal = (Literal.Long) literalExpression.literal;
                            exprLongs.add(literal.value);
                            exprRadixes.add(literal.radix);
                        }
                    });
                }
            }
        });
        List<Integer> expectedRadixes = Arrays.asList(10, 10, 10, 10, 10, 16, 16, 2, 2, 8, 8);
        assertEquals(Arrays.asList(0L, 1L, -2L, Long.MAX_VALUE, Long.MIN_VALUE, 0x9ffL, 0xffffffffffffffffL, 0b1010L, 0xffffffffffffffffL, 511L, 0xffffffffffffffffL), constantKeys);
        assertEquals(expectedRadixes, constantRadixes);
        assertEquals(Arrays.asList(0L, 1L, 2L, Long.MAX_VALUE, Long.MIN_VALUE, 0x9ffL, 0xffffffffffffffffL, 0b1010L, 0xffffffffffffffffL, 511L, 0xffffffffffffffffL), exprLongs);
        assertEquals(expectedRadixes, exprRadixes);
    }

    @Test
    public void testChar() throws IOException {
        List<Long> constantKeys = new ArrayList<>();
        List<Character> exprChars = new ArrayList<>();
        TestReader.test("tokens/char", new UnpickV3Visitor() {
            @Override
            public void visitGroupDefinition(GroupDefinition groupDefinition) {
                for (GroupConstant constant : groupDefinition.constants) {
                    constantKeys.add(((Literal.Long) constant.key).value);
                    constant.value.accept(new ExpressionVisitor() {
                        @Override
                        public void visitLiteralExpression(LiteralExpression literalExpression) {
                            exprChars.add(((Literal.Character) literalExpression.literal).value);
                        }
                    });
                }
            }
        });
        List<Character> expectedChars = Arrays.asList('a', '"', '§', '\b', '\t', '\n', '\f', '\r', '\'', '"', '\\', '\0', '\12', '\123', '\74');
        assertEquals(expectedChars.stream().map(c -> (long) c).collect(Collectors.toList()), constantKeys);
        assertEquals(expectedChars, exprChars);
    }

    @Test
    public void testString() throws IOException {
        List<String> constantKeys = new ArrayList<>();
        List<String> exprStrings = new ArrayList<>();
        TestReader.test("tokens/string", new UnpickV3Visitor() {
            @Override
            public void visitGroupDefinition(GroupDefinition groupDefinition) {
                for (GroupConstant constant : groupDefinition.constants) {
                    constantKeys.add(((Literal.String) constant.key).value);
                    constant.value.accept(new ExpressionVisitor() {
                        @Override
                        public void visitLiteralExpression(LiteralExpression literalExpression) {
                            exprStrings.add(((Literal.String) literalExpression.literal).value);
                        }
                    });
                }
            }
        });
        assertEquals(Collections.singletonList("Hello '§\b\t\n\f\r'\"\\\0\12\123\1234\74\747\08"), constantKeys);
        assertEquals(Collections.singletonList(""), exprStrings);
    }

    @Test
    public void testFieldDescriptor() throws IOException {
        List<String> fieldDescriptors = new ArrayList<>();
        TestReader.test("tokens/field_descriptor", new UnpickV3Visitor() {
            @Override
            public void visitTargetField(TargetField targetField) {
                fieldDescriptors.add(targetField.fieldDesc);
            }
        });
        assertEquals(Arrays.asList("B", "C", "D", "F", "I", "J", "S", "Z", "Ljava/lang/String;", "[I", "[[Ljava/lang/String;"), fieldDescriptors);
    }

    @Test
    public void testMethodDescriptor() throws IOException {
        List<String> methodDescriptors = new ArrayList<>();
        TestReader.test("tokens/method_descriptor", new UnpickV3Visitor() {
            @Override
            public void visitTargetMethod(TargetMethod targetMethod) {
                methodDescriptors.add(targetMethod.methodDesc);
            }
        });
        assertEquals(Arrays.asList("()V", "([[[Ljava/lang/Object;IIIDDZZLjava/lang/Object;[[[[[I)Ljava/lang/Thread;"), methodDescriptors);
    }

    @Test
    public void testIdentifierStartsWithNumber() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/identifier_starts_with_number", 2, 12);
    }

    @Test
    public void testDoubleMissingWholePart() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/double_missing_whole_part", 3, 5);
    }

    @Test
    public void testDoubleMissingFracPart() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/double_missing_frac_part", 3, 7);
    }

    @Test
    public void testDoubleMissingExpValue() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/double_missing_exp_value", 3, 9);
    }

    @Test
    public void testDoubleMissingExpValueAfterSign() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/double_missing_exp_value_after_sign", 3, 10);
    }

    @Test
    public void testIntegerOutOfBoundsPositiveDecimal() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/integer_out_of_bounds_positive_decimal", 3, 9);
    }

    @Test
    public void testIntegerOutOfBoundsNegativeDecimal() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/integer_out_of_bounds_negative_decimal", 3, 10);
    }

    @Test
    public void testIntegerOutOfBoundsPositiveHex() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/integer_out_of_bounds_positive_hex", 3, 9);
    }

    @Test
    public void testIntegerOutOfBoundsNegativeHex() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/integer_out_of_bounds_negative_hex", 3, 10);
    }

    @Test
    public void testIntegerMissingValueHex() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/integer_missing_value_hex", 3, 11);
    }

    @Test
    public void testIntegerOutOfBoundsPositiveBinary() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/integer_out_of_bounds_positive_binary", 3, 9);
    }

    @Test
    public void testIntegerOutOfBoundsNegativeBinary() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/integer_out_of_bounds_negative_binary", 3, 10);
    }

    @Test
    public void testIntegerMissingValueBinary() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/integer_missing_value_binary", 3, 11);
    }

    @Test
    public void testIntegerOutOfBoundsPositiveOctal() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/integer_out_of_bounds_positive_octal", 3, 9);
    }

    @Test
    public void testIntegerOutOfBoundsNegativeOctal() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/integer_out_of_bounds_negative_octal", 3, 10);
    }

    @Test
    public void testCharEmpty() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/char_empty", 3, 10);
    }

    @Test
    public void testCharMultipleChars() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/char_multiple_chars", 3, 11);
    }

    @Test
    public void testCharUnclosed() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/char_unclosed", 3, 10);
    }

    @Test
    public void testStringUnclosed() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/string_unclosed", 3, 10);
    }

    @Test
    public void testFieldDescriptorInvalidChar() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/field_descriptor_invalid_char", 2, 26);
    }

    @Test
    public void testFieldDescriptorVoid() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/field_descriptor_void", 2, 26);
    }

    @Test
    public void testFieldDescriptorUnterminatedObject() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/field_descriptor_unterminated_object", 2, 43);
    }

    @Test
    public void testFieldDescriptorEmptyObject() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/field_descriptor_empty_object", 2, 27);
    }

    @Test
    public void testFieldDescriptorObjectIllegalPeriod() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/field_descriptor_object_illegal_period", 2, 31);
    }

    @Test
    public void testFieldDescriptorObjectIllegalBracket() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/field_descriptor_object_illegal_bracket", 2, 27);
    }

    @Test
    public void testFieldDescriptorUntypedArray() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/field_descriptor_untyped_array", 2, 27);
    }

    @Test
    public void testMethodDescriptorFieldDescriptor() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/method_descriptor_field_descriptor", 2, 27);
    }

    @Test
    public void testMethodDescriptorUnclosedParen() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/method_descriptor_unclosed_paren", 2, 28);
    }

    @Test
    public void testMethodDescriptorNoReturnType() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/method_descriptor_no_return_type", 2, 29);
    }

    @Test
    public void testMethodDescriptorVoidParameter() throws IOException {
        TestReader.assertThrowsParseError("tokens/invalid/method_descriptor_void_parameter", 2, 28);
    }
}
