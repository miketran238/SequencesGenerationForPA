package parser;
import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.CreationReference;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.IntersectionType;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NameQualifiedType;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeMethodReference;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;

//extends ASTVisitor

/**
 * @author Mike
 * Original from StatTypeVisitor to ParameterVisitor
 * Now adapts to fit Program AutoRepair
 * Space between tokens always generated at the end of one token
 */
public class PA_Visitor extends ASTVisitor {


	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
	private static final boolean TOKEN_SIMPLE_NAME = true;
	private static final boolean TOKEN_TYPE = true;
	private static final boolean USE_SIMPLE_METHOD_NAME = false;
	private String className, superClassName;
	private int numOfExpressions = 0, numOfResolvedExpressions = 0;
	private StringBuilder fullTokens = new StringBuilder();
	private String fullSequence = null;
	private String[] fullSequenceTokens;
	private HashMap<ASTNode, String> abstractSequences;

	public PA_Visitor(String className, String superClassName) {
		super(false);
		this.className = className;
		this.superClassName = superClassName;
		this.abstractSequences = new HashMap<ASTNode, String>();
	}

	public String[] getFullSequenceTokens() {
		if (fullSequenceTokens == null)
			buildFullSequence();
		return fullSequenceTokens;
	}

	public String getFullSequence() {
		if (fullSequence == null)
			buildFullSequence();
		return fullSequence;
	}

	private void buildFullSequence() {
		ArrayList<String> parts = buildSequence(fullTokens);
		this.fullSequence = parts.get(0);
		this.fullSequenceTokens = new String[parts.size() - 1];
		for (int i = 1; i < parts.size(); i++)
			this.fullSequenceTokens[i - 1] = parts.get(i);
	}

	private ArrayList<String> buildSequence(StringBuilder tokens) {
		tokens.append(" ");
		ArrayList<String> l = new ArrayList<>();
		StringBuilder sequence = new StringBuilder(), token = null;
		for (int i = 0; i < tokens.length(); i++) {
			char ch = tokens.charAt(i);
			if (ch == ' ') {
				if (token != null) {
					String t = token.toString();
					l.add(t);
					sequence.append(t + " ");
					token = null;
				}
			} else {
				if (token == null)
					token = new StringBuilder();
				token.append(ch);
			}
		}
		l.add(0, sequence.toString());
		return l;
	}

	public int getNumOfExpressions() {
		return numOfExpressions;
	}

	public int getNumOfResolvedExpressions() {
		return numOfResolvedExpressions;
	}

	private Type getType(VariableDeclarationFragment node) {
		ASTNode p = node.getParent();
		if (p instanceof VariableDeclarationExpression)
			return ((VariableDeclarationExpression) p).getType();
		if (p instanceof VariableDeclarationStatement)
			return ((VariableDeclarationStatement) p).getType();
		return null;
	}

	static String getUnresolvedType(Type type) {
		if (type.isArrayType()) {
			ArrayType t = (ArrayType) type;
			return getUnresolvedType(t.getElementType())
					+ getDimensions(t.getDimensions());
		} else if (type.isIntersectionType()) {
			IntersectionType it = (IntersectionType) type;
			@SuppressWarnings("unchecked")
			ArrayList<Type> types = new ArrayList<>(it.types());
			String s = getUnresolvedType(types.get(0));
			for (int i = 1; i < types.size(); i++)
				s += " & " + getUnresolvedType(types.get(i));
			return s;
		} else if (type.isParameterizedType()) {
			ParameterizedType t = (ParameterizedType) type;
			return getUnresolvedType(t.getType());
		} else if (type.isUnionType()) {
			UnionType it = (UnionType) type;
			@SuppressWarnings("unchecked")
			ArrayList<Type> types = new ArrayList<>(it.types());
			String s = getUnresolvedType(types.get(0));
			for (int i = 1; i < types.size(); i++)
				s += " | " + getUnresolvedType(types.get(i));
			return s;
		} else if (type.isNameQualifiedType()) {
			NameQualifiedType qt = (NameQualifiedType) type;
			return qt.getQualifier().getFullyQualifiedName() + "."
			+ qt.getName().getIdentifier();
		} else if (type.isPrimitiveType()) {
			return type.toString();
		} else if (type.isQualifiedType()) {
			QualifiedType qt = (QualifiedType) type;
			return getUnresolvedType(qt.getQualifier()) + "."
			+ qt.getName().getIdentifier();
		} else if (type.isSimpleType()) {
			return type.toString();
		} else if (type.isWildcardType()) {
			WildcardType wt = (WildcardType) type;
			String s = "?";
			if (wt.getBound() != null) {
				if (wt.isUpperBound())
					s += "extends ";
				else
					s += "super ";
				s += getUnresolvedType(wt.getBound());
			}
			return s;
		}

		return null;
	}

	private static String getDimensions(int dimensions) {
		String s = "";
		for (int i = 0; i < dimensions; i++)
			s += "[]";
		return s;
	}

