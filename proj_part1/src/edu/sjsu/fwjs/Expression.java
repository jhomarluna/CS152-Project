package edu.sjsu.fwjs;

import java.util.ArrayList;
import java.util.List;

/**
IfExpr        // evaluates condition, then one branch
WhileExpr     // repeatedly evaluates condition and body
SeqExpr       // evaluates both once, returns second result
VarDeclExpr   // creates local variable
AssignExpr    // updates existing/global variable
FunctionDecl  // creates closure
FunctionApp   // evaluates function and arguments, calls closure
 */


/**
 * FWJS expressions.
 */
public interface Expression {
    /**
     * Evaluate the expression in the context of the specified environment.
     */
    public Value evaluate(Environment env);
}

// NOTE: Using package access so that all implementations of Expression
// can be included in the same file.

/**
 * FWJS constants.
 */
class ValueExpr implements Expression {
    private Value val;
    public ValueExpr(Value v) {
        this.val = v;
    }
    public Value evaluate(Environment env) {
        return this.val;
    }
}

/**
 * Expressions that are a FWJS variable.
 */
class VarExpr implements Expression {
    private String varName;
    public VarExpr(String varName) {
        this.varName = varName;
    }
    public Value evaluate(Environment env) {
        return env.resolveVar(varName);
    }
}

/**
 * A print expression.
 */
class PrintExpr implements Expression {
    private Expression exp;
    public PrintExpr(Expression exp) {
        this.exp = exp;
    }
    public Value evaluate(Environment env) {
        Value v = exp.evaluate(env);
        System.out.println(v.toString());
        return v;
    }
}
/**
 * Binary operators (+, -, *, etc).
 * Currently only numbers are supported.
 */
class BinOpExpr implements Expression {
    private Op op;
    private Expression e1;
    private Expression e2;
    public BinOpExpr(Op op, Expression e1, Expression e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    @SuppressWarnings("incomplete-switch")
    public Value evaluate(Environment env) {
        Value left = e1.evaluate(env);
        Value right = e2.evaluate(env);

        if (!(left instanceof IntVal) || !(right instanceof IntVal)) {
            throw new RuntimeException("Binary operands must be integers.");
        }

        // Our Code:
        switch(op) {
            case ADD: 
                return new IntVal(((IntVal) left).toInt() + ((IntVal) right).toInt());
            case SUBTRACT:
                return new IntVal(((IntVal) left).toInt() - ((IntVal) right).toInt());
            case MULTIPLY:
                return new IntVal(((IntVal) left).toInt() * ((IntVal) right).toInt());
            case DIVIDE:
                return new IntVal(((IntVal) left).toInt() / ((IntVal) right).toInt());
            case MOD:
                return new IntVal(((IntVal) left).toInt() % ((IntVal) right).toInt());
            case GT:
                return new BoolVal(((IntVal) left).toInt() > ((IntVal) right).toInt());
            case GE:
                return new BoolVal(((IntVal) left).toInt() >= ((IntVal) right).toInt());
            case LT:
                return new BoolVal(((IntVal) left).toInt() < ((IntVal) right).toInt());
            case LE:
                return new BoolVal(((IntVal) left).toInt() <= ((IntVal) right).toInt());
            case EQ:
                return new BoolVal(((IntVal) left).toInt() == ((IntVal) right).toInt());
            default:
                throw new RuntimeException("Unknown binary operator.");
        }
    }
}

/**
 * If-then-else expressions.
 * Unlike JS, if expressions return a value.
 */
class IfExpr implements Expression {
    private Expression cond;
    private Expression thn;
    private Expression els;
    public IfExpr(Expression cond, Expression thn, Expression els) {
        this.cond = cond;
        this.thn = thn;
        this.els = els;
    }
    public Value evaluate(Environment env) {
        // Our code: first check if the cond is BoolVal
        Value condition = cond.evaluate(env);
        if (!(condition instanceof BoolVal)) {
            throw new RuntimeException("The condition is not BoolVal");
        }
        // Retun a Value based on the condition.
        return ((BoolVal) condition).toBoolean() ? thn.evaluate(env) : els.evaluate(env);
    }
}

/**
 * While statements (treated as expressions in FWJS, unlike JS).
 */
class WhileExpr implements Expression {
    private Expression cond;
    private Expression body;
    public WhileExpr(Expression cond, Expression body) {
        this.cond = cond;
        this.body = body;
    }
    public Value evaluate(Environment env) {
        // Our code: check if cond is met, keep evaluating while it is true
        while (true) {
            // evaluate the condition
            Value condition = cond.evaluate(env);

            // if not instanceof BoolVal throw error
            if (!(condition instanceof BoolVal)) {
                throw new RuntimeException("Not a Boolean"); 
            }

            // if false, break from loop and stop evaluating
            if (!((BoolVal) condition).toBoolean()) {
                break;
            }

            // evaluate the body if condition is met
            body.evaluate(env);
        }
        return new NullVal();
    }
}

/**
 * Sequence expressions (i.e. 2 back-to-back expressions).
 */
class SeqExpr implements Expression {
    private Expression e1;
    private Expression e2;
    public SeqExpr(Expression e1, Expression e2) {
        this.e1 = e1;
        this.e2 = e2;
    }
    public Value evaluate(Environment env) {
        // evaluate the first expression, then the second, and return the value of the second expression
        e1.evaluate(env);
        return e2.evaluate(env);
    }
}

/**
 * Declaring a variable in the local scope.
 */
class VarDeclExpr implements Expression {
    private String varName;
    private Expression exp;
    public VarDeclExpr(String varName, Expression exp) {
        this.varName = varName;
        this.exp = exp;
    }
    public Value evaluate(Environment env) {
        // Our code: create the variable in the local scope
        env.createVar(varName, exp.evaluate(env));
        return new NullVal();
    }
}

/**
 * Updating an existing variable.
 * If the variable is not set already, it is added
 * to the global scope.
 */
class AssignExpr implements Expression {
    private String varName;
    private Expression e;
    public AssignExpr(String varName, Expression e) {
        this.varName = varName;
        this.e = e;
    }
    public Value evaluate(Environment env) {
        // Our code: update the variable in the environment, creating it in the global scope if it doesn't exist
        Value value = e.evaluate(env);
        env.updateVar(varName, value);
        return value;
    }
}

/**
 * A function declaration, which evaluates to a closure.
 */
class FunctionDeclExpr implements Expression {
    private List<String> params;
    private Expression body;
    public FunctionDeclExpr(List<String> params, Expression body) {
        this.params = params;
        this.body = body;
    }
    public Value evaluate(Environment env) {
        // Our Code: create and return a closure value representing the function
        return new ClosureVal(params, body, env);
    }
}

/**
 * Function application.
 */
class FunctionAppExpr implements Expression {
    private Expression f;
    private List<Expression> args;
    public FunctionAppExpr(Expression f, List<Expression> args) {
        this.f = f;
        this.args = args;
    }
    public Value evaluate(Environment env) {
        // Our Code: evaluate the function expression and its arguments, then apply the closure
        Value function = f.evaluate(env);

        if (!(function instanceof ClosureVal)) {
            throw new RuntimeException("Can only call a function.");
        }

        ClosureVal closure = (ClosureVal) function;

        List<Value> argValues = new ArrayList<Value>();

        for (Expression arg : args) {
            argValues.add(arg.evaluate(env));
        }

        return closure.apply(argValues);
            
    }
}

