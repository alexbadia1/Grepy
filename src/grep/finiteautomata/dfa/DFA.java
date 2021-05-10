package grep.finiteautomata.dfa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import grep.finiteautomata.DeltaFunction;
import grep.finiteautomata.FiniteAutomata;
import grep.finiteautomata.states.State;

public class DFA extends FiniteAutomata {
	public Hashtable<HashSet<State>, State> subsetStateMap;
	private int stateId;
	private HashSet<State> startingStateKey;
	private ArrayList<HashSet<State>> acceptingStateKeys;
	
	
	public DFA(ArrayList<String> alphabet, int id) {
		super(new ArrayList<State>(), 
				alphabet, 
				new ArrayList<State>(), 
				new ArrayList<DeltaFunction>(), 
				new ArrayList<State>(), id);
		this.stateId = 0;
		this.subsetStateMap = new Hashtable<HashSet<State>, State>();
		this.startingStateKey = new HashSet<State>();
		this.acceptingStateKeys = new ArrayList<HashSet<State>>();
		
	}// constructor
	
	public State getStartingState() {
		return this.subsetStateMap.get(this.startingStateKey);
	}// setStartingState
	
	public void setStartingStateKey(HashSet<State> nfaStates) {;
		this.startingStateKey = nfaStates;
	}// setStartingState
	
	public void setStartState(State newStartState) {;
		super.startStates.add(newStartState);
	}// setStartingState
	
	public ArrayList<State> getAcceptingStates() {
		ArrayList<State> acceptingStates = new ArrayList<State>();
		
		for (HashSet<State> key: this.acceptingStateKeys) {
			acceptingStates.add(this.subsetStateMap.get(key));
		}// for
		
		return acceptingStates;
	}// getAcceptingStates
	
	public void setAcceptingStatesKeys(ArrayList<HashSet<State>> nfaStates) {;
		this.acceptingStateKeys = nfaStates;
	}// setStartingState
	
	public void addAcceptedStates(State state) {
		super.acceptedStates.add(state);
	}// setStartingState
	
	public void addAcceptingStateKeys(HashSet<State> nfaStates) {
		this.acceptingStateKeys.add(nfaStates);
	}// addAcceptingState
	
	public int useStateId () {
		return this.stateId++;
	}// useStateId
}// NFA