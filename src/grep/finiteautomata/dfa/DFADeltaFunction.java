package grep.finiteautomata.dfa;
import java.util.ArrayList;

import grep.finiteautomata.DeltaFunction;
import grep.finiteautomata.states.State;

public class DFADeltaFunction extends DeltaFunction {
	public DFADeltaFunction(State inputState, String inputSymbol, State outputState) {
		super(inputState, inputSymbol, new ArrayList<State>());
		this.endingStates.add(outputState);
	}// constructor
	
	public void addAcceptedState(State newState) {
		super.startingState.children.add(newState);
		super.endingStates.add(newState);
	}// addEndState
	
	public State getAcceptedState() {
		if (super.endingStates.size() != 1) {return null;}
		return super.endingStates.get(0);
	}// addEndState
}// class