public class Token {
	public String type;
	public String lexeme;
	
	/**
	 * Token Priority:
	 * 
	 * 1.) SYMBOL_KLEENE_STAR
	 * 2.) IMPLIED_CONCATENATION
	 * 3.) SYMBOL_UNION
	 * 
	 */
	public int priority;
	
	Token(String newType, String newLexeme, int newPriority) {
		this.type = newType;
		this.lexeme = newLexeme;
		this.priority = newPriority;
	}// constructor
	
	public boolean tokensAreEqual(Token token1, Token token2) {
		if (token1.type.compareTo(token2.type) == 0) {
			if (token1.lexeme.compareTo(token2.lexeme) == 0) {
				return true;
			}// if 
		}// if
		
		return false;
	}// areTokensEqual
}// class
