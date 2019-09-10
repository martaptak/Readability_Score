package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;

public class Main {

	private static DecimalFormat df = new DecimalFormat(".##");
	private static int charactersCount = 0;

	private static int words;

	private static int sentencesNumber;

	private static int syllables = 0;

	private static int polysyllables = 0;

	private static String text = null;

	public static void main(String[] args) {


		File file = new File(args[0]);

		try (Scanner scanner = new Scanner(file)) {

			if (scanner.hasNextLine()) {
				text = scanner.nextLine();
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		}

		if (text != null) {
			calculate();
			printResult();

		} else {
			System.out.println("Error! No text uploaded");
		}
	}

	private static String ages(int score) {

		switch (score) {
			case 1:
				return "5-6";
			case 2:
				return "6-7";
			case 3:
				return "7-9";
			case 4:
				return "9-10";
			case 5:
				return "10-11";
			case 6:
				return "11-12";
			case 7:
				return "12-13";
			case 8:
				return "13-14";
			case 9:
				return "14-15";
			case 10:
				return "15-16";
			case 11:
				return "16-17";
			case 12:
				return "17-18";
			case 13:
				return "18-24";
			case 14:
			default:
				return "24+";

		}
	}

	private static void calculate() {

		String[] sentences = text.split("[.?!]");
		int[] wordsCount = new int[sentences.length];

		int i = 0;
		for (String sentence : sentences) {
			String[] words = sentence.trim().split(" ");
			wordsCount[i] = words.length;
			i++;
			for (String word : words) {
				int syllablesInWord = countSyllables(word);
				syllables += syllablesInWord;
				if (syllablesInWord > 2) {
					polysyllables++;
				}
			}
		}

		for (char c : text.toCharArray()) {
			if (c != '\n' && c != '\t' && c != ' ') {
				charactersCount++;
			}
		}
		words = Arrays.stream(wordsCount).sum();
		sentencesNumber = sentences.length;
	}

	private static void printResult() {

		System.out.println("The text is:");
		System.out.println(text);
		System.out.println();
		System.out.println("Words: " + words);
		System.out.println("Sentences: " + sentencesNumber);
		System.out.println("Characters: " + charactersCount);
		System.out.println("Syllables: " + syllables);
		System.out.println("Polysyllables: " + polysyllables);
		System.out.println();

		printSummary(determineTypeOfScore());


	}

	private static void printSummary(int score) {

		if (score == 0) {
			System.out.println("No such method");
		} else {
			System.out.println("This text should be understood in average by " + ages(score) + " year " +
					"olds. ");
		}

	}

	private static int determineTypeOfScore() {

		System.out.println("Enter the score you want to calculate (ATI, FK, SMOG, CL, all): ");
		Scanner scanner = new Scanner(System.in);
		String input = scanner.next();

		double score;

		switch (input.toUpperCase()) {
			case "ATI":
				score = automatedReadabilityIndex();
				break;
			case "FK":
				score = fleschKincaidReadabilityTests();
				break;
			case "SCMOG":
				score = smog();
				break;
			case "CL":
				score = colemanLiauIndex();
				break;
			case "ALL":
				score = all();
				break;
			default:
				score = 0;
		}
		return (int) score;
	}

	private static double all() {

		return (smog() + colemanLiauIndex() + fleschKincaidReadabilityTests() + automatedReadabilityIndex()) / 4.0;

	}

	private static double smog() {

		double score = 1.043 * Math.sqrt(polysyllables * 30.0 / sentencesNumber) + 3.1291;

		System.out.println("Simple Measure of Gobbledygook: " + df.format(score) + " (about " + ages((int) score) + " " +
				"year " +
				"olds)");

		return score;
	}

	private static double colemanLiauIndex() {

		double L = ((double)charactersCount / (double) words) * 100.0;
		System.out.println(L);

		double S = ((double)sentencesNumber / (double)words) * 100.0;
		System.out.println(S);
		double score = (0.0588 * L) - (0.296 * S) - 15.8;

		System.out.println("Coleman–Liau index: " + df.format(score) + " (about " + ages((int) score) + " year " +
				"olds)");

		return score;
	}

	private static double fleschKincaidReadabilityTests() {

		double score = 0.39 * words / sentencesNumber + 11.8 * syllables / words - 15.59;
		System.out.println("Flesch–Kincaid readability tests: " + df.format(score) + " (about " + ages((int) score) + " year " +
				"olds)");
		return score;
	}

	private static double automatedReadabilityIndex() {

		double score = 4.71 * charactersCount / words + 0.5 * words / sentencesNumber - 21.43;
		System.out.println("Automated Readability Index: " + df.format(score) + " (about " + ages((int) score) + " " +
				"year " +
				"olds)");

		return score;
	}

	private static int countSyllables(String word) {
    	
		Set<Character> vowels = new HashSet<>();
		vowels.add('a');
		vowels.add('e');
		vowels.add('i');
		vowels.add('o');
		vowels.add('u');
		vowels.add('y');

		if (word == null) {
			throw new NullPointerException("the word parameter was null.");
		} else if (word.length() == 0) {
			return 0;
		} else if (word.length() == 1) {
			return 1;
		}

		word = word.toLowerCase(Locale.ENGLISH);

		if (word.charAt(word.length() - 1) == 'e') {
			word = word.substring(0, word.length() - 1);
		}

		int count = 0;
		boolean prevIsVowel = false;
		for (char c : word.toCharArray()) {
			boolean isVowel = vowels.contains(c);
			if (isVowel && !prevIsVowel) {
				++count;
			}
			prevIsVowel = isVowel;
		}

		return Math.max(count, 1);
	}

}
