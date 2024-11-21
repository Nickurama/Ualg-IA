import java.util.ArrayList;
import java.util.function.Function;

public class Neuron implements IPropagable
{
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
	private Matrix targetOutputCache; // can be null

	public Neuron(double bias)
	{
		this.weights = new Matrix(new double[][]{{ bias }});
		this.forwardNeurons = new ArrayList<>();
		this.backwardNeurons = new ArrayList<>();

		this.resetValues();
	}

	private static double sigmoid(double z) { return 1.0 / (1.0 + Math.exp(-z)); }

	private static double dSigmoid(double z) { return sigmoid(z) * (1.0 - sigmoid(z)); }

	@Override
	public void connect(IPropagable that, double weight)
	{
		this.fwrdConnect(that);
		that.backConnect(this, weight);
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
	public void backpropagate(Matrix deltas, double learningRate)
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
			this.targetOutputCache = new Matrix(deltas);
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
			this.backwardNeurons.get(i).backpropagate(this.deltaCache.multiply(this.weights.get(i + 1, 0)), learningRate);

		// calculate weight delta
		Matrix weightDeltas = this.inputCache.dot(this.deltaCache.transpose());
		weightDeltas = weightDeltas.multiply(learningRate * (2.0 / this.inputCache.columns()));

		// update weights
		this.weights = this.weights.add(weightDeltas);
	}

	private boolean isOutputNeuron()
	{
		return this.forwardNeurons.isEmpty();
	}

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

	private boolean isClear()
	{
		return numInputs + backpropInputs == 0;
	}

	private void resetValues()
	{
		this.backpropInputs = 0;
		this.numInputs = 0;
		this.sumCache = null;
		this.outCache = null;
		this.deltaCache = null;
		this.inputCache = null;
		this.targetOutputCache = null;
	}

	public double getError()
	{
		if (!isOutputNeuron())
			throw new IllegalCallerException("Only output neurons can calculate error!");

		Matrix err = this.targetOutputCache.sub(this.outCache);
		err = err.dot(err.transpose()).multiply(1.0 / this.targetOutputCache.columns());
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
}
