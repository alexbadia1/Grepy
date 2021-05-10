package grep.finiteautomata.states;

public class StartState extends State{
	public StartState(int name) {
		super(name);
		super.isStart = true;
	}// constructor
}// AcceptedState
