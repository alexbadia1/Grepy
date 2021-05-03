import java.util.ArrayList;

public class State {
	public int name = 0;
	public boolean isStart = false;
	public boolean isAccepting = false;
	public ArrayList<State> children = new ArrayList<State>();
	
	public State(int name, boolean isStart, boolean isAccepting) {
		super();
		this.name = name;
		this.isStart = isStart;
		this.isAccepting = isAccepting;
	}// constructor
	
	public void resetFlags() {
		this.isAccepting = false;
		this.isStart = false;
	}// resetFlags
}// class
