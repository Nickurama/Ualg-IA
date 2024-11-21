import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
	public static void main(String[] args)
	{
		// trainingNetworkXOR();
		trainingNetwork_BinarySum_MultipleOutputs();
		// premadeNetworks();
	}

	private static void trainingNetwork_BinarySum_MultipleOutputs()
	{
		Neuron.setSeed(-682544829822166666l);

		int iterations = 1000;
		double maxError = 0.00001;
		double learningRate = 8.0;

		Matrix trainingSet = new Matrix(new double[][] {
			{ 0, 1, 0, 1 },
			{ 0, 0, 1, 1 },
		});
		Matrix targetOutput = new Matrix(new double[][] {
			{ 0, 1, 1, 0 },
			{ 0, 0, 0, 1 },
		});

		InputNode x1 = new InputNode(trainingSet.getRow(0));
		InputNode x2 = new InputNode(trainingSet.getRow(1));
		Neuron n1 = new Neuron();
		Neuron n2 = new Neuron();
		Neuron n3 = new Neuron();
		Neuron n4 = new Neuron();
		Neuron n5 = new Neuron();
		Neuron n6 = new Neuron();

		ArrayList<InputNode> inputNodes = new ArrayList<>();
		inputNodes.add(x1);
		inputNodes.add(x2);
		ArrayList<Neuron> outputNeurons = new ArrayList<>();
		outputNeurons.add(n5);
		outputNeurons.add(n6);

		NeuralNetwork network = new NeuralNetwork(inputNodes, outputNeurons, trainingSet, targetOutput);

		// input layer
		x1.connect(n1);
		x1.connect(n2);
		x2.connect(n1);
		x2.connect(n2);

		// hidden layer 1
		n1.connect(n3);
		n1.connect(n4);
		n2.connect(n3);
		n2.connect(n4);

		// hidden layer 2
		n3.connect(n5);
		n3.connect(n6);
		n4.connect(n5);
		n4.connect(n6);

		network.setPrettyPrint(true);
		network.setPrintWhileTraining(false);
		network.setPrintWeights(true);

		// network.train(iterations, learningRate);
		network.train(maxError, learningRate);

		network.iterativePropagation();
	}

	private static void trainingNetworkXOR()
	{
		Neuron.setSeed(7723607231041998711L);

		int iterations = 130;
		double maxError = 0.00001;
		double learningRate = 37.0;

		Matrix trainingSet = new Matrix(new double[][] {
			{ 0, 1, 0, 1 },
			{ 0, 0, 1, 1 },
		});
		Matrix targetOutput = new Matrix(new double[][] {
			{ 0, 1, 1, 0 },
		});

		InputNode x1 = new InputNode(trainingSet.getRow(0));
		InputNode x2 = new InputNode(trainingSet.getRow(1));
		Neuron n1 = new Neuron();
		Neuron n2 = new Neuron();

		ArrayList<InputNode> inputNodes = new ArrayList<>();
		inputNodes.add(x1);
		inputNodes.add(x2);
		ArrayList<Neuron> outputNeurons = new ArrayList<>();
		outputNeurons.add(n2);

		NeuralNetwork network = new NeuralNetwork(inputNodes, outputNeurons, trainingSet, targetOutput);

		// setup connections
		x1.connect(n1);
		x1.connect(n2);
		x2.connect(n1);
		x2.connect(n2);
		n1.connect(n2);

		network.setPrettyPrint(true);
		network.setPrintWhileTraining(false);
		network.setPrintWeights(true);

		// network.train(iterations, learningRate);
		network.train(maxError, learningRate);

		network.iterativePropagation();
	}

	private static void premadeNetworks()
	{
		Scanner sc = new Scanner(System.in);
		while (sc.hasNext())
		{
			// runAND(sc);
			runXOR(sc);
		}
		sc.close();
	}

	private static void runXOR(Scanner sc)
	{
		// setup neurons
		InputNode x1 = new InputNode(new Matrix(new double[][] {{ sc.nextDouble() }}));
		InputNode x2 = new InputNode(new Matrix(new double[][] {{ sc.nextDouble() }}));

		Neuron n1 = new Neuron(300.0); // w0
		Neuron n2 = new Neuron(-900.0); // w3

		// setup connections
		x1.connect(n1, -200.0); // w1
		x1.connect(n2, 400.0); // w4
		x2.connect(n1, -200.0); // w2
		x2.connect(n2, 400.0); // w6
		n1.connect(n2, 700.0); // w5

		// propagate
		x1.propagate(); // input node
		x2.propagate(); // input node

		// get result
		System.out.println((n2.output()));
	}

	private static void runAND(Scanner sc)
	{
		// setup neurons
		InputNode x1 = new InputNode(new Matrix(new double[][] {{ sc.nextDouble() }}));
		InputNode x2 = new InputNode(new Matrix(new double[][] {{ sc.nextDouble() }}));

		Neuron n1 = new Neuron(-1.5);

		// setup connections
		x1.connect(n1, 1.0);
		x2.connect(n1, 1.0);

		// propagate
		x1.propagate(); // input node
		x2.propagate(); // input node

		// get result
		System.out.println((n1.output()));
	}

}
