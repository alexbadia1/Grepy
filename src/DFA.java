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
		// Print out the all states
		String states = "{";
		int count = 0;
		for (State state: this.states.values()) {
			if (count < this.states.values().size() - 1) {
				states += state.name  + ", ";
			}// if
			
			else {
				states += state.name;
			}// else
			count++;
		}// for
		states += "}";
		
		// Print out deltas
		String deltas = "{\n\t";
		for (DFADeltaFunction delta: this.transitions) {
			String endState = "{ " + String.valueOf(this.states.get(delta.getOutputState()).name)  + " }";
			
			deltas += "Delta(" + String.valueOf(this.states.get(delta.getInputState()).name) + ", " 
					+ delta.getInputSymbol() + ") = " 
					+ endState + "\n\t";
		}// for
		deltas += "}\n";
		
		// Print out the accepted states
		String acceptingStates = "{";
		int acceptCount = 0;
		for (HashSet<State> acceptStateKey: this.acceptingStateKeys) {
			if (acceptCount < this.acceptingStateKeys.size() - 1) {
				acceptingStates += String.valueOf(this.states.get(acceptStateKey).name)  + ", ";
			}// if
			else {
				acceptingStates += String.valueOf(this.states.get(acceptStateKey).name);
			}// else
			acceptCount++;
		}// for
		acceptingStates += "}";
		
		String ans = "\n\n";
		ans += "DFA: \n"; 
		ans += "Start State = " + String.valueOf(this.states.get(this.startingStateKey).name) + "\n";
		ans += "States = " + states + "\n";
		ans += "Transitions = " + deltas;
		ans += "Accepting State = " + acceptingStates;
		
		return ans;
	}// toString
}// NFA