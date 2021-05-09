import java.util.Hashtable;
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
		for(DFADeltaFunction df: this.dfa.transitions) {
			this.table.put(
					(df.getInputSymbol()+String.valueOf(this.dfa.states.get(df.getInputState()).name)), 
					this.dfa.states.get(df.getOutputState())
			);// this.table.put
		}// for
	}// convertDfaToStackMachine
	
	private void resetStack() {
		// Initialize stack
		this.stack = new Stack<State>();
		this.stack.push(this.dfa.getStartingState());
	}// resetStack
	
	public void test(String input) {
		
		// Test
		this.test(input, 0);
		
		// Reset Stack
		this.resetStack();
	}// testHelper
	
	private boolean test(String input, int pos) {
		if (pos > input.length() - 1) {
			
			System.out.println("hmm.... ");
			
			for (State s: this.dfa.getAcceptingStates()) {
				if (this.stack.peek().name == s.name) {
					
					// Pop accepting state and push nothing
					System.out.println("Popping Accepting State: " + this.stack.pop().name);
					
					return true;
				}// if
				
				else {
					
					System.out.println("Ended on an invalid state: " + this.stack.peek().name);
					return false;
				}// else
			}// for
		}// if
		
		// Read character
		String symbol = String.valueOf(input.charAt(pos++));
		
		try {
			// 2.) Peek Stack
			State currState = this.stack.peek();
			
			// Look up move in hash table
			System.out.println("Looking up: " + (symbol + String.valueOf(currState.name)));
			State push = this.table.get((symbol + String.valueOf(currState.name)));
			
			if (push == null) {
				// No production found
				System.out.println("Looking up: Not Found: " + (symbol + String.valueOf(currState.name)));
				return false;
			}// if
			
			// Pop
			System.out.println("Popping: " + this.stack.pop().name);
			
			// Push
			System.out.println("Pushing: " + push.name);
			this.stack.push(push);
			
			// Recursively do this
			this.test(input, pos);
		}// try
		catch (Exception e) {
			// Still characters, but empty stack
			// Reject.
			
			System.out.println("Still characters, but empty stack");
			return false;
		}// catch
		
		return true;
	}// test
}// StackMachine
