package ProgrammingLanguages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class SyntaxAnalyzer {
	private String lexeme;
	private String prevLex;
	private int token;
	private int prevToken;
	private boolean error = false;
	private Queue<String> queue = new LinkedList<String>();
	private String fileName;
	
	//keywords
	final String program = "program";
	final String begin = "begin";
	final String ifKeyword = "if";
	final String then = "then";
	final String loop = "loop";
	final String end = "end";	
	HashMap<String, Integer> keywords = new HashMap<String, Integer>();
	
	//tokens
	final int intNumber = 10;
	final int variable = 11;
	
	final int equals = 20;
	final int add = 21;
	final int sub = 22;
	final int multi = 23;
	final int div = 24;
	final int leftParentheses = 25;
	final int rightParentheses = 26;
	
	final int lessThan = 30;
	final int greaterThan = 31;
	final int semiColon = 32;
	
	private SyntaxAnalyzer() throws IOException {
			checkFileExist();
		
			programParser();	
	}
	
	private boolean checkFileExist() {
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter the file name of the program you want analyzed: ");
		String fileName = scan.nextLine();
		File file = new File(fileName);
		
		if(file.exists() && !file.isDirectory()) {
		this.fileName = fileName;
		   return true;
		}
		else {
			return checkFileExist();
		}
	}
	
	private void programParser() throws IOException {
		setKeywords();		
		fileRead();
		
		checkStartOfFile();
		
		statementList();
		
		if(token == keywords.get(end) && prevToken == semiColon){
			error();
		}
		
		if(token == keywords.get(end) && !error) {
			System.out.println("This program is correct");
		}
		else {
			System.out.println("This program has at least one error.");
		}
	}

	private void setKeywords() {
		keywords.put(program, 0);
		keywords.put(begin, 1);
		keywords.put(ifKeyword, 2);
		keywords.put(then, 3);
		keywords.put(loop, 4);
		keywords.put(end, -1);
	}
	
	private void fileRead() throws IOException{		
		File f = new File(fileName);		
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		
		int c = 0;
		String temp = "";
		
		while((c = br.read()) != -1) {
			char character = (char) c;
			
			if(character==';' || character=='(' || character==')' ||
					character=='/' || character=='*' || character=='-' ||
					character=='+' || character=='=' || character=='<' || character=='>') 
			{	
				if(temp != "") {
					queue.add(temp);
					temp = "";
				}
				
				queue.add(String.valueOf(character));
			}
			
			else if((int)character<=126 && (int)character>=33){
				temp+=character;
				if(temp.equals("end")) {
					queue.add(temp);
				}
			}
			else {
				if(temp != "") {
					queue.add(temp);
					temp = "";
				}	
			}
		}		
	}
	
	private void getNextLex(){		
		if(queue.size()>0) {
			prevLex = lexeme;
			prevToken = token;
			
			lexeme = queue.remove();
			
			getNextToken();
		}
	}
	
	private void getNextToken() {		
		boolean isVariable = checkIfNumberOrVariable();
		
		if(keywords.get(lexeme)!= null) {
			token = keywords.get(lexeme);
		}
		else if(lexeme.equals(";")) {
			token = semiColon;
		} 
		else if(lexeme.equals("(")) {
			token = leftParentheses;
		}
		else if(lexeme.equals(")")) {
			token = rightParentheses;
		}
		else if(lexeme.equals("/")) {
			token = div;
		}
		else if(lexeme.equals("*")) {
			token = multi;
		}
		else if(lexeme.equals("-")) {
			token = sub;
		}
		else if(lexeme.equals("+")) {
			token = add;
		} 
		else if(lexeme.equals("=")) {
			token = equals;
		} 
		else if(lexeme.equals("<")) {
			token = lessThan;
		} 
		else if(lexeme.equals(">")) {
			token = greaterThan;
		}
		else if(isVariable) {
			token = variable;
		}
		else if(!isVariable) {
			token = intNumber;
		}
		
		//System.out.println("Token is: " + token + " Lexeme is: " + lexeme);
	}
	
	private boolean checkIfNumberOrVariable() {
		boolean isVariable = false;
		
		for(int i = 0; i<lexeme.length(); i++) {
			if((int)lexeme.charAt(i)>57 || (int)lexeme.charAt(i)<48) {
				isVariable = true;
			}
		}
		
		return isVariable;
	}
	
	private void checkStartOfFile() {
		getNextLex();
		
		if(token != keywords.get(program)) {
			error();
		}
		
		getNextLex();
		
		if(token != keywords.get(begin)) {
			error();
		}
		
	}
	
	private void statementList() {
		//System.out.println("Enter statementList");
		getNextLex();
		
		statement();
		
		while(token == semiColon){
			getNextLex();
			
			statement();
		}
				
		//System.out.println("Exit statementList");
	}

	private void statement() {
		if(token == variable && (prevToken == variable || prevToken == intNumber)  ||
				token == keywords.get(ifKeyword) && (prevToken == variable || prevToken == intNumber) ||
					token == keywords.get(loop) && (prevToken == variable || prevToken == intNumber)) {
			error();
		}
		
		if(token == variable) {
			assignmentStatement();
		}
		else if(token == keywords.get(ifKeyword)) {
			ifStatement();
		}
		else if(token == keywords.get(loop)) {
			loopStatement();
		}
	}
	

	private void assignmentStatement() {
		//System.out.println("Enter AssignmentStatement");
		
		getNextLex();
		//check if curr lex is equals if it is then get the next lex and call expression
		//to starting checking past the equals sign
		if(token != equals) {
			error();
		}
		
		getNextLex();
		
		expression();
		
		//System.out.println("Exit AssignmentStatement");
		
		statement();
	}

	private void ifStatement() {
		//System.out.println("Enter if");
		
		getNextLex();
		
		if(token == leftParentheses) {
			logicExpression();
		}
		else {
			error();
		}
		
		if(token == rightParentheses) {
			getNextLex();
			
			if(token == keywords.get(then)) {
				getNextLex();
				statement();
			}
			else {
				error();
			}
		}
		else {
			error();
		}
	
		//System.out.println("Exit if");
	}

	private void logicExpression() {
		//System.out.println("Enter Logic");
		getNextLex();
		
		if(token != variable) {
			error();
		}
		
		getNextLex();
	
		
		if(token == greaterThan || token == lessThan) {
			getNextLex();
		}
		else {
			error();
		}
		
		if(token != variable) {
			error();
		}
		
		getNextLex();
		//System.out.println("Exit Logic");
	}

	private void loopStatement() {
		//System.out.println("Enter Loop");
		getNextLex();
		
		if(token == leftParentheses) {
			logicExpression();
		}
		else {
			error();
		}
		
		if(token == rightParentheses) {
			getNextLex();
			statement();
		}
		else {
			error();
		}
		//System.out.println("Exit Loop");
	}
	
	private void expression() {
		//System.out.println("Enter Expression");
		term();
		
		while(token == add || token == sub) {
			getNextLex();
			term();
		}
		//System.out.println("Exit Expression");
	}
	
	private void term() {
		//System.out.println("Enter Term");
		factor();
		
		while(token == multi || token == div) {
			getNextLex();
			factor();
		}
		//System.out.println("Exit Term");
	}

	private void factor() {
		//System.out.println("Enter Factor");
		if(token == intNumber || token == variable) {
			
			getNextLex();
		}
		else if(token == leftParentheses) {
			getNextLex();
			
			expression();
			
			if(token == rightParentheses) {
				getNextLex();
			}
			else {
				error();
			}
		}
		else {
			error();
		}
		//System.out.println("Exit Factor");
	}

	private void error() {
		error = true;
		
		System.out.println("Syntax Error Detected");
	}

	public static void main(String[] args) throws IOException {
		SyntaxAnalyzer test = new SyntaxAnalyzer();
	}
}