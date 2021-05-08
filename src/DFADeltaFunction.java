import java.util.HashSet;

public class DFADeltaFunction {
	private HashSet<State> inputStateKey = null;
	private HashSet<State> outputStateKey = null;
	private String inputSymbol = "[Insert Transition]";
	
	DFADeltaFunction(HashSet<State> inputStateKey, String inputSymbol, HashSet<State> outputState) {
		this.inputStateKey = inputStateKey;
		this.inputSymbol = inputSymbol;
		this.outputStateKey = outputState;
	}// constructor

	public HashSet<State> getInputState() {
		return inputStateKey;
	}// getStartingState

	public String getInputSymbol() {
		return inputSymbol;
	}// getTransitionSymbol

	public HashSet<State> getOutputState() {
		return outputStateKey;
	}// getEndingStates
}// class