	public static String getResolvedType(Type type) {
		ITypeBinding tb = type.resolveBinding();
		if (tb == null || tb.isRecovered())
			return getUnresolvedType(type);
		tb = tb.getTypeDeclaration();
		if (tb.isLocal() || tb.getQualifiedName().isEmpty())
			return getUnresolvedType(type);
		if (type.isArrayType()) {
			ArrayType t = (ArrayType) type;
			return getResolvedType(t.getElementType())
					+ getDimensions(t.getDimensions());
		} else if (type.isIntersectionType()) {
			IntersectionType it = (IntersectionType) type;
			@SuppressWarnings("unchecked")
			ArrayList<Type> types = new ArrayList<>(it.types());
			String s = getResolvedType(types.get(0));
			for (int i = 1; i < types.size(); i++)
				s += " & " + getResolvedType(types.get(i));
			return s;
		} else if (type.isParameterizedType()) {
			ParameterizedType t = (ParameterizedType) type;
			return getResolvedType(t.getType());
		} else if (type.isUnionType()) {
			UnionType it = (UnionType) type;
			@SuppressWarnings("unchecked")
			ArrayList<Type> types = new ArrayList<>(it.types());
			String s = getResolvedType(types.get(0));
			for (int i = 1; i < types.size(); i++)
				s += " | " + getResolvedType(types.get(i));
			return s;
		} else if (type.isNameQualifiedType()) {
			return tb.getQualifiedName();
		} else if (type.isPrimitiveType()) {
			return type.toString();
		} else if (type.isQualifiedType()) {
			return tb.getQualifiedName();
		} else if (type.isSimpleType()) {
			return tb.getQualifiedName();
		} else if (type.isWildcardType()) {
			WildcardType wt = (WildcardType) type;
			String s = "?";
			if (wt.getBound() != null) {
				if (wt.isUpperBound())
					s += "extends ";
				else
					s += "super ";
				s += getResolvedType(wt.getBound());
			}
			return s;
		}

		return null;
	}

	@Override
	public void preVisit(ASTNode node) {
		if (node instanceof Expression) {
			numOfExpressions++;
			Expression e = (Expression) node;
			if (e.resolveTypeBinding() != null
					&& !e.resolveTypeBinding().isRecovered())
				numOfResolvedExpressions++;
		} else if (node instanceof Statement) {
			if (node instanceof ConstructorInvocation) {
				numOfExpressions++;
				if (((ConstructorInvocation) node).resolveConstructorBinding() != null
						&& !((ConstructorInvocation) node)
						.resolveConstructorBinding().isRecovered())
					numOfResolvedExpressions++;
			} else if (node instanceof SuperConstructorInvocation) {
				numOfExpressions++;
				if (((SuperConstructorInvocation) node)
						.resolveConstructorBinding() != null
						&& !((SuperConstructorInvocation) node)
						.resolveConstructorBinding().isRecovered())
					numOfResolvedExpressions++;
			}
		} else if (node instanceof Type) {
			numOfExpressions++;
			Type t = (Type) node;
			if (t.resolveBinding() != null && !t.resolveBinding().isRecovered())
				numOfResolvedExpressions++;
		}
	}

	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(ArrayAccess node) {
		Expression a = node.getArray(); //the array
		Expression e1 = node.getIndex(); //the element
		// In definition, ArrayAccess has only one dimension
		a.accept(this);
		this.fullTokens.append("[ "); 
		e1.accept(this);
		this.fullTokens.append(" ] ");
		return false;
	}

	@Override
	public boolean visit(ArrayCreation node) {
		// String utype = getUnresolvedType(node.getType()), rtype =
		// getResolvedType(node.getType());
		// this.partialTokens.append(" new " + utype + " ");
		// this.fullTokens.append(" new " + rtype + " ");
		this.fullTokens.append("new ");
		//		String type = node.getType().resolveBinding().getQualifiedName();
		String type = getReturnType(node);
		//type = type.substring(0,type.length()-2) + " "; //Remove one square bracelet
		this.fullTokens.append(type);
		//initializer = {f}
		if (node.getInitializer() != null)
		{
			this.fullTokens.append("[ ] ");
			node.getInitializer().accept(this);
		}
		//No initializer --> goes through all the dimension
		else
		{
			for (int i = 0; i < node.dimensions().size(); i++)
			{
				this.fullTokens.append("[ ");
				((Expression) (node.dimensions().get(i))).accept(this);
				this.fullTokens.append("] ");
			}
		}
		return false;
	}

	@Override
	public boolean visit(ArrayInitializer node) {
		this.fullTokens.append("{ ");
		//		for( ASTNode e : ((NodeList)((ArrayInitializer)node).expressions()))
		//		{
		//			e.accept(this);
		//			System.out.println(e.resolveTypeBinding().getQualifiedName());
		//			this.fullTokens.append(", ");
		//		}
		for( int i = 0; i < node.expressions().size(); i++ )
		{
			((ASTNode) node.expressions().get(i)).accept(this);
		}
		//return false;
		this.fullTokens.append("} ");
		return false;
	}


