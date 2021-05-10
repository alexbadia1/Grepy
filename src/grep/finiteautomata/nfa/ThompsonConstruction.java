package grep.finiteautomata.nfa;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;

import grep.Util;
import grep.finiteautomata.DeltaFunction;
import grep.finiteautomata.states.AcceptedState;
import grep.finiteautomata.states.StartState;
import grep.finiteautomata.states.State;
import grep.lexer.Token;

public class ThompsonConstruction {
	/**
	 * Tokens returned from parse.
	 * 
	 * Parse tokens are the Lexical tokens in a post-fix
	 * order, taking into account the priority of operators:
	 *   - Kleene Star
	 *   - Concatenation
	 *   - Union
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
	 * Continuously growing IDs too keep track of each node and NFA constructed
	 */
	private int nfaId = 0;
	private int stateId = 0;
	
	/**
	 * Alphabet specified by an input file. 
	 * 
	 * If no alphabet file is specified, will default to the symbols in the regex.
	 */
	private ArrayList<String> alphabet;
	
	public ThompsonConstruction(Queue<Token> tokens, ArrayList<String> newAlphabet){
		this.tokens = tokens;
		this.alphabet = newAlphabet;
	}// constructor
	
	/**
	 * Constructs an Epsilon NFA from a series of post fix order regular expression tokens.
	 * 
	 * As each token is consumed, an atomic NFA is built, or an operation is performed
	 * on already existing atomic NFA's such as kleene star, concatenation, or union.
	 */
	public NFA thompsonConstruction() {
		System.out.println("\n\n\n" + Util.divider);
		System.out.println("NFA by Thompson Construction...");
		System.out.println(Util.divider);
		while (!tokens.isEmpty()) {
			Token currentToken = tokens.remove();
			System.out.print("\n\n[" + currentToken.type + "] : [" + currentToken.lexeme+ "] ");
			switch(currentToken.type) {
				case "TERM_METACHARACTER": 
				case "TERM_UNICODE":
				case "TERM_NUMERIC":
				case "TERM_ALPHABETIC":
					this.term(currentToken);
					break;
				case "SYMBOL_KLEENE_STAR":
					this.kleeneStar();
					break;
				case "IMPLIED_CONCATENATION":
					this.concatenation();
					break;
				case "SYMBOL_UNION":
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
		System.out.println("Atomic NFA for Symbol: ");
		System.out.println(Util.divider);
		
		// Start
		State start = new StartState(this.stateId++);
		
		// Accept State
		State accept = new AcceptedState(this.stateId++);
		
		// States
		ArrayList<State> states = new ArrayList<State>();
		states.add(start);
		states.add(accept);
		
		// Transitions
		ArrayList<DeltaFunction> transitions = new ArrayList<DeltaFunction>();
		ArrayList<State> deltaEndStates = new ArrayList<State>();
		deltaEndStates.add(accept);
		transitions.add(new NFADeltaFunction(start, newToken.lexeme, deltaEndStates));
		
		// Create NFA and push onto stack
		NFA newNFA = new NFA(states, this.alphabet, start, transitions, accept, nfaId++);
		
		newNFA.toString();
		this.nfaStack.push(newNFA);
	}// generateNfaForTerm
	
	/**
	 * Unions two Atomic NFA's
	 */
	private void union() {
		System.out.println("Unioning 2 NFAs: ");
		System.out.println(Util.divider);
		
		// 1.) Get the two NFA's off the stack and effectively 
		// throw away by not returning them to the stack.
		NFA nfa2 = this.nfaStack.pop();
		NFA nfa1 = this.nfaStack.pop();
		
		// 1.1.) Make sure nfa1 and nfa2 start and accepting states flag's are cleaned
		nfa1.getStartState().resetFlags();
		nfa1.getAccepetedState().resetFlags();
		nfa2.getStartState().resetFlags();
		nfa2.getAccepetedState().resetFlags();
				
		// 2.) Create new start and end states for union
		State newStart = new StartState(stateId++);
		State newAccept = new AcceptedState(stateId++);
		
		// 3.) Combine states from the two popped NFA's
		ArrayList<State> newStates = new ArrayList<State>();
		
		// 3.1.) Add new start state
		newStates.add(newStart);
		
		// 3.2.) Add states from nfa1 and nfa2
		newStates.addAll(nfa1.getStates());
		newStates.addAll(nfa2.getStates());
		
		// 3.3.) Add new accept/end state
		newStates.add(newAccept);
		
		// 4.) Combine both old nfa's transitions with new transitions
		ArrayList<DeltaFunction> newTransitions = new ArrayList<DeltaFunction>();
		
		// 4.1) Add nfa1 and nfa2 transitions
		newTransitions.addAll(nfa1.getDelta());
		newTransitions.addAll(nfa2.getDelta());
		
		// 4.2) Add Delta(newStart, ?) = {nfa1.startState, nfa2.startState}
		ArrayList<State> newStartDeltaEndStates = new ArrayList<State>();
		newStartDeltaEndStates.add(nfa1.getStartState());
		newStartDeltaEndStates.add(nfa2.getStartState());
		newTransitions.add(new NFADeltaFunction(newStart, "?", newStartDeltaEndStates));
		
		// 4.3) Add Delta(nfa1.acceptState, ?) = {newAccept}
		//      Add Delta(nfa2.acceptState, ?) = {newAccept}
		ArrayList<State> newEndDeltaEndStates = new ArrayList<State>();
		newEndDeltaEndStates.add(newAccept);
		
		// Don't override any existing transitions on nfa1's or nfa2's old end accepting state
		if (nfa1.getAccepetedState() != null) {
			newEndDeltaEndStates.addAll(nfa1.getAccepetedState().children);
		}// if
		
		if (nfa2.getAccepetedState() != null) {
			newEndDeltaEndStates.addAll(nfa2.getAccepetedState().children);
		}// if
		
		newTransitions.add(new NFADeltaFunction(nfa1.getAccepetedState(), "?", newEndDeltaEndStates));
		newTransitions.add(new NFADeltaFunction(nfa2.getAccepetedState(), "?", newEndDeltaEndStates));
		
		NFA newNfa = new NFA(newStates, this.alphabet, newStart, newTransitions, newAccept, nfaId++);
		newNfa.toString();
		
		// Create new NFA and push onto stack
		 this.nfaStack.push(newNfa);
	}// union
	
	/**
	 * Concatenates two Atomic NFA's
	 */
	private void concatenation() {
		System.out.println("Concatenating 2 NFAs: ");
		System.out.println(Util.divider);
		
		// 1.) Get the two NFA's off the stack and effectively 
		// throw away by not returning them to the stack.
		NFA nfa2 = this.nfaStack.pop();
		NFA nfa1 = this.nfaStack.pop();
		
		// 1.1.) Only clean nfa1 end accept state flags
		//       and nfa2 start states flags
		nfa1.getAccepetedState().resetFlags();
		nfa2.getStartState().resetFlags();
				
		// 2.) Combine states from the two popped NFA's
		ArrayList<State> newStates = new ArrayList<State>();
		
		// 2.2.) Add states from nfa1 and nfa2
		newStates.addAll(nfa1.getStates());
		newStates.addAll(nfa2.getStates());
		
		// 3.) Combine both old nfa's transitions with new transitions
		ArrayList<DeltaFunction> newTransitions = new ArrayList<DeltaFunction>();
		
		// 3.1) Add nfa1 and nfa2 transitions
		newTransitions.addAll(nfa1.getDelta());
		newTransitions.addAll(nfa2.getDelta());
		
		// 3.2) Add Delta(nfa1.acceptState, ?) = {nfa2.startState, [nfa1.startState for kleene star]}
		ArrayList<State> newStartDeltaEndStates = new ArrayList<State>();
		newStartDeltaEndStates.add(nfa2.getStartState());
		
		if (nfa1.getAccepetedState() != null) {
			newStartDeltaEndStates.addAll(nfa1.getAccepetedState().children); // Avoid overwriting nfa1's end accept state transitions
		}// if
		
		newTransitions.add(new NFADeltaFunction(nfa1.getAccepetedState(), "?", newStartDeltaEndStates));
		
		NFA newNFA = new NFA(newStates, this.alphabet, nfa1.getStartState(), newTransitions, nfa2.getAccepetedState(), nfaId++);
		newNFA.toString();
		
		// 4.) Create new NFA and push onto stack
		 this.nfaStack.push(newNFA);
	}// concatenation
	
	/**
	 * Applies a kleene star operation to the NFA
	 */
	private void kleeneStar() {
		System.out.println("Kleene Staring NFA: ");
		System.out.println(Util.divider);
		
		// 1.) Get one NFA off the stack and effectively 
		// throw away by not returning it to the stack.
		NFA nfa = this.nfaStack.pop();
		
		// 1.1.) Clean start and accepting states flags
		nfa.getStartState().resetFlags();
		nfa.getAccepetedState().resetFlags();
		
		// 2.) Create new start and end states for union
		State newStart = new StartState(stateId++);
		State newAccept = new AcceptedState(stateId++);
		
		// 3.) Combine states the popped NFA
		ArrayList<State> newStates = new ArrayList<State>();
		
		// 3.1.) Add new start state
		newStates.add(newStart);
		
		// 3.2.) Add states from NFA
		newStates.addAll(nfa.getStates());
		
		// 3.3.) Add new accept/end state
		newStates.add(newAccept);
		
		// 4.) Combine old NFA's transitions with new transitions
		ArrayList<DeltaFunction> newTransitions = new ArrayList<DeltaFunction>();
		
		// 4.1) Add old NFA transitions
		newTransitions.addAll(nfa.getDelta());
		
		// 4.2) Add Delta(newStart, ?) = {nfa.startState}
		ArrayList<State> newStartDeltaEndStates = new ArrayList<State>();
		newStartDeltaEndStates.add(nfa.getStartState());
		newStartDeltaEndStates.add(newAccept); // Spontaneous transition from start to end
		newTransitions.add(new NFADeltaFunction(newStart, "?", newStartDeltaEndStates));
		
		// 4.3) Add Delta(nfa.acceptState, ?) = {newAccept}
		ArrayList<State> newEndDeltaEndStates = new ArrayList<State>();
		newEndDeltaEndStates.add(newAccept);
		newEndDeltaEndStates.add(nfa.getStartState()); // Looping ? transition
		
		if (nfa.getAccepetedState() != null) {
			newEndDeltaEndStates.addAll(nfa.getAccepetedState().children); // Avoid overwriting NFA's old end accept state transitions
		}// if
		
		newTransitions.add(new NFADeltaFunction(nfa.getAccepetedState(), "?", newEndDeltaEndStates));
		
		// Create new NFA and push onto stack
		NFA newNFA = new NFA(newStates, this.alphabet, newStart, newTransitions, newAccept, nfaId++);
		newNFA.toString();
		
		// Push nfa back on stack
		this.nfaStack.push(newNFA);
	}// kleeneStar
}// class
