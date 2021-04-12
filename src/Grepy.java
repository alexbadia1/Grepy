import java.util.Scanner;

public class Grepy {
	public static void main (String[] args) {
		Scanner myObj = new Scanner(System.in);
		
		System.out.print("Regular Expression: ");
		
		String regex = myObj.nextLine();
		System.out.println();
		Lexer lexer = new Lexer();
		lexer.lex(regex);
	}// main
}// class
