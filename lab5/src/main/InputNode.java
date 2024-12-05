import java.util.ArrayList;

/**
 * This class represents an input node in a neural network.
 */
public class InputNode implements IPropagable
{
	// serializable
	private static final long serialVersionUID = 140L;

	private static final String IDENTIFIER = "x";
	private static int count = 1;
	private String name;
	private ArrayList<IPropagable> forwardNeurons;
	private Matrix input;

	/**
	 * instantiates an input node
	 * @param in a matrix with a single row, with all the training inputs for the node
	 */
	public InputNode(Matrix in)
	{
		this.forwardNeurons = new ArrayList<>();
		set(in);
		setDefaultName();
	}

	/**
	 * instantiates an input node
	 * @param in a matrix with a single row, with all the training inputs for the node
	 * @param nodeName a string representation of the node
	 */
	public InputNode(Matrix in, String nodeName)
	{
		this(in);
		this.name = nodeName;
		count--;
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
	public void connect(IPropagable that)
	{
		this.fwrdConnect(that);

		that.backConnect(this, Neuron.getRandomWeight());
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
	public void backpropagate(Matrix deltas, double learningRate, int numOutputs)
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

	/**
	 * changes the input matrix of this node.
	 * @param in the new input matrix.
	 */
	public void set(Matrix in)
	{
		if (in.rows() > 1)
			throw new IllegalArgumentException("Input should have only 1 row.");

		this.input = in;
		resetCaches();
	}

	/**
	 * generates a default name for the node
	 */
	private void setDefaultName()
	{
		this.name = IDENTIFIER + count;
		count++;
	}

	@Override
	public String name()
	{
		return this.name;
	}

	@Override
	public ArrayList<String> getWeightInfo(ArrayList<String> previousInfo)
	{
		for (IPropagable p : forwardNeurons)
			p.getWeightInfo(previousInfo);
		return previousInfo;
	}
}
