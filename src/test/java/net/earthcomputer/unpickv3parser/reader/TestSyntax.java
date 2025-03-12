package net.earthcomputer.unpickv3parser.reader;

import net.earthcomputer.unpickv3parser.tree.DataType;
import net.earthcomputer.unpickv3parser.tree.GroupConstant;
import net.earthcomputer.unpickv3parser.tree.GroupDefinition;
import net.earthcomputer.unpickv3parser.tree.GroupFormat;
import net.earthcomputer.unpickv3parser.tree.GroupScope;
import net.earthcomputer.unpickv3parser.tree.GroupType;
import net.earthcomputer.unpickv3parser.tree.Literal;
import net.earthcomputer.unpickv3parser.tree.TargetField;
import net.earthcomputer.unpickv3parser.tree.TargetMethod;
import net.earthcomputer.unpickv3parser.tree.UnpickV3Visitor;
import net.earthcomputer.unpickv3parser.tree.expr.CastExpression;
import net.earthcomputer.unpickv3parser.tree.expr.ExpressionVisitor;
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
                classNames.add(targetField.className);
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
                for (GroupConstant constant : groupDefinition.constants) {
                    constant.value.accept(new ExpressionVisitor() {
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
                methodNames.add(targetMethod.methodName);
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
                paramGroups.add(targetMethod.paramGroups);
                returnGroups.add(targetMethod.returnGroup);
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
        List<GroupType> groupTypes = new ArrayList<>();
        List<DataType> groupDataTypes = new ArrayList<>();
        List<Boolean> groupStrict = new ArrayList<>();
        List<List<Object>> groupConstants = new ArrayList<>();
        List<@Nullable GroupFormat> groupFormats = new ArrayList<>();
        List<@Nullable String> groupPackageScopes = new ArrayList<>();
        List<@Nullable String> groupClassScopes = new ArrayList<>();
        List<@Nullable String> groupMethodScopes = new ArrayList<>();
        TestReader.test("syntax/group_definition", new UnpickV3Visitor() {
            @Override
            public void visitGroupDefinition(GroupDefinition groupDefinition) {
                groupNames.add(groupDefinition.name);
                groupTypes.add(groupDefinition.type);
                groupDataTypes.add(groupDefinition.dataType);
                groupStrict.add(groupDefinition.strict);
                groupFormats.add(groupDefinition.format);
                if (groupDefinition.scope instanceof GroupScope.Package) {
                    groupPackageScopes.add(((GroupScope.Package) groupDefinition.scope).packageName);
                } else {
                    groupPackageScopes.add(null);
                }
                if (groupDefinition.scope instanceof GroupScope.Class) {
                    groupClassScopes.add(((GroupScope.Class) groupDefinition.scope).className);
                } else {
                    groupClassScopes.add(null);
                }
                if (groupDefinition.scope instanceof GroupScope.Method) {
                    GroupScope.Method methodScope = (GroupScope.Method) groupDefinition.scope;
                    groupMethodScopes.add(methodScope.className + "." + methodScope.methodName + methodScope.methodDesc);
                } else {
                    groupMethodScopes.add(null);
                }
                groupConstants.add(groupDefinition.constants.stream().map(constant -> {
                    if (constant.key instanceof Literal.Long) {
                        return ((Literal.Long) constant.key).value;
                    } else if (constant.key instanceof Literal.Double) {
                        return ((Literal.Double) constant.key).value;
                    } else if (constant.key instanceof Literal.String) {
                        return ((Literal.String) constant.key).value;
                    } else if (constant.key instanceof Literal.Class) {
                        return ((Literal.Class) constant.key).descriptor;
                    } else if (constant.key instanceof Literal.Null) {
                        return null;
                    } else {
                        throw new AssertionError("Unexpected constant key type: " + constant.key.getClass().getName());
                    }
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
            null,
            null,
            null
        ), groupNames);
        assertEquals(Arrays.asList(
            GroupType.CONST,
            GroupType.CONST,
            GroupType.CONST,
            GroupType.CONST,
            GroupType.CONST,
            GroupType.CONST,
            GroupType.CONST,
            GroupType.CONST,
            GroupType.CONST,
            GroupType.CONST,
            GroupType.CONST,
            GroupType.CONST,
            GroupType.CONST,
            GroupType.FLAG,
            GroupType.FLAG,
            GroupType.CONST,
            GroupType.CONST,
            GroupType.CONST,
            GroupType.CONST,
            GroupType.CONST,
            GroupType.CONST,
            GroupType.CONST,
            GroupType.CONST,
            GroupType.CONST
        ), groupTypes);
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
            DataType.INT,
            DataType.INT,
            DataType.INT
        ), groupDataTypes);
        assertEquals(Arrays.asList(
            Collections.emptyList(),
            Collections.singletonList(0L),
            Collections.singletonList(0L),
            Collections.singletonList(0L),
            Collections.singletonList(0L),
            Arrays.asList(0L, 1.0, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY),
            Arrays.asList(0L, 1.0),
            Arrays.asList(0L, 1.0, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY),
            Arrays.asList(0L, 1.0),
            Arrays.asList("", null),
            Collections.singletonList(""),
            Arrays.asList("[I", null),
            Collections.singletonList("[I"),
            Collections.singletonList(0L),
            Collections.singletonList(0L),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.singletonList(0L),
            Collections.singletonList(0L),
            Collections.singletonList(0L),
            Collections.singletonList(0L)
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
            null,
            null,
            null
        ), groupFormats);
        for (int i = 0; i < groupPackageScopes.size(); i++) {
            if (i == 21) {
                assertEquals("foo.bar", groupPackageScopes.get(i));
            } else {
                assertNull(groupPackageScopes.get(i));
            }
        }
        for (int i = 0; i < groupClassScopes.size(); i++) {
            if (i == 22) {
                assertEquals("foo.Bar", groupClassScopes.get(i));
            } else {
                assertNull(groupClassScopes.get(i));
            }
        }
        for (int i = 0; i < groupMethodScopes.size(); i++) {
            if (i == 23) {
                assertEquals("foo.Bar.baz()V", groupMethodScopes.get(i));
            } else {
                assertNull(groupMethodScopes.get(i));
            }
        }
        for (int i = 0; i < groupStrict.size(); i++) {
            assertEquals(groupStrict.get(i), i == 2);
        }
    }

    @Test
    public void testClassNameTwoDots() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/class_name_two_dots", 2, 18);
    }

    @Test
    public void testMethodNameLessThan() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/method_name_less_than", 2, 25);
    }

    @Test
    public void testMethodNameMissingGreaterThan() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/method_name_missing_greater_than", 2, 29);
    }

    @Test
    public void testMethodNameAngledBracketsIncorrect() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/method_name_angled_brackets_incorrect", 2, 24);
    }

    @Test
    public void testTargetMethodDuplicateParam() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/target_method_duplicate_param", 4, 11);
    }

    @Test
    public void testTargetMethodDuplicateReturn() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/target_method_duplicate_return", 4, 5);
    }

    @Test
    public void testGroupDefinitionFlagDefaultGroup() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_flag_default_group", 2, 1);
    }

    @Test
    public void testGroupDefinitionFlagInvalidDataType() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_flag_invalid_data_type", 2, 6);
    }

    @Test
    public void testGroupDefinitionIntConstantForString() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_int_constant_for_string", 3, 5);
    }

    @Test
    public void testGroupDefinitionStringConstantForInt() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_string_constant_for_int", 3, 5);
    }

    @Test
    public void testGroupDefinitionFloatConstantForInt() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_float_constant_for_int", 3, 5);
    }

    @Test
    public void testGroupDefinitionStringConstantForFloat() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_string_constant_for_float", 3, 5);
    }

    @Test
    public void testGroupDefinitionIntConstantForClass() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_int_constant_for_class", 3, 5);
    }

    @Test
    public void testGroupDefinitionClassConstantForInt() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_class_constant_for_int", 3, 12);
    }

    @Test
    public void testGroupDefinitionClassConstantForString() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_class_constant_for_string", 3, 12);
    }

    @Test
    public void testGroupDefinitionStringConstantForClass() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_string_constant_for_class", 3, 5);
    }

    @Test
    public void testGroupDefinitionNullConstantForInt() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_null_constant_for_int", 3, 5);
    }

    @Test
    public void testGroupDefinitionNullConstantForFloat() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_null_constant_for_float", 3, 5);
    }

    @Test
    public void testGroupDefinitionDuplicateKey() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_duplicate_key", 4, 5);
    }

    @Test
    public void testGroupDefinitionMultipleFormats() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_multiple_formats", 4, 5);
    }

    @Test
    public void testGroupDefinitionInvalidFormat() throws IOException {
        TestReader.assertThrowsParseError("syntax/invalid/group_definition_invalid_format", 3, 14);
    }
}
