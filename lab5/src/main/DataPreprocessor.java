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
	 * automatically takes the highest and lowest value from the data.
	 * @param dataFile the file to read the data from
	 * @param normalizedFile the file to write the normalized data to
	 * @param separator the string that separates the values to normalize in each line
	 */
	public static void normalize(String dataFile, String normalizedFile, String separator) throws IOException
	{
		File writeFile = new File(normalizedFile);
		BufferedWriter writer = new BufferedWriter(new FileWriter(writeFile));

		double minValue = Double.MAX_VALUE;
		double maxValue = Double.MIN_VALUE;

		ArrayList<ArrayList<Double>> values = getValuesFromFile(dataFile, separator);
		for (int i = 0; i < values.size(); i++)
		{
			for (int j = 0; j < values.get(i).size(); j++)
			{
				double value = values.get(i).get(j);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}

		double dataDifference = maxValue - minValue;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < values.size(); i++)
		{
			ArrayList<Double> currLine = values.get(i);
			for (int j = 0; j < currLine.size() - 1; j++)
			{
				double oldValue = currLine.get(j);
				double normalized = (oldValue - minValue) / dataDifference;
	 			builder.append(normalized);
				builder.append(separator);
			}
			builder.append(currLine.get(currLine.size() - 1));
			builder.append('\n');
		}
		writer.write(builder.toString());
		writer.flush();
		writer.close();
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
	public static void cropEdges(String datafile, String writefile, String separator, int rows, int ammount) throws IOException
	{
		File writeFile = new File(writefile);
		BufferedWriter writer = new BufferedWriter(new FileWriter(writeFile));
		StringBuilder builder = new StringBuilder();

		ArrayList<ArrayList<Double>> values = getValuesFromFile(datafile, separator);
		int cols = values.get(0).size() / rows;
		for (int i = 0; i < values.size(); i++)
		{
			for (int j = ammount; j < rows - ammount; j++)
			{
				for (int k = ammount; k < cols - ammount; k++)
				{
					int currIndex = j * cols + k;
					double value = values.get(i).get(currIndex);
					builder.append(value);
					builder.append(separator);
				}
			}
			builder.setLength(builder.length() - 1);
			builder.append("\n");
		}

		writer.write(builder.toString());
		writer.flush();
		writer.close();
	}

	/**
	 * Performs a dithering-like noise to each image on a file, deleting every other pixel
	 * @param datafile the file path where the images are encoded in (each row should contain an image)
	 * @param writefile the file to save the output to
	 * @param separator the separator between each pixel for each image
	 * @throws IOException if an IO error occurs
	 */
	public static void ditheringNoise(String datafile, String writefile, String separator) throws IOException
	{
		File writeFile = new File(writefile);
		BufferedWriter writer = new BufferedWriter(new FileWriter(writeFile));
		StringBuilder builder = new StringBuilder();

		ArrayList<ArrayList<Double>> values = getValuesFromFile(datafile, separator);
		ArrayList<ArrayList<Double>> croppedValues = new ArrayList<>();
		for (int i = 0; i < values.size(); i++)
		{
			for (int j = 0; j < values.get(i).size(); j++)
			{
				if (j % 2 == 0)
				{
					builder.append(values.get(i).get(j));
					builder.append(separator);
				}
			}
			builder.setLength(builder.length() - 1);
			builder.append("\n");
		}

		writer.write(builder.toString());
		writer.flush();
		writer.close();
	}

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
	 * shuffles the rows of a double[][] matrix.
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
		{
			String[] tokens = line.split(separator);
			double[] row = new double[tokens.length];
			for (int i = 0; i < tokens.length; i++)
				row[i] = Double.parseDouble(tokens[i]);
			result[row_num] = row;
			row_num++;
		}
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
}
