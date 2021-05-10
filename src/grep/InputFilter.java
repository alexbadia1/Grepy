package grep;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class InputFilter {
	private String nfaFilename;
	private String dfaFilename;
	private String regex;
	private String testFilename;
	
	private String args[];

	protected InputFilter(String[] args) {
		super();
		this.args = args;
	}// constructor
	
	protected boolean isValid() {
		int length = this.args.length;
		
		if (this.args[0].equals("-n")) {
			// Output should be 4 args then, 6 in length
			if (length != 6) {
				System.out.println("Error: Expected at 6 args, but received " + length + " argument(s)");
				return false;
			}// if
			
			// Get valid NFA filename
			if (!isValidFilename(this.args[1])) {
				System.out.println("Error:Invalid filename '" + this.args[1] + "' at pos 2");
				return false;
			}// if
			this.nfaFilename = this.args[1];
			
			// Get valid DFA filename
			if (!isValidFilename(this.args[3])) {
				System.out.println("Error:Invalid filename '" + this.args[3] + "' at pos 4");
				return false;
			}// if
			this.nfaFilename = this.args[3];
			
			// Get regex and test later
			this.regex = this.args[4];
			
			// Valid test filename
			if (!isValidFilename(this.args[5])) {
				System.out.println("Error:Invalid filename '" + this.args[5] + "' at pos 6");
				return false;
			}// if
			this.testFilename = this.args[5];
		}// if
		
		else if (this.args[0].equals("-d")) {
			System.out.println("Warning: NFA DOT file was never specified");
			this.nfaFilename = this.generateFilename();
			
			// Output should be 3 args then, 4 in length
			if (length != 4) {
				System.out.println("Error:Expected at 4 args, but received " + length + " argument(s)");
				return false;
			}// if
			this.dfaFilename = this.args[1];
			
			// Get valid DFA filename
			if (!isValidFilename(this.args[1])) {
				System.out.println("Error:Invalid filename '" + this.args[1] + "' at pos 2");
				return false;
			}// if
			
			// Get regex and test later
			this.regex = this.args[2];
			
			// Valid test filename
			if (!isValidFilename(this.args[3])) {
				System.out.println("Error:Invalid filename '" + this.args[3] + "' at pos 4");
				return false;
			}// if
			this.testFilename = this.args[3];
		}// if
		
		else {
			System.out.println("Warning: NFA DOT file was never specified");
			System.out.println("Warning: DFA DOT file was never specified");
			this.nfaFilename = this.generateFilename();
			this.dfaFilename = this.generateFilename();
			
			if (length != 2) {
				System.out.println("Error:Expected at least 2 args, but received " + length + " argument(s)");
				return false;
			}// if
			
			this.regex = this.args[0];
			
			if (!isValidFilename(this.args[1])) {
				System.out.println("Error:Invalid filename '" + this.args[1] + "' at pos 6");
				return false;
			}// if
			
			this.testFilename = this.args[2];
		}// if
		
		
			
		return true;
	}// parseArgs
	
	protected String getNfaFilename() {
		return nfaFilename;
	}

	protected String getDfaFilename() {
		return dfaFilename;
	}

	protected String getRegex() {
		return regex;
	}

	protected String getTestFilename() {
		return testFilename;
	}

	protected String[] getArgs() {
		return args;
	}
	
	/**
	 * If no name was specified for the DFA or NFA, generate a big 
	 * random number for there file name, just for the hell of it.
	 * 
	 * @return random 10 byte integer
	 */
	private String generateFilename() {
		int maxBytes = 8;
		Random randNum = new Random();
		byte[] byteArray = new byte[maxBytes];
		randNum.nextBytes(byteArray);
		
		// BigInteger type
		BigInteger bigInt = new BigInteger(byteArray);
		
		return String.valueOf(bigInt);
	}// generateBigInt

	private boolean isValidFilename(String filename) {
		try {
			File file = new File(filename + "1.txt");
			file.createNewFile();
			file.delete();
			return true;
		}// try 
		catch (IOException e) {
			return false;
		}// catch
	}// isValidFilename
}// InputFilter
