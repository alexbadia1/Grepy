import java.util.Scanner;

public class Grepy {
	public static void main (String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Regular Expression: ");
		
		String regex = scanner.nextLine();
		System.out.println();
		Lexer lexer = new Lexer();
		lexer.lex(regex);
		lexer.printTokenList();
		
		// Parse tokens 
		Parser parser = new Parser();
		parser.parse(lexer.getTokenArrayList());
		parser.printTokenList();
		
		// Create the NFA using Thompson Construction
		ThompsonConstruction thompsonConstructor = new ThompsonConstruction(parser.getParsedTokens());
		NFA nfa = thompsonConstructor.thompsonConstruction();
		nfa.toString();
		nfa.toGraph();
		
		// Keep testing
		while (true) {
			System.out.print("Test: ");
			String test = scanner.nextLine();
			System.out.println();
//			System.out.println(nfa.accepts(test));
		}// while
	}// main
}// class
