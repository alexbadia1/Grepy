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
		
		ShuntingYardAlgorithm parser = new ShuntingYardAlgorithm();
		parser.convertToPostFix(lexer.getTokenArrayList());
		parser.printTokenList();
	}// main
}// class
