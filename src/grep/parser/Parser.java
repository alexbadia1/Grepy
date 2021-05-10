package grep.parser;
import java.util.Stack;

import grep.Util;
import grep.lexer.Token;

import java.util.Queue;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.LinkedList;

public class Parser {
	private ArrayList<Token> lexTokens = new ArrayList<Token>();
	private Queue<Token> parsedTokens = new LinkedList<Token>();
	private Stack<Token> operatorStack = new Stack<Token>();
	
	public void parse(ArrayList<Token> newTokens) {
		int index = 0;
		Token currentToken = null;
		
		while (index < newTokens.size()) {
			currentToken = newTokens.get(index);
			
			// Current token is Term
			if (currentToken.type.matches("^TERM_ALPHABETIC$|^TERM_NUMERIC$|^TERM_UNICODE$")) {
				
				// Add directly to output queue
				parsedTokens.add(currentToken);
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
						parsedTokens.add(operatorStack.pop());
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
						parsedTokens.add(operatorStack.pop());
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
			this.parsedTokens.add(operatorStack.pop());
		}// while
	}// convertToPostFix
	
	public Queue<Token> getParsedTokens() {
		return this.parsedTokens;
	}// getParsedTokens
	
	public void printTokenList() {
		System.out.println("\n" + Util.divider);
		System.out.println("Parse: Post-Fix Order");
		System.out.println(Util.divider);
		for (Token token: this.parsedTokens) {
			System.out.println("  " + token.type);
		}// for
	}// printTokenList
}// class
