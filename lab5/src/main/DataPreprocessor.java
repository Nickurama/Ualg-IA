import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
	 * normalizes data from a file, and places it onto another file
	 * @pre dataMinValue cannot be equal to dataMaxValue
	 * @param dataFile the file to read the data from
	 * @param normalizedFile the file to write the normalized data to
	 * @param dataMinValue the minimum value in the data set
	 * @param dataMaxValue the maximum value in the data set
	 * @param separator the string that separates the values to normalize in each line
	 */
	public static void normalize(String dataFile, String normalizedFile, String separator) throws IOException
	{
		File readFile = new File(dataFile);
		File writeFile = new File(normalizedFile);
		BufferedReader reader = new BufferedReader(new FileReader(readFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(writeFile));
		String line;

		double minValue = Double.MAX_VALUE;
		double maxValue = Double.MIN_VALUE;

		ArrayList<ArrayList<Double>> values = new ArrayList<ArrayList<Double>>();

		while ((line = reader.readLine()) != null)
		{
			String[] tokens = line.split(separator);
			values.add(new ArrayList<Double>());

			for (int i = 0; i < tokens.length; i++)
			{
				double value = Double.parseDouble(tokens[i]);
				values.get(values.size() - 1).add(value);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		// System.out.println("min: " + minValue);
		// System.out.println("max: " + maxValue);

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

		reader.close();
		writer.close();
	}

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

		// return result;
	}

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
