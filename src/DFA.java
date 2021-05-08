import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

public class DFA {
	public Hashtable<HashSet<State>, State> states;
	public ArrayList<DFADeltaFunction> transitions;
	private int stateId;
	private HashSet<State> startingStateKey;
	private ArrayList<HashSet<State>> acceptingStateKeys;
	
	public DFA() {
		this.stateId = 0;
		this.states = new Hashtable<HashSet<State>, State>();
		this.transitions = new ArrayList<DFADeltaFunction>();
		this.startingStateKey = new HashSet<State>();
		this.acceptingStateKeys = new ArrayList<HashSet<State>>();
	}// constructor
	
	public State getStartingState() {
		return this.states.get(this.startingStateKey);
	}// setStartingState
	
	public void setStartingState(HashSet<State> nfaStates) {;
		this.startingStateKey = nfaStates;
	}// setStartingState
	
	public ArrayList<State> getAcceptingStates() {
		ArrayList<State> acceptingStates = new ArrayList<State>();
		
		for (HashSet<State> key: this.acceptingStateKeys) {
			acceptingStates.add(this.states.get(key));
		}// for
		
		return acceptingStates;
	}// getAcceptingStates
	
	public void setAcceptingStates(ArrayList<HashSet<State>> nfaStates) {;
		this.acceptingStateKeys = nfaStates;
	}// setStartingState
	
	public void addAcceptingState(HashSet<State> nfaStates) {
		this.acceptingStateKeys.add(nfaStates);
	}// addAcceptingState
	
	public int useStateId () {
		return this.stateId++;
	}// useStateId

	public String toString() {
		String states = "{";
		for (State state: this.states.values()) {
			states += state.name  + ", ";
		}// for
		states += "}";
		
		String deltas = "{\n\t";
		for (DFADeltaFunction delta: this.transitions) {
			String endState = "{ " + String.valueOf(this.states.get(delta.getOutputState()).name)  + " }";
			
			deltas += "Delta(" + String.valueOf(this.states.get(delta.getInputState()).name) + ", " 
					+ delta.getInputSymbol() + ") = " 
					+ endState + "\n\t";
		}// for
		deltas += "}\n";
		
		String acceptingStates = "{";
		for (HashSet<State> acceptStateKey: this.acceptingStateKeys) {
			acceptingStates += String.valueOf(this.states.get(acceptStateKey).name)  + ", ";
		}// for
		acceptingStates += "}";
		
		String ans = "";
		ans += "DFA: \n"; 
		ans += "Start State = " + String.valueOf(this.states.get(this.startingStateKey).name) + "\n";
		ans += "States = " + states + "\n";
		ans += "Transitions = " + deltas;
		ans += "Accepting State = " + acceptingStates;
		
		return ans;
	}// toString
}// NFA