import java.util.Scanner;

public class Grepy {
	public static void main (String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Regular Expression: ");
		
		String regex = scanner.nextLine();
		System.out.println();
		
		// Lex regular expression into tokens
		Lexer lexer = new Lexer();
		lexer.lex(regex);
		lexer.printTokenList();
		
		// Parse tokens to take into account precedence 
		Parser parser = new Parser();
		parser.parse(lexer.getTokenArrayList());
		parser.printTokenList();
		
		// Create the NFA using Thompson Construction
		ThompsonConstruction thompsonConstructor = new ThompsonConstruction(parser.getParsedTokens());
		NFA nfa = thompsonConstructor.thompsonConstruction();
		nfa.toString();
		nfa.toGraph();
		
		// Convert NFA to DFA using subset/powerset construction
		SubsetConstruction s = new SubsetConstruction(nfa);
		s.subsetConstruction();
		System.out.println(s.getDfa().toString());
		
		// Keep testing
//		while (true) {
//			System.out.print("Test: ");
//			String test = scanner.nextLine();
//			System.out.println();
////			System.out.println(nfa.accepts(test));
//		}// while
	}// main
}// class
