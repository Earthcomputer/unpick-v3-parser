package net.earthcomputer.unpickv3parser.tree;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record TargetMethod(
    String className,
    String methodName,
    String methodDesc,
    Map<Integer, String> paramGroups,
    @Nullable String returnGroup
) {
}
