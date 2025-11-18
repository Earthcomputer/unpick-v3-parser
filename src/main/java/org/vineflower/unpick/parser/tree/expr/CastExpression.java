package org.vineflower.unpick.parser.tree.expr;

import org.vineflower.unpick.parser.tree.DataType;

public final class CastExpression extends Expression {
    public final DataType castType;
    public final Expression operand;

    public CastExpression(DataType castType, Expression operand) {
        this.castType = castType;
        this.operand = operand;
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visitCastExpression(this);
    }

    @Override
    public Expression transform(ExpressionTransformer transformer) {
        return transformer.transformCastExpression(this);
    }
}
