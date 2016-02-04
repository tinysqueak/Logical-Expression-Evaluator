
import java.util.*;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * This class implements the <code>LogicalExpression</code> interface.
 * In addition to the required methods for implementation from the <code>LogicalExpression</code> 
 * interface, the class also contains several of its own methods that help in evaluation of the
 * logical sentence. In addition to the normal evaluation methods, there are also JavaScript based
 * evaluation methods that modify the sentence to pass to a JavaScript evaluation engine.
 * @author William Yang
 *
 */
public class LogicalExpressionDemo implements LogicalExpression {

	//String containing the original logical sentence
	private String sentence;
	
	/*
	 * A StringBuilder containing the original sentence. This is initialized
	 * and modified in the initEval method, which replaces the each variable character
	 * with its respective truth value.
	 */
	private StringBuilder evaluateSentence;

	/*
	 * Values corresponding the validity, satisfiability, and contingency of the logical expression.
	 * Validity is assumed to be true by default, while satisfiability and contingency are assumed to
	 * be false by default. These values are later modified in the test() method if the assumptions
	 * turn out to be incorrect.
	 */
	private boolean valid = true;
	private boolean satisfiable = false;
	private boolean contingent = false;

	//a HashSet is used to store all the unique variable characters
	private HashSet<Character> variableSet;
	
	//an ArrayList containing the characters in the variableSet HashSet
	private ArrayList<Character> variableArray;

	//a HashMap that delegates a particular truth value to a specific variable
	private HashMap<Character, Boolean> variableMap;

	/**
	 * Initializes the <code>sentence</code> field to the specified logical sentence. 
	 * Also initializes the variableSet <code>HashSet</code> and invokes <code>numberOfVars()</code> 
	 * to obtain the number of unique variables and <code>test()</code> to evaluate the logical sentence.
	 * @param sentence The logical sentence as a <code>String</code>
	 */
	public LogicalExpressionDemo(String sentence) {

		this.sentence = sentence;
		variableSet = new HashSet<Character>();
		numberOfVars();
		
		/*
		 * normally would invoke test() here, but I have commented it out to test the results of my implementation
		 * against that of the JavaScript engine (see main method)
		 */
		//test();

	}

	/**
	 * Iterates through each character in the sentence and adds each non-operator 
	 * character (a variable) into the <code>variableSet</code>. Since a <code>Set</code> cannot have duplicate 
	 * elements, this means that the <code>variableSet</code> will only contain all the unique 
	 * variables in the sentence.
	 */
	public void numberOfVars() {
	
		for(int i = 0; i < sentence.length(); i++) {
	
			switch(sentence.charAt(i)) {
	
			case '~':
				break;
	
			case '|':
				break;
	
			case '&':
				break;
	
			case '(':
				break;
	
			case ')':
				break;
	
			default:
				variableSet.add(sentence.charAt(i));
				break;
	
			}
	
		}
	
		/*
		 * the characters in the Set are added to a variableArray, which will make it simpler to
		 * iterate through and add them to the variableMap
		 */
		variableArray = new ArrayList<Character>(variableSet);
	
	}