	@Override
	public boolean visit(ArrayType node) {
		String type = node.getElementType().resolveBinding().getQualifiedName();
		System.out.println(type);
		return false;
	}

	@Override
	public boolean visit(AssertStatement node) {
		this.fullTokens.append("assert ");
		// this.partialTokens.append(" assert ");
		node.getExpression().accept(this);
		this.fullTokens.append("; ");
		return false;
	}

	@Override
	public boolean visit(Assignment node) {
		node.getLeftHandSide().accept(this);
		this.fullTokens.append(" = ");
		node.getRightHandSide().accept(this);
		return false;
	}

	@Override
	public boolean visit(Block node) {
		//		for (Statement s: (ArrayList<Statement>) node.statements())
		//		{
		//			s.accept(this);
		//		}
		//Don't add parenthesis if it is methoddeclaration
		if ( node.getParent() instanceof MethodDeclaration )
		{
			return super.visit(node);
		}
		this.fullTokens.append("{ ");
		for( int i = 0; i < node.statements().size(); i++)
		{
			((ASTNode) node.statements().get(i)).accept(this);
		}
		this.fullTokens.append("} ");
		return false;
	}

	@Override
	public boolean visit(BooleanLiteral node) {
		this.fullTokens.append("bool_lit ");
		return false;
	}

	@Override
	public boolean visit(BreakStatement node) {
		String toAppend = node.getLabel() == null ? "" : node.getLabel().getIdentifier() + " ";
		this.fullTokens.append("break " + toAppend + "; ");
		return false;
	}

	@Override
	public boolean visit(CastExpression node) {
		CastExpression obj = (CastExpression) node;
		//		String result = " (" + resolveType(obj.getType().resolveBinding()) + ") ";
		String result = " (" + getReturnType(node) + ") ";
		this.fullTokens.append(result);
		node.getExpression().accept(this);
		return false;
	}

