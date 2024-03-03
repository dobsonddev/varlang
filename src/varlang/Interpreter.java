package varlang;
import java.io.IOException;

import varlang.Env;
import varlang.Value;
import varlang.AST.Program;

/**
 * This main class implements the Read-Eval-Print-Loop of the interpreter with
 * the help of Reader, Evaluator, and Printer classes. 
 * 
 * @author hridesh
 *
 */

//4.7.1 (Env.java - ExtendEnvList class)
//(let ((a 1) (b 2)) (+ a b)) - multiple var/val pairs allowed in a single expression
//(let ((x 1)) (let ((y 2)) (let ((z 3)) (+ x y z)))) - that plus nested lets/assignments working due to nested, local scoping

//4.10.2 (Evaluator.java - Evaluator method, testEnvProjectMethods())
//See terminal - "All environment tests passed"

//4.10.6 (Env.java - Created DynamicError, Evaluator.java - added same-scope/env reassignment checking)
//(let ((a 3) (a 4)) a) - NOT ALLOWED (DYNAMIC ERROR), because redefinition within the same scope/env is not allowed bc of func lang characteristics - like immutability.
//(let ((a 3)) (let ((a 4)) a)) - ALLOWED, because redefinition occurs within a nested let, which creates its own local/inner scope/env



public class Interpreter {
	public static void main(String[] args) {
		System.out.println("Type a program to evaluate and press the enter key," + 
							" e.g. (let ((a 3) (b 100) (c 84) (d 279) (e 277)) (+ (* a b) (/ c (- d e)))) \n" + 
							"Press Ctrl + C to exit.");
		Evaluator evaluator = new Evaluator();
		evaluator.testEnvProjectMethods();
		System.out.println("All environment tests passed!");
		Reader reader = new Reader();
		Evaluator eval = new Evaluator();
		Printer printer = new Printer();
		REPL: while (true) { // Read-Eval-Print-Loop (also known as REPL)
			Program p = null;
			try {
				p = reader.read();
				if(p._e == null) continue REPL;
				Value val = eval.valueOf(p);
				printer.print(val);
			} catch (Env.LookupException e) {
				printer.print(e);
			} catch (IOException e) {
				System.out.println("Error reading input:" + e.getMessage());
			} catch (NullPointerException e) {
				System.out.println("Error:" + e.getMessage());
			}
		}
	}
}
