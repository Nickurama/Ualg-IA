import java.util.ArrayList;

public class Neuron implements IPropagable
{
	private ArrayList<Double> weights; // forward
	private ArrayList<IPropagable> forwardNeurons;
	private ArrayList<IPropagable> backwardNeurons;
	private Double bias;

	private ArrayList<Double> inputs;
	private double sumCache;
	private double outCache;

	public Neuron(Double bias)
	{
		// shallow copy
		// this.weights = weights;
		this.bias = bias;
		this.weights = new ArrayList<>();
		this.forwardNeurons = new ArrayList<>();
		this.backwardNeurons = new ArrayList<>();

		this.inputs = new ArrayList<>();
		this.sumCache = -1;
		this.outCache = -1;
	}

	private static double sigmoid(double z) { return 1 / (1 + Math.exp(-z)); }

	private static double dSigmoid(double z) { return sigmoid(z) * (1 - sigmoid(z)); }

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
		return inputs.size() == backwardNeurons.size();
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
	public void addInput(Double in) { this.inputs.add(in); }

	@Override
	public double output() { return this.outCache; } // requires propagation

	private double summation()
	{
		// if (this.inputs.size() != this.weights.size())
		// 	throw new IllegalArgumentException("Inputs and weights do not have the same size");

		double result = bias;
		for (int i = 0; i < this.inputs.size(); i++)
			result += this.inputs.get(i);
			// result += this.inputs.get(i) * weights.get(i);
		return result;
	}

	// public double output(ArrayList<Double> inputs) { return sigmoid(summation(inputs)); }
	//
	// public double binaryOutput(ArrayList<Double> inputs) { return output(inputs) >= 0.5 ? 1 : 0; }
}
