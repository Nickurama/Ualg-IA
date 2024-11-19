import java.util.ArrayList;
import java.util.function.Function;

public class Neuron implements IPropagable
{
	// network
	// private ArrayList<Double> weights; // forward
	private Matrix weights; // first weight is bias
	private ArrayList<IPropagable> forwardNeurons;
	private ArrayList<IPropagable> backwardNeurons;
	// private Double bias;
	// private boolean hasTrained;

	// propagation
	// private ArrayList<Double> inputsCache;
	private static final Function<Double, Double> SIGMOID_FUNC = z -> sigmoid(z);
	// private Matrix inputsCache; // can be null
	private double numInputs;
	private Matrix sumCache; // can be null
	private Matrix outCache; // can be null

	// backpropagation
	// private ArrayList<Double> dWeightsCache;
	// private double dSigmoidCache;
	// private double dBiasCache;
	// private double errorTermCache;

	public Neuron(double bias)
	{
		// this.bias() = bias;
		// this.weights = new ArrayList<>();
		this.weights = new Matrix(new double[][]{{ bias }});
		this.forwardNeurons = new ArrayList<>();
		this.backwardNeurons = new ArrayList<>();

		// this.dWeightsCache = new ArrayList<>();
		this.resetValues(true);
		// this.hasTrained = false;
	}

	private static double sigmoid(double z) { return 1.0 / (1.0 + Math.exp(-z)); }

	private static double dSigmoid(double z) { return sigmoid(z) * (1.0 - sigmoid(z)); }

	@Override
	public void connect(IPropagable that, double weight)
	{
		// this.weights = this.weights.addRow(new double[] { weight });
		// this.weights.add(weight);
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
		//FIXME
		// if (this.inputsCache == null)
		// 	return false;
		return this.numInputs == backwardNeurons.size();
	}

	@Override
	public void propagate()
	{
		numInputs++;
		if (!hasAllInputs())
			return;

		Matrix inputs = gatherInputs();
		this.sumCache = this.weights.transpose().dot(inputs);
		this.outCache = sumCache.apply(SIGMOID_FUNC);

		for (IPropagable p : this.forwardNeurons)
			p.propagate();
		// for (int i = 0; i < this.forwardNeurons.size(); i++)
		// {
		// 	IPropagable curr = forwardNeurons.get(i);
		// 	// curr.addInput(outCache * weights.get(i, 0));
		// 	curr.propagate();
		// }
	}

	private Matrix gatherInputs()
	{
		int numTrainingCases = this.backwardNeurons.get(0).output().columns();
		Matrix inputs = new Matrix(1, numTrainingCases, 1);
		for (IPropagable a : backwardNeurons)
			inputs = inputs.appendAsRows(a.output());
		return inputs;
	}

	// private Matrix summation()
	// {
	// 	return this.weights.transpose().dot(this.inputsCache);
	// 	// double result = this.bias();
	// 	// for (int i = 0; i < this.inputsCache.size(); i++)
	// 	// 	result += this.inputsCache.get(i);
	// 	// return result;
	// }

	// @Override
	// public void addInput() // a_n^(0), a_n^(1), ..., a_n^(m);
	// {
	// 	// if (this.inputsCache == null)
	// 	// 	this.inputsCache = new Matrix(1, inputsRow.columns, 1.0);
	// 	// this.inputsCache = this.inputsCache.appendAsRows(inputsRow);
	// 	numInputs++;
	// }

	@Override
	public Matrix output() // requires propagation
	{
		return this.outCache;
	}

	// TODO
	// @Override
	// public void backpropagate(double errorDifference)
	// {
	// 	if (!this.forwardNeurons.isEmpty())
	// 	{
	// 		this.dWeightsCache.add(0.0);
	// 		if (!hasAllErrorTerms())
	// 			return;
	// 	}
	//
	// 	updateErrorTerm();
	// 	updateWeightDeltas(errorDifference);
	//
	// 	for (IPropagable p : backwardNeurons)
	// 		p.backpropagate(errorDifference);
	//
	// 	resetValues(false);
	// }

	// private boolean hasAllErrorTerms()
	// {
	// 	return this.dWeightsCache.size() >= this.forwardNeurons.size();
	// }

	// private void updateErrorTerm()
	// {
	// 	this.errorTermCache = dSigmoid(sumCache);
	// 	double sum = 0;
	// 	for (int i = 0; i < forwardNeurons.size(); i++)
	// 		sum += weights.get(i, 0) * forwardNeurons.get(i).errorTerm();
	// 	if (forwardNeurons.size() > 0)
	// 		this.errorTermCache *= sum;
	// }

	// must be ran after having errorTermCache
	// private void updateWeightDeltas(double errorDifference)
	// {
	// 	// calculate bias delta
	// 	this.dBiasCache += errorDifference * this.errorTerthis.weights.transpose().dot(inputs)m();
	// 	// calculate weights delta
	// 	for (int i = 0; i < weights.rows(); i++)
	// 	{
	// 		double delta = errorDifference * dSigmoid(this.sumCache) * this.forwardNeurons.get(i).errorTerm();
	// 		this.dWeightsCache.set(i, this.dWeightsCache.get(i) + delta);
	// 	}
	// }

	// @Override
	// public double errorTerm()
	// {
	// 	// FIXME remove
	// 	return this.errorTermCache;
	// }

	@Override
	public void resetCaches()
	{
		if (this.isClear())
			return;

		resetValues(true);
		for (IPropagable p : forwardNeurons)
			p.resetCaches();
	}

	private boolean isClear()
	{
		// FIXME
		return false;
		// return this.inputsCache == null;
	}

	private void resetValues(boolean clearWeightsDeltas)
	{
		// FIXME
		if (clearWeightsDeltas)
		{
			// this.dWeightsCache.clear();
			// this.dBiasCache = 0;
		}
		// this.inputsCache.clear();
		// this.inputsCache = null;
		this.numInputs = 0;
		this.sumCache = null;
		this.outCache = null;
		// this.errorTermCache = 0;
		// this.dSigmoidCache = -1;
		// this.hasTrained = false;
	}

	// @Override
	// public void train(double learningRate, double numTrainingSets)
	// {
	// 	this.train(learningRate * (2 / numTrainingSets)); // 2 can be replaced with 1
	// }
	//
	// @Override
	// public void train(double quotient)
	// {
	// 	if (this.hasTrained)
	// 		return;
	// 	this.hasTrained = true;
	//
	// 	this.weights.set(0, 0, this.bias() + quotient * this.dBiasCache);
	// 	// this.bias = this.bias() + quotient * this.dBiasCache;
	// 	for (int i = 0; i < this.weights.rows(); i++)
	// 	{
	// 		double delta = quotient * this.dWeightsCache.get(i);
	// 		this.weights.set(i, 0, this.weights.get(i, 0) + delta);
	// 	}
	//
	// 	for (IPropagable p : forwardNeurons)
	// 		p.train(quotient);
	// }

	@Override
	public Matrix weights()
	{
		return this.weights;
	}

	@Override
	public double bias()
	{
		return this.weights.get(0, 0);
		// return this.bias();
	}

	// public double evaluateOutput() { return this.output() >= 0.5 ? 1 : 0; }
}
