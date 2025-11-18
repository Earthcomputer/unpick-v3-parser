package net.earthcomputer.unpickv3parser.retrofit;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Retrofitter implements Closeable {
    private static final int API = Opcodes.ASM9;

    private final Map<String, ClassData> classDataMap = new HashMap<>();
    private final List<ZipFile> moduleRoots = new ArrayList<>();

    public Retrofitter(Path oldJavaHome) throws IOException {
        try (Stream<Path> paths = Files.walk(oldJavaHome.resolve("jmods"))) {
            for (Path path : (Iterable<Path>) paths::iterator) {
                if (path.getFileName().toString().endsWith(".jmod")) {
                    moduleRoots.add(new ZipFile(path.toFile()));
                }
            }
        } catch (Throwable e) {
            for (ZipFile root : moduleRoots) {
                try {
                    root.close();
                } catch (IOException e1) {
                    e.addSuppressed(e1);
                }
            }

            throw e;
        }
    }

    @Override
    public void close() throws IOException {
        for (ZipFile root : moduleRoots) {
            root.close();
        }

        moduleRoots.clear();
    }

    public ClassVisitor createClassVisitor(ClassVisitor classVisitor) {
        return new RetrofitClassVisitor(classVisitor);
    }

    private ClassData getClassData(String className) {
        ClassData data = classDataMap.get(className);
        if (data != null) {
            return data;
        }

        ClassReader reader = null;
        for (ZipFile moduleRoot : moduleRoots) {
            ZipEntry entry = moduleRoot.getEntry("classes/" + className + ".class");
            if (entry != null) {
                try {
                    reader = new ClassReader(moduleRoot.getInputStream(entry));
                } catch (IOException e) {
                    throw new IllegalStateException("Error reading JDK class file " + className, e);
                }
                break;
            }
        }

        if (reader == null) {
            throw new RetrofitException("Class not found in this JDK: " + className);
        }

        List<ClassData> parents = new ArrayList<>();
        if (reader.getSuperName() != null) {
            parents.add(getClassData(reader.getSuperName()));
        }
        for (String itf : reader.getInterfaces()) {
            parents.add(getClassData(itf));
        }

        var visitor = new ClassVisitor(API) {
            final Set<String> fields = new HashSet<>();
            final Set<String> methods = new HashSet<>();

            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                if ((access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) != 0) {
                    fields.add(name + ":" + descriptor);
                }

                return null;
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if ((access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) != 0) {
                    methods.add(name + descriptor);
                }

                return null;
            }
        };

        reader.accept(visitor, ClassReader.SKIP_CODE);

        data = new ClassData(visitor.fields, visitor.methods);

        for (ClassData parent : parents) {
            data.fields.addAll(parent.fields);
            data.methods.addAll(parent.methods);
        }

        classDataMap.put(className, data);
        return data;
    }

    private void checkInternalName(String internalName) {
        if (isJdkClass(internalName)) {
            getClassData(internalName);
        }
    }

    private void checkType(Type type) {
        if (type.getSort() == Type.OBJECT) {
            checkInternalName(type.getInternalName());
        } else if (type.getSort() == Type.ARRAY) {
            checkType(type.getElementType());
        } else if (type.getSort() == Type.METHOD) {
            for (Type argType : type.getArgumentTypes()) {
                checkType(argType);
            }
            checkType(type.getReturnType());
        }
    }

    private void checkField(String owner, String name, String descriptor) {
        if (isJdkClass(owner)) {
            ClassData data = getClassData(owner);
            if (!data.fields.contains(name + ":" + descriptor)) {
                throw new RetrofitException("Field not found in this JDK: " + owner + "." + name + ":" + descriptor);
            }
        }
    }

    private void checkMethod(String owner, String name, String descriptor) {
        if (isJdkClass(owner)) {
            ClassData data = getClassData(owner);
            if (!data.methods.contains(name + descriptor)) {
                throw new RetrofitException("Method not found in this JDK: " + owner + "." + name + descriptor);
            }
        }
    }

    private void checkHandle(Handle handle) {
        int kind = handle.getTag();
        if (kind == Opcodes.H_GETFIELD || kind == Opcodes.H_PUTFIELD || kind == Opcodes.H_GETSTATIC || kind == Opcodes.H_PUTSTATIC) {
            checkField(handle.getOwner(), handle.getName(), handle.getDesc());
        } else {
            checkMethod(handle.getOwner(), handle.getName(), handle.getDesc());
        }
    }

    private void checkConstant(Object constant) {
        switch (constant) {
            case Type type -> checkType(type);
            case Handle handle -> checkHandle(handle);
            case ConstantDynamic condy -> {
                checkHandle(condy.getBootstrapMethod());
                for (int i = 0; i < condy.getBootstrapMethodArgumentCount(); i++) {
                    checkConstant(condy.getBootstrapMethodArgument(i));
                }
            }
            default -> {
            }
        }
    }

    private static boolean isJdkClass(String className) {
        return className.startsWith("java/") || className.startsWith("javax/");
    }

    private record ClassData(Set<String> fields, Set<String> methods) {
    }

    public static class RetrofitException extends RuntimeException {
        public RetrofitException(String message) {
            super(message);
        }
    }

    private class RetrofitClassVisitor extends ClassVisitor {
        protected RetrofitClassVisitor(ClassVisitor classVisitor) {
            super(API, classVisitor);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            checkInternalName(superName);
            if (interfaces != null) {
                for (String iface : interfaces) {
                    checkInternalName(iface);
                }
            }

            super.visit(Opcodes.V17, access, name, signature, superName, interfaces);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            checkType(Type.getType(descriptor));
            return super.visitField(access, name, descriptor, signature, value);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            checkType(Type.getMethodType(descriptor));

            if (exceptions != null) {
                for (String exception : exceptions) {
                    checkInternalName(exception);
                }
            }

            return new RetrofitMethodVisitor(super.visitMethod(access, name, descriptor, signature, exceptions));
        }
    }

    private class RetrofitMethodVisitor extends MethodVisitor {
        protected RetrofitMethodVisitor(MethodVisitor methodVisitor) {
            super(API, methodVisitor);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            if (opcode == Opcodes.NEW && "java/lang/MatchException".equals(type)) {
                type = "java/lang/IllegalStateException";
            }

            checkType(Type.getObjectType(type));
            super.visitTypeInsn(opcode, type);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            checkField(owner, name, descriptor);
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if ("java/lang/MatchException".equals(owner)) {
                owner = "java/lang/IllegalStateException";
            }

            checkMethod(owner, name, descriptor);
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
            checkType(Type.getMethodType(descriptor));
            checkHandle(bootstrapMethodHandle);
            for (Object arg : bootstrapMethodArguments) {
                checkConstant(arg);
            }
            super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
        }

        @Override
        public void visitLdcInsn(Object value) {
            checkConstant(value);
            super.visitLdcInsn(value);
        }

        @Override
        public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
            checkType(Type.getType(descriptor));
            super.visitMultiANewArrayInsn(descriptor, numDimensions);
        }
    }
}