	@Override
	//catch ( R(e) ) { R(b1) }
	public boolean visit(CatchClause node) {
		this.fullTokens.append("catch ( ");
		node.getException().accept(this);
		this.fullTokens.append(") ");
		node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(CharacterLiteral node) {
		this.fullTokens.append("char_lit ");
		return false;
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		//		ITypeBinding tb = node.getType().resolveBinding();
		//		if (tb != null && tb.getTypeDeclaration().isLocal())
		//			return false;
		//		String utype = getUnresolvedType(node.getType()), rtype = getResolvedType(node
		//				.getType());
		//		this.fullTokens.append(rtype + "() ");
		//		// this.partialTokens.append(" new " + utype + "() ");
		//		if (node.getAnonymousClassDeclaration() != null)
		//			node.getAnonymousClassDeclaration().accept(this);
		//		
		//		for (int i = 0; i < node.arguments().size(); i++){
		//			String result=getS((ASTNode) node.arguments().get(i));
		//			ASTNode nodeReturnType=(ASTNode)node.arguments().get(i);
		//			String strReturnType=getReturnType(nodeReturnType);
		//			if(!strReturnType.isEmpty()){	
		//				this.fullTokens.append(" " + strReturnType + "|||"+result+" ");
		//			}
		//		}

		if ( node.getExpression() != null)
			node.getExpression().accept(this);
		this.fullTokens.append("new ");
		//		ITypeBinding tb = node.getType().resolveBinding();
		//		String type = tb.getName();
		String type = getReturnType(node);
		this.fullTokens.append(type + " ( ");
		for (int i = 0; i < node.arguments().size(); i++)
		{
			((ASTNode) node.arguments().get(i)).accept(this);
			if ( i < node.arguments().size()-1 )
			{
				this.fullTokens.append(", ");
			}
		}
		this.fullTokens.append(") ");
		return false;
	}

	@Override
	public boolean visit(ConditionalExpression node) 
	{	
		node.getExpression().accept(this);
		this.fullTokens.append("? ");
		node.getThenExpression().accept(this);
		this.fullTokens.append(": ");
		node.getElseExpression().accept(this);
		//Mark the end
		//this.fullTokens.append("; ");
		return false;
	}

	@Override
	public boolean visit(ConstructorInvocation node) {
		String name = getReturnType(node);
		this.fullTokens.append(name + " this  ( ");
		for (int i = 0; i < node.arguments().size(); i++)
		{
			((ASTNode) node.arguments().get(i)).accept(this);
			if ( i < node.arguments().size()-1)
			{
				this.fullTokens.append(", ");
			}
		}
		this.fullTokens.append(") ");
		return false;
	}

	@Override
	public boolean visit(ContinueStatement node) {
		String toAppend = node.getLabel() == null ? "" : node.getLabel().getIdentifier() + " ";
		this.fullTokens.append("continue " + toAppend + "; ");
		return false;
	}

	@Override
	public boolean visit(CreationReference node) {
		return false;
	}

	@Override
	public boolean visit(Dimension node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(DoStatement node) {
		this.fullTokens.append("do ");
		((ASTNode) node.getBody()).accept(this);
		this.fullTokens.append("while (");
		((ASTNode) node.getExpression()).accept(this);
		//Mark the end
		this.fullTokens.append(") ");
		return false;
	}

	@Override
	public boolean visit(EmptyStatement node) {
		return false;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		//for ( R(f1) : R(e1) ) R(stmt)
		this.fullTokens.append("for ( ");
		node.getParameter().accept(this);
		this.fullTokens.append(": ");
		node.getExpression().accept(this);
		this.fullTokens.append(") ");
		node.getBody().accept(this);
		//Mark the end
		//this.fullTokens.append("} ");
		return super.visit(node);
	}

	@Override
	public boolean visit(EnumConstantDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(ExpressionMethodReference node) {
		return false;
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		node.getExpression().accept(this);
		this.fullTokens.append("; ");
		return false;
	}

	@Override
	public boolean visit(FieldAccess node) {
		//R(e) Type(e).f
		ITypeBinding tb = node.resolveTypeBinding();

		Expression exp=node.getExpression();
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
		}
		//		IVariableBinding vb = node.resolveFieldBinding();
		//		String typeE = "";
		//		if(vb != null)
		//		{
		//			typeE = vb.getDeclaringClass().getName();
		//		}

		//		//Take only simplename instead of FQN
		//		typeE = typeE.substring(typeE.lastIndexOf('.')+1);
		//		
		//		typeE = getReturnType(node);
		String typeE = "";
		String result = typeE
				+ " . " +node.getName().getIdentifier();
		this.fullTokens.append(result + " ");
		return false;
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		//		this.fullTokens.append(node.getType().resolveBinding().getName() + " ");
		this.fullTokens.append(getReturnType(node));
		for( int i = 0; i < node.fragments().size(); i++ )
		{
			((ASTNode) node.fragments().get(i)).accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(ForStatement node) {
		this.fullTokens.append("for ( ");
		for( int i = 0; i < node.initializers().size(); i++ )
		{
			((ASTNode) node.initializers().get(i)).accept(this);
		}
		this.fullTokens.append("; ");
		if (node.getExpression() != null) 
			node.getExpression().accept(this);
		this.fullTokens.append("; ");
		for( int i = 0; i < node.updaters().size(); i++ )
		{
			((ASTNode) node.updaters().get(i)).accept(this);
		}
		this.fullTokens.append(") ");
		node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(IfStatement node) {
		this.fullTokens.append("if ( ");
		node.getExpression().accept(this);
		this.fullTokens.append(") ");
		node.getThenStatement().accept(this);
		if ( node.getElseStatement() != null )
		{
			this.fullTokens.append("else ");
			if (!(node.getElseStatement() instanceof Block))
			{
				this.fullTokens.append("{ ");
				node.getElseStatement().accept(this);
				this.fullTokens.append("} ");
			}
			else
				node.getElseStatement().accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(InfixExpression node) {
		node.getLeftOperand().accept(this);
		node.getRightOperand().accept(this);
		return false;
	}

	@Override
	public boolean visit(Initializer node) {
		node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(InstanceofExpression node) {
		node.getLeftOperand().accept(this);
		this.fullTokens.append("instanceof ");
		//		String rtype = getResolvedType(node.getRightOperand());
		//		rtype = rtype.substring(rtype.lastIndexOf('.')+1);
		String rtype = getReturnType(node);
		this.fullTokens.append(rtype + " ");
		return false;
	}

	@Override
	public boolean visit(LabeledStatement node) {
		this.fullTokens.append(node.getLabel().getIdentifier() + " : ");
		node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(LambdaExpression node) {
		return false;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		if (node.getBody() != null && !node.getBody().statements().isEmpty())
			node.getBody().accept(this);
		return false;
	}

	@Override
	//R(e) . m ( R(e1),..., R(en) )
	public boolean visit(MethodInvocation node) {
		//R(e)
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
		} 
		this.fullTokens.append("." + node.getName().getIdentifier() + " ( ");
		//R(e1) ... R(en)
		for (int i = 0; i < node.arguments().size(); i++)
		{
			((ASTNode) node.arguments().get(i)).accept(this);
			if ( i <node.arguments().size()-1 )
			{
				this.fullTokens.append(", ");
			}
		}
		this.fullTokens.append(") ");
		return false;
	}

	@Override
	public boolean visit(Modifier node) {
		return false;
	}

	@Override
	public boolean visit(NormalAnnotation node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(NullLiteral node) {
		// this.fullTokens.append(" null ");
		// this.partialTokens.append(" null ");
		this.fullTokens.append("null ");
		return false;
	}

	@Override
	public boolean visit(NumberLiteral node) {
		// this.fullTokens.append(" number ");
		// this.partialTokens.append(" number ");
		this.fullTokens.append("number_lit ");
		return false;
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(ParenthesizedExpression node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(PostfixExpression node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(PrefixExpression node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(QualifiedName node) {
		String result = "";
		result = node.getQualifier().toString();
		this.fullTokens.append(result + " . " + node.getName().getIdentifier() + " ");
		return false;
	}

	@Override
	public boolean visit(ReturnStatement node) {
		this.fullTokens.append("return ");
		if ( node.getExpression()!= null)
			node.getExpression().accept(this);
		this.fullTokens.append("; ");
		return super.visit(node);
	}

	@Override
	public boolean visit(SimpleName node) {
		String result = getReturnType(node) + " ";
		//		String result = node.resolveTypeBinding().getName() + " ";
		if ( !result.equals("") )
		{
			result += " ";
		}
		if( TOKEN_SIMPLE_NAME )
			result = result + node.getIdentifier() + " ";
		this.fullTokens.append(result);
		return false;
	}

	@Override
	public boolean visit(SingleMemberAnnotation node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		//Type(id) id = R(e1)
		//		String type = node.getType().resolveBinding().getName();
		String type = getReturnType(node);
		type = type.isEmpty()? "" : " ";
		String result = type + node.getName().getIdentifier() + " ";
		this.fullTokens.append(result);
		if (node.getInitializer() != null) {
			// this.partialTokens.append("= ");
			this.fullTokens.append("= ");
			node.getInitializer().accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(StringLiteral node) {
		this.fullTokens.append("string_lit ");
		return false;
	}

	@Override
	//Type(P) super ( R(e1) ... R(en) )
	//*P is the type of super class
	public boolean visit(SuperConstructorInvocation node) 
	{
		IMethodBinding b = node.resolveConstructorBinding();
		ITypeBinding tb = null;
		if (b != null && b.getDeclaringClass() != null)
			tb = b.getDeclaringClass().getTypeDeclaration();
		if (tb != null) {
			if (tb.isLocal() || tb.getQualifiedName().isEmpty())
				return false;
		}
		String name = superClassName;
		// this.partialTokens.append(" " + name + " ");
		if (tb != null)
			name = getName(tb);
		this.fullTokens.append(name + " super  ( ");
		for (int i = 0; i < node.arguments().size(); i++)
		{
			((ASTNode) node.arguments().get(i)).accept(this);
			if ( i < node.arguments().size()-1)
			{
				this.fullTokens.append(", ");
			}
		}
		this.fullTokens.append(") ");
		return false;
	}

	@Override
	public boolean visit(SuperFieldAccess node) {
		//		IVariableBinding b = node.resolveFieldBinding();
		//		ITypeBinding tb = null;
		//		if (b != null && b.getDeclaringClass() != null) {
		//			tb = b.getDeclaringClass().getTypeDeclaration();
		//			if (tb.isLocal() || tb.getQualifiedName().isEmpty())
		//				return false;
		//			// this.partialTokens.append(" " + getName(tb) + " ");
		//			// this.fullTokens.append(" " + getQualifiedName(tb) + " ");
		//		} else {
		//			// this.partialTokens.append(" super ");
		//			// this.fullTokens.append(" super ");
		//		}
		//		String name = "." + node.getName().getIdentifier();
		//		// this.partialTokens.append(" " + name + " ");
		//		if (tb != null)
		//			name = getQualifiedName(tb) + name;
		//		// this.fullTokens.append(" " + name + " ");
		//		return false;
		String name = "super . " +  node.getName().getIdentifier();
		this.fullTokens.append(name + " ");
		return false;
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		//		IMethodBinding b = node.resolveMethodBinding();
		//		ITypeBinding tb = null;
		//		if (b != null && b.getDeclaringClass() != null)
		//			tb = b.getDeclaringClass().getTypeDeclaration();
		//		if (tb != null) {
		//			if (tb.isLocal() || tb.getQualifiedName().isEmpty())
		//				return false;
		//			// this.partialTokens.append(" " + getName(tb) + " ");
		//			this.fullTokens.append(" " + getQualifiedName(tb) + " ");
		//		} else {
		//			// this.partialTokens.append(" super ");
		//			this.fullTokens.append(" super ");
		//		}
		this.fullTokens.append("super . " + node.getName().getIdentifier() + " ( ");
		for (int i = 0; i < node.arguments().size(); i++)
		{
			((ASTNode) node.arguments().get(i)).accept(this);
			if ( i <node.arguments().size()-1 )
			{
				this.fullTokens.append(", ");
			}
		}
		this.fullTokens.append(") ");
		return false;
	}

	@Override
	public boolean visit(SuperMethodReference node) {
		return false;
	}

	@Override
	public boolean visit(SwitchCase node) {

		if ( node.isDefault())
		{
			this.fullTokens.append("default ");
		}
		else
		{
			this.fullTokens.append("case ");
			node.getExpression().accept(this);
		}
		this.fullTokens.append(": ");
		return false;
	}

	@Override
	public boolean visit(SwitchStatement node) {
		this.fullTokens.append("switch ( ");
		node.getExpression().accept(this);
		this.fullTokens.append(") { ");
		for(int i = 0; i < node.statements().size(); i++ )
		{
			((ASTNode) node.statements().get(i)).accept(this);
			this.fullTokens.append("; ");
		}
		this.fullTokens.append("} ");
		return false;
	}

	@Override
	public boolean visit(SynchronizedStatement node) {
		this.fullTokens.append("synchronized ( ");
		node.getExpression().accept(this);
		this.fullTokens.append(") ");
		node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(ThisExpression node) {
		String className = getReturnType(node);
		this.fullTokens.append(className + "this ");
		//		this.fullTokens.append(" . this ");
		return false;
	}

	@Override
	public boolean visit(ThrowStatement node) {
		this.fullTokens.append("throw ");
		node.getExpression().accept(this);
		this.fullTokens.append("; ");
		return false;
	}

	@Override
	public boolean visit(TryStatement node) {
		this.fullTokens.append("try ");
		node.getBody().accept(this);
		if ( node.catchClauses() != null )
		{
			for(int i = 0; i < node.catchClauses().size(); i++)
			{
				((ASTNode) node.catchClauses().get(i)).accept(this);
			}
		}
		if ( node.getFinally() != null )
		{
			this.fullTokens.append("finally ");
			node.getFinally().accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclarationStatement node) {
		return false;
	}

	@Override
	public boolean visit(TypeLiteral node) {
		return false;
	}

	@Override
	public boolean visit(TypeMethodReference node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeParameter node) {
		this.fullTokens.append(node.getName() + " extends ");
		for(int i = 0; i < node.typeBounds().size(); i++)
		{
			ITypeBinding tb = ((Type) node.typeBounds().get(i)).resolveBinding();
			this.fullTokens.append(tb.getName());
			if ( i < node.typeBounds().size()-1)
			{
				this.fullTokens.append(" , ");
			}
		}
		return false;
	}

	@Override
	public boolean visit(VariableDeclarationExpression node) {
		//		ITypeBinding tb = node.getType().resolveBinding();

		//		if (tb != null && tb.getTypeDeclaration().isLocal())
		//			return false;
		// String utype = getUnresolvedType(node.getType()), rtype =
		// getResolvedType(node.getType());
		// this.partialTokens.append(" " + utype + " ");
		// this.fullTokens.append(" " + rtype + " ");

		//		this.fullTokens.append(tb.getName() + " ");
		String type = getReturnType(node);
		type = type.isEmpty()? "" : type + " ";
		this.fullTokens.append(type);
		for (int i = 0; i < node.fragments().size(); i++)
			((ASTNode) node.fragments().get(i)).accept(this);
		return false;
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		//		ITypeBinding tb = node.getType().resolveBinding();

		//		if (tb != null && tb.getTypeDeclaration().isLocal())
		//			return false;
		// String utype = getUnresolvedType(node.getType()), rtype =
		// getResolvedType(node.getType());
		// this.partialTokens.append(" " + utype + " ");
		// this.fullTokens.append(" " + rtype + " ");

		//		this.fullTokens.append(tb.getName() + " ");
		String type = getReturnType(node);
		type = type.isEmpty()? "" : type + " ";
		this.fullTokens.append(type);
		for (int i = 0; i < node.fragments().size(); i++)
			((ASTNode) node.fragments().get(i)).accept(this);
		return false;
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		this.fullTokens.append(node.getName());
		if (node.getInitializer() != null) {
			this.fullTokens.append(" = ");
			node.getInitializer().accept(this);
		}
		else
			this.fullTokens.append(" ");
		return false;
	}

	@Override
	public boolean visit(WhileStatement node) {
		this.fullTokens.append("while ( ");
		node.getExpression().accept(this);
		this.fullTokens.append(") ");
		node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(IntersectionType node) {
		for( int i = 0; i < node.types().size(); i++ )
		{
			this.fullTokens.append(
					((Type)node.types().get(i)).resolveBinding().getName());
			if ( i < node.types().size()-1 )
			{
				this.fullTokens.append("& ");
			}
		}
		return false;
	}

	@Override
	public boolean visit(ParameterizedType node) {
		node.getType().accept(this);
		this.fullTokens.append("< ");
		for(int i = 0; i < node.typeArguments().size(); i++ )
		{
			((ASTNode) node.typeArguments().get(i)).accept(this);
			if ( i < node.typeArguments().size()-1)
			{
				this.fullTokens.append(", ");
			}
		}
		this.fullTokens.append("> ");
		return false;
	}

	@Override
	public boolean visit(UnionType node) {
		for(int i = 0; i < node.types().size(); i++)
		{
			ITypeBinding tb = ((Type) node.types().get(i)).resolveBinding();
			this.fullTokens.append(tb.getName());
			if ( i < node.types().size()-1)
			{
				this.fullTokens.append(" | ");
			}
		}
		return false;
	}

	@Override
	public boolean visit(NameQualifiedType node) {
		return false;
	}

	@Override
	public boolean visit(PrimitiveType node) {
		this.fullTokens.append(node.getPrimitiveTypeCode().toString());
		return false;
	}

	@Override
	public boolean visit(QualifiedType node) {
		//		String type = node.getQualifier().resolveBinding().getName();
		String type = getReturnType(node);
		this.fullTokens.append(type + " . "
				+ node.getName().getIdentifier() + " ");
		return false;
	}

	@Override
	public boolean visit(SimpleType node) {
		this.fullTokens.append(node.getName() + " ");
		return false;
	}

	@Override
	public boolean visit(WildcardType node) {
		this.fullTokens.append("? ");
		if ( node.isUpperBound() )
		{
			this.fullTokens.append("extends" + 
					node.getBound().resolveBinding().getName() + " ");
		}
		else
		{
			this.fullTokens.append("super" + 
					node.getBound().resolveBinding().getName() + " ");
		}

		return false;
	}

	private String resolveType(ITypeBinding tb) {
		String result = "";
		if(tb==null){
			return "";
		}
		if (tb.isArray())
			return getQualifiedName(tb.getComponentType().getTypeDeclaration())
					+ getDimensions(tb.getDimensions());
		return tb.getQualifiedName();
	}

	// need to override
	private String getQualifiedName(ITypeBinding tb) {

		if (tb.isArray())
			return getQualifiedName(tb.getComponentType().getTypeDeclaration())
					+ getDimensions(tb.getDimensions());
		return tb.getQualifiedName();
	}

	private String getName(ITypeBinding tb) {
		if (tb.isArray())
			return getName(tb.getComponentType().getTypeDeclaration())
					+ getDimensions(tb.getDimensions());
		return tb.getName();
	}

	public String getS(ASTNode node) {
		String cachedResult = abstractSequences.get(node);
		String result = "";
		if (cachedResult != null) {
			return cachedResult;
		} else if (node instanceof SimpleName) 
		{
			SimpleName obj = (SimpleName) node;
			IBinding b = obj.resolveBinding();
			if (b != null) {
				if (b instanceof IVariableBinding) {
					IVariableBinding vb = (IVariableBinding) b;
					ITypeBinding tb = vb.getType();
					if (tb != null) {
						tb = tb.getTypeDeclaration();
						if (tb.isLocal() || tb.getQualifiedName().isEmpty())
							return "";
						return resolveType(tb) + "#var";
						// this.partialTokens.append(" " + getName(tb) + " ");
					}
				} else if (b instanceof ITypeBinding) {
					ITypeBinding tb = (ITypeBinding) b;
					tb = tb.getTypeDeclaration();
					if (tb.isLocal() || tb.getQualifiedName().isEmpty())
						return "";
					return getQualifiedName(tb) + "#var";
				}
			} else {
				return "";
				// this.partialTokens.append(" " + node.getIdentifier() + " ");
			}
		} else if (node instanceof MethodInvocation) {
			MethodInvocation obj = (MethodInvocation) node;
			IMethodBinding b = obj.resolveMethodBinding();
			ITypeBinding tb = null;
			if (b != null) {
				tb = b.getDeclaringClass();
				if (tb != null) {
					tb = tb.getTypeDeclaration();
					if (tb.isLocal() || tb.getQualifiedName().isEmpty())
						result = "";
				}
			}
			String typeResult=resolveType(tb);

			if(typeResult.equals("")){
				result = "";
			}
			this.fullTokens.append(" ");
			// this.partialTokens.append(" ");
			String name = "";
			if (obj.getExpression() != null) {
				name += getS(obj.getExpression()) + ".";
			} 
			//String name = resolveType(tb) + "." + obj.getName().getIdentifier() + "()";
			name += resolveType(tb) + "." + obj.getName().getIdentifier() + "()";
			result = name;

		} else if (node instanceof ClassInstanceCreation) {
			ClassInstanceCreation obj = (ClassInstanceCreation) node;
			ITypeBinding tb = obj.resolveTypeBinding();
			result = resolveType(tb);
		} else if (node instanceof ConstructorInvocation) {
			result = "this()";
		} else if (node instanceof SuperConstructorInvocation) {
			result = "super()";
		} else if (node instanceof QualifiedName) {
			//			QualifiedName obj = (QualifiedName) node;
			//			ITypeBinding tb = obj.resolveTypeBinding();
			//			result = resolveType(tb) + "|||"
			//					+ resolveType(obj.getName().resolveTypeBinding())+"|||"+obj.getName().getIdentifier();
			QualifiedName obj = (QualifiedName) node;
			//ITypeBinding tb = obj.resolveTypeBinding();
			ITypeBinding pb = obj.getQualifier().resolveTypeBinding();
			result = resolveType(pb) + "."
					+ 		obj.getName().getIdentifier();

		} else if (node instanceof FieldAccess) {
			// S(e).f
			FieldAccess obj = (FieldAccess) node;
			ITypeBinding tb = obj.resolveTypeBinding();
			IVariableBinding vb = obj.resolveFieldBinding();
			result = resolveType(tb) + "."
					//					+ resolveType(vb.getDeclaringClass())
					+ obj.getName().getIdentifier();

		} else if (node instanceof ArrayAccess) {
			// S(a)[n]
			int dim = 1;
			ArrayAccess obj = (ArrayAccess) node;
			Expression exp = obj.getArray();

			while(exp instanceof ArrayAccess)
			{
				exp = ((ArrayAccess) exp).getArray();
				if(exp !=null)
				{
					dim++;
				}
			}
			result += getS(exp);
			result = result.replace("[]", "");
			// In definition, ArrayAccess has only one dimension
			//result = resolveType(exp.resolveTypeBinding()) + "[1]";
			result += "[" + dim + "]";
		} else if (node instanceof CastExpression) {
			CastExpression obj = (CastExpression) node;
			// (Type(TypeName))S(e)
			result = "(" + resolveType(obj.getType().resolveBinding()) + ")"
					//+ resolveType(obj.getExpression().resolveTypeBinding());
					+ getS(obj.getExpression());
		} else if (node instanceof StringLiteral) {
			result = "String#lit";
		} else if (node instanceof NumberLiteral) {
			result = "Number#lit";
		} else if (node instanceof CharacterLiteral) {
			result = "Character#lit";
		} else if (node instanceof TypeLiteral) {
			TypeLiteral obj = (TypeLiteral) node;
			result = resolveType(obj.resolveTypeBinding()) + ".class#lit";
		} else if (node instanceof BooleanLiteral) {
			result = "Boolean#lit";
		} else if (node instanceof NullLiteral) {
			result = "Null#lit";
		}
		abstractSequences.put(node, result);
		return result;
	}


	public String getReturnType(ASTNode node) {
		if ( !TOKEN_TYPE )
		{
			return "";
		}
		else 
		{
			String result = "";
			ITypeBinding tb=null;
			if (node instanceof SimpleName) {
				SimpleName obj = (SimpleName) node;
				tb = obj.resolveTypeBinding();
			} else if (node instanceof SingleVariableDeclaration) {
				tb = ((SingleVariableDeclaration) node).getType().resolveBinding();
			} else if (node instanceof MethodInvocation) {
				MethodInvocation obj = (MethodInvocation) node;
				IMethodBinding mb = null;
				mb = obj.resolveMethodBinding();
				if (mb != null)
				{
					tb =  mb.getReturnType();
				}

			} else if (node instanceof ClassInstanceCreation) {
				ClassInstanceCreation obj = (ClassInstanceCreation) node;
				tb = obj.resolveTypeBinding();
			} else if (node instanceof ConstructorInvocation) {
				IMethodBinding b = ((ConstructorInvocation) node).resolveConstructorBinding();
				if (b != null && b.getDeclaringClass() != null)
					tb = b.getDeclaringClass().getTypeDeclaration();
				if (tb != null) {
					if (tb.isLocal() || tb.getQualifiedName().isEmpty())
						return "";
				}
				if (tb != null)
					result = getName(tb);
				return result;

			} else if (node instanceof SuperConstructorInvocation) {
				result = "super()";
				return result;
			} else if (node instanceof QualifiedName) {
				QualifiedName obj = (QualifiedName) node;
				tb = obj.resolveTypeBinding();
			} else if (node instanceof QualifiedType) {
				Type obj = ((QualifiedType) node).getQualifier();
				tb = obj.resolveBinding();

			} else if (node instanceof FieldAccess) {
				FieldAccess obj = (FieldAccess) node;
				tb = obj.resolveTypeBinding();
				IVariableBinding vb = obj.resolveFieldBinding();
				System.out.println("FA");
				if(vb != null)
				{
					result = vb.getDeclaringClass().getName();
				}
				return result;
			} else if (node instanceof FieldDeclaration ) {
				tb = ((FieldDeclaration) node).getType().resolveBinding();
			}
			else if (node instanceof InstanceofExpression) {
				String rtype = getResolvedType(((InstanceofExpression) node).getRightOperand());
				rtype = rtype.substring(rtype.lastIndexOf('.')+1);
				return rtype;
			}
			else if (node instanceof ArrayAccess) {
				// S(a)[n]
				ArrayAccess obj = (ArrayAccess) node;
				tb=obj.resolveTypeBinding();
			} else if (node instanceof CastExpression) {
				CastExpression obj = (CastExpression) node;
				tb = obj.getType().resolveBinding();
			} else if (node instanceof StringLiteral) {
				result = "string_lit";
				return result;
			} else if (node instanceof NumberLiteral) {
				result = "number_lit";
				return result;
			} else if (node instanceof CharacterLiteral) {
				result = "char_lit";
				return result;
			} else if (node instanceof TypeLiteral) {
				TypeLiteral obj = (TypeLiteral) node;
				tb=obj.resolveTypeBinding();
			} else if (node instanceof BooleanLiteral) {
				result = "bool_lit";
				return result;
			} else if (node instanceof NullLiteral) {
				result = "null_lit";
				return result;
			} else if (node instanceof ThisExpression) {
				return className + " "; 
			} else if ( node instanceof VariableDeclarationStatement ) {
				tb = ((VariableDeclarationStatement) node).getType().resolveBinding();

			} else if ( node instanceof VariableDeclarationExpression ) {
				tb = ((VariableDeclarationExpression) node).getType().resolveBinding();
			}

			if(tb!=null){
				result=tb.getName();			
			}
			return result;
		}
	}

}
