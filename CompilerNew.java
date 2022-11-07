package compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

//import java.util.Scanner
//import static compiler.Scanner.*;
public class CompilerNew {
	static boolean hadError = false;

	public static void main(String[] args) throws IOException {
		if (args.length > 1) {
			System.out.println("Lexer!");
			System.exit((64));
		} else if (args.length == 1) {
			runFile(args[0]);
		} else {
			runFile("file.txt");
			// runPrompt();
		}
	}

	public static void runPrompt() throws IOException { // unused prompt function
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		for (;;) {
			System.out.print("> ");
			String line = reader.readLine();
			if (line == null)
				break;
			run(line);
			hadError = false;
		}
	}

	public static void runFile(String path) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(path)); // Grab the file and turn it into an array of bytes
		run(new String(bytes, Charset.defaultCharset())); // Then use that array of bytes and turn it into a String of
															// characters and feed it to run();

		// report error
		if (hadError) {
			System.out.println("\nFailure!");
			System.exit(65);
		}
			else {
			System.out.println("\nSUCCESS!");
		}
	}

	public static void run(String source) {
		Scanner scanner = new Scanner(source); // feed the String of characters into a new scanner object
		List<Token> tokens = scanner.scanTokens(); // Scan the tokens of the string and return it as a list of tokens

		for (Token token : tokens) { // print all tokens
			System.out.println(token);
		}
	}

	public static void error(int line, String message) {
		report(line, "", message);
	}

	public static void report(int line, String where, String message) {
		System.err.println("[line " + line + "] Error!" + where + ": " + message);
		hadError = true;
	}
}
