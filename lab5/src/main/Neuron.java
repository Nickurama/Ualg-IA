import java.util.ArrayList;
import java.util.Random;
import java.util.function.Function;

/**
 * Represents a neuron in a neural network.
 */
public class Neuron implements IPropagable
{
	// serializable
	private static final long serialVersionUID = 139L;

	// naming
	private static final String IDENTIFIER = "n";
	private static int count = 1;
	private String name;
	private boolean hasGivenInfo;

	// network
	private Matrix weights; // first weight is bias
	private ArrayList<IPropagable> forwardNeurons;
	private ArrayList<IPropagable> backwardNeurons;

	// propagation
	private static final Function<Double, Double> SIGMOID_FUNC = z -> sigmoid(z);
	private static final Function<Double, Double> D_SIGMOID_FUNC = z -> dSigmoid(z);
	private int numInputs;
	private Matrix inputCache; // can be null
	private Matrix sumCache; // can be null
	private Matrix outCache; // can be null
	private int numInputsGhost;
	private Matrix outCacheGhost; // can be null

	// backpropagation
	private int backpropInputs;
	private Matrix deltaCache; // can be null

	/**
	 * Instantiates a neuron.
	 * @param bias the bias of the neuron
	 * @param neuronName a string representation for the neuron
	 */
	public Neuron(double bias, String neuronName)
	{
		this.weights = new Matrix(new double[][]{{ bias }});
		this.forwardNeurons = new ArrayList<>();
		this.backwardNeurons = new ArrayList<>();

		this.resetValues();
		this.name = neuronName;
	}

	/**
	 * Instantiates a neuron.
	 * @param bias the bias of the neuron
	 */
	public Neuron(double bias)
	{
		this(bias, generateDefaultName());
	}

	/**
	 * Instantiates a neuron with a random bias.
	 * @param neuronName a string representation for the neuron
	 */
	public Neuron(String neuronName)
	{
		this(getRandomWeight(), neuronName);
	}

	/**
	 * Instantiates a neuron with a random bias.
	 */
	public Neuron()
	{
		this(getRandomWeight(), generateDefaultName());
	}

	/**
	 * @return a random weight between -1 and 1.
	 */
	public static double getRandomWeight()
	{
		return RandomNumberGenerator.getRandomBounded(1);
	}

	/**
	 * sigmoid function
	 * @param z sigmoid parameter
	 * @return sigmoid
	 */
	private static double sigmoid(double z) { return 1.0 / (1.0 + Math.exp(-z)); }

	/**
	 * sigmoid derivative function
	 * @param z sigmoid derivative parameter
	 * @return sigmoid derivative
	 */
	private static double dSigmoid(double z) { return sigmoid(z) * (1.0 - sigmoid(z)); }

	@Override
	public void connect(IPropagable that, double weight)
	{
		this.fwrdConnect(that);
		that.backConnect(this, weight);
	}

	@Override
	public void connect(IPropagable that)
	{
		this.fwrdConnect(that);
		that.backConnect(this, getRandomWeight());
	}

	@Override
	public void backConnect(IPropagable that, double connWeight)
	{
		this.backwardNeurons.add(that);
		this.weights = this.weights.addRow(new double[] { connWeight });
	}

	@Override
	public void fwrdConnect(IPropagable that)
	{
		this.forwardNeurons.add(that);
	}

	/**
	 * @return true if the neuron has all the necessary inputs to propagate itself
	 * @param isGhostInputs if the neuron is propagating or ghostpropagating
	 */
	private boolean hasAllInputs(boolean isGhostInputs)
	{
		if (isGhostInputs)
			return this.numInputsGhost == backwardNeurons.size();
		return this.numInputs == backwardNeurons.size();
	}

	@Override
	public void propagate()
	{
		final boolean isGhost = false;

		numInputs++;
		if (!hasAllInputs(isGhost))
			return;

		this.inputCache = gatherInputs(isGhost);
		this.sumCache = this.weights.transpose().dot(this.inputCache);
		this.outCache = sumCache.apply(SIGMOID_FUNC);

		for (IPropagable p : this.forwardNeurons)
			p.propagate();
	}

	@Override
	public void ghostpropagate()
	{
		final boolean isGhost = true;

		numInputsGhost++;
		if (numInputsGhost > backwardNeurons.size()) // ghostpropagating has been done before
			numInputsGhost = 1;
		if (!hasAllInputs(isGhost))
			return;

		this.outCacheGhost = this.weights.transpose().dot(gatherInputs(isGhost)).apply(SIGMOID_FUNC);
		
		for (IPropagable p : this.forwardNeurons)
			p.ghostpropagate();
	}


	/**
	 * Gathers all the inputs.
	 * @pre should have all inputs
	 * @param isGhostInputs if the neuron is propagating or ghostpropagating
	 * @return a matrix with all the inputs
	 */
	private Matrix gatherInputs(boolean isGhostInputs)
	{
		int numTrainingCases = isGhostInputs ? this.backwardNeurons.get(0).ghostOutput().columns() : this.backwardNeurons.get(0).output().columns();
		Matrix inputs = new Matrix(1, numTrainingCases, 1);

		if (isGhostInputs)
			for (IPropagable a : backwardNeurons)
				inputs = inputs.appendAsRows(a.ghostOutput());
		else
			for (IPropagable a : backwardNeurons)
				inputs = inputs.appendAsRows(a.output());

		return inputs;
	}

