import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * This class represents an input node in a neural network.
 */
public class InputNode implements IPropagable
{
	// serializable
	private static final long serialVersionUID = 140L;

	public static final String IDENTIFIER = "x";
	private static int count = 1;
	private String name;
	private ArrayList<IPropagable> forwardNeurons;
	private Matrix input;
	private Matrix ghostInput;

	/**
	 * instantiates an input node
	 * @param in a matrix with a single row, with all the training inputs for the node
	 */
	public InputNode(Matrix in)
	{
		init(new ArrayList<>(), in, null, getDefaultName());
		set(in);
	}

	/**
	 * initializes an input node
	 * @param forwardNeurons the forward connections
	 * @param in the input matrix it holds
	 * @param ghostIn the ghost input matrix it holds (can be null)
	 * @param name the name of the neuron
	 */
	private final void init(ArrayList<IPropagable> forwardNeurons, Matrix in, Matrix ghostIn, String name)
	{
		this.forwardNeurons = forwardNeurons;
		this.input = in;
		this.ghostInput = ghostIn;
		this.name = name;
	}

	public InputNode(String name)
	{
		init(new ArrayList<>(), new Matrix(new double[][]{{}}), null, name);
	}

	public InputNode()
	{
		init(new ArrayList<>(), new Matrix(new double[][]{{}}), null, getDefaultName());
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
	public void ghostpropagate()
	{
		for (IPropagable p : forwardNeurons)
			p.ghostpropagate();
	}

	@Override
	public Matrix output()
	{
		return this.input;
	}

	@Override
	public Matrix ghostOutput()
	{
		return this.ghostInput;
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
	public final void resetCaches()
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
	public final void set(Matrix in)
	{
		if (in.rows() > 1)
			throw new IllegalArgumentException("Input should have only 1 row.");

		this.input = in;
		resetCaches();
	}

	/**
	 * changes the ghost input matrix of this node.
	 * @param in the new ghost input matrix.
	 */
	public void setGhost(Matrix in)
	{
		if (in.rows() > 1)
			throw new IllegalArgumentException("Input should have only 1 row.");

		this.ghostInput = in;
	}

	/**
	 * generates a default name for the node
	 */
	private String getDefaultName()
	{
		String name = IDENTIFIER + count;
		count++;
		return name;
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

	// private void writeObject(ObjectOutputStream out) throws IOException
	// {
	// 	out.writeObject(this.forwardNeurons);
	// 	out.writeObject(this.input);
	// 	out.writeObject(this.ghostInput);
	// 	out.writeObject(this.name);
	// }
	//
	// @SuppressWarnings("unchecked")
	// private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException
	// {
	// 	this.init((ArrayList<IPropagable>)in.readObject(), (Matrix)in.readObject(), (Matrix)in.readObject(), (String)in.readObject());
	// }
}
