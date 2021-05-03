import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;

public class ThompsonConstruction {
	/**
	 * Tokens returned from parse
	 * 
	 * Parse tokens are the Lexical tokens in a post-fix
	 * order, taking into account the priority of operators:
	 *   - Kleene Star
	 *   - Concatenation
	 *   - Union
	 * 
	 * I would've just used recursive descent parsing and read
	 * the tree in depth first post order traversal, but Michael didn't 
	 * give us a formal grammar for the regular language we'd be testing...
	 */
	private Queue<Token> tokens;
	
	/**
	 * Holds the atomic NFA's derived during Thompson construction.
	 * 
	 * Atomic NFA's are popped out of the stack as needed, to perform compound 
	 * operations such as concatenation, union, or other operations like kleene star.
	 */
	private Stack<NFA> nfaStack = new Stack<NFA>();
	
	/**
	 * Continuously negative growing ID for each node of the NFA.
	 * 
	 * A temporary way to identify each node in the NFA, however,
	 * the ID ordering will be corrected after traversal of the final NFA.
	 */
	private int nfaId = 0;
	
	ThompsonConstruction(Queue<Token> tokens){
		this.tokens = tokens;
	}// constructor
	
	/**
	 * Constructs an Epsilon NFA from a series of post fix order regular expression tokens.
	 * 
	 * As each token is consumed, an atomic NFA is built, or an operation is performed
	 * on already existing atomic NFA's such as kleene star, concatenation, or union.
	 */
	public NFA thompsonConstruction() {
		while (!tokens.isEmpty()) {
			Token currentToken = tokens.remove();
			switch(currentToken.type) {
				case "TERM_METACHARACTER": 
				case "TERM_UNICODE":
				case "TERM_NUMERIC":
				case "TERM_ALPHABETIC":
					System.out.println("Found: " + currentToken.type);
					this.term(currentToken);
					break;
				case "SYMBOL_KLEENE_STAR":
					System.out.println("Found: " + currentToken.type);
					this.kleeneStar();
					break;
				case "IMPLIED_CONCATENATION":
					System.out.println("Found: " + currentToken.type);
					this.concatenation();
					break;
				case "SYMBOL_UNION":
					System.out.println("Found: " + currentToken.type);
					this.union();
					break;
				default:
					break;
			}// switch
		}// while
		
		return nfaStack.pop();
	}// thompsonConstruction
	
	/**
	 * Creates an Atomic NFA that accepts a single term
	 */
	private void term(Token newToken) {
		System.out.println("Generating Symbol NFA");
		
		// Start
		State start = new State(this.nfaId++, true, false);
		
		// Accept State
		State accept = new State(this.nfaId++, false, true);
		
		// States
		ArrayList<State> states = new ArrayList<State>();
		states.add(start);
		states.add(accept);
		
		// Transitions
		ArrayList<Delta> transitions = new ArrayList<Delta>();
		ArrayList<State> deltaEndStates = new ArrayList<State>();
		deltaEndStates.add(accept);
		transitions.add(new Delta(start, newToken.lexeme, deltaEndStates));
		
		// Create NFA and push onto stack
		NFA newNFA = new NFA(start, states, transitions, accept);
		System.out.println("Atomic Symbol for: " + newToken.lexeme + "\n" + newNFA.toString());
		this.nfaStack.push(newNFA);
	}// generateNfaForTerm
	
	/**
	 * Unions two Atomic NFA's
	 */
	private void union() {
		System.out.println("Performing Union");
		
		// 1.) Get the two NFA's off the stack and effectively 
		// throw away by not returning them to the stack.
		NFA nfa2 = this.nfaStack.pop();
		NFA nfa1 = this.nfaStack.pop();
		
		// 1.1.) Make sure nfa1 and nfa2 start and accepting states flag's are cleaned
		nfa1.startState.resetFlags();
		nfa1.acceptingState.resetFlags();
		nfa2.startState.resetFlags();
		nfa2.acceptingState.resetFlags();
		
		// 2.) Create new start and end states for union
		State newStart = new State(nfaId++, true, false);
		State newAccept = new State(nfaId++, false, true);
		
		// 3.) Combine states from the two popped NFA's
		ArrayList<State> newStates = new ArrayList<State>();
		
		// 3.1.) Add new start state
		newStates.add(newStart);
		
		// 3.2.) Add states from nfa1 and nfa2
		newStates.addAll(nfa1.states);
		newStates.addAll(nfa2.states);
		
		// 3.3.) Add new accept/end state
		newStates.add(newAccept);
		
		// 4.) Combine both old nfa's transitions with new transitions
		ArrayList<Delta> newTransitions = new ArrayList<Delta>();
		
		// 4.1) Add nfa1 and nfa2 transitions
		newTransitions.addAll(nfa1.transitions);
		newTransitions.addAll(nfa2.transitions);
		
		// 4.2) Add Delta(newStart, epsilon) = {nfa1.startState, nfa2.startState}
		ArrayList<State> newStartDeltaEndStates = new ArrayList<State>();
		newStartDeltaEndStates.add(nfa1.startState);
		newStartDeltaEndStates.add(nfa2.startState);
		newTransitions.add(new Delta(newStart, "epsilon", newStartDeltaEndStates));
		
		// 4.3) Add Delta(nfa1.acceptState, epsilon) = {newAccept}
		//      Add Delta(nfa2.acceptState, epsilon) = {newAccept}
		ArrayList<State> newEndDeltaEndStates = new ArrayList<State>();
		newEndDeltaEndStates.add(newAccept);
		
		// Don't override any existing transitions on nfa1's or nfa2's old end accepting state
		if (nfa1.acceptingState != null) {
			newEndDeltaEndStates.addAll(nfa1.acceptingState.children);
		}// if
		
		if (nfa2.acceptingState != null) {
			newEndDeltaEndStates.addAll(nfa2.acceptingState.children);
		}// if
		
		newTransitions.add(new Delta(nfa1.acceptingState, "epsilon", newEndDeltaEndStates));
		newTransitions.add(new Delta(nfa2.acceptingState, "epsilon", newEndDeltaEndStates));
		
		// Create new NFA and push onto stack
		 this.nfaStack.push(new NFA(newStart, newStates, newTransitions, newAccept));
	}// union
	
