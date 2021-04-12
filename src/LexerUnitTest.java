import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LexerUnitTest {
	private Lexer lexer;
	
	@BeforeEach
    void setUp() throws Exception {
        lexer = new Lexer();
    }// setUp
	
	@Test
	void shouldAcceptLetters() {
		lexer.lex("abcdefghijklmnopqrstuvwxyz");
		for (Token token : lexer.getTokenArrayList()) {
			if (token.type != "TERM_ALPHABETIC") {
				fail("Lexer did not correctly tokenize input to TERM_ALPHABETIC");
			}// if
		}// for
	}// test
	
	@Test
	void shouldAcceptNumbers() {
		lexer.lex("0123456789");
		for (Token token : lexer.getTokenArrayList()) {
			if (token.type != "TERM_NUMERIC") {
				fail("Lexer did not correctly tokenize input to TERM_NUMERIC");
			}// if
		}// for
	}// test
	
	@Test
	void shouldAcceptGroups() {
		lexer.lex("ab(cd(ef))");
		int index = 0;
		String[] correctTokensTypes = new String[] {
				"TERM_ALPHABETIC", // a
				"TERM_ALPHABETIC", // b
				"SYMBOL_OPEN_GROUP", // (
				"TERM_ALPHABETIC", // c
				"TERM_ALPHABETIC", // d
				"SYMBOL_OPEN_GROUP", // (
				"TERM_ALPHABETIC", // e
				"TERM_ALPHABETIC", // f
				"SYMBOL_CLOSE_GROUP", // )
				"SYMBOL_CLOSE_GROUP", // )
				};
		for (Token token : lexer.getTokenArrayList()) {
			if (token.type != correctTokensTypes[index]) {
				fail("Lexer failed to tokenize with Groups correctly!");
			}// if
			index++;
			if (index > correctTokensTypes.length) {
				fail("Lexer generated to many tokens!");
			}// if
		}// for
	}// test
	
	@Test
	void shouldAcceptAlphaNumericWithGroupsWithKleeneStars() {
		lexer.lex("a1(9d(e*f))*");
		int index = 0;
		String[] correctTokensTypes = new String[] {
				"TERM_ALPHABETIC", // a
				"TERM_NUMERIC", // 1
				"SYMBOL_OPEN_GROUP", // (
				"TERM_NUMERIC", // 9
				"TERM_ALPHABETIC", // d
				"SYMBOL_OPEN_GROUP", // (
				"TERM_ALPHABETIC", // e
				"SYMBOL_KLEENE_STAR", // *
				"TERM_ALPHABETIC", // f
				"SYMBOL_CLOSE_GROUP",// )
				"SYMBOL_CLOSE_GROUP", // )
				"SYMBOL_KLEENE_STAR" // *
				};
		for (Token token : lexer.getTokenArrayList()) {
			if (token.type != correctTokensTypes[index]) {
				fail("Lexer failed to tokenize with Groups and Kleene Stars correctly!");
			}// if
			index++;
			if (index > correctTokensTypes.length) {
				fail("Lexer generated to many tokens!");
			}// if
		}// for
	}// test
	
	@Test
	void shouldAcceptAlphaNumericWithGroupsWithKleeneStarsWithConcatenation() {
		lexer.lex("a+1(9+d(e*f))*");
		int index = 0;
		String[] correctTokensTypes = new String[] {
				"TERM_ALPHABETIC", // a
				"SYMBOL_CONCATENATION", // +
				"TERM_NUMERIC", // 1
				"SYMBOL_OPEN_GROUP", // (
				"TERM_NUMERIC", // 9
				"SYMBOL_CONCATENATION", // +
				"TERM_ALPHABETIC", // d
				"SYMBOL_OPEN_GROUP", // (
				"TERM_ALPHABETIC", // e
				"SYMBOL_KLEENE_STAR", // *
				"TERM_ALPHABETIC", // f
				"SYMBOL_CLOSE_GROUP",// )
				"SYMBOL_CLOSE_GROUP", // )
				"SYMBOL_KLEENE_STAR" // *
				};
		for (Token token : lexer.getTokenArrayList()) {
			if (token.type != correctTokensTypes[index]) {
				fail("Lexer failed to tokenize with Groups correctly!");
			}// if
			index++;
			if (index > correctTokensTypes.length) {
				fail("Lexer generated to many tokens!");
			}// if
		}// for
	}// test
	
	@Test
	void shouldAcceptUnicode() {
		lexer.lex("\u00D8+1(9+d(\u00B1*f))*");
		int index = 0;
		String[] correctTokensTypes = new String[] {
				"TERM_UNICODE", // a
				"SYMBOL_CONCATENATION", // +
				"TERM_NUMERIC", // 1
				"SYMBOL_OPEN_GROUP", // (
				"TERM_NUMERIC", // 9
				"SYMBOL_CONCATENATION", // +
				"TERM_ALPHABETIC", // d
				"SYMBOL_OPEN_GROUP", // (
				"TERM_UNICODE", // ±
				"SYMBOL_KLEENE_STAR", // *
				"TERM_ALPHABETIC", // f
				"SYMBOL_CLOSE_GROUP",// )
				"SYMBOL_CLOSE_GROUP", // )
				"SYMBOL_KLEENE_STAR" // *
				};
		for (Token token : lexer.getTokenArrayList()) {
			if (token.type != correctTokensTypes[index]) {
				fail("Lexer failed to tokenize with Groups correctly!");
			}// if
			index++;
			if (index > correctTokensTypes.length) {
				fail("Lexer generated to many tokens!");
			}// if
		}// for
	}// test
	// ÷
	@Test
	void shouldRejectValidJavaRegularExpressionCharactersNotInOurLanguage() {
		boolean failed = true;
		
		failed = !lexer.lex("[a]+1(9+d(e*f))*");
		
		// Lexer should not attempt to tokenize a rejected regular expression
		if (!failed) {
			fail("Regular Expression tried to tokenize a valid, but rejected regular expression");
		}// if
	}// test

}// LexerUnitTest
