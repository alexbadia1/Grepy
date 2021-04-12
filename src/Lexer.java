import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
 *  - String SYMBOL_OPEN_GROUP ::== )
 *  - String SYMBOL_CONCATENATION ::== +
 *  - String SYMBOL_KLEENE_STAR ::== *
 *  
 */
public class Lexer {
	private String regExp;
	private int currentIndex;
	private ArrayList<Token> tokenArrayList;
	
	Lexer() {
		this.currentIndex = 0;
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
			Boolean isValidRegEx = true;
			Boolean acceptedRegEx = true;
			
			System.out.println();
			System.out.println("Grepy read: " + this.regExp);
			System.out.println();
			System.out.println("Token Stream:");
			
			for (int i = 0; i < this.regExp.length() && isValidRegEx; ++i) {
				lexeme = Character.toString(this.regExp.charAt(i));
				
				// Backslash to escape meta characters
				if (Pattern.compile("^\\\\$").matcher(lexeme).find()) {
					String peekedLexeme = Character.toString(this.regExp.charAt(i + 1));
					
					if (peekedLexeme.chars().anyMatch(c -> "\\.[]{}()*+?^$|".indexOf(c) >= 0)) {
						// Advance lexeme
						i++;
						this.tokenArrayList.add(new Token("TERM", peekedLexeme));
						System.out.println("TERM: " + peekedLexeme);
					}// if
					
					else {
						System.out.println("Invalid RegExp, Expected [\\\\\\\\.[]{}()*+?^$|] at pos (" + i + "), ");
						isValidRegEx = false;
					}// else
				}// if
				
				// Alphabet
				else if (Pattern.compile("^[a-z]$", Pattern.CASE_INSENSITIVE).matcher(lexeme).find()) {
					this.emitToken(new Token("TERM_ALPHABETIC", Character.toString(lexeme.charAt(0))));
				}// else if
				
				// Number
				else if (Pattern.compile("^[0-9]$").matcher(lexeme).find()) {
					this.emitToken(new Token("TERM_NUMERIC", Character.toString(lexeme.charAt(0))));
				}// if
				
				// Meta-Character
				else if (Pattern.compile("^!|@|#|%|&|-|_|~|`|,|>|<|;|:|\"|\'$").matcher(lexeme).find()) {
					this.emitToken(new Token("TERM_METACHARACTER", Character.toString(lexeme.charAt(0))));
				}// if
				
				// Unicode
				else if (Pattern.compile("[\\u0080-\\u9fff]").matcher(lexeme).find()) {
					this.emitToken(new Token("TERM_UNICODE", Character.toString(lexeme.charAt(0))));
				}// else if
				
				// Symbols
				else if (Pattern.compile("^\\(|\\)|\\*|\\+$").matcher(lexeme).find()) {
					switch (this.regExp.charAt(i)) {
						case '(':
							this.emitToken(new Token("SYMBOL_OPEN_GROUP", Character.toString(lexeme.charAt(0))));
							break;
						case ')': 
							this.emitToken(new Token("SYMBOL_CLOSE_GROUP", Character.toString(lexeme.charAt(0))));
							break;
						case '*': 
							this.emitToken(new Token("SYMBOL_KLEENE_STAR", Character.toString(lexeme.charAt(0))));
							break;
						case '+': 
							this.emitToken(new Token("SYMBOL_CONCATENATION", Character.toString(lexeme.charAt(0))));
							break;
						default:
							// This should never happen...
							break;
					}// switch
				}// else
				
				else {
					acceptedRegEx = false;
					isValidRegEx = false;
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
			
			else if (!isValidRegEx) {
				return false;
			}// if
			
			else {
				return true;
			}// else
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
		this.tokenArrayList.add(newToken);
		
		System.out.println("[" + newToken.type +"] : [" + newToken.lexeme + "]");
	}// emitToken

	/**
	 * Returns the regular expression as a stream of tokens maintaining infix order.
	 * 
	 * @return An ArrayList of tokens generated by the Lexer.
	 */
	public ArrayList<Token> getTokenArrayList() {
		return this.tokenArrayList;
	}// getTokens
}// class
