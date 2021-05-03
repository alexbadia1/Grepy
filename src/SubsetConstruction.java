public class SubsetConstruction {
	private NFA nfa;
	private DFA dfa;
	
	public SubsetConstruction(NFA nfa) {
		super();
		this.nfa = nfa;
	}// constructor
	
	public NFA getNfa() {
		return this.nfa;
	}// getNfa

	public DFA getDfa() {
		return this.dfa;
	}// getDfa
	
	public void subsetConstruction() {
		/**
		 * 1.) Make a transition table for the NFA
		 * 
		 * 2.) Remove epsilon transitions from the NFA
		 * 
		 * 3.) Use subset construction to create the following DFA
		 * 
		 * 4.) Implement Hopcroft's algorithm to minimize the DFA
		 */
	}// subsetConstruction
}// class
