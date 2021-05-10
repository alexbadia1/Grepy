package grep.stackmachine;

import grep.finiteautomata.states.State;

public class StackProduction {
	private String read;
	private State pop;
	private State push;
	
	public StackProduction(String read, State pop, State push) {
		super();
		this.read = read;
		this.pop = pop;
		this.push = push;
	}// constructor

	public String getRead() {
		return read;
	}// getRead

	public State getPop() {
		return pop;
	}// getPop

	public State getPush() {
		return push;
	}// getPush
}// StackProduction
