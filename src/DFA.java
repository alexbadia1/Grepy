import java.util.ArrayList;

public class DFA {
	public State startState = null;
	public ArrayList<State> states = new ArrayList<State>();
	public ArrayList<DeltaFunction> transitions = new ArrayList<DeltaFunction>();
	public ArrayList<State> acceptingStates = new ArrayList<State>();
	
	public DFA(State startState, ArrayList<State> states, ArrayList<DeltaFunction> transitions, ArrayList<State> acceptingStates) {
		super();
		this.startState = startState;
		this.states = states;
		this.transitions = transitions;
		this.acceptingStates = acceptingStates;
	}// constructor
	
	private ArrayList<ArrayList<DeltaFunction>> searchForDeltaFunctions(State inputState, String inputSymbol) {
		ArrayList<ArrayList<DeltaFunction>> functions = new ArrayList<ArrayList<DeltaFunction>>();
		ArrayList<DeltaFunction> correct = new ArrayList<DeltaFunction>();
		ArrayList<DeltaFunction> epsilons = new ArrayList<DeltaFunction>();
		for(int i = 0; i < transitions.size(); ++i) {
			// Get a delta transition
			DeltaFunction transition = transitions.get(i);
			if (transition.getStartingState().name == inputState.name ) {
				// See if it's the correct delta transition
				if(transition.getTransitionSymbol().compareTo(inputSymbol) == 0) {
					// Add noon-delta transitions later
					correct.add(transition);
				}// if
				
				// See if it's an epsilon transition
				else if (transition.getTransitionSymbol().compareTo("?") == 0) {
					
					// Add epsilon transitions for the state
					//
					// Make sure epsilon transitions are performed first without consuming input
					epsilons.add(transition);
				}// else if
			}// if
		}// for
		functions.add(epsilons);
		functions.add(correct);
		return functions;
	}// searchForDeltaFunction
	
	/**
	 * Searches an NFA for the corresponding delta 
	 * function and returns the corresponding ending state(s).
	 * 
	 * @param inputState symbol for transition from starting state
	 * @param inputSymbol starting state
	 * @return ArrayList of ending states, null if no transition exists
	 */
	private ArrayList<State> deltaFunction(State inputState, String inputSymbol) {
		// Look for corresponding transition
		int index = 0;
		boolean found = false;
		while(index < transitions.size() && !found) {
			// Get a delta transition
			DeltaFunction transition = transitions.get(index);
			
			// See if it's the correct delta transition
			if( transition.getStartingState().name == inputState.name 
			    && transition.getTransitionSymbol() == inputSymbol) {
				found = true;
				
				// Return the correct ending states
				return transition.getEndingStates();
			}// if
			
			// Keep searching
			index++;
		}// while
		
		return null;
	}// deltaFunction
	
    /**
     * Test whether there is some path for the NFA to reach
     * an accepting state, from the given state and reading
     * the given string at the given character position.
     * @param s the current state
     * @param in the input string
     * @param pos the index of the next character in the string
     * @return true iff the NFA accepts on some path
     */
	private boolean deltaStar(State currentState, String inputString, int pos) {
		// If no more symbols to read
		if (pos == inputString.length()) {
			
			// Accept iff current, technically final, state is in DFA's accepted state(s)
			State match = this.acceptingStates.stream().filter(state -> currentState.name == state.name).findFirst().orElse(null);
			return match != null;
		}// if

		// Read first character in input string
		String currentCharacter = Character.toString(inputString.charAt(pos++));
		
		// Initialize list to hold output states for delta function
		ArrayList<ArrayList<State>> nextStates = new ArrayList<ArrayList<State>>();
		
		// Initialize array to hold delta functions we need to perform
		ArrayList<ArrayList<DeltaFunction>> deltaFunctionsToDo;
		deltaFunctionsToDo = this.searchForDeltaFunctions(currentState, currentCharacter);
		
		if (currentCharacter.contains("a") || currentCharacter.contains("b")) {
			
			// Perform delta function on current characters and epsilon characters
			for (int i = 0; i < deltaFunctionsToDo.size(); ++i) {
				nextStates.add(new ArrayList<State>());
				for (DeltaFunction deltaFunction: deltaFunctionsToDo.get(i)) {
					nextStates.get(i).addAll(deltaFunction.getEndingStates());
				}// for
			}// for
			
			System.out.print("\u03B4(q"+ String.valueOf(currentState.name) +", "+ currentCharacter + ") -> ");
			
			for (State state: nextStates.get(0)) {
				System.out.print("Correct: ");
				System.out.print("q" + String.valueOf(state.name) + " ");
			}// for
			
			
			for (State state: nextStates.get(1)) {
				System.out.print("Epsilon: ");
				System.out.print("q" + String.valueOf(state.name));
			}// for
			
			System.out.println();
		}// if
		
		else {
			System.out.println("Invalid alphabet symbol.  Please use {Alphabet learned from .txt file}");
			return false; // no transition, just reject
		}// else
		
		// At this point, nextStates is an array of 0 or more next states.
		// Try each move recursively and if it leads to an accepting state return true.
		for (State state: nextStates.get(0)) {
			// Perform delta functions without consuming input
			if (deltaStar(state, inputString, (pos - 1))) {return true;}// if
		}// for
		
		for (State state: nextStates.get(1)) {
			// Consume character and perform delta functions on output states
			if (deltaStar(state, inputString, pos)) {return true;}// if
		}// for
		
		System.out.println("all moves fail, return false");
		return false; // all moves fail, return false
	}// deltaStar


    /**
     * Test whether the NFA accepts the string.
     * @param in the String to test
     * @return true iff the NFA accepts on some path
     */
	public boolean accepts(String in) {
		return this.deltaStar(this.startState, in, 0); // start in q0 at char 0
    }//accepts
	

	public String toString() {
		String states = "{";
		for (State state: this.states) {
			states += state.name  + ", ";
		}// for
		states += "}";
		
		String deltas = "{\n\t";
		for (DeltaFunction delta: this.transitions) {
			String endStates = "{ ";
			for (State deltaState: delta.getEndingStates()) {
				endStates += deltaState.name  + " ";
			}// for
			endStates += "}";
			deltas += "";
			
			
			deltas += "Delta(" + String.valueOf(delta.getStartingState().name) + ", " 
					+ delta.getTransitionSymbol() + ") = " 
					+ endStates + "\n\t";
		}// for
		deltas += "}\n";
		
		String acceptingStates = "{";
		for (State state: this.acceptingStates) {
			acceptingStates += state.name  + ", ";
		}// for
		acceptingStates += "}";
		
		String ans = "";
		ans += "NFA: \n"; 
		ans += "Start State = " + String.valueOf(this.startState.name) + "\n";
		ans += "States = " + states + "\n";
		ans += "Transitions = " + deltas;
		ans += "Accepting State = " + acceptingStates;
		
		return ans;
	}// toString
}// NFA