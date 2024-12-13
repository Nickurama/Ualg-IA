/**
 * Represents the statistics of a binary classifier, with tools to calculate
 * all kinds of useful statistics.
 */
public class BinaryClassifierStats
{
	// data
	private int truePositives;
	private int falsePositives;
	private int trueNegatives;
	private int falseNegatives;

	// metrics
	private double precision;
	private double accuracy;
	private double kappa;
	private double recall;

	/**
	 * Instantiates the binary classifier statistics, calculating them and caching them.
	 * @param classifierOutput the classifier's outputs
	 * @param expectedOutput the expected outputs
	 */
	public BinaryClassifierStats(Matrix classifierOutput, Matrix expectedOutput)
	{
		if (classifierOutput.rows() > 1)
			throw new IllegalArgumentException("Not the output of a binary classifier");
		if (expectedOutput.rows() > 1)
			throw new IllegalArgumentException("Not the expected output of a binary classifier");
		if (expectedOutput.columns() != classifierOutput.columns())
			throw new IllegalArgumentException("Classifier and expected outputs don't match");

		this.truePositives = 0;
		this.trueNegatives = 0;
		this.falsePositives = 0;
		this.falseNegatives = 0;


		for (int i = 0; i < classifierOutput.columns(); i++)
		{
			boolean output = evaluateOutput(classifierOutput.get(0, i));
			boolean expected = evaluateOutput(expectedOutput.get(0, i));

			if (output == expected && output == true) truePositives++;
			else if (output == expected && output == false) trueNegatives++;
			else if (output != expected && output == true) falsePositives++;
			else if (output != expected && output == false) falseNegatives++;
		}

		this.accuracy = calcAccuracy(truePositives, trueNegatives, falsePositives, falseNegatives);
		this.precision = calcPrecision(truePositives, falsePositives);
		this.kappa = calcKappa(truePositives, trueNegatives, falsePositives, falseNegatives);
		this.recall = calcRecall(truePositives, falseNegatives);
	}

	/**
	 * the "agreement" score (ranges between 0 and 1)
	 * @param tPos true positives
	 * @param tNeg true negatives
	 * @param fPos false positives
	 * @param fNeg false negatives
	 * @return the accuracy
	 */
	public static double calcAccuracy(int tPos, int tNeg, int fPos, int fNeg)
	{
		return (double)(tPos + tNeg) / (double)(tPos + tNeg + fPos + fNeg);
	}

	/**
	 * how good the guesses are (ranges between 0 and 1)
	 * @param tPos true positives
	 * @param fPos false positives
	 * @return the precision
	 */
	public static double calcPrecision(int tPos, int fPos)
	{
		return (double)tPos / (double)(tPos + fPos);
	}

	/**
	 * score ranging from -1 to 1, where
	 * -1 is total disagreement,
	 * 0 is random guess,
	 * 1 is total agreement,
	 * @param tPos true positives
	 * @param tNeg true negatives
	 * @param fPos false positives
	 * @param fNeg false negatives
	 * @return the kappa
	 */
	public static double calcKappa(int tPos, int tNeg, int fPos, int fNeg)
	{
		return (2.0 * (double)(tPos * tNeg - fNeg * fPos)) / (double)((tPos + fPos)*(fPos + tNeg) + (tPos + fNeg)*(fNeg + tNeg));
	}

	/**
	 * how well the classifier identifies true positives (ranges between 0 and 1)
	 * @param tPos true positive
	 * @param fNeg false negative
	 * @return the recall
	 */
	public static double calcRecall(int tPos, int fNeg)
	{
		return (double)tPos / (double)(tPos + fNeg);
	}

	private boolean evaluateOutput(double output)
	{
		return output >= 0.5 ? true : false;
	}

	/**
	 * the "agreement" score (ranges between 0 and 1)
	 * @return the precision
	 */
	public double precision() { return this.precision; }

	/**
	 * how good the guesses are (ranges between 0 and 1)
	 * @return the accuracy
	 */
	public double accuracy() { return this.accuracy; }

	/**
	 * score ranging from -1 to 1, where
	 * -1 is total disagreement,
	 * 0 is random guess,
	 * 1 is total agreement,
	 * @return the kappa
	 */
	public double kappa() { return this.kappa; }

	/**
	 * how well the classifier identifies true positives (ranges between 0 and 1)
	 * @return the recall
	 */
	public double recall() { return this.recall; }
}
