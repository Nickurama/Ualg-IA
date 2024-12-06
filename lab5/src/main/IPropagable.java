import java.util.ArrayList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Class representative of a node that can propagate a matrix
 * of values through a network.
 * The main purpose is to be used as a node/neuron in a
 * machine learning neural network.
 */
public interface IPropagable
{
	/**
	 * @return the output of the current node
	 */
	public Matrix output();

	/**
	 * @return the ghost output of the current node
	 */
	public Matrix ghostOutput();

	/**
	 * connects two nodes with a given weight.
	 * (directional)
	 * @param that the node to connect to
	 * @param weight the weight of the connection
	 */
	public void connect(IPropagable that, double weight);

	/**
	 * connects to a node, with a random weight
	 * @param that the node to connect to
	 */
	public void connect(IPropagable that);

	/**
	 * propagates forward through the network, clearing caches.
	 */
	public void resetCaches();

	/**
	 * performs a forward propagation.
	 * (caches results for backpropagation)
	 */
	public void propagate();

	/**
	 * performs a forward propagation.
	 * !!!does not cache results for backpropagation!!!
	 */
	public void ghostpropagate();

	/**
	 * performs a backpropagation.
	 * resets caches automatically.
	 * @pre propagation of all nodes has been done
	 * @param deltas the deltas from the calling node (error term)
	 * @param learningRate the learning rate
	 * @param numOutputs number of output nodes in the graph
	 */
	public void backpropagate(Matrix deltas, double learningRate, int numOutputs);

	/**
	 * saves a connection as a backwards connection.
	 * (against the direction of the connection)
	 * @param that the node to register the connection
	 * @param connWeight the connection weight
	 */
	public void backConnect(IPropagable that, double connWeight);

	/**
	 * saves a forward connection
	 * @param that the node to connect to
	 */
	public void fwrdConnect(IPropagable that);

	/**
	 * first index (0) is the bias
	 * @return the weights of the connections of the current node
	 */
	public Matrix weights();

	/**
	 * @return the bias of the node
	 */
	public double bias();

	/**
	 * a string representation of the node.
	 * @return the node's name
	 */
	public String name();

	/**
	 * propagates forward, collecting information about the weights.
	 * @param previousInfo the previous weight information list
	 * @return the new weight information list
	 */
	public ArrayList<String> getWeightInfo(ArrayList<String> previousInfo); // propagates forward
}
