import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParserTest {
	private Parser parser;
	private Token token = new Token("TEST_TOKEN", "TEST", 0);
	
	@BeforeEach
    void setUp() throws Exception {
        parser = new Parser();
    }// setUp
	
	@Test
	void postFixOrderTest() {
		// Input in tokens
		ArrayList<Token> mockLexTokens = new ArrayList<Token>();
		mockLexTokens.add(new Token("TERM_ALPHABETIC", "a", -1));
		mockLexTokens.add(new Token("IMPLIED_CONCATENATION", "", 2));
		mockLexTokens.add(new Token("SYMBOL_OPEN_GROUP", "", -1));
		mockLexTokens.add(new Token("TERM_ALPHABETIC", "b", -1));
		mockLexTokens.add(new Token("SYMBOL_UNION", "+", 3));
		mockLexTokens.add(new Token("TERM_ALPHABETIC", "c", -1));
		mockLexTokens.add(new Token("SYMBOL_CLOSE_GROUP", "", -1));
		mockLexTokens.add(new Token("SYMBOL_KLEENE_STAR", "*", 1));
		mockLexTokens.add(new Token("IMPLIED_CONCATENATION", "", 2));
		mockLexTokens.add(new Token("TERM_ALPHABETIC", "d", -1));
		
		// Expected post fix order output tokens
		Queue<Token> expectedOutputQueue = new LinkedList<Token>();
		expectedOutputQueue.add(new Token("TERM_ALPHABETIC", "a", -1));
		expectedOutputQueue.add(new Token("TERM_ALPHABETIC", "b", -1));
		expectedOutputQueue.add(new Token("TERM_ALPHABETIC", "c", -1));
		expectedOutputQueue.add(new Token("SYMBOL_UNION", "+", 3));
		expectedOutputQueue.add(new Token("SYMBOL_KLEENE_STAR", "*", 3));
		expectedOutputQueue.add(new Token("IMPLIED_CONCATENATION", "", 2));
		expectedOutputQueue.add(new Token("TERM_ALPHABETIC", "d", -1));
		expectedOutputQueue.add(new Token("IMPLIED_CONCATENATION", "", 2));
		
		parser.parse(mockLexTokens);
		parser.printTokenList();
		Queue<Token> parsedTokens = parser.getParsedTokens();
		
		// Not same size, so must fail
		if (expectedOutputQueue.size() != parsedTokens.size()) {
			fail("Parser did not return the correct amount of tokens!");
		}// if
		
		// Same amount of tokens, so compare them
		while(!expectedOutputQueue.isEmpty()) {
			if (!token.tokensAreEqual(expectedOutputQueue.remove(), parsedTokens.remove())) {
				fail("Parser failed to parse the regex tokens into post order!");
			}// if
		}// while
	}// test

}// class
