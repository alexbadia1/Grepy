import java.util.ArrayList;

public class DeltaFunction {
	private State startingState = null;
	private String transitionSymbol = "[Insert Transition]";
	private ArrayList<State> endingStates = new ArrayList<State>();
	
	DeltaFunction(State startingState, String transitionSymbol, ArrayList<State> endingStates) {
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
}// class
