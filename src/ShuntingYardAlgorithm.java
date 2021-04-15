import java.util.Stack;
import java.util.Queue;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.LinkedList;

public class ShuntingYardAlgorithm {
	private ArrayList<Token> lexTokens = new ArrayList<Token>();
	private Queue<Token> outputQueue = new LinkedList<Token>();
	private Stack<Token> operatorStack = new Stack<Token>();
	
	public void convertToPostFix(ArrayList<Token> newTokens) {

		int index = 0;
		Token currentToken = null;
		
		while (index < newTokens.size()) {
			currentToken = newTokens.get(index);
			
			// Current token is Term
			if (currentToken.type.matches("^TERM_ALPHABETIC$|^TERM_NUMERIC$|^TERM_UNICODE$")) {
				
				// Add directly to output queue
				outputQueue.add(currentToken);
			}// if
			
			// Current token is a union symbol, kleene start or implied concatenation
			else if (currentToken.type.matches("^SYMBOL_UNION$|^SYMBOL_KLEENE_STAR$|^IMPLIED_CONCATENATION$")) {
				
				// Peek first token on stack
				Token peekedToken = null;
				
				// While there are tokens on the stack, pop all operators off the stack
				while (!operatorStack.isEmpty()) {
					// Peek the next token
					peekedToken = operatorStack.peek();
					
					// If token in operator stack is an operator with greater or equal priority...
					if (peekedToken.type.matches("^SYMBOL_UNION$|^SYMBOL_KLEENE_STAR$|^IMPLIED_CONCATENATION$")
						&& 	peekedToken.priority <= currentToken.priority) {
						
						// Pop operator token to output queue
						outputQueue.add(operatorStack.pop());
					}// if
					
					else {
						break;
					}// else
				}// while
				
				// Push current token onto operator stack
				operatorStack.push(currentToken);
			}// else if
			
			// Token is "("
			else if (currentToken.type.matches("^SYMBOL_OPEN_GROUP$")) {
				operatorStack.push(currentToken);
			}// if
			
			// Token is ")"
			else if (currentToken.type.matches("^SYMBOL_CLOSE_GROUP$")) {
				Token peekedToken = null;
				
				while (!operatorStack.isEmpty()) {
					
					peekedToken = operatorStack.peek();
					
					if (!peekedToken.type.matches("^SYMBOL_OPEN_GROUP$")) { 
						outputQueue.add(operatorStack.pop());
					}// if 
					
					else {
						break;
					}// else
				}// while
				
				// Since regex is validated before hand, there should always be matching parenthesis
				if (operatorStack.peek().type.matches("^SYMBOL_OPEN_GROUP$")) {
					// Discard left parenthesis
					operatorStack.pop();
					
					// Discard right parenthesis from token queue, by skipping over it
					// That means there should only be ")" tokens left in the tokenList after this.
				}// if
			}// else if
			
			// Get next token
			index++;
		}// while
		
		// Pop all remaining operators in the stack
		while (!operatorStack.isEmpty()) {
			this.outputQueue.add(operatorStack.pop());
		}// while
	}// convertToPostFix
	
	public void printTokenList() {
		System.out.println("Parser: ");
		for (Token token: this.outputQueue) {
			System.out.println(token.type + " ");
		}// for
	}// printTokenList
}// class
