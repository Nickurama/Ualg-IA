import java.util.ArrayList;

public class InputNode implements IPropagable
{
	// private Matrix weights; // forward
	private ArrayList<IPropagable> forwardNeurons;
	private Matrix input;
	// private ArrayList<Double> dWeightsCache;

	public InputNode(double[] in)
	{
		this.input = new Matrix(new double[][] { in });
		// this.weights = new Matrix(new double[][]{{}});
		this.forwardNeurons = new ArrayList<>();
		// this.dWeightsCache = new ArrayList<>();
	}

	@Override
	public void propagate()
	{
		for (IPropagable p : forwardNeurons)
			p.propagate();

		// for (int i = 0; i < this.forwardNeurons.size(); i++)
		// {
		// 	IPropagable curr = forwardNeurons.get(i);
		// 	curr.markInputReady(input * weights.get(i, 0));
		// 	curr.propagate();
		// }
	}

	@Override
	public Matrix output()
	{
		return this.input;
	}

	@Override
	public void connect(IPropagable that, double weight)
	{
		// this.weights = this.weights.addRow(new double[] { weight });
		this.fwrdConnect(that);

		that.backConnect(this, weight);
	}

	@Override
	public void backConnect(IPropagable that, double weight)
	{
		throw new IllegalAccessError("backConnect() should never be called for class InputNode");
	}

	@Override
	public void fwrdConnect(IPropagable that)
	{
		this.forwardNeurons.add(that);
	}

	// @Override
	// public void addInput(Double in)
	// {
	// 	throw new IllegalAccessError("addInput() should never be called for class InputNode");
	// }

	@Override
	public void resetCaches()
	{
		// this.dWeightsCache.clear();
		for (IPropagable p : forwardNeurons)
			p.resetCaches();
	}

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
	// 	updateWeightDeltas(errorDifference);
	// }
	//
	// private boolean hasAllErrorTerms()
	// {
	// 	return this.dWeightsCache.size() >= this.forwardNeurons.size();
	// }
	//
	// private void updateWeightDeltas(double errorDifference)
	// {
	// 	for (int i = 0; i < weights.rows(); i++)
	// 	{
	// 		double delta = errorDifference * this.input * this.forwardNeurons.get(i).errorTerm();
	// 		this.dWeightsCache.set(i, this.dWeightsCache.get(i) + delta);
	// 	}
	// }
	//
	// @Override
	// public void train(double learningRate, double numTrainingSets)
	// {
	// 	this.train(learningRate * (2.0 / numTrainingSets)); // 2 can be replaced with 1
	// }
	//
	// @Override
	// public void train(double quotient)
	// {
	// 	for (int i = 0; i < this.weights.rows(); i++)
	// 	{
	// 		double delta = quotient * this.dWeightsCache.get(i);
	// 		this.weights.set(i, 0, this.weights.get(i, 0) + delta);
	// 	}
	//
	// 	for (IPropagable p : forwardNeurons)
	// 		p.train(quotient);
	// }
	//
	// @Override
	// public double errorTerm()
	// {
	// 	throw new IllegalAccessError("errorTerm() should never be called for class InputNode");
	// }

	@Override
	public Matrix weights()
	{
		throw new IllegalAccessError("InputNodes have no weights");
		// return this.weights;
	}

	@Override
	public double bias()
	{
		throw new IllegalAccessError("InputNodes have no bias");
	}

	public void set(double[] newIn)
	{
		this.input = new Matrix(new double[][]{ newIn });
	}
}
