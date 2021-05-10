package grep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import grep.finiteautomata.dfa.SubsetConstruction;
import grep.finiteautomata.nfa.NFA;
import grep.finiteautomata.nfa.ThompsonConstruction;
import grep.lexer.Lexer;
import grep.parser.Parser;
import grep.stackmachine.StackMachine;

public class Grepy {
	public static void main (String[] args) {
		Arrays.toString(args);
		
		String nfaFilename = "nfa";
		String dfaFilename = "dfa";
		String testFilename = "test";
		
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Regular Expression: ");
		
		String regex = scanner.nextLine();
		System.out.println();
		
		ArrayList<String> sigma = new ArrayList<String>();
		
		for (int i = 97; i < 100; ++i) {
			sigma.add(Character.toString(i));
		}// for
		
		// Lex regular expression into tokens
		Lexer lexer = new Lexer();
		lexer.lex(regex);
		lexer.printTokenList();
		
		// Parse tokens to take into account precedence 
		Parser parser = new Parser();
		parser.parse(lexer.getTokenArrayList());
		parser.printTokenList();
		
		// Create the NFA using Thompson Construction
		ThompsonConstruction thompsonConstructor = new ThompsonConstruction(parser.getParsedTokens(), sigma);
		NFA nfa = thompsonConstructor.thompsonConstruction();
		nfa.toString();
		nfa.toGraph();
		
		// Convert NFA to DFA using subset/powerset construction
		SubsetConstruction s = new SubsetConstruction(nfa);
		s.subsetConstruction();
		System.out.print("Final DFA: ");
		s.getDfa().toString();
		s.getDfa().toGraph();
		s.getDfa().export(nfaFilename);
		
		
		StackMachine m = new StackMachine(s.getDfa());
		// Keep testing
		while (true) {
			System.out.print("Test: ");
			String test = scanner.nextLine();
			System.out.println();
			m.test(test);
		}// while
	}// main
}// class