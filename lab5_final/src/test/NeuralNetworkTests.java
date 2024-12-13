import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

class NeuralNetworkTests
{
	@Test
	public void shouldPerformIteration()
	{
		// Arrange
		int iterations = 1;
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
		network.setPrinting(false);

		x1.connect(n1, 0.1);
		x1.connect(n2, 0.1);
		x2.connect(n1, 0.1);
		x2.connect(n2, 0.1);
		n1.connect(n2, 0.1);

		double error = 0.0000000005;
		double[] expected = new double[] {
			0.5281802279,
			0.5491755457,
			0.5491755457,
			0.5699922622,
		};

		// Act
		network.train(iterations, learningRate);
		Matrix output = network.output();

		// Assert
		assertTrue(Math.abs(output.get(0, 0) - expected[0]) <= error);
		assertTrue(Math.abs(output.get(0, 1) - expected[1]) <= error);
		assertTrue(Math.abs(output.get(0, 2) - expected[2]) <= error);
		assertTrue(Math.abs(output.get(0, 3) - expected[3]) <= error);
	}
}
