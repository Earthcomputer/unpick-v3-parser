package net.earthcomputer.unpickv3parser.tree.expr;

public final class UnaryExpression extends Expression {
    public final Expression operand;
    public final Operator operator;

    public UnaryExpression(Expression operand, Operator operator) {
        this.operand = operand;
        this.operator = operator;
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visitUnaryExpression(this);
    }

    @Override
    public Expression remap(ExpressionRemapper remapper) {
        return remapper.remapUnaryExpression(this);
    }

    public enum Operator {
        NEGATE, BIT_NOT,
    }
}
