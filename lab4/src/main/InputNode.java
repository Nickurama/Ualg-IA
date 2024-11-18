import java.util.ArrayList;

public class InputNode implements IPropagable
{
	private ArrayList<Double> weights; // forward
	private ArrayList<IPropagable> forwardNeurons;
	private double input;

	public InputNode(double in)
	{
		this.input = in;
		this.weights = new ArrayList<>();
		this.forwardNeurons = new ArrayList<>();
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

	public void setInput(double newIn)
	{
		this.input = newIn;
	}
}
