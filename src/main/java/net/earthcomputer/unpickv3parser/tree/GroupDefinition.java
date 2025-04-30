package net.earthcomputer.unpickv3parser.tree;

import net.earthcomputer.unpickv3parser.tree.expr.Expression;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
