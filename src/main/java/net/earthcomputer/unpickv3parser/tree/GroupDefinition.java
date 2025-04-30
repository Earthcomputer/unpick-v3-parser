package net.earthcomputer.unpickv3parser.tree;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.earthcomputer.unpickv3parser.tree.expr.Expression;

public record GroupDefinition(
        List<GroupScope> scopes,
        boolean flags,
        boolean strict,
        DataType dataType,
        @Nullable String name,
        List<Expression> constants,
        @Nullable GroupFormat format
) {
}
