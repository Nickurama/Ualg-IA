import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
}
