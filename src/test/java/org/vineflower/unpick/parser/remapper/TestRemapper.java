package org.vineflower.unpick.parser.remapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.vineflower.unpick.parser.UnpickV3Reader;
import org.vineflower.unpick.parser.UnpickV3Remapper;
import org.vineflower.unpick.parser.UnpickV3Writer;

public final class TestRemapper {
    private static final Map<String, List<String>> PACKAGES = Map.of(
            "unmapped.foo", List.of("unmapped.foo.A", "unmapped.foo.B"),
            "unmapped.bar", List.of("unmapped.bar.C")
    );
    private static final Map<String, String> CLASSES = Map.of(
            "unmapped.foo.A", "mapped.foo.X",
            "unmapped.foo.B", "mapped.bar.Y",
            "unmapped.bar.C", "mapped.bar.Z"
    );
    private static final Map<MemberKey, String> FIELDS = Map.of(
            new MemberKey("unmapped.foo.B", "baz", "I"), "quux"
    );
    private static final Map<MemberKey, String> METHODS = Map.of(
            new MemberKey("unmapped.foo.B", "foo2", "(Lunmapped/foo/A;)V"), "bar2"
    );

    @Test
    public void testTargetField() throws IOException {
        test("target_field mapped.bar.Y quux I g", "target_field unmapped.foo.B baz I g");
    }

    @Test
    public void testFieldDescriptor() throws IOException {
        test("target_field mapped.bar.Z foo Lmapped/foo/X; g", "target_field unmapped.bar.C foo Lunmapped/foo/A; g");
    }

    @Test
    public void testTargetMethod() throws IOException {
        test("target_method mapped.bar.Y bar2 (Lmapped/foo/X;)V", "target_method unmapped.foo.B foo2 (Lunmapped/foo/A;)V");
    }

    @Test
    public void testTargetAnnotation() throws IOException {
        test("target_annotation mapped.bar.Y baz", "target_annotation unmapped.foo.B baz");
    }

    @Test
    public void testFieldExpression() throws IOException {
        test("group int\n\tmapped.bar.Y.quux", "group int\n\tunmapped.foo.B.baz");
    }

    @Test
    public void testTypedFieldExpression() throws IOException {
        test("group float\n\tmapped.bar.Y.quux:int", "group float\n\tunmapped.foo.B.baz:int");
    }

    @Test
    public void testFieldExpressionWrongType() throws IOException {
        test("group float\n\tmapped.bar.Y.baz:float", "group float\n\tunmapped.foo.B.baz:float");
    }

    @Test
    public void testPackageScope() throws IOException {
        test(
                "group int\n\t@scope class mapped.foo.X\n\t@scope class mapped.bar.Y\n\t0\n\t1",
                "group int\n\t@scope package unmapped.foo\n\t0\n\t1"
        );
    }

    @Test
    public void testClassScope() throws IOException {
        test("group int\n\t@scope class mapped.foo.X", "group int\n\t@scope class unmapped.foo.A");
    }

    @Test
    public void testMethodScope() throws IOException {
        test("group int\n\t@scope method mapped.bar.Y bar2 (Lmapped/foo/X;)V", "group int\n\t@scope method unmapped.foo.B foo2 (Lunmapped/foo/A;)V");
    }

    @Test
    public void testDocs() throws IOException {
        test("#: test\ngroup int", "#: test\ngroup int");
    }

    private static void test(String expectedRemapped, String original) throws IOException {
        expectedRemapped = "unpick v4\n\n" + expectedRemapped + "\n";
        original = "unpick v4\n\n" + original + "\n";

        String remapped;
        try (UnpickV3Reader reader = new UnpickV3Reader(new StringReader(original))) {
            UnpickV3Writer writer = new UnpickV3Writer();
            UnpickV3Remapper remapper = new UnpickV3Remapper(writer) {
                @Override
                protected String mapClassName(String className) {
                    return CLASSES.getOrDefault(className, className);
                }

                @Override
                protected String mapFieldName(String className, String fieldName, String fieldDesc) {
                    return FIELDS.getOrDefault(new MemberKey(className, fieldName, fieldDesc), fieldName);
                }

                @Override
                protected String mapMethodName(String className, String methodName, String methodDesc) {
                    return METHODS.getOrDefault(new MemberKey(className, methodName, methodDesc), methodName);
                }

                @Override
                protected List<String> getClassesInPackage(String pkg) {
                    return PACKAGES.getOrDefault(pkg, List.of());
                }

                @Override
                protected String getFieldDesc(String className, String fieldName) {
                    return "I";
                }
            };
            reader.accept(remapper);
            remapped = writer.getOutput().replace(System.lineSeparator(), "\n");
        }
        assertEquals(expectedRemapped, remapped);
    }

    private record MemberKey(String owner, String name, String descriptor) {
    }
}
