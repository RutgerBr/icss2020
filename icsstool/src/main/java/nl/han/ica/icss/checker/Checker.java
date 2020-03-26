package nl.han.ica.icss.checker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.*;

public class Checker
{
    private static final int GLOBAL_SCOPE = 0;

    private LinkedList<HashMap<String, ExpressionType>> variableTypes;
    private int currentScope = 0;

    public void check(AST ast)
    {
        variableTypes = new LinkedList<>();
        variableTypes.add(new HashMap<>());

        currentScope = 0;

        for (ASTNode node : ast.root.getChildren())
        {
            checkUndefinedVariablesInScope(node);
            checkDeclarationValue(node);
//            checkOperationOperands(node);
            checkIfStatementHasBool(node);
        }
    }

    //CH01 & CH06
    private void checkUndefinedVariablesInScope(ASTNode node)
    {
        if (node instanceof Stylerule)
        {
            currentScope++;
            variableTypes.add(new HashMap<>());
        }
        if (node instanceof VariableAssignment)
        {
            variableTypes.get(currentScope).put(((VariableAssignment) node).name.name, getExpressionType(((VariableAssignment) node).expression));
        }
        if (node instanceof VariableReference)
        {
            if (!variableTypes.get(currentScope).containsKey(((VariableReference) node).name) &&
                    !variableTypes.get(GLOBAL_SCOPE).containsKey(((VariableReference) node).name))
            {
                node.setError("Variable used but undefined in current scope");
            }
        }
        for (ASTNode nodes : node.getChildren())
        {
            checkUndefinedVariablesInScope(nodes);
        }
    }

    //CH02 werkt helaas niet volledig
//    private void checkOperationOperands(ASTNode node)
//    {
//        if (node instanceof Operation)
//        {
//            ExpressionType lhsNode = getExpressionType(((Operation) node).lhs);
//            ExpressionType rhsNode = getExpressionType(((Operation) node).rhs);
//
//            if (node instanceof MultiplyOperation)
//            {
//                if (lhsNode != ExpressionType.SCALAR && rhsNode != ExpressionType.SCALAR)
//                    node.setError("Expected SCALAR expressions in multiplication");
//            } else if (node instanceof AddOperation || node instanceof SubtractOperation)
//            {
//                if (!lhsNode.equals(rhsNode))
//                {
//                    node.setError("Expected operands to be the same expressiontype");
//                }
//            }
//        }
//        for (ASTNode nodes : node.getChildren())
//        {
//            checkOperationOperands(nodes);
//        }
//    }

    //CH03 in .g4

    //CH04
    private void checkDeclarationValue(ASTNode node)
    {
        String colorError = "Expected color literal for color declaration";
        if (node instanceof Declaration)
        {
            if (((Declaration) node).property.name.contains("color"))
            {
                if (!(((Declaration) node).expression instanceof ColorLiteral) && !(((Declaration) node).expression instanceof VariableReference))
                {
                    node.setError(colorError);
                } else
                {
                    if (((Declaration) node).expression instanceof VariableReference && !variableTypeContainsReference((VariableReference) ((Declaration) node).expression, ExpressionType.COLOR))
                    {
                        node.setError(colorError);
                    }
                }
            }
            if (((Declaration) node).property.name.contains("width") || ((Declaration) node).property.name.contains("height"))
            {
                if (((Declaration) node).expression instanceof VariableReference
                        && (variableTypeContainsReference((VariableReference) ((Declaration) node).expression, ExpressionType.SCALAR)
                        || variableTypeContainsReference((VariableReference) ((Declaration) node).expression, ExpressionType.COLOR)
                        || variableTypeContainsReference((VariableReference) ((Declaration) node).expression, ExpressionType.BOOL)))
                {
                    node.setError("Expected percentage or pixel literal for width or height declaration");
                }
            }
        }

        for (ASTNode nodes : node.getChildren())
        {
            checkDeclarationValue(nodes);
        }
    }

    //CH05
    private void checkIfStatementHasBool(ASTNode node)
    {
        if (node instanceof IfClause)
        {
            Expression conditionalExpression = ((IfClause) node).getConditionalExpression();
            if (!(conditionalExpression instanceof BoolLiteral))
            {
                if (conditionalExpression instanceof VariableReference)
                {
                    if (!(variableTypes.getFirst().get(((VariableReference) conditionalExpression).name) == ExpressionType.BOOL))
                    {
                        node.setError("If condition must be of type boolean");
                    }
                }
            }
        }
        for (ASTNode nodes : node.getChildren())
        {
            checkIfStatementHasBool(nodes);
        }
    }

    private ExpressionType getExpressionType(Expression expression)
    {
        if (expression instanceof BoolLiteral)
        {
            return ExpressionType.BOOL;
        } else if (expression instanceof ColorLiteral)
        {
            return ExpressionType.COLOR;
        } else if (expression instanceof PercentageLiteral)
        {
            return ExpressionType.PERCENTAGE;
        } else if (expression instanceof PixelLiteral)
        {
            return ExpressionType.PIXEL;
        } else if (expression instanceof ScalarLiteral)
        {
            return ExpressionType.SCALAR;
        } else if (expression instanceof VariableReference)
        {
            return variableTypes.getFirst().get(((VariableReference) expression).name);
        }
        return ExpressionType.UNDEFINED;
    }

    private boolean variableTypeContainsReference(VariableReference node, ExpressionType type)
    {
        for (HashMap variableType : variableTypes)
        {
            if (variableType.get(node.name) == type)
            {
                return true;
            }
        }
        return false;
    }
}