	/**
	 * Concatenates two Atomic NFA's
	 */
	private void concatenation() {
		System.out.println("Performing Concatenation");
		
		// 1.) Get the two NFA's off the stack and effectively 
		// throw away by not returning them to the stack.
		NFA nfa2 = this.nfaStack.pop();
		NFA nfa1 = this.nfaStack.pop();
		
		// 1.1.) Only clean nfa1 end accept state flags
		//       and nfa2 start states flags
		nfa1.acceptingState.resetFlags();
		nfa2.startState.resetFlags();
		
		// 2.) Combine states from the two popped NFA's
		ArrayList<State> newStates = new ArrayList<State>();
		
		// 2.2.) Add states from nfa1 and nfa2
		newStates.addAll(nfa1.states);
		newStates.addAll(nfa2.states);
		
		// 3.) Combine both old nfa's transitions with new transitions
		ArrayList<Delta> newTransitions = new ArrayList<Delta>();
		
		// 3.1) Add nfa1 and nfa2 transitions
		newTransitions.addAll(nfa1.transitions);
		newTransitions.addAll(nfa2.transitions);
		
		// 3.2) Add Delta(nfa1.acceptState, epsilon) = {nfa2.startState, [nfa1.startState for kleene star]}
		ArrayList<State> newStartDeltaEndStates = new ArrayList<State>();
		newStartDeltaEndStates.add(nfa2.startState);
		
		if (nfa1.acceptingState != null) {
			newStartDeltaEndStates.addAll(nfa1.acceptingState.children); // Avoid overwriting nfa1's end accept state transitions
		}// if
		
		newTransitions.add(new Delta(nfa1.acceptingState, "epsilon", newStartDeltaEndStates));
		
		// 4.) Create new NFA and push onto stack
		 this.nfaStack.push(new NFA(nfa1.startState, newStates, newTransitions, nfa2.acceptingState));
	}// concatenation
	
	/**
	 * Applies a kleene star operation to the NFA
	 */
	private void kleeneStar() {
		System.out.println("Performing Kleen Star");
		
		// 1.) Get one NFA off the stack and effectively 
		// throw away by not returning it to the stack.
		NFA nfa = this.nfaStack.pop();
		
		// 1.1.) Clean start and accepting states flags
		nfa.startState.resetFlags();
		nfa.acceptingState.resetFlags();
		
		// 2.) Create new start and end states for union
		State newStart = new State(nfaId++, true, false);
		State newAccept = new State(nfaId++, false, true);
		
		// 3.) Combine states the popped NFA
		ArrayList<State> newStates = new ArrayList<State>();
		
		// 3.1.) Add new start state
		newStates.add(newStart);
		
		// 3.2.) Add states from NFA
		newStates.addAll(nfa.states);
		
		// 3.3.) Add new accept/end state
		newStates.add(newAccept);
		
		// 4.) Combine old NFA's transitions with new transitions
		ArrayList<Delta> newTransitions = new ArrayList<Delta>();
		
		// 4.1) Add old NFA transitions
		newTransitions.addAll(nfa.transitions);
		
		// 4.2) Add Delta(newStart, epsilon) = {nfa.startState}
		ArrayList<State> newStartDeltaEndStates = new ArrayList<State>();
		newStartDeltaEndStates.add(nfa.startState);
		newStartDeltaEndStates.add(newAccept); // Spontaneous transition from start to end
		newTransitions.add(new Delta(newStart, "epsilon", newStartDeltaEndStates));
		
		// 4.3) Add Delta(nfa.acceptState, epsilon) = {newAccept}
		ArrayList<State> newEndDeltaEndStates = new ArrayList<State>();
		newEndDeltaEndStates.add(newAccept);
		newEndDeltaEndStates.add(nfa.startState); // Looping epsilon transition
		
		if (nfa.acceptingState != null) {
			newEndDeltaEndStates.addAll(nfa.acceptingState.children); // Avoid overwriting NFA's old end accept state transitions
		}// if
		
		newTransitions.add(new Delta(nfa.acceptingState, "epsilon", newEndDeltaEndStates));
		
		// Create new NFA and push onto stack
		NFA newNFA = new NFA(newStart, newStates, newTransitions, newAccept);
		System.out.println("Kleene Star: \n" + newNFA.toString());
		this.nfaStack.push(newNFA);
	}// kleeneStar
}// class
