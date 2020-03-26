package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class EvalExpressions implements Transform
{

    private LinkedList<HashMap<String, Literal>> variableValues;

    public EvalExpressions()
    {
        variableValues = new LinkedList<>();
    }

    @Override
    public void apply(AST ast)
    {
        evalNode(ast.root);
    }

    private void evalNode(ASTNode root)
    {
        ArrayList<ASTNode> nodes = root.getChildren();
        for (ASTNode child : nodes)
        {
            if (child instanceof VariableAssignment)
            {
                addAssignedVariableToList((VariableAssignment) child);
            }
            if (child instanceof IfClause || child instanceof Declaration)
            {
                transformExpression(child);
            }
            if (!child.getChildren().isEmpty())
            {
                evalNode(child);
            }
        }
    }

    private void addAssignedVariableToList(VariableAssignment astNode)
    {
        HashMap<String, Literal> map = new HashMap<>();
        String name = astNode.name.name;
        if (!(astNode.expression instanceof Literal))
        {
            transformExpression(astNode.expression);
        }
        if (astNode.expression instanceof BoolLiteral)
        {
            map.put(name, (BoolLiteral) astNode.expression);
        }
        if (astNode.expression instanceof ColorLiteral)
        {
            map.put(name, (ColorLiteral) astNode.expression);
        }
        if (astNode.expression instanceof PercentageLiteral)
        {
            map.put(name, (PercentageLiteral) astNode.expression);
        }
        if (astNode.expression instanceof PixelLiteral)
        {
            map.put(name, (PixelLiteral) astNode.expression);
        }
        if (astNode.expression instanceof ScalarLiteral)
        {
            map.put(name, (ScalarLiteral) astNode.expression);
        }
        variableValues.add(map);
    }

    private void transformExpression(ASTNode astNode)
    {
        ArrayList<ASTNode> children = astNode.getChildren();
        for (ASTNode child : children)
        {
            if (child instanceof VariableReference)
            {
                astNode.removeChild(child);
                child = transformVariableReference((VariableReference) child);
                astNode.addChild(child);
            }
            if (child instanceof Operation)
            {
                astNode.removeChild(child);
                child = transformOperation((Operation) child);
                astNode.addChild(child);
            }
        }

    }

    private Literal transformVariableReference(VariableReference astNode)
    {
        for (HashMap map : variableValues)
        {
            String name = astNode.name;
            if (map.containsKey(name))
            {
                return (Literal) map.get(name);
            }
        }
        return null;
    }

    private Literal transformOperation(Operation astNode)
    {
        Expression left = astNode.lhs;
        Expression right = astNode.rhs;

        if (!(left instanceof Literal))
        {
            astNode.removeChild(left);
        }
        if (!(right instanceof Literal))
        {
            astNode.removeChild(right);
        }
        if (left instanceof VariableReference)
        {
            astNode.lhs = transformVariableReference((VariableReference) left);
        }
        if (right instanceof VariableReference)
        {
            astNode.rhs = transformVariableReference((VariableReference) right);
        }
        if (left instanceof Operation)
        {
            astNode.lhs = transformOperation((Operation) left);
        }
        if (right instanceof Operation)
        {
            astNode.rhs = transformOperation((Operation) right);
        }

        return operate(astNode);
    }

    private Literal operate(Operation operation)
    {
        Expression left = operation.lhs;
        Expression right = operation.rhs;

        if (operation instanceof AddOperation)
        {
            return operateAdd(left, right);
        }
        if (operation instanceof SubtractOperation)
        {
            return operateSubstract(left, right);
        }

        if (operation instanceof MultiplyOperation)
        {
            return operateMultiply(left, right);
        }
        return null;
    }

    private Literal operateSubstract(Expression left, Expression right)
    {
        if (left instanceof PercentageLiteral)
        {
            int value = ((PercentageLiteral) left).value - ((PercentageLiteral) right).value;
            return new PercentageLiteral(value);
        }
        if (left instanceof PixelLiteral)
        {
            int value = ((PixelLiteral) left).value - ((PixelLiteral) right).value;
            return new PixelLiteral(value);
        }
        if (left instanceof ScalarLiteral)
        {
            int value = ((ScalarLiteral) left).value - ((ScalarLiteral) right).value;
            return new ScalarLiteral(value);
        }
        return null;
    }

    private Literal operateAdd(Expression left, Expression right)
    {
        if (left instanceof PercentageLiteral)
        {
            int value = ((PercentageLiteral) left).value + ((PercentageLiteral) right).value;
            return new PercentageLiteral(value);
        }
        if (left instanceof PixelLiteral)
        {
            int value = ((PixelLiteral) left).value + ((PixelLiteral) right).value;
            return new PixelLiteral(value);
        }
        if (left instanceof ScalarLiteral)
        {
            int value = ((ScalarLiteral) left).value + ((ScalarLiteral) right).value;
            return new ScalarLiteral(value);
        }
        return null;
    }

    private Literal operateMultiply(Expression left, Expression right)
    {
        if (left instanceof ScalarLiteral && right instanceof ScalarLiteral)
        {
            int value = ((ScalarLiteral) left).value * ((ScalarLiteral) right).value;
            return new ScalarLiteral(value);
        } else if (left instanceof ScalarLiteral)
        {
            return multiply((ScalarLiteral) left, right);
        } else if (right instanceof ScalarLiteral)
        {
            return multiply((ScalarLiteral) right, left);
        }
        return null;
    }

    private Literal multiply(ScalarLiteral scalar, Expression expression)
    {
        if (expression instanceof PercentageLiteral)
        {
            int value = scalar.value * ((PercentageLiteral) expression).value;
            return new PercentageLiteral(value);
        }
        if (expression instanceof PixelLiteral)
        {
            int value = scalar.value * ((PixelLiteral) expression).value;
            return new PixelLiteral(value);
        }
        return null;
    }
}
