package net.earthcomputer.unpickv3parser.tree.expr;

import net.earthcomputer.unpickv3parser.tree.DataType;
import org.jetbrains.annotations.Nullable;

public final class FieldExpression extends Expression {
    public final String className;
    public final String fieldName;
    @Nullable
    public final DataType fieldType;

    public FieldExpression(String className, String fieldName, @Nullable DataType fieldType) {
        this.className = className;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visitFieldExpression(this);
    }

    @Override
    public Expression remap(ExpressionRemapper remapper) {
        return remapper.remapFieldExpression(this);
    }
}
