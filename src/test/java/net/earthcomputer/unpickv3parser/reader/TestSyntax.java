package net.earthcomputer.unpickv3parser.reader;

import net.earthcomputer.unpickv3parser.tree.DataType;
import net.earthcomputer.unpickv3parser.tree.GroupDefinition;
import net.earthcomputer.unpickv3parser.tree.GroupFormat;
import net.earthcomputer.unpickv3parser.tree.GroupScope;
import net.earthcomputer.unpickv3parser.tree.Literal;
import net.earthcomputer.unpickv3parser.tree.TargetField;
import net.earthcomputer.unpickv3parser.tree.TargetMethod;
import net.earthcomputer.unpickv3parser.tree.UnpickV3Visitor;
import net.earthcomputer.unpickv3parser.tree.expr.CastExpression;
import net.earthcomputer.unpickv3parser.tree.expr.Expression;
import net.earthcomputer.unpickv3parser.tree.expr.ExpressionVisitor;
import net.earthcomputer.unpickv3parser.tree.expr.FieldExpression;
import net.earthcomputer.unpickv3parser.tree.expr.LiteralExpression;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public final class TestSyntax {
    @Test
    public void testClassName() throws IOException {
        List<String> classNames = new ArrayList<>();
        TestReader.test("syntax/class_name", new UnpickV3Visitor() {
            @Override
            public void visitTargetField(TargetField targetField) {
                classNames.add(targetField.className());
            }
        });
        assertEquals(Arrays.asList("Foo", "foo.Bar", "foo.Bar$Baz", "foo.Bar$1"), classNames);
    }

    @Test
    public void testDataType() throws IOException {
        List<DataType> dataTypes = new ArrayList<>();
        TestReader.test("syntax/data_type", new UnpickV3Visitor() {
            @Override
            public void visitGroupDefinition(GroupDefinition groupDefinition) {
                for (Expression constant : groupDefinition.constants()) {
                    constant.accept(new ExpressionVisitor() {
                        @Override
                        public void visitCastExpression(CastExpression castExpression) {
                            dataTypes.add(castExpression.castType);
                        }
                    });
                }
            }
        });
        assertEquals(Arrays.asList(DataType.BYTE, DataType.SHORT, DataType.INT, DataType.LONG, DataType.FLOAT, DataType.DOUBLE, DataType.CHAR, DataType.STRING), dataTypes);
    }

    @Test
    public void testMethodName() throws IOException {
        List<String> methodNames = new ArrayList<>();
        TestReader.test("syntax/method_name", new UnpickV3Visitor() {
            @Override
            public void visitTargetMethod(TargetMethod targetMethod) {
                methodNames.add(targetMethod.methodName());
            }
        });
        assertEquals(Arrays.asList("baz", "<init>", "<clinit>"), methodNames);
    }

    @Test
    public void testTargetMethod() throws IOException {
        List<Map<Integer, String>> paramGroups = new ArrayList<>();
        List<@Nullable String> returnGroups = new ArrayList<>();
        TestReader.test("syntax/target_method", new UnpickV3Visitor() {
            @Override
            public void visitTargetMethod(TargetMethod targetMethod) {
                paramGroups.add(targetMethod.paramGroups());
                returnGroups.add(targetMethod.returnGroup());
            }
        });
        Map<Integer, String> expectedParamGroups1 = new HashMap<>();
        expectedParamGroups1.put(0, "a");
        expectedParamGroups1.put(1, "b");
        expectedParamGroups1.put(69, "c");
        Map<Integer, String> expectedParamGroups2 = new HashMap<>();
        expectedParamGroups2.put(0, "e");
        assertEquals(Arrays.asList(Collections.emptyMap(), expectedParamGroups1, expectedParamGroups2, Collections.emptyMap()), paramGroups);
        assertEquals(Arrays.asList(null, "d", null, "f"), returnGroups);
    }

    @Test
    public void testGroupDefinition() throws IOException {
        List<@Nullable String> groupNames = new ArrayList<>();
        List<Boolean> groupFlags = new ArrayList<>();
        List<DataType> groupDataTypes = new ArrayList<>();
        List<Boolean> groupStrict = new ArrayList<>();
        List<List<Object>> groupConstants = new ArrayList<>();
        List<@Nullable GroupFormat> groupFormats = new ArrayList<>();
        List<List<String>> groupPackageScopes = new ArrayList<>();
        List<List<String>> groupClassScopes = new ArrayList<>();
        List<List<String>> groupMethodScopes = new ArrayList<>();
        TestReader.test("syntax/group_definition", new UnpickV3Visitor() {
            @Override
            public void visitGroupDefinition(GroupDefinition groupDefinition) {
                groupNames.add(groupDefinition.name());
                groupFlags.add(groupDefinition.flags());
                groupDataTypes.add(groupDefinition.dataType());
                groupStrict.add(groupDefinition.strict());
                groupFormats.add(groupDefinition.format());
                List<String> packageScopes = new ArrayList<>();
                List<String> classScopes = new ArrayList<>();
                List<String> methodScopes = new ArrayList<>();
                for (GroupScope scope : groupDefinition.scopes()) {
                    if (scope instanceof GroupScope.Package packageScope) {
                        packageScopes.add(packageScope.packageName());
                    } else if (scope instanceof GroupScope.Class classScope) {
                        classScopes.add(classScope.className());
                    } else if (scope instanceof GroupScope.Method methodScope) {
                        methodScopes.add(methodScope.className() + "." + methodScope.methodName() + methodScope.methodDesc());
                    } else {
                        throw new AssertionError("Unknown scope type: " + scope.getClass().getName());
                    }
                }
                groupPackageScopes.add(packageScopes);
                groupClassScopes.add(classScopes);
                groupMethodScopes.add(methodScopes);
                groupConstants.add(groupDefinition.constants().stream().flatMap(constant -> {
                    List<Object> constants = new ArrayList<>();
                    constant.accept(new ExpressionVisitor() {
                        @Override
                        public void visitLiteralExpression(LiteralExpression literalExpression) {
                            Literal constant = literalExpression.literal;
                            if (constant instanceof Literal.Integer integerLiteral) {
                                constants.add(integerLiteral.value());
                            } else if (constant instanceof Literal.Long longLiteral) {
                                constants.add(longLiteral.value());
                            } else if (constant instanceof Literal.Float floatLiteral) {
                                constants.add(floatLiteral.value());
                            } else if (constant instanceof Literal.Double doubleLiteral) {
                                constants.add(doubleLiteral.value());
                            } else if (constant instanceof Literal.String stringLiteral) {
                                constants.add(stringLiteral.value());
                            } else {
                                throw new AssertionError("Unexpected constant key type: " + constant.getClass().getName());
                            }
                        }

                        @Override
                        public void visitFieldExpression(FieldExpression fieldExpression) {
                            constants.add(fieldExpression.className + "." + fieldExpression.fieldName);
                        }
                    });
                    return constants.stream();
                }).collect(Collectors.toList()));
            }
        });
        assertEquals(Arrays.asList(
            null,
            null,
            "g",
            null,
            "g",
            null,
            "g",
            null,
            "g",
            null,
            "g",
            null,
            "g",
            "g",
            "g",
            "a",
            "b",
            "c",
            "d",
            "e",
            "f",
            "af",
            "bf",
            "cf",
            "df",
            null,
            null,
            null,
            null
        ), groupNames);
        assertEquals(Arrays.asList(
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            true,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ), groupFlags);
        assertEquals(Arrays.asList(
            DataType.INT,
            DataType.INT,
            DataType.INT,
            DataType.LONG,
            DataType.LONG,
            DataType.FLOAT,
            DataType.FLOAT,
            DataType.DOUBLE,
            DataType.DOUBLE,
            DataType.STRING,
            DataType.STRING,
            DataType.CLASS,
            DataType.CLASS,
            DataType.INT,
            DataType.LONG,
            DataType.INT,
            DataType.LONG,
            DataType.INT,
            DataType.LONG,
            DataType.INT,
            DataType.LONG,
            DataType.FLOAT,
            DataType.FLOAT,
            DataType.DOUBLE,
            DataType.DOUBLE,
            DataType.INT,
            DataType.INT,
            DataType.INT,
            DataType.INT
        ), groupDataTypes);
        assertEquals(Arrays.asList(
            Collections.emptyList(),
            Collections.singletonList(0),
            Collections.singletonList(0),
            Collections.singletonList(0),
            Collections.singletonList(0),
            Arrays.asList(0, 1.0),
            Arrays.asList(0, 1.0),
            Arrays.asList(0, 1.0),
            Arrays.asList(0, 1.0),
            Collections.singletonList(""),
            Collections.singletonList(""),
            Collections.singletonList("Foo.bar"),
            Collections.singletonList("Foo.bar"),
            Collections.singletonList(0),
            Collections.singletonList(0),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.singletonList(0),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.singletonList(0),
            Collections.singletonList(0),
            Collections.singletonList(0),
            Collections.emptyList()
        ), groupConstants);
        assertEquals(Arrays.asList(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            GroupFormat.DECIMAL,
            GroupFormat.HEX,
            GroupFormat.BINARY,
            GroupFormat.OCTAL,
            GroupFormat.CHAR,
            GroupFormat.DECIMAL,
            GroupFormat.DECIMAL,
            GroupFormat.HEX,
            GroupFormat.DECIMAL,
            GroupFormat.HEX,
            null,
            null,
            null,
            null
        ), groupFormats);
        for (int i = 0; i < groupPackageScopes.size(); i++) {
            if (i == 25) {
                assertEquals(Collections.singletonList("foo.bar"), groupPackageScopes.get(i));
            } else if (i == 28) {
                assertEquals(Arrays.asList("foo.bar", "baz.quux"), groupPackageScopes.get(i));
            } else {
                assertTrue(groupPackageScopes.get(i).isEmpty());
            }
        }
        for (int i = 0; i < groupClassScopes.size(); i++) {
            if (i == 26) {
                assertEquals(Collections.singletonList("foo.Bar"), groupClassScopes.get(i));
            } else {
                assertTrue(groupClassScopes.get(i).isEmpty());
            }
        }
        for (int i = 0; i < groupMethodScopes.size(); i++) {
            if (i == 27) {
                assertEquals(Collections.singletonList("foo.Bar.baz()V"), groupMethodScopes.get(i));
            } else {
                assertTrue(groupMethodScopes.get(i).isEmpty());
            }
        }
        for (int i = 0; i < groupStrict.size(); i++) {
            assertEquals(groupStrict.get(i), i == 2);
        }
    }

    @Test
    public void testClassNameTwoDots() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/class_name_two_dots", 2, 18, "Expected identifier before '.' token");
    }

    @Test
    public void testMethodNameLessThan() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/method_name_less_than", 2, 25, "Expected identifier before '(' token");
    }

    @Test
    public void testMethodNameMissingGreaterThan() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/method_name_missing_greater_than", 2, 29, "Expected '>' before '(' token");
    }

    @Test
    public void testMethodNameAngledBracketsIncorrect() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/method_name_angled_brackets_incorrect", 2, 24, "Expected identifier before 'bar' token");
    }

    @Test
    public void testTargetMethodDuplicateParam() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/target_method_duplicate_param", 4, 11, "Specified parameter 0 twice");
    }

    @Test
    public void testTargetMethodDuplicateReturn() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/target_method_duplicate_return", 4, 5, "Specified return group twice");
    }

    @Test
    public void testGroupDefinitionFlagDefaultGroup() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_flag_default_group", 3, 6, "The flags attribute is not applicable to the default group");
    }

    @Test
    public void testGroupDefinitionFlagInvalidDataType() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_flag_invalid_data_type", 3, 6, "The flags attribute is not applicable to this data type");
    }

    @Test
    public void testGroupDefinitionMultipleFlags() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_multiple_flags", 4, 6, "Duplicate flags attribute");
    }

    @Test
    public void testGroupDefinitionMultipleStrict() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_multiple_strict", 4, 6, "Duplicate strict attribute");
    }

    @Test
    public void testGroupDefinitionMultipleFormats() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_multiple_formats", 4, 6, "Duplicate format attribute");
    }

    @Test
    public void testGroupDefinitionInvalidFormat() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_invalid_format", 3, 13, "Expected group format before 'foo' token");
    }
}
