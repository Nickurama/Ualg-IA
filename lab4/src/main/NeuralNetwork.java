import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

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

	// the rows in training set should be in the same order as the input nodes / output neurons with target output
	public NeuralNetwork(ArrayList<InputNode> inputNodes, ArrayList<Neuron> outputNeurons, Matrix trainingSet, Matrix targetOutput)
	{
		this.inputNodes = inputNodes;
		this.outputNeurons = outputNeurons;
		this.trainingSet = trainingSet;
		this.targetOutput = targetOutput;
		cacheTargetOutputRows(targetOutput);
		this.prettyPrinting = false;
		this.printWhileTraining = false;
		this.printWeights = false;
	}

	private void cacheTargetOutputRows(Matrix targetOutput)
	{
		this.targetOutputRowsCache = new ArrayList<Matrix>();
		for (int i = 0; i < targetOutput.rows(); i++)
			this.targetOutputRowsCache.add(targetOutput.getRow(i));
	}

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

		System.out.println("iterations: " + iterations);
		if (!this.printWhileTraining)
			printState();
	}

	private void printState()
	{
		if (this.printWeights)
			printWeights();
		printOutputs();
		System.out.println("error: " + getError());
	}

	private void printStats(int iterations, double learningRate)
	{
		System.out.println("seed: " + Neuron.seed());
		System.out.println("iter: " + iterations);
		System.out.println("rate: " + learningRate);
	}

	private void printStats(double maxError, double learningRate)
	{
		System.out.println("seed: " + Neuron.seed());
		System.out.println("merr: " + maxError);
		System.out.println("rate: " + learningRate);
	}

	private void propagate()
	{
		for (IPropagable x : inputNodes)
			x.propagate();
	}

	private void backpropagate(double learningRate)
	{
		Iterator<Matrix> currTarget = targetOutputRowsCache.iterator();
		for (IPropagable n : outputNeurons)
			n.backpropagate(currTarget.next(), learningRate, outputNeurons.size());
	}

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

	private String formatDouble(double d)
	{
		return this.prettyPrinting ? String.format("%.3f", d) : String.valueOf(d);
	}

	public double getError()
	{
		double error = 0;
		Iterator<Matrix> currTarget = targetOutputRowsCache.iterator();
		for (Neuron out : outputNeurons)
			error += out.getError(currTarget.next());
		error /= outputNeurons.size();
		return error;
	}

	public void printWeights()
	{
		ArrayList<String> info = new ArrayList<String>();

		for (IPropagable p : inputNodes)
			info = p.getWeightInfo(info);

		info.sort((s0, s1) -> s0.compareTo(s1));

		for (String s : info)
			System.out.println(s);
	}

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

	private static void printOut(String name, Matrix out)
	{
		System.out.println(String.format("\t" + name + " >> %.1f", out.parse()));
	}

	private static void printIn()
	{
		System.out.print("<< ");
	}

	public void setPrettyPrint(boolean shouldPrettyPrinting)
	{
		this.prettyPrinting = shouldPrettyPrinting;
	}

	public void setPrintWhileTraining(boolean shouldPrintWhileTraining)
	{
		this.printWhileTraining = shouldPrintWhileTraining;
	}

	public void setPrintWeights(boolean shouldPrintWeights)
	{
		this.printWeights = shouldPrintWeights;
	}
}
