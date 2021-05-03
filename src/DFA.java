import java.util.ArrayList;

public class DFA {
	public State startState = null;
	public ArrayList<State> states = new ArrayList<State>();
	public ArrayList<Delta> transitions = new ArrayList<Delta>();
	public ArrayList<State> acceptingStates = new ArrayList<State>();
	
	public DFA(State startState, ArrayList<State> states, ArrayList<Delta> transitions, ArrayList<State> acceptingStates) {
		super();
		this.startState = startState;
		this.states = states;
		this.transitions = transitions;
		this.acceptingStates = acceptingStates;
	}// constructor

	public String toString() {
		String states = "{";
		for (State state: this.states) {
			states += state.name  + ", ";
		}// for
		states += "}";
		
		String deltas = "{\n\t";
		for (Delta delta: this.transitions) {
			String endStates = "{ ";
			for (State deltaState: delta.getEndingStates()) {
				endStates += deltaState.name  + " ";
			}// for
			endStates += "}";
			deltas += "";
			
			
			deltas += "Delta(" + String.valueOf(delta.getStartingState().name) + ", " 
					+ delta.getTransitionSymbol() + ") = " 
					+ endStates + "\n\t";
		}// for
		deltas += "}\n";
		
		String acceptingStates = "{";
		for (State state: this.acceptingStates) {
			acceptingStates += state.name  + ", ";
		}// for
		acceptingStates += "}";
		
		String ans = "";
		ans += "NFA: \n"; 
		ans += "Start State = " + String.valueOf(this.startState.name) + "\n";
		ans += "States = " + states + "\n";
		ans += "Transitions = " + deltas;
		ans += "Accepting State = " + acceptingStates;
		
		return ans;
	}// toString
}// NFA