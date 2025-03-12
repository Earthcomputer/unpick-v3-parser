package net.earthcomputer.unpickv3parser.remapper;

import net.earthcomputer.unpickv3parser.UnpickV3Reader;
import net.earthcomputer.unpickv3parser.UnpickV3Remapper;
import net.earthcomputer.unpickv3parser.UnpickV3Writer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public final class TestRemapper {
    private static final Map<String, List<String>> PACKAGES = new HashMap<>();
    private static final Map<String, String> CLASSES = new HashMap<>();
    private static final Map<UnpickV3Remapper.FieldKey, String> FIELDS = new HashMap<>();
    private static final Map<UnpickV3Remapper.MethodKey, String> METHODS = new HashMap<>();

    static {
        PACKAGES.put("unmapped.foo", Arrays.asList("A", "B"));
        PACKAGES.put("unmapped.bar", Collections.singletonList("C"));
        CLASSES.put("unmapped.foo.A", "mapped.foo.X");
        CLASSES.put("unmapped.foo.B", "mapped.bar.Y");
        CLASSES.put("unmapped.bar.C", "mapped.bar.Z");
        FIELDS.put(new UnpickV3Remapper.FieldKey("unmapped.foo.B", "baz", "I"), "quux");
        METHODS.put(new UnpickV3Remapper.MethodKey("unmapped.foo.B", "foo2", "(Lunmapped/foo/A;)V"), "bar2");
    }

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
    public void testFieldExpression() throws IOException {
        test("const int\n\t0 = mapped.bar.Y.quux", "const int\n\t0 = unmapped.foo.B.baz");
    }

    @Test
    public void testTypedFieldExpression() throws IOException {
        test("const float\n\t0 = mapped.bar.Y.quux:int", "const float\n\t0 = unmapped.foo.B.baz:int");
    }

    @Test
    public void testFieldExpressionWrongType() throws IOException {
        test("const float\n\t0 = mapped.bar.Y.baz", "const float\n\t0 = unmapped.foo.B.baz");
    }

    @Test
    public void testPackageScope() throws IOException {
        test(
            "scoped class mapped.foo.X const int\n\t0 = 0\n\t1 = 1\n\nscoped class mapped.bar.Y const int\n\t0 = 0\n\t1 = 1",
            "scoped package unmapped.foo const int\n\t0 = 0\n\t1 = 1"
        );
    }

    @Test
    public void testClassScope() throws IOException {
        test("scoped class mapped.foo.X const int", "scoped class unmapped.foo.A const int");
    }

    @Test
    public void testMethodScope() throws IOException {
        test("scoped method mapped.bar.Y bar2 (Lmapped/foo/X;)V const int", "scoped method unmapped.foo.B foo2 (Lunmapped/foo/A;)V const int");
    }

    @Test
    public void testClassKey() throws IOException {
        test("const Class\n\tclass Lmapped/foo/X; = Foo.bar", "const Class\n\tclass Lunmapped/foo/A; = Foo.bar");
    }

    private static void test(String expectedRemapped, String original) throws IOException {
        expectedRemapped = "unpick v3\n\n" + expectedRemapped + "\n";
        original = "unpick v3\n\n" + original + "\n";

        String remapped;
        try (UnpickV3Reader reader = new UnpickV3Reader(new StringReader(original))) {
            UnpickV3Writer writer = new UnpickV3Writer();
            UnpickV3Remapper remapper = new UnpickV3Remapper(writer, PACKAGES, CLASSES, FIELDS, METHODS);
            reader.accept(remapper);
            remapped = writer.getOutput().replace(System.lineSeparator(), "\n");
        }
        assertEquals(expectedRemapped, remapped);
    }
}
