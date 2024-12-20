import java.util.ArrayList;
import java.util.Random;
import java.util.function.Function;

/**
 * Represents a neuron in a neural network.
 */
public class Neuron implements IPropagable
{
	// random number generator
	private static Random rng;
	private static long seed = 0;

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
	 * Sets the seed to use for weight generation.
	 * @param seed the new seed
	 */
	public static void setSeed(long seed)
	{
		Neuron.seed = seed;
	}

	/**
	 * The seed the neurons are using.
	 * @return the seed the neurons are using
	 */
	public static long seed()
	{
		if (rng == null)
			generateRandom();
		return Neuron.seed;
	}

	/**
	 * @return a random weight between -1 and 1.
	 */
	public static double getRandomWeight()
	{
		if (rng == null)
			generateRandom();
		return rng.nextBoolean() ? rng.nextDouble() : - 1.0 * rng.nextDouble();
	}

	/**
	 * instantiates the random number generator.
	 */
	private static void generateRandom()
	{
		rng = new Random();
		if (seed == 0)
			seed = rng.nextLong();
		rng.setSeed(seed);
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
	 */
	private boolean hasAllInputs()
	{
		return this.numInputs == backwardNeurons.size();
	}

	@Override
	public void propagate()
	{
		numInputs++;
		if (!hasAllInputs())
			return;

		this.inputCache = gatherInputs();
		this.sumCache = this.weights.transpose().dot(this.inputCache);
		this.outCache = sumCache.apply(SIGMOID_FUNC);

		for (IPropagable p : this.forwardNeurons)
			p.propagate();
	}

	/**
	 * Gathers all the inputs.
	 * @pre should have all inputs
	 * @return a matrix with all the inputs
	 */
	private Matrix gatherInputs()
	{
		int numTrainingCases = this.backwardNeurons.get(0).output().columns();
		Matrix inputs = new Matrix(1, numTrainingCases, 1);
		for (IPropagable a : backwardNeurons)
			inputs = inputs.appendAsRows(a.output());
		return inputs;
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
		this.sumCache = null;
		this.outCache = null;
		this.deltaCache = null;
		this.inputCache = null;
	}

	/**
	 * The error function (MSE)
	 * @pre should be an output neuron
	 * @param targetOutput the target values for this output node.
	 * @return the error function (MSE)
	 */
	public double getError(Matrix targetOutput)
	{
		if (!isOutputNeuron())
			throw new IllegalCallerException("Only output neurons can calculate error!");

		Matrix err = targetOutput.sub(this.outCache);
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
