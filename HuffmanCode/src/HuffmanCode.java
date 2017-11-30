import java.io.PrintStream;
import java.util.*;

/**
 * Homework7, CSE 143 2017 Summer, Written by: Su Wang, enrolled in quiz session
 * AC. HuffmanCode represents HuffmanCode for a message. Huffman Coding is the
 * algorithm for compressing data into a smaller sized file.
 * 
 */
public class HuffmanCode {
	private HuffmanNode overallRoot;

	/**
	 * 
	 * HuffmanNode represents a single node in the tree that represents Huffman
	 * codes. Each node has two data, ascii value and frequency of the letter.
	 */
	private static class HuffmanNode implements Comparable<HuffmanNode> {
		public final int asciiValue;
		public final int frequency;
		public HuffmanNode left;
		public HuffmanNode right;

		/**
		 * Initialize a HuffmanNode.
		 * 
		 * @param asciiValue
		 *            should be an integer, is a character's ASCII value, -1
		 *            means this node does not contain information about a
		 *            character.
		 * @param frequency
		 *            should be an integer, which is the number of times that
		 *            this character appears.
		 */
		public HuffmanNode(int asciiValue, int frequency) {
			this(asciiValue, frequency, null, null);
		}

		/**
		 * Initialize a HuffmanNode, given a character's ASCII value, its
		 * frequency, its' left and right node.
		 * 
		 * @param asciiValue
		 *            should be an integer, is a character's ASCII value, -1
		 *            means this node does not contain information about a
		 *            character.
		 * @param frequency
		 *            should be an integer, which is the number of times that
		 *            this character appears.
		 * @param left
		 *            should be an HuffmanNode.
		 * @param right
		 *            should be an HuffmanNode.
		 */
		public HuffmanNode(int asciiValue, int frequency, HuffmanNode left, HuffmanNode right) {
			this.asciiValue = asciiValue;
			this.frequency = frequency;
			this.left = left;
			this.right = right;
		}

		/**
		 * Rank two HuffmanNodes by their frequency, HuffmanNode with less
		 * frequency ranks first.
		 */
		public int compareTo(HuffmanNode node) {
			return (this.frequency - node.frequency);
		}
	}

	/**
	 * Initialize a new HuffmanCode.
	 * 
	 * @param frequencies
	 *            is a an array of integers. For example: an array of [3,1]
	 *            means there are 3 characters with ASCII value equals 0, there
	 *            is 1 character with ASCII value equals 1. The index equals
	 *            ASCII value.
	 */
	public HuffmanCode(int[] frequencies) {
		Queue<HuffmanNode> freq = new PriorityQueue<HuffmanNode>();
		for (int i = 0; i < frequencies.length; i++) {
			int frequency = frequencies[i];
			if (frequency > 0) {
				HuffmanNode oneCharacter = new HuffmanNode(i, frequency, null, null);
				freq.add(oneCharacter);
			}
		}

		if (freq.size() > 1) {
			overallRoot = HuffmanTreeConstructor(freq).remove();
		} else if (freq.size() == 1) {
			HuffmanNode onlyNode = freq.remove();
			int f = onlyNode.frequency;
			overallRoot = new HuffmanNode(-1, f, new HuffmanNode(onlyNode.asciiValue, f), null);
		}
	}

	/**
	 * Reads a standard .code file to initialize a HuffmanCode.
	 * 
	 * @param input
	 *            is a .code file. Assume that input is not null, contains data
	 *            in valid and standard format.
	 */
	public HuffmanCode(Scanner input) {
		overallRoot = new HuffmanNode(-1, 0);
		while (input.hasNextLine()) {
			addHuffmanNode(overallRoot, Integer.parseInt(input.nextLine()), input.nextLine(), 0);
		}
	}

