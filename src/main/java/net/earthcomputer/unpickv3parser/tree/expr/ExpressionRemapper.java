package net.earthcomputer.unpickv3parser.tree.expr;

public abstract class ExpressionRemapper {
    public Expression remapBinaryExpression(BinaryExpression binaryExpression) {
        return new BinaryExpression(binaryExpression.lhs.remap(this), binaryExpression.rhs.remap(this), binaryExpression.operator);
    }

    public Expression remapCastExpression(CastExpression castExpression) {
        return new CastExpression(castExpression.castType, castExpression.operand.remap(this));
    }

    public Expression remapFieldExpression(FieldExpression fieldExpression) {
        return fieldExpression;
    }

    public Expression remapLiteralExpression(LiteralExpression literalExpression) {
        return literalExpression;
    }

    public Expression remapParenExpression(ParenExpression parenExpression) {
        return new ParenExpression(parenExpression.expression.remap(this));
    }

    public Expression remapUnaryExpression(UnaryExpression unaryExpression) {
        return new UnaryExpression(unaryExpression.operand.remap(this), unaryExpression.operator);
    }
}
