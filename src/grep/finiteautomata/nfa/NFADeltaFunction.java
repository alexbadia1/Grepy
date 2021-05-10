package grep.finiteautomata.nfa;
import java.util.ArrayList;

import grep.finiteautomata.DeltaFunction;
import grep.finiteautomata.states.State;

public class NFADeltaFunction extends DeltaFunction {
	public NFADeltaFunction(State startingState, String transitionSymbol, ArrayList<State> endingStates) {
		super(startingState, transitionSymbol, endingStates);
	}// constructor
	
	public ArrayList<State> getAcceptedStates() {
		return super.endingStates;
	}// getEndingStates
	
	public void addAcceptedStates(ArrayList<State> newStates) {
		super.startingState.children.addAll(newStates);
		super.endingStates.addAll(newStates);
	}// addEndState
}// NFADeltaFunction
