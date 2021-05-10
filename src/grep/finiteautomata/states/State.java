package grep.finiteautomata.states;

import java.util.ArrayList;

public class State {
	public int name = 0;
	public boolean isStart;
	public boolean isAccepting;
	public ArrayList<State> children;
	
	public State(int name) {
		this.name = name;
		
		this.children = new ArrayList<State>();
	}// constructor
	
	public void resetFlags() {
		this.isStart = false;
		this.isAccepting = false;
	}// resetFlags
}// class