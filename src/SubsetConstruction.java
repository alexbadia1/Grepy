import java.util.HashSet;
import java.util.Hashtable;

public class SubsetConstruction {
	private NFA nfa;
	private DFA dfa;
	private Hashtable<State, HashSet<State>> eClosureTable;
	private String[] alphabet = {"a", "b", "c"};
	
	public SubsetConstruction(NFA nfa) {
		super();
		this.nfa = nfa;
		this.dfa = new DFA();
		this.eClosureTable = new Hashtable<State, HashSet<State>>();
	}// constructor
	
	public NFA getNfa() {
		return this.nfa;
	}// getNfa

	public DFA getDfa() {
		return this.dfa;
	}// getDfa
	
	public void subsetConstruction() {
		// Calculate the epsilon enclosure for every state in the NFA, for later use
		System.out.println("Calculating e-closure for all nfa states: ");
		for(State s: this.nfa.states) {
			HashSet<State> eClosure = this.epsilonClosure(s, new HashSet<State>());
			if(!this.eClosureTable.containsKey(s)) {
				this.eClosureTable.put(s, eClosure);
			}// if
		}// for
		System.out.println(this.eClosureTable.toString());
		
		// Let epsilon-closure(nfa.start_state) be the starting DFA state
		//
		// Begin subset construction from the nfa's starting node
		this.subsetConstructionHelper(this.eClosureTable.get(this.nfa.startState), true);
	}// subsetConstruction
	
	private void subsetConstructionHelper(HashSet<State> currState, boolean isStartingState) {
		// For every symbol in the alphabet perform the delta function
		for (String symbol: this.alphabet) {
			System.out.print("\nPerforming Delta'({ ");
			for (State currS: currState) {
				System.out.print(String.valueOf(currS.name) + " ");
			}// for
			System.out.println("}, " + symbol + ") = ");
						
			HashSet<State> newDfaState = new HashSet<State>();
			
			System.out.println("  Unioning deltas...");
			for (State state: currState) {
				System.out.println("    Delta(" + String.valueOf(state.name) + ", " + symbol +  ")");
				
				DeltaFunction df = this.searchForDeltaFunction(state, symbol);
				if (df != null) {
					for(State deltaEndState: df.getEndingStates()) {
						newDfaState.add(deltaEndState);
					}// for
				}// if
			}// for
			
			// Print out newDfaState
			System.out.print("  Raw new DFA state (Before E-Closure): { ");
			for (State s: newDfaState) {
				System.out.print(String.valueOf(s.name) + " ");
			}// for
			System.out.println("}");
			
			// Don't forget to take the e-closure of the union of the delta functions
			HashSet<State> eClosureOfNewDfaState = new HashSet<State>();
			for (State s: newDfaState) {
				eClosureOfNewDfaState.addAll(this.epsilonClosure(s, new HashSet<State>()));
			}// for
			
			// Print out newDfaState
			System.out.print("  Epsilon-Closure new DFA state: { ");
			for (State s: eClosureOfNewDfaState) {
				System.out.print(String.valueOf(s.name) + " ");
			}// for
			System.out.println("}");
						
			// Add to hash table of DFA states if it already doesn't exist
			if (!eClosureOfNewDfaState.isEmpty()) {
				if (!this.dfa.states.containsKey(eClosureOfNewDfaState)) {
					System.out.println("\n\n\nI wass called\n\n\n");
					
					// Adds State(s) to DFA
					this.dfa.states.put(eClosureOfNewDfaState, new State(this.dfa.useStateId(), true, false));
					
					// Adds the transition for the state
					this.dfa.transitions.add(new DFADeltaFunction(currState, symbol, eClosureOfNewDfaState));
					
					// Sets DFA Start state
					if (isStartingState) {
						this.dfa.states.put(currState, new State(this.dfa.useStateId(), true, false));
						this.dfa.setStartingState(currState);
					}// if
					
					// Sets DFA Accepting State(s)
					setAcceptingStates: for (State eCloseState: eClosureOfNewDfaState) {
						if(eCloseState.isAccepting) {
							this.dfa.addAcceptingState(eClosureOfNewDfaState);
							break setAcceptingStates;
						}// if
					}// for
					
					this.subsetConstructionHelper(eClosureOfNewDfaState, false);
				}// if
				
				else {
					// Adds the transition for the states that loop back on themselves
					this.dfa.transitions.add(new DFADeltaFunction(currState, symbol, eClosureOfNewDfaState));
				}
			}// if
		}// for
	}// subSetConstruction
	
	private HashSet<State> epsilonClosure(State currentState, HashSet<State> epsilonClosure) {
		epsilonClosure.add(currentState);
		
		for(DeltaFunction transition: this.nfa.transitions) {
			
			if(transition.getStartingState().name == currentState.name 
					&& transition.getTransitionSymbol().equals("?")) {
				for (State state: transition.getEndingStates()) {
					this.epsilonClosure(state, epsilonClosure);
				}// for
			}// if
		}// for
		
		return epsilonClosure;
	}// epsilonClosure
	
	private DeltaFunction searchForDeltaFunction(State inputState, String inputSymbol) {
		for (DeltaFunction df: this.nfa.transitions) {
			if (df.getStartingState().name == inputState.name 
				&& df.getTransitionSymbol().equals(inputSymbol)) {
				return df;
			}// if
		}// for
		
		return null;
	}// searchForDeltaFunction
	
	private String convertToName(HashSet<State> states) {
		String name = "";
		for(State s: states) {
			name += "q" + s.name;
		}// for
		
		return name;
	}// convertStatesToName
}// class