	/**
	 * Iterates through the possible truth assignment permutations and 
	 * initializes <code>variableMap</code> and adds each unique variable from 
	 * the <code>variableArray</code> along with its truth assignment. The maximum 
	 * possible truth value permutations is obtained by bitshifting 1 right by the 
	 * number of unique variables, i.e. the size of <code>variableArray</code>, in other 
	 * words, by exponentiating 2 to the number of unique variables (as there is a max 
	 * of 2 possible values for each variable). The truth value of each variable is obtained 
	 * by seeing if bitwise anding 1 with 1 bitshifted left by an array-index number of times 
	 * is equal to 1 bitshifted left by an array-index number of times. In other words, the 
	 * truth value of each of the variables is obtained from the binary digits of a number that is  
	 * iterated up from 0 to the maximum possible of truth assignment permutations. 
	 */
	public void test() {
		
		//iterates through all possible truth assignment permutations
		for(int i = 0; i < (1<<variableArray.size()); i++) {
	
			//contains the truth assignemnts for each unique character, for one particular permuation
			variableMap = new HashMap<Character, Boolean>();
	
			//iterates through the number of unique variables
			for(int j = 0; j < variableArray.size(); j++) {
	
				/*
				 * the jth variable's truth assignment is the jth binary digit of i from the right, 
				 * where 0 corresponds to false and 1 to true in this case
				 */
				variableMap.put(variableArray.get(j), (i&(1<<j)) == (1<<j));
	
			}
	
			//initializes the variables in the sentence to their assigned truth values for one permutation
			initEval(variableMap);
	
			//set satisfiable to true if the logical expression evaluates to true at least once
			if(evaluate(evaluateSentence) == true) {
	
				satisfiable = true;
	
			}
	
			//set valid to false if the logical expression evaluates to false at least once
			if(evaluate(evaluateSentence) == false) {
	
				valid = false;
	
			}
	
			/*
			 * sets contingent to true if a logical expression is satisfiable, but not valid, i.e. 
			 * true for at least one truth assignment, but also false for at least one truth assignment
			 */
			if(satisfiable && !valid) {
	
				contingent = true;
	
			}
	
		}
		
	}

	/**
	 * Initializes <code>evaluateSentence</code> and iterates through it, changing each variable 
	 * to 'T' or 'F' depending on its truth value for one particular permutation.
	 * @param variableValues The <code>HashMap</code> of truth values for each unique variable.
	 */
	public void initEval(HashMap<Character, Boolean> variableValues) {
		
		evaluateSentence = new StringBuilder(sentence);
		
		for(int i = 0; i < evaluateSentence.length(); i++) {
	
			switch(evaluateSentence.charAt(i)) {
	
			case '~':
				break;
	
			case '|':
				break;
	
			case '&':
				break;
	
			case '(':
				break;
	
			case ')':
				break;
				
			default:
				char variable = evaluateSentence.charAt(i);
				evaluateSentence.setCharAt(i, variableValues.get(variable) ? 'T' : 'F');
				break;
	
			}
	
		}
	}

