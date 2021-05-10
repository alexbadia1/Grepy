package grep.lexer;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import grep.Util;

/**
 * lexer.java 
 * @author Alex Badia
 * 
 * A light weight Lexer that tokenizes valid regular expressions.
 * 
 * The syntax of the regular language is defined as:
 * 
 * 	- Term ::== case-sensitive alphanumeric characters
 * 		   ::== Unicode characters
 *         ::== escaped meta characters
 *  - Symbol ::== (
 *           ::== )
 *           ::== +
 *           ::== *
 *           
 * Possible tokens are:
 *  - String TERM_ALPHABETIC ::== a-z case sensitive
 *  - String TERM_NUMERIC ::== 0-9
 *  - String TERM_UNICODE ::== any unicode character [see https://home.unicode.org/]
 *  - String SYMBOL_OPEN_GROUP ::== (
 *  - String SYMBOL_CLOSE_GROUP ::== )
 *  - String SYMBOL_UNION ::== +
 *  - String SYMBOL_KLEENE_STAR ::== *
 *  - String IMPLIED_CONCATENATION
 *  
 */
public class Lexer {
	private String regExp;
	private ArrayList<Token> tokenArrayList;
	
	public Lexer() {
		this.tokenArrayList = new ArrayList<Token>();
	}// constructor
	
	/**
	 * Tokenizes regular expressions.
	 * 
	 * @param newRegularExpression user defined regular expression
	 * @return true on successful lex, false if unsuccessful
	 */
	public boolean lex(String newRegularExpression) {
		this.regExp = newRegularExpression;
		
		try {
			// Regex will fail to compile if in valid
	        Pattern.compile(this.regExp);
	        
	        // Regex must have compiled, emit tokens 
	        String lexeme;
			Boolean acceptedRegEx = true;
			
			System.out.println();
			System.out.println("Grepy read: " + this.regExp);
			System.out.println("\n" + Util.divider);
			System.out.println("Token Stream:");
			System.out.println(Util.divider);
			
			for (int i = 0; i < this.regExp.length() && acceptedRegEx; ++i) {
				lexeme = Character.toString(this.regExp.charAt(i));
				
				// Backslash to escape meta characters
				if (Pattern.compile("^\\\\$").matcher(lexeme).find()) {
					String peekedLexeme = Character.toString(this.regExp.charAt(i + 1));
					
					if (peekedLexeme.chars().anyMatch(c -> "\\.[]{}()*+?^$|".indexOf(c) >= 0)) {
						// Advance lexeme
						i++;
						this.tokenArrayList.add(new Token("TERM", peekedLexeme, -1));
						System.out.println("TERM: " + peekedLexeme);
					}// if
					
					else {
						System.out.println("Invalid RegExp, Expected [\\\\\\\\.[]{}()*+?^$|] at pos (" + i + "), ");
						acceptedRegEx = false;
					}// else
				}// if
				
				// Alphabet
				else if (Pattern.compile("^[a-z]$", Pattern.CASE_INSENSITIVE).matcher(lexeme).find()) {
					this.emitToken(new Token("TERM_ALPHABETIC", Character.toString(lexeme.charAt(0)), -1));
				}// else if
				
				// Number
				else if (Pattern.compile("^[0-9]$").matcher(lexeme).find()) {
					this.emitToken(new Token("TERM_NUMERIC", Character.toString(lexeme.charAt(0)), -1));
				}// if
				
				// Meta-Character
				else if (Pattern.compile("^!|@|#|%|&|-|_|~|`|,|>|<|;|:|\"|\'$").matcher(lexeme).find()) {
					this.emitToken(new Token("TERM_METACHARACTER", Character.toString(lexeme.charAt(0)), -1));
				}// if
				
				// Unicode
				else if (Pattern.compile("[\\u0080-\\u9fff]").matcher(lexeme).find()) {
					this.emitToken(new Token("TERM_UNICODE", Character.toString(lexeme.charAt(0)), -1));
				}// else if
				
				// Symbols
				else if (Pattern.compile("^\\(|\\)|\\*|\\+$").matcher(lexeme).find()) {
					switch (this.regExp.charAt(i)) {
						case '(':
							this.emitToken(new Token("SYMBOL_OPEN_GROUP", Character.toString(lexeme.charAt(0)), -1));
							break;
						case ')': 
							this.emitToken(new Token("SYMBOL_CLOSE_GROUP", Character.toString(lexeme.charAt(0)), -1));
							break;
						case '*': 
							this.emitToken(new Token("SYMBOL_KLEENE_STAR", Character.toString(lexeme.charAt(0)), 1));
							break;
						case '+': 
							this.emitToken(new Token("SYMBOL_UNION", Character.toString(lexeme.charAt(0)), 3));
							break;
						default:
							// This should never happen...
							break;
					}// switch
				}// else if
				
				else {
					acceptedRegEx = false;
				}// else
			}// for
			
			if (!acceptedRegEx) {
				String errorMessage = "Rejected Regular Expression\n\n";
				errorMessage += "While the regular expression used is valid, this program uses a restricted syntax:\r\n\n"
						+ "   - Term ::== case-sensitive alphanumeric characters\r\n"
						+ "          ::== Unicode characters\r\n"
						+ "          ::== escaped meta characters\r\n\n"
						+ "   - Symbol ::== (\r\n"
						+ "            ::== )\r\n"
						+ "            ::== +\r\n"
						+ "            ::== *";
				System.out.println(errorMessage);
				return false;
			}// if
			
			return true;
	    }// try 
		
		// Invalid regular expression
		catch (PatternSyntaxException e) {
			System.out.println("Invalid Regular Expression: " + e.getDescription() + ".\n");
			System.out.println("NOTE: The syntax of the regular language is defined as:\r\n\n"
					+ "   - Term ::== case-sensitive alphanumeric characters\r\n"
					+ "          ::== Unicode characters\r\n"
					+ "          ::== escaped meta characters\r\n\n"
					+ "   - Symbol ::== (\r\n"
					+ "            ::== )\r\n"
					+ "            ::== +\r\n"
					+ "            ::== *");
			
			return false;
	    }// catch
	}// lex
	
	/**
	 * Emits a token create from the regular expression
	 * 
	 * @param newToken current token from the regular expression
	 */
	private void emitToken(Token newToken) {
		Token prevToken = null;
		if (this.tokenArrayList.size() > 0) {
			prevToken = this.tokenArrayList.get(this.tokenArrayList.size() - 1);
		}// if 
		
		// Add implied concatenation
		if (prevToken != null) {
			if (newToken.type.contains("TERM") || newToken.type.contains("SYMBOL_OPEN_GROUP")) {
				if (
					prevToken.type.contains("TERM") 
					|| prevToken.type.contains("SYMBOL_CLOSE_GROUP")
					|| prevToken.type.contains("SYMBOL_KLEENE_STAR")
					) {
					this.tokenArrayList.add(new Token("IMPLIED_CONCATENATION", "", 2));
				}// if
			}// if
		}// if
		
		this.tokenArrayList.add(newToken);
		
		System.out.println("  [" + newToken.type +"] : [" + newToken.lexeme + "]");
	}// emitToken

	/**
	 * Returns the regular expression as a stream of tokens maintaining infix order.
	 * 
	 * @return An ArrayList of tokens generated by the Lexer.
	 */
	public ArrayList<Token> getTokenArrayList() {
		return this.tokenArrayList;
	}// getTokens
	
	public void printTokenList() {
		for (Token token: this.getTokenArrayList()) {
			System.out.println("  " + token.type);
		}// for
	}// printTokenList
}// class
