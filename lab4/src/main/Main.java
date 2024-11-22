import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
	public static void main(String[] args)
	{
		playground();
		// trainingNetworkXOR();
		// trainingNetworkXORRand();
		// trainingNetwork_BinarySum_MultipleOutputs();
		// premadeNetworks();
	}

	private static void playground()
	{
		Neuron.setSeed(-682544829822166666l);

		int iterations = 100000;
		double maxError = 0.00001;
		double learningRate = 0.9;

		Matrix trainingSet = new Matrix(new double[][] {
			{ 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1 },

			{ 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1 },
			{ 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1 },
		});
		Matrix targetOutput = new Matrix(new double[][] {
			{ 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0 },
			{ 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 1 },
			{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 1 },
		});

		InputNode x1 = new InputNode(trainingSet.getRow(0));
		InputNode x2 = new InputNode(trainingSet.getRow(1));
		InputNode x3 = new InputNode(trainingSet.getRow(2));
		InputNode x4 = new InputNode(trainingSet.getRow(3));

		// hidden layer 1
		Neuron n1 = new Neuron();
		Neuron n2 = new Neuron();
		Neuron n3 = new Neuron();
		Neuron n4 = new Neuron();

		// hidden layer 2
		Neuron n5 = new Neuron();
		Neuron n6 = new Neuron();
		Neuron n7 = new Neuron();
		Neuron n8 = new Neuron();

		// hidden layer 3
		Neuron n9 = new Neuron();
		Neuron n10 = new Neuron();
		Neuron n11 = new Neuron();
		Neuron n12 = new Neuron();

		// output layer
		Neuron n13 = new Neuron();
		Neuron n14 = new Neuron();
		Neuron n15 = new Neuron();

		ArrayList<InputNode> inputNodes = new ArrayList<>();
		inputNodes.add(x1);
		inputNodes.add(x2);
		inputNodes.add(x3);
		inputNodes.add(x4);
		ArrayList<Neuron> outputNeurons = new ArrayList<>();
		outputNeurons.add(n13);
		outputNeurons.add(n14);
		outputNeurons.add(n15);

		NeuralNetwork network = new NeuralNetwork(inputNodes, outputNeurons, trainingSet, targetOutput);

		// input layer
		x1.connect(n1);
		x1.connect(n2);
		x1.connect(n3);
		x1.connect(n4);
		x2.connect(n1);
		x2.connect(n2);
		x2.connect(n3);
		x2.connect(n4);
		x3.connect(n1);
		x3.connect(n2);
		x3.connect(n3);
		x3.connect(n4);
		x4.connect(n1);
		x4.connect(n2);
		x4.connect(n3);
		x4.connect(n4);

		// hidden layer 1
		n1.connect(n5);
		n1.connect(n6);
		n1.connect(n7);
		n1.connect(n8);
		n2.connect(n5);
		n2.connect(n6);
		n2.connect(n7);
		n2.connect(n8);
		n3.connect(n5);
		n3.connect(n6);
		n3.connect(n7);
		n3.connect(n8);
		n4.connect(n5);
		n4.connect(n6);
		n4.connect(n7);
		n4.connect(n8);

		// hidden layer 2
		n5.connect(n9);
		n5.connect(n10);
		n5.connect(n11);
		n5.connect(n12);
		n6.connect(n9);
		n6.connect(n10);
		n6.connect(n11);
		n6.connect(n12);
		n7.connect(n9);
		n7.connect(n10);
		n7.connect(n11);
		n7.connect(n12);
		n8.connect(n9);
		n8.connect(n10);
		n8.connect(n11);
		n8.connect(n12);

		// hidden layer 3
		n9.connect(n13);
		n9.connect(n14);
		n9.connect(n15);
		n10.connect(n13);
		n10.connect(n14);
		n10.connect(n15);
		n11.connect(n13);
		n11.connect(n14);
		n11.connect(n15);
		n12.connect(n13);
		n12.connect(n14);
		n12.connect(n15);

		network.setPrettyPrint(true);
		network.setPrintWhileTraining(false);
		network.setPrintWeights(true);

		network.train(iterations, learningRate);
		// network.train(maxError, learningRate);

		network.iterativePropagation();
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

	private static void trainingNetworkXORRand()
	{
		// Neuron.setSeed(7723607231041998711L);
		// Neuron.setSeed(-2967061369577244698L);
		// Neuron.setSeed(5356092256304775059); // the 37.0 learning rate ankle breaker

		// int iterations = 2;
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

		// System.out.println(Neuron.seed());

		// network.iterativePropagation();
	}

	private static void trainingNetworkXOR()
	{
		Neuron.setSeed(7723607231041998711L);

		int iterations = 2;
		double maxError = 0.00001;
		// double learningRate = 37.0;
		double learningRate = 1.0;

		Matrix trainingSet = new Matrix(new double[][] {
			{ 0, 1, 0, 1 },
			{ 0, 0, 1, 1 },
		});
		Matrix targetOutput = new Matrix(new double[][] {
			{ 0, 1, 1, 0 },
		});

		InputNode x1 = new InputNode(trainingSet.getRow(0));
		InputNode x2 = new InputNode(trainingSet.getRow(1));
		Neuron n1 = new Neuron(0.1);
		Neuron n2 = new Neuron(0.1);

		ArrayList<InputNode> inputNodes = new ArrayList<>();
		inputNodes.add(x1);
		inputNodes.add(x2);
		ArrayList<Neuron> outputNeurons = new ArrayList<>();
		outputNeurons.add(n2);

		NeuralNetwork network = new NeuralNetwork(inputNodes, outputNeurons, trainingSet, targetOutput);

		// setup connections
		x1.connect(n1, 0.1);
		x1.connect(n2, 0.1);
		x2.connect(n1, 0.1);
		x2.connect(n2, 0.1);
		n1.connect(n2, 0.1);

		network.setPrettyPrint(true);
		network.setPrintWhileTraining(true);
		network.setPrintWeights(true);

		network.train(iterations, learningRate);
		// network.train(maxError, learningRate);

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