	/**
	 * Evaluates the modified logical sentence that has the truth values substituted in place 
	 * of the variable characters. Iterates over the entire sentence twice (inefficient?), looking 
	 * for the or operator the first time and the and operator the second time. If either operator is found 
	 * not within a set of parentheses, then both subsections of the sentence are recursively evaluated, and 
	 * either the disjunction or conjunction of the two values are returned, for the or/and cases respectively. 
	 * If neither of these breakpoints are reached, the method then checks for the negation operator and parentheses 
	 * at the beginning of the sentence and deals with each appropriately. Finally, in the recursive base case, if the
	 * sentence length is 1, then the truth value is returned, true for 'T' and false for 'F'.
	 * @param evaluate The logical sentence as a <code>StringBuilder</code>, initialized with truth values to evaluate.
	 * @return The truth value of the initialized logical sentence after evaluation.
	 */
	public boolean evaluate(StringBuilder evaluate) {
					
		if(evaluate.length() != 1) {
			
			/*
			 * Helps indicate whether or not the current character is within a set of parentheses.
			 * A value of 0 indicates that the current character is not in a set of parentheses, while 
			 * any value greater than 0 indicates that the character is within a set of parentheses. A 
			 * negative value indicates the user failed to correctly parenthesize the logical sentence, 
			 * with more closed parentheses than open ones.
			 */
			int parens = 0;
	
			//first searches for the or operator, as that is the one with least precedence
			for(int i = 0; i < evaluate.length(); i++) {
	
				switch(evaluate.charAt(i)) {
	
				case '(':
					parens++;
					break;
	
				case ')':
					parens--;
					break;
	
				case '|':
					//if not in parentheses, return the disjunction of the first and second halves of the sentence
					if(parens == 0) {
	
						return evaluate(new StringBuilder(evaluate.substring(0, i))) ||
								evaluate(new StringBuilder(evaluate.substring(i + 1, evaluate.length())));
	
					}
					break;
	
				}
	
			}
	
			//next, searches for the and operator in a similar fashion
			for(int i = 0; i < evaluate.length(); i++) {
	
				switch(evaluate.charAt(i)) {
	
				case '(':
					parens++;
					break;
	
				case ')':
					parens--;
					break;
	
				case '&':
					//if not in parentheses, return the conjunction of the first and second halves of the sentence
					if(parens == 0) {
						
						return evaluate(new StringBuilder(evaluate.substring(0, i))) &&
								evaluate(new StringBuilder(evaluate.substring(i + 1, evaluate.length())));
	
					}
					break;
	
				}
	
			}
			
			/*
			 * If the method has gotten this far without entering either of the recursive breakpoints in the previous
			 * loops, then this means that the expression is either enclosed in all parentheses (since the breakpoints only 
			 * occur when the operator is not within parentheses), or there are no operators within the entire expression.
			 * This means that the sentence could have a negation operator in front of a set of parentheses, or simply a 
			 * negation operator in front of a truth value, or no negation operator in front of anything, and just parentheses.
			 * The following two cases account for this situation.
			 */
	
			//returns the negation of the rest of the sentence if the first character is the negation operator
			if(evaluate.charAt(0) == '~') {
	
				return !evaluate(new StringBuilder(evaluate.substring(1, evaluate.length())));
	
			}
	
			//returns the expression within the parentheses if the expression is enclosed by parentheses
			if(evaluate.charAt(0) == '(' && evaluate.charAt(evaluate.length() - 1) == ')') {
	
				return evaluate(new StringBuilder(evaluate.substring(1, evaluate.length() - 1)));
	
			}
	
		} 
	
		//the base case, if there is only a single character in the expression, return the truth value
		return evaluate.charAt(0) == 'T' ? true : false;
	
	}

	/**
	 * Returns whether or not the logical sentence is satisfiable for all possible truth assignments.
	 * @return Whether the logical sentence is valid or not.
	 */
	@Override
	public boolean valid() {
		return valid;
	}

	/**
	 * Returns whether or not the logical sentence evaluates to true for at least one truth assignment.
	 * @return The satisfiability of the logical sentence.
	 */
	@Override
	public boolean satisfiable() {

		return satisfiable;

	}

	/**
	 * Returns whether or not the logical sentence evaluates to true for at least one truth assignment and 
	 * also false for at least one truth assignment.
	 * @return Whether or not the logical sentence is contingent.
	 */
	@Override
	public boolean contingent() {
		return contingent;
	}

	/**
	 * Returns whether or not this logical expression is equivalent to the argument expression, i.e. 
	 * whether or not this one entails the other and the other also entails this one.
	 * @param secondExpression The <code>LogicalExressionDemo</code> with which to compare to for logical 
	 * equivalence.
	 * @return
	 * <ul>
	 * <li><b>1</b>: If the two expressions are logically equivalent.</li>
	 * <li><b>-1</b>: If they are not.</li>
	 * </ul>
	 * 
	 */
	@Override
	public int equivalent(LogicalExpressionDemo secondExpression) {

		if(this.entails(secondExpression) == 1 && secondExpression.entails(this) == 1) {

			return 1;

		}

		return -1;
	}
	
