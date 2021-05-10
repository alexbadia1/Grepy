package grep.finiteautomata.nfa;

import java.util.ArrayList;

import grep.finiteautomata.DeltaFunction;
import grep.finiteautomata.FiniteAutomata;
import grep.finiteautomata.states.State;

public class NFA extends FiniteAutomata{	
	public NFA(
			ArrayList<State> states, 
			ArrayList<String> alphabet, 
			State startState, 
			ArrayList<DeltaFunction> transitions,
			State acceptedState,
			int id) {
		super(states, alphabet, new ArrayList<State>(), transitions, new ArrayList<State>(), id, "NFA");
		super.startStates.add(startState);
		super.acceptedStates.add(acceptedState);
	}// constructor
	
	/**
	 * Thompson constructed NFA's only have one accepting state
	 * @param newAcceptedState
	 * @return
	 */
	public void setAccepetedState(State newAcceptedState) {
		if (acceptedStates.size() > 0) {
			super.acceptedStates = new ArrayList<State>();
		}// if
		
		super.acceptedStates.add(newAcceptedState);
	}// setAccepetedStates
	
	public State getAccepetedState() {
		if (acceptedStates.size() > 0) {
			return super.acceptedStates.get(0);
		}// if
		
		return null;
	}// setAccepetedStates
	
	/**
	 * Thompson constructed NFA's only have one start state
	 * @param newAcceptedState
	 * @return
	 */
	public void setStartState(State newStartState) {
		if (startStates.size() > 0) {
			super.startStates = new ArrayList<State>();
		}// if
		
		super.startStates.add(newStartState);
	}// setStartState
	
	public State getStartState() {
		if (startStates.size() > 0) {
			return super.startStates.get(0);
		}// if
		
		return null;
	}// getStartState
}// NFA
