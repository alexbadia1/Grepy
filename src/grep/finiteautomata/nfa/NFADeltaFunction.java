package grep.finiteautomata.nfa;
import java.util.ArrayList;

import grep.finiteautomata.State;

public class NFADeltaFunction {
	private State startingState = null;
	private String transitionSymbol = "[Insert Transition]";
	private ArrayList<State> endingStates = new ArrayList<State>();
	
	public NFADeltaFunction(State startingState, String transitionSymbol, ArrayList<State> endingStates) {
		this.startingState = startingState;
		this.transitionSymbol = transitionSymbol;
		this.endingStates = endingStates;
		
		// Perform the "delta function" by 
		// linking the input state to the ending states
		this.startingState.children = this.endingStates;
	}// constructor

	public State getStartingState() {
		return startingState;
	}// getStartingState

	public String getTransitionSymbol() {
		return transitionSymbol;
	}// getTransitionSymbol

	public ArrayList<State> getEndingStates() {
		return endingStates;
	}// getEndingStates
	
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
		
		System.out.println(ans);
		
		return ans;
	}// toString
}// NFADeltaFunction