	/**
	 * Since logical entailment involves comparing two different logical sentences, a <code>combinedVariableSet</code> 
	 * is used to store all the unique variables across both logical sentences. Then, a similar process is used to 
	 * permute through all the possible truth assignments and assign truth values as in the <code>test()</code> method. 
	 * The method then returns a value depending on whether or not this logical sentence entails the one passed as
	 * an argument, i.e. whether or not all the truth assignments that satisfy this one also satisfy the other.
	 * @param secondExpression The <code>LogicalExpressionDemo</code> with which to determine entailment.
	 * @return
	 * <ul>
	 * <li><b>1</b>: If this expression entails the other.</li>
	 * <li><b>-1</b>: If this expression does not entail the other.</li>
	 * </ul>
	 */
	@Override
	public int entails(LogicalExpressionDemo secondExpression) {
		
		/*
		 * Creates a new HashSet and adds all the variables from both expressions into it.
		 * Since a Set can contain no duplicate elements, the combinedVariableSet now contains 
		 * all the unique elements across both sentences.
		 */
		HashSet<Character> combinedVariableSet = new HashSet<Character>();
		combinedVariableSet.addAll(this.variableSet);
		combinedVariableSet.addAll(secondExpression.variableSet);
		
		//an ArrayList is used to more easily access the variables that are in the combinedVariableSet
		ArrayList<Character> combinedVariableArray = new ArrayList<Character>(combinedVariableSet);
		
		/*
		 * Assigns truth values for each unique variable. See the prior documentation in and about the 
		 * test() method for further information.
		 */
		for(int i = 0; i < (1<<combinedVariableArray.size()); i++) {
	
			HashMap<Character, Boolean> combinedMap = new HashMap<Character, Boolean>();
			
			for(int j = 0; j < combinedVariableArray.size(); j++) {
				
				combinedMap.put(combinedVariableArray.get(j), (i&(1<<j)) == (1<<j));
	
			}
	
			/*
			 * Initializes the variables to their assigned truth values for both expressions.
			 * Since some variables may be present in one sentence but not the other, there is some slight 
			 * inefficiency in that the sentence with the fewest number of unique variables may be initialized
			 * to the same permutation of truth values more than once.
			 */
			initEval(combinedMap);
			secondExpression.initEval(combinedMap);
	
			/*
			 * if the truth assignments that satisfy this sentence do not also satisfy the other sentence, then
			 * this sentence does not entail the other
			 */
			if(evaluate(evaluateSentence) == true && secondExpression.evaluate(secondExpression.evaluateSentence) == false) {
	
				return -1;
	
			}
	
		}
	
		return 1;
		
	}

	/*
	 * The following methods rely on using a JavaScript engine to evaluate the logical sentence, which may be more efficient 
	 * than my own implementation of evaluation. Their implementation is essentially the same as their non-script based counterparts, 
	 * and as such, documentation for them is omitted (look at the non-script based counterparts for more info).
	 * The major differences are that instead of replacing the variable with 'T' or 'F' depending on the truth value 
	 * assignment, as in initEval, initScriptEval replaces the variable with "true" or "false", while also making the necessary
	 * accommodations to the iteration index. Additionally, as previously mentioned, the scriptEvaluate method passes the
	 * logical sentence with initialized values to a JavaScript engine to evaluate it.
	 * 
	 * These methods were originally used to verify that my own implementation of evaluation was working correctly,
	 * and I have decided to keep them as they have been quite helpful in helping to debug my code.
	 */
	
	public void scriptTest() {
	
		for(int i = 0; i < (1<<variableArray.size()); i++) {
	
			variableMap = new HashMap<Character, Boolean>();
	
			for(int j = 0; j < variableArray.size(); j++) {
	
				variableMap.put(variableArray.get(j), (i&(1<<j)) == (1<<j));
	
			}
	
			initScriptEval(variableMap);
	
			if(scriptEvaluate(evaluateSentence) == true) {
	
				satisfiable = true;
	
			}
	
			if(scriptEvaluate(evaluateSentence) == false) {
	
				valid = false;
	
			}
	
			if(satisfiable && !valid) {
	
				contingent = true;
	
			}
	
		}
	
	}

	public void initScriptEval(HashMap<Character, Boolean> variableValues) {
	
		evaluateSentence = new StringBuilder(sentence);
	
		for(int i = 0; i < evaluateSentence.length(); i++) {
	
			switch(evaluateSentence.charAt(i)) {
	
			case '~':
				evaluateSentence.setCharAt(i, '!');
				break;
	
			case '|':
				evaluateSentence.insert(i, '|');
				i++;
				break;
	
			case '&':
				evaluateSentence.insert(i, '&');
				i++;
				break;
	
			case '(':
				break;
	
			case ')':
				break;
	
			default:
				char variable = evaluateSentence.charAt(i);
				evaluateSentence.replace(i, i + 1, variableValues.get(variable) ? "true" : "false");
				i += variableValues.get(variable) ? 3 : 4;
				break;
	
			}
	
		}
	}

