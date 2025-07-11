package net.earthcomputer.unpickv3parser.tree;

public sealed interface GroupScope {
    record Package(String packageName) implements GroupScope {
    }

    record Class(String className) implements GroupScope {
    }

    record Method(String className, String methodName, String methodDesc) implements GroupScope {
    }
}
