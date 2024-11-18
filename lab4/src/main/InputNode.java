import java.util.ArrayList;

public class InputNode implements IPropagable
{
	private ArrayList<Double> weights; // forward
	private ArrayList<IPropagable> forwardNeurons;
	private double input;
	private ArrayList<Double> dWeightsCache;

	public InputNode(double in)
	{
		this.input = in;
		this.weights = new ArrayList<>();
		this.forwardNeurons = new ArrayList<>();
		this.dWeightsCache = new ArrayList<>();
	}

	@Override
	public void propagate()
	{
		for (int i = 0; i < this.forwardNeurons.size(); i++)
		{
			IPropagable curr = forwardNeurons.get(i);
			curr.addInput(input * weights.get(i));
			curr.propagate();
		}
	}

	@Override
	public double output()
	{
		return this.input;
	}

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
		throw new IllegalAccessError("backConnect() should never be called for class InputNode");
	}

	@Override
	public void fwrdConnect(IPropagable that)
	{
		this.forwardNeurons.add(that);
	}

	@Override
	public void addInput(Double in)
	{
		throw new IllegalAccessError("addInput() should never be called for class InputNode");
	}

	@Override
	public void resetCaches()
	{
		this.dWeightsCache.clear();
		for (IPropagable p : forwardNeurons)
			p.resetCaches();
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

		updateWeightDeltas(errorDifference);
	}

	private boolean hasAllErrorTerms()
	{
		return this.dWeightsCache.size() >= this.forwardNeurons.size();
	}

	private void updateWeightDeltas(double errorDifference)
	{
		for (int i = 0; i < weights.size(); i++)
		{
			double delta = errorDifference * this.input * this.forwardNeurons.get(i).errorTerm();
			this.dWeightsCache.set(i, this.dWeightsCache.get(i) + delta);
		}
	}

	@Override
	public void train(double learningRate, double numTrainingSets)
	{
		this.train(learningRate * (2.0 / numTrainingSets)); // 2 can be replaced with 1
	}

	@Override
	public void train(double quotient)
	{
		for (int i = 0; i < this.weights.size(); i++)
		{
			double delta = quotient * this.dWeightsCache.get(i);
			this.weights.set(i, this.weights.get(i) + delta);
		}

		for (IPropagable p : forwardNeurons)
			p.train(quotient);
	}

	@Override
	public double errorTerm()
	{
		throw new IllegalAccessError("errorTerm() should never be called for class InputNode");
	}

	@Override
	public ArrayList<Double> weights()
	{
		return this.weights;
	}

	@Override
	public double bias()
	{
		throw new IllegalAccessError("bias() should never be called for class InputNode");
	}

	public void set(double newIn)
	{
		this.input = newIn;
	}
}
