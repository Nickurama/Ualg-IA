import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Responsible for processing data in an AI context.
 * Has several utility methods for data preprocessing.
 */
public class DataPreprocessor
{
	/**
	 * normalizes data from a file, and places it onto another file.
	 * @param dataFile the file to read the data from
	 * @param normalizedFile the file to write the normalized data to
	 * @param separator the string that separates the values to normalize in each line
	 */
	// public static void normalize(String dataFile, String normalizedFile, String separator) throws IOException
	// {
	// 	File writeFile = new File(normalizedFile);
	// 	BufferedWriter writer = new BufferedWriter(new FileWriter(writeFile));
	//
	// 	// double minValue = Double.MAX_VALUE;
	// 	// double maxValue = Double.MIN_VALUE;
	// 	int rows = getNumLines(dataFile);
	// 	double[] minValues = new double[rows];
	// 	double[] maxValues = new double[rows];
	//
	// 	ArrayList<ArrayList<Double>> values = getValuesFromFile(dataFile, separator);
	// 	for (int i = 0; i < values.size(); i++)
	// 	{
	// 		double minValue = values.get(i).get(0);
	// 		double maxValue = values.get(i).get(0);
	// 		for (int j = 0; j < values.get(i).size(); j++)
	// 		{
	// 			double value = values.get(i).get(j);
	// 			if (value > maxValue)
	// 				maxValue = value;
	// 			if (value < minValue)
	// 				minValue = value;
	// 		}
	// 		minValues[i] = minValue;
	// 		maxValues[i] = maxValue;
	// 	}
	//
	// 	StringBuilder builder = new StringBuilder();
	// 	for (int i = 0; i < values.size(); i++)
	// 	{
	// 		double dataDifference = maxValues[i] - minValues[i];
	// 		ArrayList<Double> currLine = values.get(i);
	// 		for (int j = 0; j < currLine.size() - 1; j++)
	// 		{
	// 			double oldValue = currLine.get(j);
	// 			double normalized = (oldValue - minValues[i]) / dataDifference;
	//  			builder.append(normalized);
	// 			builder.append(separator);
	// 		}
	// 		builder.append(currLine.get(currLine.size() - 1));
	// 		builder.append('\n');
	// 	}
	// 	writer.write(builder.toString());
	// 	writer.flush();
	// 	writer.close();
	// }

	/**
	 * normalizes each row linearly
	 * @param data the array that holds the arrays to normalize
	 * @return an array of individually normalized arrays
	 */
	public static double[][] normalize(double[][] data)
	{
		double[][] result = new double[data.length][];
		for (int i = 0; i < data.length; i++)
			result[i] = normalize(data[i]);
		return result;
	}

	/**
	 * normalizes the elements linearly
	 * @param data the numbers to normalize
	 * @return the normalized values
	 */
	public static double[] normalize(double[] data)
	{
		double[] result = new double[data.length];

		double min = data[0];
		double max = data[0];
		for (int i = 0; i < data.length; i++)
		{
			double curr = data[i];
			if (curr < min)
				min = curr;
			if (curr > max)
				max = curr;
		}

		double diff = max - min;
		for (int i = 0; i < data.length; i++)
			result[i] = (data[i] - min) / diff;

		return result;
	}

	/**
	 * gets the (double/numeric) values from a string
	 * @param data the string with the values
	 * @param separator the separator of the values
	 * @return the values within the string
	 */
	public static double[] getValues(String data, String separator)
	{
		String[] tokens = data.split(separator);
		double[] values = new double[tokens.length];
		for (int i = 0; i < tokens.length; i++)
			values[i] = Double.parseDouble(tokens[i]);
		return values;
	}

	/**
	 * makes a string with the values, separated by the given string
	 * @param values the values to insert in the string
	 * @param separator the separation of each value
	 * @return a string with the values separated by the given string
	 */
	public static String makeString(double[] values, String separator)
	{
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < values.length - 1; i++)
		{
			builder.append(values[i]);
			builder.append(separator);
		}
		builder.append(values[values.length - 1]);

