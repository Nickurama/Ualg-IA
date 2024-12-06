import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Represents a neural network
 */
public class NeuralNetwork implements Externalizable
{
	// serializable
	private static final long serialVersionUID = 137L;

	// constants
	private static final String OUT_FILE = "errorData.csv";

	// member variables
	private ArrayList<InputNode> inputNodes;
	private ArrayList<Neuron> outputNeurons;
	transient private Matrix trainingSet;
	transient private Matrix targetOutput;
	private ArrayList<Matrix> targetOutputRowsCache;
	private ArrayList<Matrix> targetTestOutputRowsCache;

	// caches and states
	private ArrayList<String> nodeInfoCache;
	private int iterationsDone;
	private boolean areNeuronsPropagated;

	// early stopping and testing
	transient private Matrix testingSet; // can be null
	transient private Matrix testingTargetOutputs; // can be null
	private boolean earlyStopping;
	private double minimumTestingError;
	private boolean cachedTestErrorBeforeBackpropagation;
	private double cachedTestError;

	// flags
	private boolean prettyPrinting;
	private boolean shouldPrintWhileTraining;
	private boolean shouldPrintWeights;
	private boolean shouldPrintOutputs;
	private boolean shouldPrint;
	private boolean shouldPrintTestingError;
	private boolean shouldExport;