	/**
	 * Store current Huffman Codes to a given output stream in a standard
	 * format.
	 * 
	 * @param output
	 *            should be an PrintStream object, should not be null, throws
	 *            IllegalArgumentException if violated.
	 */
	public void save(PrintStream output) {
		if (output == null) {
			throw new IllegalArgumentException();
		} else {
			HuffmanCodeGenerater(overallRoot, "", output);
		}
	}

	/**
	 * Read bits from input and translate into corresponding characters to
	 * output.
	 * 
	 * @param input
	 *            should be an BitInputStream, stop reading when input is
	 *            empty.Assumptions are made that input contains standard and
	 *            legal format.
	 * @param output
	 *            should be an PrintStream.
	 */
	public void translate(BitInputStream input, PrintStream output) {
		HuffmanNode root = overallRoot;
		while (input.hasNextBit()) {
			int nextBit = input.nextBit();
			if (nextBit == 0) {
				root = root.left;
			} else {
				root = root.right;
			}
			if (root.asciiValue != -1) {
				output.write(root.asciiValue);
				root = overallRoot;
			}
		}
	}

	/**
	 * 
	 * @param frequencyQueue
	 *            is a priorityQueue of HuffmanNode, with each node represents a
	 *            character's frequency and ASCII value.
	 * @returns a Queue of HuffmanNode with only one HuffmanNode left, which
	 *          represents all characters and their frequencies in a tree,
	 *          characters with higher frequencies appear at higher level in the
	 *          tree.
	 */
	private Queue<HuffmanNode> HuffmanTreeConstructor(Queue<HuffmanNode> frequencyQueue) {
		if (frequencyQueue.size() <= 1) {
			return frequencyQueue;
		}
		HuffmanNode firstNode = frequencyQueue.remove();
		HuffmanNode secondNode = frequencyQueue.remove();
		HuffmanNode combineTwoNode = new HuffmanNode(-1, (firstNode.frequency + secondNode.frequency), firstNode,
				secondNode);
		frequencyQueue.add(combineTwoNode);
		return HuffmanTreeConstructor(frequencyQueue);
	}

	/**
	 * A helper method to print the HuffmanNode tree into a standard format to
	 * PrintStream, for each node, the first line is the character's ASCII
	 * value, the second line is its HuffmanCode. HuffmanCode can be derived by
	 * going through the HuffmanNode tree from the root to find a leaf, each
	 * leaf represent a char, "0" is added to the code if go left, "1" is added
	 * to the code if go right.
	 * 
	 * @param root is a HuffmanNode, should be the overallRoot of the tree.
	 * @param code is a string, represents a char's HuffmanCode.
	 * @param output should be a PrintStream, the ASCII value and Huffman Code will be printed to this output.
	 */
	private void HuffmanCodeGenerater(HuffmanNode root, String code, PrintStream output) {
		if (root != null && root.asciiValue != -1) {
			output.println(String.valueOf(root.asciiValue));
			output.println(code);
		} else if (root != null) {
			HuffmanCodeGenerater(root.left, code + "0", output);
			HuffmanCodeGenerater(root.right, code + "1", output);
		}
	}

	/**
	 * A helper method for initializing a HuffmanCode object. To get a HuffmanNode given a code from a .code file.
	 * @param root should be a HuffmanNode.
	 * @param asciiValue should be an integer.
	 * @param code should be a string. consist of 0 and 1
	 * @param index should be an integer.
	 * @return a HuffmanNode object.
	 */
	private HuffmanNode addHuffmanNode(HuffmanNode root, int asciiValue, String code, int index) {
		if (code.length() == index) {
			return new HuffmanNode(asciiValue, 0);
		} else if (root == null) {
			root = new HuffmanNode(-1, 0);
		}
		if (code.charAt(index) == '0') {
			root.left = addHuffmanNode(root.left, asciiValue, code, index + 1);
		} else {
			root.right = addHuffmanNode(root.right, asciiValue, code, index + 1);
		}
		return root;
	}
}
