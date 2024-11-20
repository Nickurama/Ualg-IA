import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

class NeuronTests
{
	@Test
	public void shouldMakeLogicAnd()
	{
		// Arrange
		InputNode x1 = new InputNode(new Matrix(new double[][]{{ 0, 1, 0, 1 }}));
		InputNode x2 = new InputNode(new Matrix(new double[][]{{ 0, 0, 1, 1 }}));
		Neuron n1 = new Neuron(-1.5);
		
		x1.connect(n1, 1.0);
		x2.connect(n1, 1.0);

		double error = 0.0000000005;
		double[] expected = new double[] {
			0.1824255238,
			0.3775406688,
			0.3775406688,
			0.6224593312,
		};

		// Act
		x1.propagate();
		x2.propagate();
		Matrix output = n1.output();

		// Assert
		assertTrue(Math.abs(output.get(0, 0) - expected[0]) <= error);
		assertTrue(Math.abs(output.get(0, 1) - expected[1]) <= error);
		assertTrue(Math.abs(output.get(0, 2) - expected[2]) <= error);
		assertTrue(Math.abs(output.get(0, 3) - expected[3]) <= error);
	}

	@Test
	public void shouldMakeLogicXor()
	{
		// Arrange
		InputNode x1 = new InputNode(new Matrix(new double[][]{{ 0, 1, 0, 1 }}));
		InputNode x2 = new InputNode(new Matrix(new double[][]{{ 0, 0, 1, 1 }}));
		Neuron n1 = new Neuron(300); // w0
		Neuron n2 = new Neuron(-900); // w3

		x1.connect(n1, -200.0); // w1
		x1.connect(n2, 400.0); // w4
		x2.connect(n1, -200.0); // w2
		x2.connect(n2, 400.0); // w6
		n1.connect(n2, 700.0); // w5
		
		double error = 0.0000000005;
		double[] expected = new double[] {
			0.0,
			1.0,
			1.0,
			0.0,
		};

		// Act
		x1.propagate();
		x2.propagate();
		Matrix output = n2.output();

		// Assert
		assertTrue(Math.abs(output.get(0, 0) - expected[0]) <= error);
		assertTrue(Math.abs(output.get(0, 1) - expected[1]) <= error);
		assertTrue(Math.abs(output.get(0, 2) - expected[2]) <= error);
		assertTrue(Math.abs(output.get(0, 3) - expected[3]) <= error);
	}
}
