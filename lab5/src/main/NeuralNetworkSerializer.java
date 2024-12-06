import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class NeuralNetworkSerializer
{
	private static class Connection
	{
		private String from;
		private String to;
		private double weight;

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

		public boolean isBias() { return to == null; };
	}

	public static void saveNetwork(NeuralNetwork nn, String file) throws IOException
	{
		File outFile = new File(file);
		FileOutputStream fileOutStream = new FileOutputStream(outFile);
		ObjectOutputStream objOutStream = new ObjectOutputStream(fileOutStream);
		// this.writeExternal(objOutStream);
		writeNetwork(nn, objOutStream);
		objOutStream.flush();
		objOutStream.close();
		fileOutStream.close();
	}

	private static void writeNetwork(NeuralNetwork nn, ObjectOutputStream out) throws IOException
	{
		out.writeUTF(nn.getWeightInfo());
	}

	public static NeuralNetwork loadNetwork(String file) throws ClassNotFoundException, IOException
	{
		File readFile = new File(file);
		FileInputStream fileInStream = new FileInputStream(readFile);
		ObjectInputStream objInStream = new ObjectInputStream(fileInStream);
		NeuralNetwork network = readNetwork(objInStream);
		objInStream.close();
		fileInStream.close();
		return network;
	}

	private static NeuralNetwork readNetwork(ObjectInputStream in) throws IOException
	{
		ArrayList<Connection> connections = new ArrayList<>();
		ArrayList<Connection> biases = new ArrayList<>();
		HashMap<String, IPropagable> nodesHash = new HashMap<>();

		// read file
		String s = in.readUTF();
		String[] lines = s.split("\\r?\\n|\\r");
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
