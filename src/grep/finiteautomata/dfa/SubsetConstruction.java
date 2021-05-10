package grep.finiteautomata.dfa;
import java.util.HashSet;
import java.util.Hashtable;

import grep.finiteautomata.DeltaFunction;
import grep.finiteautomata.nfa.NFA;
import grep.finiteautomata.nfa.NFADeltaFunction;
import grep.finiteautomata.states.StartState;
import grep.finiteautomata.states.State;

public class SubsetConstruction {
	private NFA nfa;
	private DFA dfa;
	private Hashtable<State, HashSet<State>> eClosureTable;
	
	public SubsetConstruction(NFA nfa) {
		super();
		this.nfa = nfa;
		this.dfa = new DFA(nfa.getSigma(), nfa.getId());
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
		System.out.println("Calculating e-closure for all nfa states: " + this.nfa.getSigma());
		for(State s: this.nfa.getStates()) {
			HashSet<State> eClosure = this.epsilonClosure(s, new HashSet<State>());
			if(!this.eClosureTable.containsKey(s)) {
				this.eClosureTable.put(s, eClosure);
			}// if
		}// for
		System.out.println(this.eClosureTable.toString());
		
		// Let epsilon-closure(nfa.start_state) be the starting DFA state
		//
		// Begin subset construction from the nfa's starting node
		this.subsetConstruction(this.eClosureTable.get(this.nfa.getStartState()), true);
	}// subsetConstruction
	
	private void subsetConstruction(HashSet<State> currState, boolean isDfaStartingState) {
		// For every symbol in the alphabet perform the delta function
		for (String symbol: nfa.getSigma()) {
			System.out.print("\nPerforming Delta'({ ");
			for (State currS: currState) {
				System.out.print(String.valueOf(currS.name) + " ");
			}// for
			System.out.println("}, " + symbol + ") = ");
						
			// In subset contstruction, a DFA state is technically a set of NFA states
			HashSet<State> newDfaState = new HashSet<State>();
			
			System.out.println("  Unioning deltas...");
			
			// Perform the delta function for each state in the subset
			for (State state: currState) {
				System.out.println("    Delta(" + String.valueOf(state.name) + ", " + symbol +  ")");
				
				// Perform the NFA's delta function for each NFA state in the subset
				NFADeltaFunction nfaDeltaFunction = (NFADeltaFunction)this.searchForNfaDeltaFunction(state, symbol);
				
				// If a delta function was found, that is... if a move exists, add the end states as
				// part of the set of NFA states that make up the DFA state.
				if (nfaDeltaFunction != null) {
					for(State nfaDeltaAcceptedState: nfaDeltaFunction.getAcceptedStates()) {
						newDfaState.add(nfaDeltaAcceptedState);
					}// for
				}// if
			}// for
			
			// Print out newDfaState
			System.out.print("  Raw new DFA state (Before E-Closure): { ");
			for (State s: newDfaState) {
				System.out.print(String.valueOf(s.name) + " ");
			}// for
			System.out.println("}");
			
			// Take the e-closure of the current set of NFA states that make up the new DFA state
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
				if (!this.dfa.subsetStateMap.containsKey(eClosureOfNewDfaState)) {
					
					// Sets DFA Start state and puts start state into the hash table
					if (isDfaStartingState) {
						State singleStartState = new StartState(this.dfa.useStateId());
						this.dfa.subsetStateMap.put(currState, singleStartState);
						this.dfa.setStartingStateKey(currState);
						
						System.out.print("\n\n\nAssigning Start State: " + String.valueOf(singleStartState.name) + "\n\n\n");
						this.dfa.setStartState(singleStartState);
						this.dfa.addState(singleStartState);
						
						isDfaStartingState = false;
					}// if
					
					// Adds State(s) to DFA
					State singleEClosureOfNewDfaState = new State(this.dfa.useStateId());
					this.dfa.subsetStateMap.put(eClosureOfNewDfaState, singleEClosureOfNewDfaState);
					this.dfa.addState(singleEClosureOfNewDfaState);
					
					// Adds the transition for the state
					State singleCurrentState = this.dfa.subsetStateMap.get(currState);
					this.dfa.addDelta(new DFADeltaFunction(singleCurrentState, symbol, singleEClosureOfNewDfaState));
					
					// Sets DFA Accepting State(s)
					setAcceptingStates: for (State eCloseState: eClosureOfNewDfaState) {
						if(eCloseState.isAccepting) {
							this.dfa.addAcceptingStateKeys(eClosureOfNewDfaState);
							this.dfa.addAcceptedStates(singleEClosureOfNewDfaState);
							break setAcceptingStates;
						}// if
					}// for
					
					this.subsetConstruction(eClosureOfNewDfaState, false);
				}// if
				
				else {
					// Adds the transition for the states that loop back on themselves
					State singleCurrentState = this.dfa.subsetStateMap.get(currState);
					State singleEndState = this.dfa.subsetStateMap.get(eClosureOfNewDfaState);
					this.dfa.addDelta(new DFADeltaFunction(singleCurrentState, symbol, singleEndState));
				}// else
			}// if
		}// for
	}// subSetConstruction
	
	private HashSet<State> epsilonClosure(State currentState, HashSet<State> epsilonClosure) {
		epsilonClosure.add(currentState);
		
		for(DeltaFunction transition: this.nfa.getDelta()) {
			NFADeltaFunction nfaTransition = (NFADeltaFunction) transition;
			
			if(nfaTransition.getStartingState().name == currentState.name 
					&& nfaTransition.getTransitionSymbol().equals("?")) {
				for (State state: nfaTransition.getAcceptedStates()) {
					this.epsilonClosure(state, epsilonClosure);
				}// for
			}// if
		}// for
		
		return epsilonClosure;
	}// epsilonClosure
	
	private DeltaFunction searchForNfaDeltaFunction(State inputState, String inputSymbol) {
		for (DeltaFunction df: this.nfa.getDelta()) {
			if (df.getStartingState().name == inputState.name 
				&& df.getTransitionSymbol().equals(inputSymbol)) {
				return df;
			}// if
		}// for
		
		return null;
	}// searchForDeltaFunction
}// class
