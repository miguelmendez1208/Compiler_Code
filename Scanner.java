package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import static
import static compiler.TokenType.*;

public class Scanner {
	public final String source;
	public final List<Token> tokens = new ArrayList<>();
	public int start = 0;
	public int current = 0;
	public int line = 1;

	Scanner(String source) {
		this.source = source;
	}

	List<Token> scanTokens() {
		while (!isAtEnd()) {
			// begin next lexeme.
			start = current;
			scanToken(); // scan each token and place into list

		}
		tokens.add(new Token(EOF, "", null, line));
		return tokens;

	}

	public boolean isAtEnd() {
		return current >= source.length();
	}

	public void scanToken() {
		char c = advance();
		switch (c) {
		case '(':
			addToken(LEFT_PAREN);
			break;
		case ')':
			addToken(RIGHT_PAREN);
			break;
		case '{':
			addToken(LEFT_BRACE);
			break;
		case '}':
			addToken(RIGHT_BRACE);
			break;
		case ',':
			addToken(COMMA);
			break;
		case '.':
			addToken(match('.') ? RANGE_SEPARATOR : DOT);
			break;
		case '-':
			addToken(MINUS);
			break;
		case '|':
			addToken(VERTICAL_LINE);
			break;
		case '+':
			addToken(PLUS);
			break;
		case ';':
			addToken(SEMICOLON);
			break;
		case '*':
			addToken(STAR);
			break;
		case ':':
			addToken(match(':') ? ((match('=') ? ASSIGNMENT : COLON)) : COLON);
			break;
		case '!':
			addToken(match('=') ? BANG_EQUAL : BANG);
			break;
		case '=':
			addToken(match('=') ? EQUAL_EQUAL : EQUAL);
			break;
		case '<':
			addToken(match('=') ? LESS_EQUAL : LESS);
			break;
		case '>':
			addToken(match('=') ? GREATER_EQUAL : GREATER);
			break;
		case '/':
			if (match('/')) {
				while (peek() != '\n' && !isAtEnd())
					advance();
			} else {
				addToken(SLASH);
			}
			break;
		case ' ':
		case '\r':
		case '\t':
			break;

		case '\n':
			line++;
			break;
		case '"':
			string();
			break;
		default:
			if (isDigit(c)) {
				number();// If the first character in the token is a number break into number function
			} else if (isAlpha(c)) {
				if (isLowerCase(c)) {
					identifier(); // if first character is lowercase break into identifier function
				} else {
					type_reference(); // if first character is uppercase break into type reference function
				}
			}

			else {
				CompilerNew.error(line, "unexpected character. ");
			}

		}

	}

	public void identifier() {
		while (isAlphaNumeric(peek()))
			advance();
		String text = source.substring(start, current);
		TokenType type = keywords.get(text);
		if (type == null)
			type = IDENTIFIER;
		else if (type != null)
			type = RESERVE_WORD;
		addToken(type);
	}

	public void type_reference() {
		while (isAlphaNumeric(peek()))
			advance();
		String text = source.substring(start, current);
		TokenType type = keywords.get(text);
		if (type == null)
			type = TYPE_REFERENCE;
		else if (type != null)
			type = RESERVE_WORD;
		addToken(type);
	}

	public static final Map<String, TokenType> keywords;

	static { // hashmap containing reserved words
		keywords = new HashMap<>();
		keywords.put("and", AND);
		keywords.put("class", CLASS);
		keywords.put("else", ELSE);
		keywords.put("false", FALSE);
		keywords.put("for", FOR);
		keywords.put("fun", FUN);
		keywords.put("if", IF);
		keywords.put("nil", NIL);
		keywords.put("or", OR);
		keywords.put("print", PRINT);
		keywords.put("return", RETURN);
		keywords.put("super", SUPER);
		keywords.put("this", THIS);
		keywords.put("true", TRUE);
		keywords.put("var", VAR);
		keywords.put("while", WHILE);
		keywords.put("tags", TAGS);
		keywords.put("TAGS", TAGS);
		keywords.put("begin", BEGIN);
		keywords.put("BEGIN", BEGIN);
		keywords.put("sequence", SEQUENCE);
		keywords.put("SEQUENCE", SEQUENCE);
		keywords.put("integer", INTEGER);
		keywords.put("INTEGER", INTEGER);
		keywords.put("date", DATE);
		keywords.put("DATE", DATE);
		keywords.put("end", END);
		keywords.put("END", END);
	}

	public boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
	}

	public boolean isLowerCase(char c) {
		return (c >= 'a' && c <= 'z');
	}

	public boolean isAlphaNumeric(char c) {
		return isAlpha(c) || isDigit(c);
	}

	public boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	public void number() {
		while (isDigit(peek()))
			advance();

		// look for a fractional part.
		if (peek() == '.' && isDigit(peekNext())) {
			advance();
			while (isDigit(peek()))
				advance();
		}
		addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
	}

	public char peekNext() {
		if (current + 1 > source.length())
			return '\0';
		return source.charAt(current + 1);
	}

	public void string() {
		while (peek() != '"' && !isAtEnd()) {
			if (peek() == '\n')
				line++;
			advance();
		}
		if (isAtEnd()) {
			return;
		}
		advance(); // closing "

		String value = source.substring(start + 1, current - 1);
		addToken(STRING, value);
	}

	public char peek() {
		if (isAtEnd())
			return '\0';
		return source.charAt(current);
	}

	public boolean match(char expected) {
		if (isAtEnd())
			return false;
		if (source.charAt(current) != expected)
			return false;

		current++;
		return true;
	}

	public char advance() {
		return source.charAt(current++);
	}

	public void addToken(TokenType type) {
		addToken(type, null);
	}

	public void addToken(TokenType type, Object literal) {
		String text = source.substring(start, current);
		tokens.add(new Token(type, text, literal, line));
	}

}
