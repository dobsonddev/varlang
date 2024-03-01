package varlang;
import static varlang.AST.*;
import static varlang.Value.*;
import java.util.Arrays;

import java.util.List;
import java.util.ArrayList;

import varlang.AST.AddExp;
import varlang.AST.NumExp;
import varlang.AST.DivExp;
import varlang.AST.MultExp;
import varlang.AST.Program;
import varlang.AST.SubExp;
import varlang.AST.VarExp;
import varlang.AST.Visitor;
import varlang.Env.EmptyEnv;
import varlang.Env.ExtendEnv;
import varlang.Env.ExtendEnvList;

public class Evaluator implements Visitor<Value> {

	public void testEnvProjectMethods() {
		Env emptyEnv = new EmptyEnv();
		assert emptyEnv.isEmpty() : "EmptyEnv should be empty";
		assert !emptyEnv.hasBinding("x") : "EmptyEnv should not have bindings";

		Value val1 = new NumVal(1);
		Env extendEnv = new ExtendEnv(emptyEnv, "x", val1);
		assert !extendEnv.isEmpty() : "ExtendEnv should not be empty";
		assert extendEnv.hasBinding("x") : "ExtendEnv should have binding for 'x'";
		assert !extendEnv.hasBinding("y") : "ExtendEnv should not have binding for 'y'";

		List<String> vars = Arrays.asList("x", "y");
		List<Value> vals = Arrays.asList(val1, new NumVal(2));
		Env extendEnvList = new ExtendEnvList(emptyEnv, vars, vals);
		assert !extendEnvList.isEmpty() : "ExtendEnvList should not be empty";
		assert extendEnvList.hasBinding("x") : "ExtendEnvList should have binding for 'x'";
		assert extendEnvList.hasBinding("y") : "ExtendEnvList should have binding for 'y'";
		assert !extendEnvList.hasBinding("z") : "ExtendEnvList should not have binding for 'z'";
	}

	
	Value valueOf(Program p) {
		Env env = new EmptyEnv();
		// Value of a program in this language is the value of the expression
		return (Value) p.accept(this, env);
	}
	
	@Override
	public Value visit(AddExp e, Env env) {
		List<Exp> operands = e.all();
		double result = 0;
		for(Exp exp: operands) {
			NumVal intermediate = (NumVal) exp.accept(this, env); // Dynamic type-checking
			result += intermediate.v(); //Semantics of AddExp in terms of the target language.
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(NumExp e, Env env) {
		return new NumVal(e.v());
	}

	@Override
	public Value visit(DivExp e, Env env) {
		List<Exp> operands = e.all();
		NumVal lVal = (NumVal) operands.get(0).accept(this, env);
		double result = lVal.v(); 
		for(int i=1; i<operands.size(); i++) {
			NumVal rVal = (NumVal) operands.get(i).accept(this, env);
			result = result / rVal.v();
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(MultExp e, Env env) {
		List<Exp> operands = e.all();
		double result = 1;
		for(Exp exp: operands) {
			NumVal intermediate = (NumVal) exp.accept(this, env); // Dynamic type-checking
			result *= intermediate.v(); //Semantics of MultExp.
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(Program p, Env env) {
		return (Value) p.e().accept(this, env);
	}

	@Override
	public Value visit(SubExp e, Env env) {
		List<Exp> operands = e.all();
		NumVal lVal = (NumVal) operands.get(0).accept(this, env);
		double result = lVal.v();
		for(int i=1; i<operands.size(); i++) {
			NumVal rVal = (NumVal) operands.get(i).accept(this, env);
			result = result - rVal.v();
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(VarExp e, Env env) {
		// Previously, all variables had value 42. New semantics.
		return env.get(e.name());
	}	

	@Override
	public Value visit(LetExp e, Env env) { // New for varlang.
		List<String> names = e.names();
		List<Exp> value_exps = e.value_exps();
		List<Value> values = new ArrayList<Value>(value_exps.size());
		
		for(Exp exp : value_exps) 
			values.add((Value)exp.accept(this, env));
		
		Env new_env = new ExtendEnvList(env, names, values);

		return (Value) e.body().accept(this, new_env);		
	}	
	
}
