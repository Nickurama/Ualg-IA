import java.util.ArrayList;

public class Neuron implements IPropagable
{
	// network
	private ArrayList<Double> weights; // forward
	private ArrayList<IPropagable> forwardNeurons;
	private ArrayList<IPropagable> backwardNeurons;
	private Double bias;
	private boolean hasTrained;

	// propagation
	private ArrayList<Double> inputsCache;
	private double sumCache;
	private double outCache;

	// backpropagation
	private ArrayList<Double> dWeightsCache;
	private double dSigmoidCache;
	private double dBiasCache;
	private double errorTermCache;

	public Neuron(Double bias)
	{
		this.bias = bias;
		this.weights = new ArrayList<>();
		this.forwardNeurons = new ArrayList<>();
		this.backwardNeurons = new ArrayList<>();

		this.inputsCache = new ArrayList<>();
		this.dWeightsCache = new ArrayList<>();
		this.resetValues(true);
		this.hasTrained = false;
	}

	private static double sigmoid(double z) { return 1.0 / (1.0 + Math.exp(-z)); }

	private static double dSigmoid(double z) { return sigmoid(z) * (1.0 - sigmoid(z)); }

	@Override
	public void connect(IPropagable that, Double weight)
	{
		this.weights.add(weight);
		this.fwrdConnect(that);

		that.backConnect(this);
	}

	@Override
	public void backConnect(IPropagable that)
	{
		this.backwardNeurons.add(that);
	}

	@Override
	public void fwrdConnect(IPropagable that)
	{
		this.forwardNeurons.add(that);
	}

	private boolean hasAllInputs()
	{
		return inputsCache.size() == backwardNeurons.size();
	}

	@Override
	public void propagate()
	{
		if (!hasAllInputs())
			return;

		this.sumCache = summation();
		this.outCache = sigmoid(sumCache);

		for (int i = 0; i < this.forwardNeurons.size(); i++)
		{
			IPropagable curr = forwardNeurons.get(i);
			curr.addInput(outCache * weights.get(i));
			curr.propagate();
		}
	}

	@Override
	public void addInput(Double in) { this.inputsCache.add(in); }

	@Override
	public double output() { return this.outCache; } // requires propagation

	private double summation()
	{
		double result = bias;
		for (int i = 0; i < this.inputsCache.size(); i++)
			result += this.inputsCache.get(i);
		return result;
	}

	@Override
	public void backpropagate(double errorDifference)
	{
		if (!this.forwardNeurons.isEmpty())
		{
			this.dWeightsCache.add(0.0);
			if (!hasAllErrorTerms())
				return;
		}

		updateErrorTerm();
		updateWeightDeltas(errorDifference);

		for (IPropagable p : backwardNeurons)
			p.backpropagate(errorDifference);

		resetValues(false);
	}

	private boolean hasAllErrorTerms()
	{
		return this.dWeightsCache.size() >= this.forwardNeurons.size();
	}

	private void updateErrorTerm()
	{
		this.errorTermCache = dSigmoid(sumCache);
		double sum = 0;
		for (int i = 0; i < forwardNeurons.size(); i++)
			sum += weights.get(i) * forwardNeurons.get(i).errorTerm();
		if (forwardNeurons.size() > 0)
			this.errorTermCache *= sum;
	}

	// must be ran after having errorTermCache
	private void updateWeightDeltas(double errorDifference)
	{
		// calculate bias delta
		this.dBiasCache += errorDifference * this.errorTerm();
		// calculate weights delta
		for (int i = 0; i < weights.size(); i++)
		{
			double delta = errorDifference * dSigmoid(this.sumCache) * this.forwardNeurons.get(i).errorTerm();
			this.dWeightsCache.set(i, this.dWeightsCache.get(i) + delta);
		}
	}

	@Override
	public double errorTerm()
	{
		return this.errorTermCache;
	}

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
		return this.inputsCache.isEmpty();
	}

	private void resetValues(boolean clearWeightsDeltas)
	{
		if (clearWeightsDeltas)
		{
			this.dWeightsCache.clear();
			this.dBiasCache = 0;
		}
		this.inputsCache.clear();
		this.sumCache = -1;
		this.outCache = -1;
		this.errorTermCache = 0;
		this.dSigmoidCache = -1;
		this.hasTrained = false;
	}

	@Override
	public void train(double learningRate, double numTrainingSets)
	{
		this.train(learningRate * (2 / numTrainingSets)); // 2 can be replaced with 1
	}

	@Override
	public void train(double quotient)
	{
		if (this.hasTrained)
			return;
		this.hasTrained = true;

		this.bias = this.bias + quotient * this.dBiasCache;
		for (int i = 0; i < this.weights.size(); i++)
		{
			double delta = quotient * this.dWeightsCache.get(i);
			this.weights.set(i, this.weights.get(i) + delta);
		}

		for (IPropagable p : forwardNeurons)
			p.train(quotient);
	}

	@Override
	public ArrayList<Double> weights()
	{
		return this.weights;
	}

	@Override
	public double bias()
	{
		return this.bias;
	}

	public double evaluateOutput() { return this.output() >= 0.5 ? 1 : 0; }
}
