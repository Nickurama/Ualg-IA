import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Represents a neural network
 */
public class NeuralNetwork
{
	ArrayList<InputNode> inputNodes;
	ArrayList<Neuron> outputNeurons;
	Matrix trainingSet;
	Matrix targetOutput;
	ArrayList<Matrix> targetOutputRowsCache;

	boolean prettyPrinting;
	boolean printWhileTraining;
	boolean printWeights;
	boolean shouldPrint;

	/**
	 * instantiates a neural network.
	 * @pre the input nodes should be in the same order as the trainingSet rows
	 * @pre the output nodes should be in the same order as the targetOutput rows
	 * @param inputNodes the input layer
	 * @param outputNeurons the output layer
	 * @param trainingSet the training set, each column should be a new set, and each row a different input
	 * @param targetOutput the target output, each column should be the target output of a set, and each row a different output
	 */
	public NeuralNetwork(ArrayList<InputNode> inputNodes, ArrayList<Neuron> outputNeurons, Matrix trainingSet, Matrix targetOutput)
	{
		this.inputNodes = inputNodes;
		this.outputNeurons = outputNeurons;
		this.trainingSet = trainingSet;
		this.targetOutput = targetOutput;
		cacheTargetOutputRows(targetOutput);
		this.shouldPrint = true;
		this.prettyPrinting = false;
		this.printWhileTraining = false;
		this.printWeights = false;
	}

	/**
	 * caches the target output rows, separating each row for individual use.
	 * @param targetOutput the target output matrix
	 */
	private void cacheTargetOutputRows(Matrix targetOutput)
	{
		this.targetOutputRowsCache = new ArrayList<Matrix>();
		for (int i = 0; i < targetOutput.rows(); i++)
			this.targetOutputRowsCache.add(targetOutput.getRow(i));
	}

	/**
	 * trains the neural network for a certain ammount of iterations.
	 * @param iterations the number of times to perform backpropagation
	 * @param learningRate the learning rate
	 */
	public void train(int iterations, double learningRate)
	{
		printStats(iterations, learningRate);

		propagate();
		for (int i = 0; i < iterations; i++)
		{
			backpropagate(learningRate);
			propagate();

			if (this.printWhileTraining)
				printState();
		}

		if (!this.printWhileTraining)
			printState();
	}

	/**
	 * trains the neural network untill the error is below the specified one.
	 * @param maxError the maximum error the network should have
	 * @param learningRate the learning rate
	 */
	public void train(double maxError, double learningRate)
	{
		printStats(maxError, learningRate);

		propagate();
		int iterations = 0;
		while (getError() > maxError)
		{
			iterations++;
			backpropagate(learningRate);
			propagate();

			if (this.printWhileTraining)
				printState();
		}

		if (this.shouldPrint)
			System.out.println("iterations: " + iterations);
		if (!this.printWhileTraining)
			printState();
	}

	/**
	 * prints the state of the network, including weights, outputs and the error
	 */
	private void printState()
	{
		if (!this.shouldPrint)
			return;
		if (this.printWeights)
			printWeights();
		printOutputs();
		System.out.println("error: " + getError());
	}

	/**
	 * prints statistics with number of iterations.
	 * @param iterations the iterations to perform
	 * @param learningRate the learning rate
	 */
	private void printStats(int iterations, double learningRate)
	{
		if (!this.shouldPrint)
			return;
		System.out.println("seed: " + Neuron.seed());
		System.out.println("iter: " + iterations);
		System.out.println("rate: " + learningRate);
	}

	/**
	 * prints  statistics with the maximum error.
	 * @param maxError the maximum error
	 * @param learningRate the learning rate
	 */
	private void printStats(double maxError, double learningRate)
	{
		if (!this.shouldPrint)
			return;
		System.out.println("seed: " + Neuron.seed());
		System.out.println("merr: " + maxError);
		System.out.println("rate: " + learningRate);
	}

	/**
	 * performs the propagation of the network.
	 */
	private void propagate()
	{
		for (IPropagable x : inputNodes)
			x.propagate();
	}

	/**
	 * performs a backwards pass (backpropagation) of the network
	 * @param learningRate the rate of learning
	 */
	private void backpropagate(double learningRate)
	{
		Iterator<Matrix> currTarget = targetOutputRowsCache.iterator();
		for (IPropagable n : outputNeurons)
			n.backpropagate(currTarget.next(), learningRate, outputNeurons.size());
	}

