package grep.finiteautomata;
import java.util.ArrayList;

import grep.finiteautomata.states.State;

public class DeltaFunction {
	protected State startingState;
	protected String transitionSymbol = "[Insert Transition]";
	protected ArrayList<State> endingStates;
	
	public DeltaFunction(State startingState, String transitionSymbol, ArrayList<State> endingStates) {
		this.startingState = startingState;
		this.transitionSymbol = transitionSymbol;
		this.endingStates = endingStates;
		
		// Actually link start state to children
		this.startingState.children.addAll(endingStates);
	}// constructor
	
	public State getStartingState() {
		return startingState;
	}// getStartingState

	public String getTransitionSymbol() {
		return transitionSymbol;
	}// getTransitionSymbol
	
	public String toString() {
		String ans = "Delta";
		ans += "(" + String.valueOf(this.startingState.name) + ", " + this.transitionSymbol +") = ";
		ans += "{";
		for (int i = 0; i < endingStates.size(); ++i) {
			if (i < endingStates.size() - 1) {
				ans += endingStates.get(i).name + ", ";
			}// if
			
			else {
				ans += endingStates.get(i).name;
			}// else
		}// for
		ans += "}";

		return ans;
	}// toString
}// DeltaFunction