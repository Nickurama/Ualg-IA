import java.util.Scanner;

public class Main
{
	public static void main(String[] args)
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
		InputNode x1 = new InputNode(sc.nextDouble());
		InputNode x2 = new InputNode(sc.nextDouble());

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
		printOut(n2.output());
	}

	private static void runAND(Scanner sc)
	{
		// setup neurons
		InputNode x1 = new InputNode(sc.nextDouble());
		InputNode x2 = new InputNode(sc.nextDouble());

		Neuron n1 = new Neuron(-1.5);

		// setup connections
		x1.connect(n1, 1.0);
		x2.connect(n1, 1.0);

		// propagate
		x1.propagate(); // input node
		x2.propagate(); // input node

		// get result
		printOut(n1.output());
	}

	private static void printOut(double d)
	{
		System.out.println(String.format("\t>> %.1f", d));
	}
	
	private static void printIn()
	{
		System.out.print("<< ");
	}
}