		return builder.toString();
	}

	/**
	 * crops the edges of images saved in a file and saves the output into a file.
	 * each image should be a row in the file, and each value in a line should be a single pixel
	 * @param datafile the file path of the image to crop
	 * @param writefile the file path of where the output should be saved to
	 * @param separator the separator of each pixel in a row
	 * @param rows how many rows of pixels each image has (NOT HOW MANY IMAGES THERE ARE)
	 * @param ammount the ammount of pixels to crop (on every side)
	 * @throws IOException if an IO error occurs.
	 */
	// public static void cropEdges(String datafile, String writefile, String separator, int rows, int ammount) throws IOException
	// {
	// 	File writeFile = new File(writefile);
	// 	BufferedWriter writer = new BufferedWriter(new FileWriter(writeFile));
	// 	StringBuilder builder = new StringBuilder();
	//
	// 	ArrayList<ArrayList<Double>> values = getValuesFromFile(datafile, separator);
	// 	int cols = values.get(0).size() / rows;
	// 	for (int i = 0; i < values.size(); i++)
	// 	{
	// 		for (int j = ammount; j < rows - ammount; j++)
	// 		{
	// 			for (int k = ammount; k < cols - ammount; k++)
	// 			{
	// 				int currIndex = j * cols + k;
	// 				double value = values.get(i).get(currIndex);
	// 				builder.append(value);
	// 				builder.append(separator);
	// 			}
	// 		}
	// 		builder.setLength(builder.length() - 1);
	// 		builder.append("\n");
	// 	}
	//
	// 	writer.write(builder.toString());
	// 	writer.flush();
	// 	writer.close();
	// }

	public static double[][] cropEdges(double[][] data, int rows, int ammount)
	{
		double[][] result = new double[data.length][];
		for (int i = 0; i < data.length; i++)
			result[i] = cropEdges(data[i], rows, ammount);
		return result;
	}

	/**
	 * crops the edges of a flattened image
	 * @param data the flattened image
	 * @param rows how many rows the image should have (pixel height)
	 * @param ammount the ammount of pixels to crop from EACH SIDE
	 * @return the cropped image flattened
	 */
	public static double[] cropEdges(double[] data, int rows, int ammount)
	{
		int cols = data.length / rows;
		int newSize = (rows - 2 * ammount) * (cols - 2 * ammount);
		double[] result = new double[newSize];

		int curr = 0;
		for (int i = ammount; i < rows - ammount; i++)
		{
			for (int j = ammount; j < cols - ammount; j++)
			{
				int currIndex = i * cols + j;
				result[curr++] = data[currIndex];
			}
		}

		return result;
	}

	/**
	 * Performs a dithering-like noise to each image on a file, deleting every other pixel
	 * @param datafile the file path where the images are encoded in (each row should contain an image)
	 * @param writefile the file to save the output to
	 * @param separator the separator between each pixel for each image
	 * @throws IOException if an IO error occurs
	 */
	// public static void ditheringNoise(String datafile, String writefile, String separator) throws IOException
	// {
	// 	File writeFile = new File(writefile);
	// 	BufferedWriter writer = new BufferedWriter(new FileWriter(writeFile));
	// 	StringBuilder builder = new StringBuilder();
	//
	// 	ArrayList<ArrayList<Double>> values = getValuesFromFile(datafile, separator);
	// 	ArrayList<ArrayList<Double>> croppedValues = new ArrayList<>();
	// 	for (int i = 0; i < values.size(); i++)
	// 	{
	// 		for (int j = 0; j < values.get(i).size(); j++)
	// 		{
	// 			if (j % 2 == 0)
	// 			{
	// 				builder.append(values.get(i).get(j));
	// 				builder.append(separator);
	// 			}
	// 		}
	// 		builder.setLength(builder.length() - 1);
	// 		builder.append("\n");
	// 	}
	//
	// 	writer.write(builder.toString());
	// 	writer.flush();
	// 	writer.close();
	// }

	/**
	 * Gets all the (double) values from a file, the outer list contains each row, the inner list contains each element of a row
	 * @param file the file path where the values are
	 * @param separator the separator of each element on a given row of the file
	 * @return the values on the file
	 * @throws IOException if an IO error occurs
	 */
	public static ArrayList<ArrayList<Double>> getValuesFromFile(String file, String separator) throws IOException
	{
		File readFile = new File(file);
		BufferedReader reader = new BufferedReader(new FileReader(readFile));
		String line;

		ArrayList<ArrayList<Double>> values = new ArrayList<ArrayList<Double>>();

		while ((line = reader.readLine()) != null)
		{
			String[] tokens = line.split(separator);
			values.add(new ArrayList<Double>());

			for (int i = 0; i < tokens.length; i++)
			{
				double value = Double.parseDouble(tokens[i]);
				values.get(values.size() - 1).add(value);
			}
		}

		reader.close();
		return values;
	}

	/**
	 * shuffles the rows of a double[][] matrix (inplace).
	 * @param matrix the matrix to shuffle (inplace)
	 */
	public static void shuffleRows(double[][] matrix)
	{
		double[][] result = new double[matrix.length][];

		Random rng = RandomNumberGenerator.get();
		HashMap<Integer, Integer> randomKeyToIndex = new HashMap<>(); // each random number is associated with an index of the matrix
		ArrayList<Integer> rngValues = new ArrayList<>();
		for (int i = 0; i < matrix.length; i++)
		{
			int key = rng.nextInt();
			rngValues.add(key);
			randomKeyToIndex.put(key, i);
		}
		rngValues.sort((x, y) -> Integer.compare(x, y));
		for (int i = 0; i < matrix.length; i++)
			result[i] = matrix[randomKeyToIndex.get(rngValues.get(i))];

		for (int i = 0; i < matrix.length; i++)
			matrix[i] = result[i];

		// return result;
	}

	/**
	 * shuffles two matrix, preserving the relationship between their rows.
	 * for example, if the 7th row in the original matrix is placed in index 5,
	 * the 7th row of the other matrix will also be placed inn index 5.
	 * @param matrix0 the first matrix to shuffle (inplace)
	 * @param matrix1 the second matrix to shuffle (inplace)
	 */
	public static void shuffleRowsPreserve(double[][] matrix0, double[][] matrix1)
	{
		if (matrix0.length != matrix1.length)
			throw new IllegalArgumentException("matrices were not the same size");

		double[][] corresponding = new double[matrix0.length][];
		double[][] result = new double[matrix0.length][];

		Random rng = RandomNumberGenerator.get();
		HashMap<Integer, Integer> randomKeyToIndex = new HashMap<>(); // each random number is associated with an index of the matrix
		ArrayList<Integer> rngValues = new ArrayList<>();
		for (int i = 0; i < matrix0.length; i++)
		{
			int key = rng.nextInt();
			rngValues.add(key);
			randomKeyToIndex.put(key, i);
		}
		rngValues.sort((x, y) -> Integer.compare(x, y));
		for (int i = 0; i < matrix0.length; i++)
		{
			int currUniqueRandIndex = randomKeyToIndex.get(rngValues.get(i));
			result[i] = matrix0[currUniqueRandIndex];
			corresponding[i] = matrix1[currUniqueRandIndex];
		}

		for (int i = 0; i < matrix0.length; i++)
		{
			matrix0[i] = result[i];
			matrix1[i] = corresponding[i];
		}
	}

	/**
	 * reads a double[][] matrix from a file where each line is a row.
	 * @param file the file to read the matrix from
	 * @param separator the column separator in the file
	 * @return the matrix read
	 * @throws IOException when an IO error occurs
	 */
	public static double[][] readMatrix(String file, String separator) throws IOException
	{
		File f = new File(file);
		BufferedReader reader = new BufferedReader(new FileReader(f));
		double[][] result = new double[getNumLines(file)][];

		String line;
		int row_num = 0;
		while ((line = reader.readLine()) != null)
			result[row_num++] = getValues(line, separator);

		reader.close();
		return result;
	}

	/**
	 * gets the number of lines from a file.
	 * @param file the file to get the number of lines from
	 * @return the number of lines from a file
	 * @throws IOException when an IO error occurs.
	 */
	public static int getNumLines(String file) throws IOException
	{
		File f = new File(file);
		BufferedReader reader = new BufferedReader(new FileReader(f));
		int lines = 0;
		while (reader.readLine() != null)
			lines++;
		reader.close();
		return lines;
	}

	/**
	 * gets the inputs / target outputs (dataset) and splits it according
	 * to the trainingToTestingRatio.
	 * (automatically transposes)
	 * @param inputs the inputs of the neural network (each row is an input, is column is an element of the input)
	 * @param outputs the outputs of the neural network (each row in a target output, a column is an element of the output)
	 * @param trainingToTestingRatio the ratio of training to testing (should be between 0 and 1)
	 * @return an array with 4 elements: [0 = training set], [1 = training set target output], [2 - testing set], [3 - testing set target output]
	 */
	public static Matrix[] getSplitSetsFromDataset(double[][] inputs, double[][] outputs, double trainingToTestingRatio)
	{
		Matrix targetOutputMatrix = new Matrix(outputs);
		Matrix inputMatrix = new Matrix(inputs);
		Matrix[] targetOutputSets = targetOutputMatrix.splitByRows(trainingToTestingRatio); // needs to be transposed!
		Matrix[] inputSets = inputMatrix.splitByRows(trainingToTestingRatio); // needs to be transposed!

		Matrix trainingSet = inputSets[0].transpose();
		Matrix trainingTargetOutput = targetOutputSets[0].transpose();
		Matrix testingSet = inputSets[1].transpose();
		Matrix testingTargetOutput = targetOutputSets[1].transpose();

		Matrix[] result = new Matrix[4];
		result[0] = trainingSet;
		result[1] = trainingTargetOutput;
		result[2] = testingSet;
		result[3] = testingTargetOutput;
		return result;
	}
}