	/**
	 * prints the outputs with respect to each input.
	 */
	public void printOutputs()
	{
		StringBuilder builder = new StringBuilder();

		builder.append("(");
		for (int i = 0; i < this.inputNodes.size() - 1; i++)
		{
			builder.append(this.inputNodes.get(i).name());
			builder.append(", ");
		}
		builder.append(this.inputNodes.get(this.inputNodes.size() - 1).name());

		builder.append(") = (");

		for (int i = 0; i < this.outputNeurons.size() - 1; i++)
		{
			builder.append(this.outputNeurons.get(i).name());
			builder.append(", ");
		}
		builder.append(this.outputNeurons.get(this.outputNeurons.size() - 1).name());
		builder.append(")\n");

		for (int i = 0; i < this.trainingSet.columns(); i++)
		{
			builder.append("(");
			for (int j = 0; j < this.trainingSet.rows() - 1; j++)
			{
				builder.append(formatDouble(this.trainingSet.get(j, i)));
				builder.append(", ");
			}
			builder.append(formatDouble(this.trainingSet.get(this.trainingSet.rows() - 1, i)));

			builder.append(") = (");

			for (int j = 0; j < this.outputNeurons.size() - 1; j++)
			{
				builder.append(formatDouble(this.outputNeurons.get(j).output().get(0, i)));
				builder.append(", ");
			}
			builder.append(formatDouble(this.outputNeurons.get(this.outputNeurons.size() - 1).output().get(0, i)));
			builder.append(")\n");
		}

		System.out.println(builder.toString());
	}

	/**
	 * @return a matrix the outputs of the network, with each row in respect to an output and each columns in respect to each training input
	 */
	public Matrix output()
	{
		double[][] out = new double[this.outputNeurons.size()][this.trainingSet.columns()];
		for (int i = 0; i < this.trainingSet.columns(); i++)
			for (int j = 0; j < this.outputNeurons.size(); j++)
				out[j][i] = this.outputNeurons.get(j).output().get(0, i);
		return new Matrix(out);
	}

	/**
	 * formats a double value for printing.
	 * @param d the double to format
	 * @return the formatted version of the double
	 */
	private String formatDouble(double d)
	{
		return this.prettyPrinting ? String.format("%.3f", d) : String.valueOf(d);
	}

	/**
	 * @return the MSE (average of errors of all outputs)
	 */
	public double getError()
	{
		double error = 0;
		Iterator<Matrix> currTarget = targetOutputRowsCache.iterator();
		for (Neuron out : outputNeurons)
			error += out.getError(currTarget.next());
		error /= outputNeurons.size();
		return error;
	}

	/**
	 * prints the weights of each connection in the network.
	 */
	public void printWeights()
	{
		ArrayList<String> info = new ArrayList<String>();

		for (IPropagable p : inputNodes)
			info = p.getWeightInfo(info);

		info.sort((s0, s1) -> s0.compareTo(s1));

		for (String s : info)
			System.out.println(s);
	}

	/**
	 * performs propagation with user input.
	 */
	public void iterativePropagation()
	{
		Scanner sc = new Scanner(System.in);
		printIn();
		while (sc.hasNext())
		{
			for (InputNode x : this.inputNodes)
				x.set(new Matrix(new double[][] {{ sc.nextDouble() }}));
			propagate();

			// get result
			for (Neuron n : this.outputNeurons)
				printOut(n.name(), n.output());
			printIn();
		}
		sc.close();
	}

	/**
	 * prints the output of the iterative mode.
	 * @pre the matrix should have a single value
	 * @param name the name of the output
	 * @param out the matrix value to print
	 */
	private static void printOut(String name, Matrix out)
	{
		System.out.println(String.format("\t" + name + " >> %.1f", out.parse()));
	}

	/**
	 * prints the input of the iterative mode.
	 */
	private static void printIn()
	{
		System.out.print("<< ");
	}

	/**
	 * sets pretty printing on or off.
	 * @param shouldPrettyPrinting
	 */
	public void setPrettyPrint(boolean shouldPrettyPrinting)
	{
		this.prettyPrinting = shouldPrettyPrinting;
	}

	/**
	 * sets printing of the state while training on or off.
	 * @param shouldPrintWhileTraining
	 */
	public void setPrintWhileTraining(boolean shouldPrintWhileTraining)
	{
		this.printWhileTraining = shouldPrintWhileTraining;
	}

	/**
	 * sets printing of the weights on or off during training.
	 * @param shouldPrintWeights
	 */
	public void setPrintWeights(boolean shouldPrintWeights)
	{
		this.printWeights = shouldPrintWeights;
	}

	/**
	 * sets printing on or off during training.
	 * @param shouldPrint
	 */
	public void setPrinting(boolean shouldPrint)
	{
		this.shouldPrint = shouldPrint;
	}
}
