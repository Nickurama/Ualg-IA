import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
	public static void main(String[] args)
	{
		Matrix W = new Matrix(new double[][]{
			{ 2 },
			{ 3 },
			{ 5 },
			{ 4 },
		});
		Matrix I = new Matrix(new double[][]{
			{ 1, 3, 5, 9 },
			{ 2, 4, 4, 5 },
			{ 0, 5, 0, 2 },
			{ 3, 4, 1, 1 },
		});

		Matrix A = W.transpose().dot(I);
		System.out.println(A);

		// premadeNetworks();
		// trainingNetworks();
	}

	// private static void trainingNetworks()
	// {
	// 	// setup neurons
	// 	InputNode x1 = new InputNode(0.0);
	// 	InputNode x2 = new InputNode(0.0);
	//
	// 	Neuron n1 = new Neuron(0.1); // w0
	// 	Neuron n2 = new Neuron(0.1); // w3
	//
	// 	// setup connections
	// 	x1.connect(n1, 0.1); // w1
	// 	x1.connect(n2, 0.1); // w4
	// 	x2.connect(n1, 0.1); // w2
	// 	x2.connect(n2, 0.1); // w6
	// 	n1.connect(n2, 0.1); // w5
	//
	// 	double learningRate = 1.0;
	//
	// 	// propagate
	// 	for (int i = 0; i < 1780; i++)
	// 	{
	// 		System.out.println("w0: " + n1.bias());
	// 		System.out.println("w1: " + x1.weights().get(0));
	// 		System.out.println("w2: " + x2.weights().get(0));
	// 		System.out.println("w3: " + n2.bias());
	// 		System.out.println("w4: " + x1.weights().get(1));
	// 		System.out.println("w5: " + n1.weights().get(0));
	// 		System.out.println("w6: " + x2.weights().get(1));
	//
	// 		x1.set(0.0);
	// 		x2.set(0.0);
	// 		x1.propagate();
	// 		x2.propagate();
	// 		double h0 = n2.output();
	// 		System.out.println("XOR(0, 0) = " + n2.output());
	// 		n2.backpropagate(0.0 - n2.output());
	//
	// 		x1.set(1.0);
	// 		x2.set(0.0);
	// 		x1.propagate();
	// 		x2.propagate();
	// 		double h1 = n2.output();
	// 		System.out.println("XOR(1, 0) = " + n2.output());
	// 		n2.backpropagate(1.0 - n2.output());
	//
	// 		x1.set(0.0);
	// 		x2.set(1.0);
	// 		x1.propagate();
	// 		x2.propagate();
	// 		double h2 = n2.output();
	// 		System.out.println("XOR(0, 1) = " + n2.output());
	// 		n2.backpropagate(1.0 - n2.output());
	//
	// 		x1.set(1.0);
	// 		x2.set(1.0);
	// 		x1.propagate();
	// 		x2.propagate();
	// 		double h3 = n2.output();
	// 		System.out.println("XOR(1, 1) = " + n2.output());
	// 		n2.backpropagate(0.0 - n2.output());
	//
	// 		x1.train(learningRate, 4);
	// 		x2.train(learningRate, 4);
	//
	// 		x1.resetCaches();
	// 		x2.resetCaches();
	//
	// 		double error = (0.0 - h0)*(0.0 - h0) + (1.0 - h1)*(1.0 - h1) + (1.0 - h2)*(1.0 - h2) + (0.0 - h3)*(0.0 - h3);
	// 		error *= 1.0 / 4.0;
	// 		System.out.println("ERROR: " + error);
	// 		System.out.println("----------------------------------------");
	// 	}
	//
	// 	Scanner sc = new Scanner(System.in);
	// 	printIn();
	// 	while (sc.hasNext())
	// 	{
	// 		x1.resetCaches();
	// 		x2.resetCaches();
	// 		x1.set(sc.nextDouble());
	// 		x2.set(sc.nextDouble());
	// 		x1.propagate(); // input node
	// 		x2.propagate(); // input node
	//
	// 		// get result
	// 		printOut(n2.output());
	// 		printIn();
	// 	}
	// 	sc.close();
	// }
	//
	// private static void premadeNetworks()
	// {
	// 	Scanner sc = new Scanner(System.in);
	// 	printIn();
	// 	while (sc.hasNext())
	// 	{
	// 		// runAND(sc);
	// 		runXOR(sc);
	// 		printIn();
	// 	}
	// 	sc.close();
	// }
	//
	// private static void runXOR(Scanner sc)
	// {
	// 	// setup neurons
	// 	InputNode x1 = new InputNode(sc.nextDouble());
	// 	InputNode x2 = new InputNode(sc.nextDouble());
	//
	// 	Neuron n1 = new Neuron(300.0); // w0
	// 	Neuron n2 = new Neuron(-900.0); // w3
	//
	// 	// setup connections
	// 	x1.connect(n1, -200.0); // w1
	// 	x1.connect(n2, 400.0); // w4
	// 	x2.connect(n1, -200.0); // w2
	// 	x2.connect(n2, 400.0); // w6
	// 	n1.connect(n2, 700.0); // w5
	//
	// 	// propagate
	// 	x1.propagate(); // input node
	// 	x2.propagate(); // input node
	//
	// 	// get result
	// 	printOut(n2.output());
	// }
	//
	// private static void runAND(Scanner sc)
	// {
	// 	// setup neurons
	// 	InputNode x1 = new InputNode(sc.nextDouble());
	// 	InputNode x2 = new InputNode(sc.nextDouble());
	//
	// 	Neuron n1 = new Neuron(-1.5);
	//
	// 	// setup connections
	// 	x1.connect(n1, 1.0);
	// 	x2.connect(n1, 1.0);
	//
	// 	// propagate
	// 	x1.propagate(); // input node
	// 	x2.propagate(); // input node
	//
	// 	// get result
	// 	printOut(n1.output());
	// }
	//
	// private static void printOut(double d)
	// {
	// 	System.out.println(String.format("\t>> %.1f", d));
	// }
	// 
	// private static void printIn()
	// {
	// 	System.out.print("<< ");
	// }
}
