package grep.finiteautomata.states;

public class AcceptedState extends State{
	public AcceptedState(int name) {
		super(name);
		super.isAccepting = true;
	}// constructor
}// AcceptedState