	@Override
	public Matrix ghostOutput() // requires propagation
	{
		return this.outCacheGhost;
	}

	@Override
	public Matrix output() // requires propagation
	{
		return this.outCache;
	}

	@Override
	public void backpropagate(Matrix deltas, double learningRate, int numOutputs)
	{
		this.backpropInputs++;
		if (this.deltaCache == null)
		{
			this.deltaCache = deltas;
		}
		else
		{
			this.deltaCache = this.deltaCache.add(deltas);
		}

		if (this.isOutputNeuron()) // deltas should be T on output node
		{
			this.deltaCache = deltas.sub(this.outCache); // (T - Y)
		}
		else if (!hasAllBackpropInputs())
		{
			return;
		}

		// calculate delta
		Matrix dSigmoid = this.sumCache.apply(D_SIGMOID_FUNC);
		Matrix dSigmoidDiag = dSigmoid.makeDiagonal();
		this.deltaCache = dSigmoidDiag.dot(this.deltaCache.transpose()).transpose();

		// backpropagate corresponding w * delta
		for (int i = 0; i < this.backwardNeurons.size(); i++)
			this.backwardNeurons.get(i).backpropagate(this.deltaCache.multiply(this.weights.get(i + 1, 0)), learningRate, numOutputs);

		// calculate weight delta
		Matrix weightDeltas = this.inputCache.dot(this.deltaCache.transpose());
		weightDeltas = weightDeltas.multiply(learningRate * (2.0 / (this.inputCache.columns() * numOutputs)));

		// update weights
		this.weights = this.weights.add(weightDeltas);

		// reset caches for this node only
		resetValues();
	}

	/**
	 * @return true if the neuron is an output neuron.
	 */
	private boolean isOutputNeuron()
	{
		return this.forwardNeurons.isEmpty();
	}

	/**
	 * @return true if the neuron has all backpropagation inputs needed to backpropagate
	 */
	private boolean hasAllBackpropInputs()
	{
		return this.backpropInputs == this.forwardNeurons.size();
	}

	@Override
	public void resetCaches()
	{
		if (this.isClear())
			return;

		resetValues();
		for (IPropagable p : forwardNeurons)
			p.resetCaches();
	}

	/**
	 * @return true if the neuron has already been cleared.
	 */
	private boolean isClear()
	{
		return numInputs + backpropInputs == 0;
	}

	/**
	 * resets the variables to their default state.
	 */
	private void resetValues()
	{
		this.hasGivenInfo = false;
		this.backpropInputs = 0;
		this.numInputs = 0;
		this.numInputsGhost = 0;
		this.outCacheGhost = null;
		this.sumCache = null;
		this.outCache = null;
		this.deltaCache = null;
		this.inputCache = null;
	}

	/**
	 * the error function (MSE) of the current neuron.
	 * @pre should be an output neuron
	 * @param targetOutput the target values for this output node.
	 * @param isGhost if the error is to be calculated to the ghost output or the normal one.
	 * @return the error function (MSE)
	 */
	public double getError(Matrix targetOutput, boolean isGhost)
	{
		if (!isOutputNeuron())
			throw new IllegalCallerException("Only output neurons can calculate error!");


		if (isGhost)
			return calcError(this.outCacheGhost, targetOutput);
		return calcError(this.outCache, targetOutput);
		// Matrix err = targetOutput.sub(this.outCache);
		// err = err.dot(err.transpose()).multiply(1.0 / targetOutput.columns());
		// return err.parse();
	}

	/**
	 * the error function (MSE).
	 * @param output the output of a neuron
	 * @param targetOutput the target output of that neuron
	 * @return the mean square error
	 */
	private static double calcError(Matrix output, Matrix targetOutput)
	{
		Matrix err = targetOutput.sub(output);
		err = err.dot(err.transpose()).multiply(1.0 / targetOutput.columns());
		return err.parse();
	}

	@Override
	public Matrix weights()
	{
		return this.weights;
	}

	@Override
	public double bias()
	{
		return this.weights.get(0, 0);
	}

	/**
	 * @return a default name for the neuron
	 */
	private static String generateDefaultName()
	{
		return IDENTIFIER + count++;
	}

	@Override
	public String name()
	{
		return this.name;
	}

	@Override
	public ArrayList<String> getWeightInfo(ArrayList<String> previousInfo)
	{
		if (this.hasGivenInfo) // requires cache reset to give info again
			return previousInfo;
		this.hasGivenInfo = true;

		for (int i = 0; i < this.backwardNeurons.size(); i++)
		{
			String newInfo = this.backwardNeurons.get(i).name() + " -> " + this.name() + " : " + this.weights.get(i + 1, 0);
			previousInfo.add(newInfo);
		}
		previousInfo.add(this.name() + " : " + this.weights.get(0, 0));

		for (IPropagable p : forwardNeurons)
			previousInfo = p.getWeightInfo(previousInfo);
		return previousInfo;
	}
}
