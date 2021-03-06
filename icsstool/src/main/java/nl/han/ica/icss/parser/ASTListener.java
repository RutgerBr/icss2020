package nl.han.ica.icss.parser;

        import java.util.Stack;

        import jdk.jshell.spi.ExecutionControlProvider;
        import nl.han.ica.icss.ast.*;
        import nl.han.ica.icss.ast.literals.*;
        import nl.han.ica.icss.ast.operations.AddOperation;
        import nl.han.ica.icss.ast.operations.MultiplyOperation;
        import nl.han.ica.icss.ast.operations.SubtractOperation;
        import nl.han.ica.icss.ast.selectors.ClassSelector;
        import nl.han.ica.icss.ast.selectors.IdSelector;
        import nl.han.ica.icss.ast.selectors.TagSelector;
        import org.antlr.v4.runtime.ParserRuleContext;
        import org.antlr.v4.runtime.tree.ErrorNode;
        import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener
{

    //Accumulator attributes:
    private AST ast;

    //Use this to keep track of the parent nodes when recursively traversing the ast
    private Stack<ASTNode> currentContainer;

    public ASTListener()
    {
        ast = new AST();
        currentContainer = new Stack<>();
    }

    public AST getAST()
    {
        return ast;
    }

    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx)
    {
        Stylesheet stylesheet = new Stylesheet();
        this.ast.root = stylesheet;
        this.currentContainer.push(stylesheet);
    }

    @Override
    public void enterStylerule(ICSSParser.StyleruleContext ctx)
    {
        Stylerule stylerule = new Stylerule();
        this.currentContainer.peek().addChild(stylerule);
        this.currentContainer.push(stylerule);
    }

    @Override
    public void exitStylerule(ICSSParser.StyleruleContext ctx)
    {
        this.currentContainer.pop();
    }

    @Override
    public void enterTagSelector(ICSSParser.TagSelectorContext ctx)
    {
        Selector tagSelecter = new TagSelector(ctx.getText());
        this.currentContainer.peek().addChild(tagSelecter);
        this.currentContainer.push(tagSelecter);
    }

    @Override
    public void exitTagSelector(ICSSParser.TagSelectorContext ctx)
    {
        this.currentContainer.pop();
    }

    @Override
    public void enterClassSelector(ICSSParser.ClassSelectorContext ctx)
    {
        Selector classSelector = new ClassSelector(ctx.getText());
        this.currentContainer.peek().addChild(classSelector);
        this.currentContainer.push(classSelector);
    }

    @Override
    public void exitClassSelector(ICSSParser.ClassSelectorContext ctx)
    {
        this.currentContainer.pop();
    }

    @Override
    public void enterIdSelector(ICSSParser.IdSelectorContext ctx)
    {
        Selector idSelector = new IdSelector(ctx.getText());
        this.currentContainer.peek().addChild(idSelector);
        this.currentContainer.push(idSelector);
    }

    @Override
    public void exitIdSelector(ICSSParser.IdSelectorContext ctx)
    {
        this.currentContainer.pop();
    }

    public void enterDeclaration(ICSSParser.DeclarationContext ctx)
    {
        Declaration declaration = new Declaration(ctx.getText());
        this.currentContainer.peek().addChild(declaration);
        this.currentContainer.push(declaration);
    }

    @Override
    public void exitDeclaration(ICSSParser.DeclarationContext ctx)
    {
        this.currentContainer.pop();
    }

    @Override
    public void enterPropertyName(ICSSParser.PropertyNameContext ctx)
    {
        PropertyName propertyName = new PropertyName(ctx.getText());
        this.currentContainer.peek().addChild(propertyName);
        this.currentContainer.push(propertyName);
    }

    @Override
    public void exitPropertyName(ICSSParser.PropertyNameContext ctx)
    {
        this.currentContainer.pop();
    }

    @Override
    public void enterIfClause(ICSSParser.IfClauseContext ctx) {
        IfClause ifClause = new IfClause();
        this.currentContainer.peek().addChild(ifClause);
        this.currentContainer.push(ifClause);
    }

    @Override
    public void exitIfClause(ICSSParser.IfClauseContext ctx) {
        this.currentContainer.pop();
    }

    @Override
    public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx)
    {
        VariableAssignment variableAssignment = new VariableAssignment();
        this.currentContainer.peek().addChild(variableAssignment);
        this.currentContainer.push(variableAssignment);
    }

    @Override
    public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx)
    {
        this.currentContainer.pop();
    }

    @Override
    public void enterVariableReference(ICSSParser.VariableReferenceContext ctx)
    {
        VariableReference variableReference = new VariableReference(ctx.getText());
        this.currentContainer.peek().addChild(variableReference);
        this.currentContainer.push(variableReference);
    }

    @Override
    public void exitVariableReference(ICSSParser.VariableReferenceContext ctx)
    {
        this.currentContainer.pop();
    }

    @Override
    public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx)
    {
        ColorLiteral colorLiteral = new ColorLiteral(ctx.getText());
        this.currentContainer.peek().addChild(colorLiteral);
        this.currentContainer.push(colorLiteral);
    }

    @Override
    public void exitColorLiteral(ICSSParser.ColorLiteralContext ctx)
    {
        this.currentContainer.pop();
    }

    @Override
    public void enterBoolLiteral(ICSSParser.BoolLiteralContext ctx)
    {
        BoolLiteral boolLiteral = new BoolLiteral(ctx.getText());
        currentContainer.peek().addChild(boolLiteral);
        currentContainer.push(boolLiteral);
    }

    @Override
    public void exitBoolLiteral(ICSSParser.BoolLiteralContext ctx)
    {
        this.currentContainer.pop();
    }

    @Override
    public void enterPercentageLiteral(ICSSParser.PercentageLiteralContext ctx)
    {
        PercentageLiteral percentageLiteral = new PercentageLiteral(ctx.getText());
        this.currentContainer.peek().addChild(percentageLiteral);
        this.currentContainer.push(percentageLiteral);
    }

    @Override
    public void exitPercentageLiteral(ICSSParser.PercentageLiteralContext ctx)
    {
        this.currentContainer.pop();
    }

    @Override
    public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx)
    {
        ScalarLiteral scalarLiteral = new ScalarLiteral(ctx.getText());
        this.currentContainer.peek().addChild(scalarLiteral);
        this.currentContainer.push(scalarLiteral);
    }

    @Override
    public void exitScalarLiteral(ICSSParser.ScalarLiteralContext ctx)
    {
        this.currentContainer.pop();
    }

    @Override
    public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx)
    {
        PixelLiteral pixelLiteral = new PixelLiteral(ctx.getText());
        this.currentContainer.peek().addChild(pixelLiteral);
        this.currentContainer.push(pixelLiteral);
    }

    @Override
    public void exitPixelLiteral(ICSSParser.PixelLiteralContext ctx)
    {
        this.currentContainer.pop();
    }

    @Override
    public void enterMultiplyOperation(ICSSParser.MultiplyOperationContext ctx)
    {
        MultiplyOperation multiplyOperation = new MultiplyOperation();
        this.currentContainer.peek().addChild(multiplyOperation);
        this.currentContainer.push(multiplyOperation);
    }

    @Override
    public void exitMultiplyOperation(ICSSParser.MultiplyOperationContext ctx)
    {
        this.currentContainer.pop();
    }

    @Override
    public void enterAddOperation(ICSSParser.AddOperationContext ctx)
    {
        AddOperation addOperation = new AddOperation();
        this.currentContainer.peek().addChild(addOperation);
        this.currentContainer.push(addOperation);
    }

    @Override
    public void exitAddOperation(ICSSParser.AddOperationContext ctx)
    {
        this.currentContainer.pop();
    }

    @Override
    public void enterSubtractOperation(ICSSParser.SubtractOperationContext ctx)
    {
        SubtractOperation subtractOperation = new SubtractOperation();
        this.currentContainer.peek().addChild(subtractOperation);
        this.currentContainer.push(subtractOperation);
    }

    @Override
    public void exitSubtractOperation(ICSSParser.SubtractOperationContext ctx)
    {
        this.currentContainer.pop();
    }
}
