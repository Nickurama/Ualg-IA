import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Serializer for NeuralNetworks
 */
public class NeuralNetworkSerializer
{
	/**
	 * Represents a connection between two neuron
	 */
	private static class Connection
	{
		private String from;
		private String to;
		private double weight;

		/**
		 * Instantiates a connection from a line of a serialized network
		 * @param str the line of a serialized network
		 */
		public Connection(String str)
		{

			String[] tokens = str.split(Neuron.WEIGHT_IDENTIFIER);
			if (str.contains(Neuron.CONNECTION_IDENTIFIER))
			{
				String[] connectionTokens = tokens[0].split(Neuron.CONNECTION_IDENTIFIER);
				from = connectionTokens[0];
				to = connectionTokens[1];
			}
			else
			{
				from = tokens[0];
				to = null;
			}
			weight = Double.parseDouble(tokens[1]);
		}

		/**
		 * @return true if the connection represents a bias (connects with "itself")
		 */
		public boolean isBias() { return to == null; };
	}

	/**
	 * saves the network to a file.
	 * @param nn the network to save
	 * @param file the file path to save the network to
	 * @throws IOException if an IO error occurs
	 */
	public static void saveNetwork(NeuralNetwork nn, String file) throws IOException
	{
		File outFile = new File(file);
		FileWriter fWriter = new FileWriter(outFile);
		BufferedWriter writer = new BufferedWriter(fWriter);
		writeNetwork(nn, writer);
		writer.flush();
		writer.close();
		fWriter.close();
	}

	/**
	 * writes a network to a file.
	 * @param nn the neural network to write
	 * @param out the stream to write to
	 * @throws IOException if an IO error ocurrs
	 */
	private static void writeNetwork(NeuralNetwork nn, BufferedWriter out) throws IOException
	{
		String s = nn.getWeightInfo();
		out.write(s);
	}

	/**
	 * loads a network from a file
	 * @param file the file path to load the network from
	 * @return the network serialized in the file
	 * @throws IOException if an IO error occurs
	 */
	public static NeuralNetwork loadNetwork(String file) throws IOException
	{
		File readFile = new File(file);
		// FileInputStream fileInStream = new FileInputStream(readFile);
		// ObjectInputStream objInStream = new ObjectInputStream(fileInStream);
		FileReader fReader = new FileReader(readFile);
		BufferedReader reader = new BufferedReader(fReader);
		NeuralNetwork network = readNetwork(reader);
		// NeuralNetwork network = readNetwork(objInStream);
		reader.close();
		fReader.close();
		// objInStream.close();
		// fileInStream.close();
		return network;
	}

	/**
	 * reads a network from a stream
	 * @param in the stream to read the network from
	 * @return the network read
	 * @throws IOException if an IO error occurs
	 */
	private static NeuralNetwork readNetwork(BufferedReader in) throws IOException
	{
		ArrayList<Connection> connections = new ArrayList<>();
		ArrayList<Connection> biases = new ArrayList<>();
		HashMap<String, IPropagable> nodesHash = new HashMap<>();

		// read file
		// String s = in.readUTF();
		// String[] lines = s.split("\\r?\\n|\\r");
		ArrayList<String> lines = new ArrayList<String>();
		while (in.ready())
			lines.add(in.readLine());

		for (String line : lines)
		{
			Connection conn = new Connection(line);
			if (conn.isBias())
				biases.add(conn);
			else
				connections.add(conn);
		}

		// add neurons
		ArrayList<Neuron> neurons = new ArrayList<>();
		for (Connection c : biases) // can only be neurons
		{
			Neuron n = new Neuron(c.weight, c.from);
			neurons.add(n);
			nodesHash.put(c.from, n);
		}

		// add inputNodes and connections
		ArrayList<InputNode> inputNodes = new ArrayList<>();
		for (Connection c : connections)
		{
			IPropagable n;
			if (!nodesHash.containsKey(c.from)) // must be InputNode
			{
				InputNode iNode = new InputNode(c.from);
				inputNodes.add(iNode);
				n = iNode;
				nodesHash.put(c.from, iNode);
			}
			else
			{
				n = nodesHash.get(c.from);
			}
			n.connect(nodesHash.get(c.to), c.weight);
		}

		inputNodes.sort((x, y) -> {
			return Integer.parseInt(x.name().substring(InputNode.IDENTIFIER.length())) - Integer.parseInt(y.name().substring(InputNode.IDENTIFIER.length()));
		});

		// identify outputNeurons (after connecting everything)
		ArrayList<Neuron> outputNeurons = new ArrayList<>();
		for (Neuron n : neurons)
			if (n.isOutputNeuron())
				outputNeurons.add(n);

		outputNeurons.sort((x, y) -> {
			return Integer.parseInt(x.name().substring(Neuron.IDENTIFIER.length())) - Integer.parseInt(y.name().substring(Neuron.IDENTIFIER.length()));
		});

		return new NeuralNetwork(inputNodes, outputNeurons);
	}
}
