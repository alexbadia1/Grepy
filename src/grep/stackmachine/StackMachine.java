package grep.stackmachine;
import grep.finiteautomata.DeltaFunction;
import grep.finiteautomata.dfa.DFA;
import grep.finiteautomata.dfa.DFADeltaFunction;
import grep.finiteautomata.states.State;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Stack;

public class StackMachine {
	private DFA dfa;
	/**
	 * Since this stack machine is made from a DFA
	 * In theory, there should never be a duplicate Read+Pop
	 * pair since that would require the DFA to have two duplicate
	 * transitions from a single state, which is not possible.
	 */
	private Hashtable<String, State> table;
	private Stack<State> stack;
	
	public StackMachine(DFA dfa) {
		super();
		this.dfa = dfa;
		this.table = new Hashtable<String, State>();
		
		// Convert DFA to Stack Machine
		this.convertDfaToStackMachine();
		
		// Initialize stack
		this.resetStack();
	}// constructor
	
	private void convertDfaToStackMachine() {
		for(DeltaFunction df: this.dfa.getDelta()) {
			DFADeltaFunction dfaDeltaFunction = (DFADeltaFunction)df;
			this.table.put(
					(dfaDeltaFunction.getTransitionSymbol()+String.valueOf(dfaDeltaFunction.getStartingState().name)),
					dfaDeltaFunction.getAcceptedState()
			);// this.table.put
		}// for
	}// convertDfaToStackMachine
	
	private void resetStack() {
		// Initialize stack
		this.stack = new Stack<State>();
		this.stack.push(this.dfa.getStartingState());
	}// resetStack
	
	public void test(String textFilename) {
		try {
			File testFile = new File(textFilename + ".txt");
			Scanner scanner = new Scanner(testFile);
			while (scanner.hasNextLine()) {
				String scannerText = scanner.nextLine().replace(" ", "");
				
				// Test Each line
				if (this.test(scannerText, 0)) {
					System.out.println("  Passed: [" + scannerText + "]");
				}// if
				
				else {
					System.out.println("  Failed: [" + scannerText + "]");
				}// else
				
				// Reset Stack
				this.resetStack();
			}// while
		}// try
		
		catch (FileNotFoundException e) {
			System.out.println("Error: Could not input file '" + textFilename + "'.");
		}// catch
	}// test
	
	private boolean test(String input, int pos) {
		if (pos > input.length() - 1) {	
			for (State s: this.dfa.getAcceptingStates()) {
				if (this.stack.peek().name == s.name) {
					
					// Pop accepting state and push nothing
					this.stack.pop();
					//System.out.println("Popping Accepting State: " + this.stack.pop().name);
					
					return true;
				}// if
			}// for
			
			//System.out.println("Ended on an invalid state: " + this.stack.peek().name);
			return false;
		}// if
		
		// Read character
		String symbol = String.valueOf(input.charAt(pos++));
		
		try {
			// 2.) Peek Stack
			State currState = this.stack.peek();
			
			// Look up move in hash table
			//System.out.println("Looking up: " + (symbol + String.valueOf(currState.name)));
			State push = this.table.get((symbol + String.valueOf(currState.name)));
			
			if (push == null) {
				// No production found
				//System.out.println("Looking up: Not Found: " + (symbol + String.valueOf(currState.name)));
				return false;
			}// if
			
			// Pop
			//System.out.println("Popping: " + this.stack.pop().name);
			this.stack.pop();
			
			// Push
			//System.out.println("Pushing: " + push.name);
			this.stack.push(push);
			
			// Recursively do this
			return this.test(input, pos);
		}// try
		catch (Exception e) {
			// Still characters, but empty stack
			// Reject.
			
			// System.out.println("Still characters, but empty stack");
			return false;
		}// catch
	}// test
}// StackMachine
