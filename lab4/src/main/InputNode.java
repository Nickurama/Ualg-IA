import java.util.ArrayList;

public class InputNode implements IPropagable
{
	private ArrayList<IPropagable> forwardNeurons;
	private Matrix input;

	public InputNode(Matrix in)
	{
		set(in);
		this.forwardNeurons = new ArrayList<>();
	}

	@Override
	public void propagate()
	{
		for (IPropagable p : forwardNeurons)
			p.propagate();
	}

	@Override
	public Matrix output()
	{
		return this.input;
	}

	@Override
	public void connect(IPropagable that, double weight)
	{
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

	@Override
	public void resetCaches()
	{
		for (IPropagable p : forwardNeurons)
			p.resetCaches();
	}

	@Override
	public void backpropagate(Matrix deltas, double learningRate)
	{
		// shouldn't do anything
	}

	@Override
	public Matrix weights()
	{
		throw new IllegalAccessError("InputNodes have no weights");
	}

	@Override
	public double bias()
	{
		throw new IllegalAccessError("InputNodes have no bias");
	}

	public void set(Matrix in)
	{
		if (in.rows() > 1)
			throw new IllegalArgumentException("Input should have only 1 row.");

		this.input = in;
	}
}