	// exporting
	transient private FileWriter fWriter;

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
		init(inputNodes, outputNeurons, trainingSet, targetOutput);
		// this.inputNodes = inputNodes;
		// this.outputNeurons = outputNeurons;
		// this.trainingSet = trainingSet;
		// this.targetOutput = targetOutput;
		// cacheTargetOutputRows(targetOutput);
		// this.areNeuronsPropagated = false;
		// this.earlyStopping = false;
		// this.cachedTestErrorBeforeBackpropagation = false;
		// this.minimumTestingError = 1.0;
		// this.cachedTestError = 0.0;
		// // nodeInfoCache = new ArrayList<String>();
		// this.shouldPrint = true;
		// this.shouldPrintOutputs = true;
		// this.prettyPrinting = false;
		// this.shouldPrintWhileTraining = false;
		// this.shouldPrintWeights = false;
		// this.shouldExport = false;
		// this.iterationsDone = 0;
	}

	public NeuralNetwork() {}; // for externalization

	public NeuralNetwork(ArrayList<InputNode> inputNodes, ArrayList<Neuron> outputNeurons)
	{
		init(inputNodes, outputNeurons);
	}

	private final void init(ArrayList<InputNode> inputNodes, ArrayList<Neuron> outputNeurons, Matrix trainingSet, Matrix targetOutput)
	{
		init(inputNodes, outputNeurons);
		setTrainingData(trainingSet, targetOutput);
	}

	private final void init(ArrayList<InputNode> inputNodes, ArrayList<Neuron> outputNeurons)
	{
		this.inputNodes = inputNodes;
		this.outputNeurons = outputNeurons;
		this.trainingSet = null;
		this.targetOutput = null;
		this.areNeuronsPropagated = false;
		this.earlyStopping = false;
		this.cachedTestErrorBeforeBackpropagation = false;
		this.minimumTestingError = 1.0;
		this.cachedTestError = 0.0;
		// nodeInfoCache = new ArrayList<String>();
		this.shouldPrint = true;
		this.shouldPrintOutputs = true;
		this.prettyPrinting = false;
		this.shouldPrintWhileTraining = false;
		this.shouldPrintWeights = false;
		this.shouldExport = false;
		this.iterationsDone = 0;
	}

	/**
	 * builds a layered network automatically
	 * @param numInputs number of inputs
	 * @param numOutputs number of outputs
	 * @param trainingSet the training set
	 * @param targetOutput the target output of the training set
	 * @param layerSizes how many neurons each layer will have
	 * @return the built layered network
	 */
	public static NeuralNetwork layeredBuilder(int numInputs, int numOutputs, Matrix trainingSet, Matrix targetOutput, ArrayList<Integer> layerSizes)
	{
		ArrayList<InputNode> inputLayer = new ArrayList<>();
		for (int i = 0; i < numInputs; i++)
			inputLayer.add(new InputNode(trainingSet.getRow(i)));

		// int numOutputBackwardConn = layerSizes.isEmpty() ? numInputs : layerSizes.getLast();
		// double outputWeightRange = 1.0 / (double)numOutputBackwardConn;
		ArrayList<Neuron> outputLayer = new ArrayList<>();
		for (int i = 0; i < numOutputs; i++)
			outputLayer.add(new Neuron());
		NeuralNetwork network = new NeuralNetwork(inputLayer, outputLayer, trainingSet, targetOutput);

		ArrayList<IPropagable> previousLayer = new ArrayList<IPropagable>(inputLayer);
		ArrayList<IPropagable> currLayer = new ArrayList<>();
		for (int layerSize : layerSizes)
		{
			// double weightRange = 1.0 / (double)previousLayer.size();
			for (int i = 0; i < layerSize; i++)
			{
				Neuron n = new Neuron();
				currLayer.add(n);
				for (IPropagable previous : previousLayer)
					previous.connect(n);
			}
			previousLayer = currLayer;
			currLayer = new ArrayList<>();
		}

		for (Neuron out : outputLayer)
			for (IPropagable previous : previousLayer)
				previous.connect(out);

		return network;
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
	 * loads a NeuralNetwork from a file.
	 * @param file the file path to load the network from
	 * @return the network read from the file
	 * @throws IOException when an IO error occurs
	 * @throws FileNotFoundException when the file was not found
	 * @throws ClassNotFoundException when it was not possible to de-serialize the class
	 */
	public static NeuralNetwork loadFromFile(String file) throws IOException, FileNotFoundException, ClassNotFoundException
	{
		File readFile = new File(file);
		FileInputStream fileInStream = new FileInputStream(readFile);
		ObjectInputStream objInStream = new ObjectInputStream(fileInStream);
		NeuralNetwork network = new NeuralNetwork();
		network.readExternal(objInStream);
		objInStream.close();
		fileInStream.close();
		return network;
	}

	/**
	 * saves/serializes a NeuralNetwork to a file.
	 * @param file the file to save the network to
	 * @throws IOException when an IO error occurs
	 */
	public void saveToFile(String file) throws IOException
	{
		File outFile = new File(file);
		FileOutputStream fileOutStream = new FileOutputStream(outFile);
		ObjectOutputStream objOutStream = new ObjectOutputStream(fileOutStream);
		// objOutStream.writeObject(this);
		this.writeExternal(objOutStream);
		objOutStream.flush();
		objOutStream.close();
		fileOutStream.close();
	}

	/**
	 * trains the neural network untill the error is below the specified one
	 * or untill the number of iterations are reached.
	 * @param maxError the maximum error the network should have (can be null)
	 * @param iterations the number of times to perform backpropagation (can be null)
	 * @param learningRate the learning rate
	 */
	public void train(Integer iterations, Double maxError, double learningRate)
	{
		if (this.shouldExport)
			initFileWriter();

		forcePropagation();
		printStats(iterations, maxError, learningRate);
		tryExportError();
		tryPrintState();

		if (iterations == null)
			iterations = Integer.MAX_VALUE;
		if (maxError == null)
			maxError = -1.0;

		for (this.iterationsDone = 0; getError() > maxError && this.iterationsDone < iterations; this.iterationsDone++)
		{
			if (detectEarlyStop())
				break;
			trainOneEpoch(learningRate);
		}

		if (!this.shouldPrintWhileTraining)
			printState();

		if (this.shouldExport)
			closeFileWriter();
	}

	/**
	 * trains the neural network for a certain ammount of iterations.
	 * @param iterations the number of times to perform backpropagation
	 * @param learningRate the learning rate
	 */
	public void train(int iterations, double learningRate)
	{
		train(iterations, null, learningRate);
	}

	/**
	 * trains the neural network untill the error is below the specified one.
	 * @param maxError the maximum error the network should have
	 * @param learningRate the learning rate
	 */
	public void train(double maxError, double learningRate)
	{
		train(null, maxError, learningRate);
	}

	/**
	 * @return if early stopping should be performed
	 */
	private boolean detectEarlyStop()
	{
		if (!this.earlyStopping)
			return false;
		double currTestingError = getTestingError();
		if (currTestingError > this.minimumTestingError)
			return true;
		this.minimumTestingError = currTestingError;
		return false;
	}

	/**
	 * trains for a single epoch
	 * @param learningRate
	 */
	private void trainOneEpoch(double learningRate)
	{
		if (!this.areNeuronsPropagated)
			propagateWithStats();
		backpropagate(learningRate);
		propagateWithStats();
	}

	/**
	 * propagates, while doing stat logic
	 */
	private void propagateWithStats()
	{
		propagate();

		tryExportError();
		tryPrintState();
	}

	/**
	 * prints state if the flag is set
	 */
	private void tryPrintState()
	{
		if (this.shouldPrintWhileTraining)
			printState();
	}

	/**
	 * exports error if the flag is set
	 */
	private void tryExportError()
	{
		if (this.shouldExport)
			exportError();
	}

	/**
	 * initializes the class file writer.
	 */
	private final void initFileWriter()
	{
		try
		{
			this.fWriter = new FileWriter(OUT_FILE);
		}
		catch (IOException e)
		{
			throw new IllegalStateException("Could not initialize file writer." + e.getMessage());
		}
	}

	private void closeFileWriter()
	{
		try
		{
			this.fWriter.flush();
			this.fWriter.close();
		}
		catch (IOException e)
		{
			throw new IllegalStateException("Could not close file writer." + e.getMessage());
		}
	}

	/**
	 * writes the current loss function output to a file
	 * @pre file writer has been initialized
	 */
	private void exportError()
	{
		try
		{
			this.fWriter.write(Double.toString(this.iterationsDone) + ';' + getError() + '\n');
		}
		catch (IOException e)
		{
			throw new IllegalStateException("Could not write to file." + e.getMessage());
		}
	}

	/**
	 * prints the state of the network, including weights, outputs and the error
	 */
	private void printState()
	{
		if (!this.shouldPrint)
			return;
		System.out.println("iter: " + this.iterationsDone);
		if (this.shouldPrintWeights)
			printWeights();
		if (this.shouldPrintOutputs)
			printOutputs();
		if (this.shouldPrintTestingError)
			System.out.println("testing error: " + getTestingError());
		System.out.println("error: " + getError());
	}

	/**
	 * prints statistics with number of iterations.
	 * @param iterations the iterations to perform (can be null)
	 * @param maxError the maximum error (can be null)
	 * @param learningRate the learning rate
	 */
	private void printStats(Integer iterations, Double maxError, double learningRate)
	{
		if (!this.shouldPrint)
			return;
		System.out.println("seed: " + RandomNumberGenerator.seed());
		if (maxError != null)
			System.out.println("merr: " + maxError);
		if (iterations != null)
			System.out.println("total iter: " + iterations);
		System.out.println("rate: " + learningRate);
	}

	/**
	 * performs the propagation of the network.
	 * if the output is cached, doesn't propagate.
	 */
	public void propagate()
	{
		if (this.areNeuronsPropagated)
			return;
		forcePropagation();
	}

	/**
	 * propagates no matter what.
	 */
	public void forcePropagation()
	{
		this.areNeuronsPropagated = true;
		for (IPropagable x : inputNodes)
			x.propagate();
	}

	public void ghostpropagate()
	{
		for (IPropagable x : inputNodes)
			x.ghostpropagate();
	}

	public Matrix evaluate(Matrix input)
	{
		setGhostInputs(input);
		ghostpropagate();

		Matrix result = ghostOutput();

		if (this.testingSet != null)
			setGhostInputs(this.testingSet);
		return result;
	}

	/**
	 * performs a backwards pass (backpropagation) of the network
	 * @param learningRate the rate of learning
	 */
	public void backpropagate(double learningRate)
	{
		this.areNeuronsPropagated = false;
		this.cachedTestErrorBeforeBackpropagation = false;
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
	 * @return a matrix with the outputs of the network, with each row in respect to an output and each columns in respect to each training input
	 */
	public Matrix output()
	{
		Matrix result = this.outputNeurons.get(0).output();
		for (int i = 1; i < this.outputNeurons.size(); i++)
			result.appendAsRows(this.outputNeurons.get(i).output());
		return result;
	}

	/**
	 * @return a matrix with the outputs of the networ, with each row in respect to a ghost output, and each column in respect to each input
	 */
	public Matrix ghostOutput()
	{
		Matrix result = this.outputNeurons.get(0).ghostOutput();
		for (int i = 1; i < this.outputNeurons.size(); i++)
			result.appendAsRows(this.outputNeurons.get(i).ghostOutput());
		return result;
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
		final boolean isGhost = false;

		propagate();
		double error = 0;
		Iterator<Matrix> currTarget = targetOutputRowsCache.iterator();
		for (Neuron out : outputNeurons)
			error += out.getError(currTarget.next(), isGhost);
		error /= outputNeurons.size();
		return error;
	}

	/**
	 * gets the error for a specific input-output set
	 * @param inputs the inputs to check for error
	 * @param targetOutputs the target output of the given inputs
	 * @return the MSE
	 */
	public double getError(Matrix inputs, Matrix targetOutputs)
	{
		final boolean isGhost = true;

		setGhostInputs(inputs);
		ghostpropagate();
		// setInputNodes(inputs);
		// forcePropagation();

		double error = 0;
		for (int i = 0; i < this.outputNeurons.size(); i++)
			error += this.outputNeurons.get(i).getError(targetOutputs.getRow(i), isGhost);
		error /= this.outputNeurons.size();

		// setInputNodes(this.trainingSet); // is in correct order
		// forcePropagation();
		setGhostInputs(this.testingSet);
		return error;
	}

	/**
	 * @return the error of the test sets
	 */
	public double getTestingError()
	{
		final boolean isGhost = true;
		if (this.testingSet == null || this.testingTargetOutputs == null)
			throw new IllegalAccessError("Testing data not set!");
		if (this.cachedTestErrorBeforeBackpropagation)
			return this.cachedTestError;

		setGhostInputs(this.testingSet);
		ghostpropagate();
		this.cachedTestError = 0;
		Iterator<Matrix> currTarget = targetTestOutputRowsCache.iterator();
		for (Neuron out : outputNeurons)
			this.cachedTestError += out.getError(currTarget.next(), isGhost);
		this.cachedTestError /= outputNeurons.size();

		this.cachedTestErrorBeforeBackpropagation = true;
		return this.cachedTestError;
	}

	/**
	 * assumes the network is a binary classifier
	 * @pre has 1 output
	 * @return the accuracy of the testing set
	 */
	// public double getAccuracy()
	// {
	// 	if (this.outputNeurons.size() > 1)
	// 		throw new IllegalAccessError("Cannot get accuracy for non-binary networks (should have only 1 output neuron).");
	// 	if (this.testingSet == null || this.testingTargetOutputs == null)
	// 		throw new IllegalAccessError("Testing data not set!");
	// }
	//
	// public double getPrecision()
	// {
	//
	// }
	//
	// public double getRecall()
	// {
	//
	// }
	//
	// public double getKappa()
	// {
	//
	// }

	/**
	 * sets the input nodes to the given matrix
	 * @param inputs the matrix to set the input nodes to
	 */
	private void setInputNodes(Matrix inputs)
	{
		for (int i = 0; i < this.inputNodes.size(); i++)
			this.inputNodes.get(i).set(inputs.getRow(i));
	}

	/**
	 * prints the weights of each connection in the network.
	 */
	public void printWeights()
	{
		System.out.print(getWeightInfo());
	}

	/**
	 * @return the weight info
	 */
	public String getWeightInfo()
	{
		StringBuilder builder = new StringBuilder();
		ArrayList<String> info = new ArrayList<String>();

		resetCache();
		for (IPropagable p : inputNodes)
			info = p.getWeightInfo(info);

		info.sort((s0, s1) -> s0.compareTo(s1));

		if (info.isEmpty())
			info = this.nodeInfoCache;
		else
			this.nodeInfoCache = info;

		for (String s : info)
		{
			builder.append(s);
			builder.append('\n');
		}
		return builder.toString();
	}

	/**
	 * resets all caches
	 */
	private void resetCache()
	{
		for (IPropagable p : inputNodes)
			p.resetCaches();
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
			forcePropagation();

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
	public void setShouldPrintWhileTraining(boolean shouldPrintWhileTraining)
	{
		this.shouldPrintWhileTraining = shouldPrintWhileTraining;
	}

	/**
	 * sets printing of the weights on or off during training.
	 * @param shouldPrintWeights
	 */
	public void setShouldPrintWeights(boolean shouldPrintWeights)
	{
		this.shouldPrintWeights = shouldPrintWeights;
	}

	/**
	 * sets printing on or off during training.
	 * @param shouldPrint
	 */
	public void setPrinting(boolean shouldPrint)
	{
		this.shouldPrint = shouldPrint;
	}

	/**
	 * sets exporting the loss function to a file
	 * during training on or off.
	 * @param value
	 */
	public void setExportingLoss(boolean value)
	{
		this.shouldExport = value;
	}

	/**
	 * sets printing the input-output pairs from
	 * training to terminal on or off.
	 * @param value
	 */
	public void setPrintOutputs(boolean value)
	{
		this.shouldPrintOutputs = value;
	}

	/**
	 * sets the testing samples.
	 * @param testingInputs the inputs to use for testing
	 * @param testingTargetOutputs the outputs to use for testing
	 */
	public void setTestingSet(Matrix testingInputs, Matrix testingTargetOutputs)
	{
		setGhostInputs(testingInputs);

		this.targetTestOutputRowsCache = new ArrayList<Matrix>();
		for (int i = 0; i < testingTargetOutputs.rows(); i++)
			this.targetTestOutputRowsCache.add(testingTargetOutputs.getRow(i));

		this.testingSet = testingInputs;
		this.testingTargetOutputs = testingTargetOutputs;
	}

	/**
	 * @param inputs the inputs for the ghost input nodes
	 */
	private void setGhostInputs(Matrix inputs)
	{
		for (int i = 0; i < this.inputNodes.size(); i++)
			this.inputNodes.get(i).setGhost(inputs.getRow(i));
	}

	/**
	 * sets printing the error of the test set to
	 * on or off.
	 * @param value
	 */
	public void setPrintingTestingError(boolean value)
	{
		this.shouldPrintTestingError = value;
	}

	/**
	 * sets early stopping on or off.
	 * @param value
	 */
	public void setEarlyStopping(boolean value)
	{
		this.earlyStopping = value;
	}

	/**
	 * @return the number of iterations done through training
	 */
	public int iterationsDone()
	{
		return this.iterationsDone;
	}

	public final void setTrainingData(Matrix trainingSet, Matrix targetOutput)
	{
		for (int i = 0; i < this.inputNodes.size(); i++)
			this.inputNodes.get(i).set(trainingSet.getRow(i));
		this.trainingSet = trainingSet;
		this.targetOutput = targetOutput;
		cacheTargetOutputRows(targetOutput);
	}

	// private void writeObject(ObjectOutputStream out) throws IOException
	// {
	// 	out.writeObject(this.inputNodes);
	// 	out.writeObject(this.outputNeurons);
	// 	// out.writeObject(this.trainingSet);
	// 	// out.writeObject(this.targetOutput);
	//
	// 	// out.writeObject(this.testingSet);
	// 	// out.writeObject(this.testingTargetOutputs);
	// 	// out.writeBoolean(this.earlyStopping);
	// 	// out.writeDouble(this.minimumTestingError);
	// }
	//
	// @SuppressWarnings("unchecked")
	// private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException
	// {
	// 	ArrayList<InputNode> inputNodes = (ArrayList<InputNode>)in.readObject();
	// 	ArrayList<Neuron> outputNeurons = (ArrayList<Neuron>)in.readObject();
	// 	// Matrix trainingSet = (Matrix)in.readObject();
	// 	// Matrix targetOutput = (Matrix)in.readObject();
	// 	this.init(inputNodes, outputNeurons);
	//
	// 	// this.testingSet = (Matrix)in.readObject();
	// 	// this.testingTargetOutputs = (Matrix)in.readObject();
	// 	// this.earlyStopping = (boolean)in.readBoolean();
	// 	// this.minimumTestingError = (double)in.readDouble();
	// }

	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeUTF(getWeightInfo());
		// out.writeInt(this.inputNodes.size());
		// for (InputNode n : this.inputNodes)
		// {
			// n.ge
			// n.writeExternal(out);
		// }
		
		// out.writeInt(this.inputNodes.size());
		// for (InputNode n : this.inputNodes)
		// 	n.writeExternal(out);
		// out.writeInt(this.outputNeurons.size());
		// for (Neuron n : this.outputNeurons)
		// 	n.writeExternal(out);
	}

	private class Connection
	{
		private String from;
		private String to;
		private double weight;

		public Connection(String str)
		{

			String[] tokens = str.split(Neuron.WEIGHT_IDENTIFIER);
			if (str.contains(Neuron.CONNECTION_IDENTIFIER))
			{
				String[] connectionTokens = tokens[0].split(Neuron.CONNECTION_IDENTIFIER);
				from = connectionTokens[0];
				to = connectionTokens[1];
			}
			else
			{
				from = tokens[0];
				to = null;
			}
			weight = Double.parseDouble(tokens[1]);
		}

		public boolean isBias() { return to == null; };
	}

	public void readExternal(ObjectInput in) throws ClassNotFoundException, IOException
	{
		ArrayList<Connection> connections = new ArrayList<>();
		ArrayList<Connection> biases = new ArrayList<>();
		HashMap<String, IPropagable> nodesHash = new HashMap<>();

		// read file
		String s = in.readUTF();
		String[] lines = s.split("\\r?\\n|\\r");
		for (String line : lines)
		{
			Connection conn = new Connection(line);
			if (conn.isBias())
				biases.add(conn);
			else
				connections.add(conn);
		}

		// add neurons
		ArrayList<Neuron> neurons = new ArrayList<>();
		for (Connection c : biases) // can only be neurons
		{
			Neuron n = new Neuron(c.weight, c.from);
			neurons.add(n);
			nodesHash.put(c.from, n);
		}

		// add inputNodes and connections
		ArrayList<InputNode> inputNodes = new ArrayList<>();
		for (Connection c : connections)
		{
			IPropagable n;
			if (!nodesHash.containsKey(c.from)) // must be InputNode
			{
				InputNode iNode = new InputNode(c.from);
				inputNodes.add(iNode);
				n = iNode;
				nodesHash.put(c.from, iNode);
			}
			else
			{
				n = nodesHash.get(c.from);
			}
			n.connect(nodesHash.get(c.to), c.weight);
		}

		inputNodes.sort((x, y) -> {
			return Integer.parseInt(x.name().substring(InputNode.IDENTIFIER.length())) - Integer.parseInt(y.name().substring(InputNode.IDENTIFIER.length()));
		});

		// identify outputNeurons (after connecting everything)
		ArrayList<Neuron> outputNeurons = new ArrayList<>();
		for (Neuron n : neurons)
			if (n.isOutputNeuron())
				outputNeurons.add(n);

		outputNeurons.sort((x, y) -> {
			return Integer.parseInt(x.name().substring(Neuron.IDENTIFIER.length())) - Integer.parseInt(y.name().substring(Neuron.IDENTIFIER.length()));
		});

		init(inputNodes, outputNeurons);
	}
}
