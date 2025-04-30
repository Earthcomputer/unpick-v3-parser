package net.earthcomputer.unpickv3parser.tree;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

public record TargetMethod(
        String className,
        String methodName,
        String methodDesc,
        Map<Integer, String> paramGroups,
        @Nullable String returnGroup
) {
}
