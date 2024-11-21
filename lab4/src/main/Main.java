import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
	public static void main(String[] args)
	{
		trainingNetworkXOR();
		// premadeNetworks();
	}

	private static void trainingNetworkXOR()
	{
		// setup neurons
		Matrix targetOutput = new Matrix(new double[][] {{ 0, 1, 1, 0 }});
		InputNode x1 = new InputNode(new Matrix(new double[][]{{ 0, 1, 0, 1}}));
		InputNode x2 = new InputNode(new Matrix(new double[][]{{ 0, 0, 1, 1}}));

		Neuron n1 = new Neuron(0.1); // w0
		Neuron n2 = new Neuron(0.1); // w3

		// setup connections
		x1.connect(n1, 0.1); // w1
		x1.connect(n2, 0.1); // w4
		x2.connect(n1, 0.1); // w2
		x2.connect(n2, 0.1); // w6
		n1.connect(n2, 0.1); // w5

		double learningRate = 37; // 9.2 / 37

		// propagate
		for (int i = 0; i < 130; i++) // 9581 - 1.0 / 130 - 37.0
		{
			x1.resetCaches();
			x2.resetCaches();

			System.out.println("w0: " + n1.bias());
			System.out.println("w1: " + n1.weights().get(1, 0));
			System.out.println("w2: " + n1.weights().get(2, 0));
			System.out.println("w3: " + n2.bias());
			System.out.println("w4: " + n2.weights().get(1, 0));
			System.out.println("w5: " + n2.weights().get(3, 0));
			System.out.println("w6: " + n2.weights().get(2, 0));

			x1.propagate();
			x2.propagate();
			Matrix result = n2.output();
			System.out.println(result);
			System.out.println("XOR(0, 0) = " + result.get(0, 0));
			System.out.println("XOR(1, 0) = " + result.get(0, 1));
			System.out.println("XOR(0, 1) = " + result.get(0, 2));
			System.out.println("XOR(1, 1) = " + result.get(0, 3));

			n2.backpropagate(targetOutput, learningRate);

			System.out.println("ERROR: " + n2.getError(targetOutput));
			System.out.println("----------------------------------------");
		}
		x1.resetCaches();
		x2.resetCaches();
		System.out.println("w0: " + n1.bias());
		System.out.println("w1: " + n1.weights().get(1, 0));
		System.out.println("w2: " + n1.weights().get(2, 0));
		System.out.println("w3: " + n2.bias());
		System.out.println("w4: " + n2.weights().get(1, 0));
		System.out.println("w5: " + n2.weights().get(3, 0));
		System.out.println("w6: " + n2.weights().get(2, 0));
		x1.propagate();
		x2.propagate();
		Matrix result = n2.output();
		System.out.println("XOR(0, 0) = " + result.get(0, 0));
		System.out.println("XOR(1, 0) = " + result.get(0, 1));
		System.out.println("XOR(0, 1) = " + result.get(0, 2));
		System.out.println("XOR(1, 1) = " + result.get(0, 3));
		System.out.println("ERROR: " + n2.getError(targetOutput));
		System.out.println("----------------------------------------");

		Scanner sc = new Scanner(System.in);
		printIn();
		while (sc.hasNext())
		{
			x1.resetCaches();
			x2.resetCaches();
			x1.set(new Matrix(new double[][] {{ sc.nextDouble() }}));
			x2.set(new Matrix(new double[][] {{ sc.nextDouble() }}));
			x1.propagate(); // input node
			x2.propagate(); // input node

			// get result
			printOut(n2.output());
			printIn();
		}
		sc.close();
	}

	private static void premadeNetworks()
	{
		Scanner sc = new Scanner(System.in);
		printIn();
		while (sc.hasNext())
		{
			// runAND(sc);
			runXOR(sc);
			printIn();
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

	private static void printOut(Matrix out)
	{
		System.out.println(String.format("\t>> %.1f", out.parse()));
	}

	private static void printIn()
	{
		System.out.print("<< ");
	}
}
