package grep;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Pattern;

import grep.finiteautomata.dfa.SubsetConstruction;
import grep.finiteautomata.nfa.NFA;
import grep.finiteautomata.nfa.ThompsonConstruction;
import grep.lexer.Lexer;
import grep.parser.Parser;
import grep.stackmachine.StackMachine;

public class Grepy {
	public static void main (String[] args) {
		System.out.println("Grepy read: " + Arrays.toString(args).replace("[", "").replace("]", "").replace(",", "") + "\n");
		
		// Check for valid input
		InputFilter input = new InputFilter(args);
		
		if(!input.isValid()) {
			System.out.println("Info: Args follow: [-n nfa-filename] [-d dfa-filename] regex [test-filename]");
			return;
		}// if
		
		// Learn alphabet
		ArrayList<String> alphabet = learnAlphabet(input.getTestFilename());
		
		for(String letter: input.getRegex().split("")) {
			if (!Pattern.compile("^\\(|\\)|\\*|\\+$").matcher(letter).find()) {
				if(!alphabet.contains(letter)) {
					alphabet.add(letter);
				}// if
			}// if
		}// for
		
		System.out.println("Final Sigma: " + alphabet);
		
		// Fail if not test file was given
		if (alphabet == null) {
			return;
		}// if
		
		// Lex regular expression into tokens
		Lexer lexer = new Lexer();
		lexer.lex(input.getRegex());
		
		// Parse tokens to take into account precedence 
		Parser parser = new Parser();
		parser.parse(lexer.getTokenArrayList());
		parser.printTokenList();
		
		// Create the NFA using Thompson Construction
		ThompsonConstruction thompsonConstructor = new ThompsonConstruction(parser.getParsedTokens(), alphabet);
		NFA nfa = thompsonConstructor.thompsonConstruction();
		System.out.println("\n\n\n" + Util.divider);
		System.out.println("Thompson Construction Resulting NFA: ");
		System.out.println(Util.divider);
		nfa.toString();
		
		// Export NFA as DOT and PNG files
		nfa.toGraph();
		nfa.export(input.getNfaFilename());
		
		// Convert NFA to DFA using subset/powerset construction
		SubsetConstruction s = new SubsetConstruction(nfa);
		s.subsetConstruction();
		System.out.println(Util.divider);
		System.out.println("Powerset/Subset Construction Resulting DFA: ");
		System.out.println(Util.divider);
		s.getDfa().toString();
		
		// Export DFA as DOT and PNG files
		s.getDfa().toGraph();
		s.getDfa().export(input.getDfaFilename());
		
		
		StackMachine stackMachine = new StackMachine(s.getDfa());
		stackMachine.test(input.getTestFilename());
	}// main
	
	private static ArrayList<String> learnAlphabet(String textFilename) {
		try {
			HashSet<String> uniqueAlhpabet = new HashSet<String>();
			ArrayList<String> sigma = new ArrayList<String>();
			
			String text = "";
			File testFile = new File(textFilename + ".txt");
			Scanner scanner = new Scanner(testFile);
			while (scanner.hasNextLine()) {
				text += scanner.nextLine().replace(" ", "");
			}// while
			
			for (String letter: text.split("")) {
				uniqueAlhpabet.add(letter);
			}// for
			
			scanner.close();
			
			for (String symbol: uniqueAlhpabet) {
				sigma.add(symbol);
			}// for
			
			return sigma;
		}// try
		
		catch (FileNotFoundException e) {
			System.out.println("Error: Could not input file '" + textFilename + "'.");
			return null;
		}// catch
	}// learnAlphabet
}// class