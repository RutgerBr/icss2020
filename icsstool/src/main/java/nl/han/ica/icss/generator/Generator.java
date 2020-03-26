package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;

public class Generator
{
//	 Changes in startcode:
//	Added toString methods in selectors, literals, propertyName, VariableAssignment, Declaration, Stylesheet en Stylerule.
    public String generate(AST ast)
    {
        return ast.root.toString();
    }
}
