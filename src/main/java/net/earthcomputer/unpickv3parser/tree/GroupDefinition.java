package net.earthcomputer.unpickv3parser.tree;

import net.earthcomputer.unpickv3parser.tree.expr.Expression;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class GroupDefinition {
    public final List<GroupScope> scopes;
    public final boolean flags;
    public final boolean strict;
    public final DataType dataType;
    @Nullable
    public final String name;
    public final List<Expression> constants;
    @Nullable
    public final GroupFormat format;

    public GroupDefinition(
        List<GroupScope> scopes,
        boolean flags,
        boolean strict,
        DataType dataType,
        @Nullable String name,
        List<Expression> constants,
        @Nullable GroupFormat format
    ) {
        this.scopes = scopes;
        this.flags = flags;
        this.strict = strict;
        this.dataType = dataType;
        this.name = name;
        this.constants = constants;
        this.format = format;
    }
}