	public boolean scriptEvaluate(StringBuilder evaluate) {
	
		String eval = new String(evaluate);
		
		boolean result = false;
	
		try {
	
			result = (Boolean) new ScriptEngineManager().getEngineByName("javascript").eval(eval);
	
		} catch(ScriptException e) {
	
			e.printStackTrace();
	
		}
	
		return result;
	
	}

	public int scriptEquivalent(LogicalExpressionDemo secondExpression) {
		
		if(this.scriptEntails(secondExpression) == 1 && secondExpression.scriptEntails(this) == 1) {
			
			return 1;
			
		}
		
		return -1;
		
	}

	public int scriptEntails(LogicalExpressionDemo secondExpression) {

		HashSet<Character> combinedVariableSet = new HashSet<Character>();
		combinedVariableSet.addAll(this.variableSet);
		combinedVariableSet.addAll(secondExpression.variableSet);
				
		ArrayList<Character> combinedVariableArray = new ArrayList<Character>(combinedVariableSet);
		
		for(int i = 0; i < (1<<combinedVariableArray.size()); i++) {

			HashMap<Character, Boolean> combinedMap = new HashMap<Character, Boolean>();
			
			for(int j = 0; j < combinedVariableArray.size(); j++) {
				
				combinedMap.put(combinedVariableArray.get(j), (i&(1<<j)) == (1<<j));

			}

			initScriptEval(combinedMap);
			secondExpression.initScriptEval(combinedMap);

			if(scriptEvaluate(evaluateSentence) == true && secondExpression.scriptEvaluate(secondExpression.evaluateSentence) == false) {

				return -1;

			}

		}

		return 1;
	}
	
	/**
	 * Used to create and test some test cases. Compares the results of evaluation based on my implementation 
	 * to that as evaluated by the JavaScript engine.
	 * @param args Unused parameter.
	 */
	public static void main(String[] args) {
		
		//should print out same results as the script based evaluation for demo2
		//can test De Morgan's law for cases
		LogicalExpressionDemo demo = new LogicalExpressionDemo("(A|B)|~(A|B)");
		
		demo.test();

		System.out.println("My Implementation:");
		System.out.println("Valid: " + demo.valid());
		System.out.println("Satisfiable: " + demo.satisfiable());
		System.out.println("Contingent: " + demo.contingent());
		System.out.println("Equivalent: " + demo.equivalent(new LogicalExpressionDemo("A|B|~A|B")));
		System.out.println("Entails: " + demo.entails(new LogicalExpressionDemo("A&~A")));
		
		
		System.out.println();
		
		
		LogicalExpressionDemo demo2 = new LogicalExpressionDemo("(A|B)|~(A|B)");
		
		demo2.scriptTest();
		
		System.out.println("Script Results:");
		System.out.println("Valid: " + demo2.valid());
		System.out.println("Satisfiable: " + demo2.satisfiable());
		System.out.println("Contingent: " + demo2.contingent());
		System.out.println("Equivalent: " + demo2.scriptEquivalent(new LogicalExpressionDemo("~(~A&A)")));
		System.out.println("Entails: " + demo2.scriptEntails(new LogicalExpressionDemo("A&~A")));		

	}

}

/**
 * The <code>LogicalExpression interface</code> with the required methods as in the specifications.
 * @author William Yang
 *
 */
interface LogicalExpression {

	public abstract boolean valid();
	public abstract boolean satisfiable();
	public abstract boolean contingent();

	public abstract int equivalent(LogicalExpressionDemo expression);
	public abstract int entails(LogicalExpressionDemo expression);

}