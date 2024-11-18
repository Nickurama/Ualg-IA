import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

class NeuronTests
{
	@Test
	public void shouldMakeLogicAnd()
	{
		// Arrange
		InputNode x1 = new InputNode(0.0);
		InputNode x2 = new InputNode(0.0);
		Neuron n1 = new Neuron(-1.5);
		
		x1.connect(n1, 1.0);
		x2.connect(n1, 1.0);

		double expected0 = 0.0;
		double expected1 = 0.0;
		double expected2 = 0.0;
		double expected3 = 1.0;

		// Act
		x1.propagate();
		x2.propagate();
		double result0 = n1.evaluateOutput();

		x1.resetCaches();
		x2.resetCaches();
		x1.set(0.0);
		x2.set(1.0);
		x1.propagate();
		x2.propagate();
		double result1 = n1.evaluateOutput();

		x1.resetCaches();
		x2.resetCaches();
		x1.set(1.0);
		x2.set(0.0);
		x1.propagate();
		x2.propagate();
		double result2 = n1.evaluateOutput();

		x1.resetCaches();
		x2.resetCaches();
		x1.set(1.0);
		x2.set(1.0);
		x1.propagate();
		x2.propagate();
		double result3 = n1.evaluateOutput();

		// Assert
		assertEquals(expected0, result0);
		assertEquals(expected1, result1);
		assertEquals(expected2, result2);
		assertEquals(expected3, result3);
	}

	@Test
	public void shouldMakeLogicXor()
	{
		// Arrange
		InputNode x1 = new InputNode(0.0);
		InputNode x2 = new InputNode(0.0);

		Neuron n1 = new Neuron(300.0); // w0
		Neuron n2 = new Neuron(-900.0); // w3

		x1.connect(n1, -200.0); // w1
		x1.connect(n2, 400.0); // w4
		x2.connect(n1, -200.0); // w2
		x2.connect(n2, 400.0); // w6
		n1.connect(n2, 700.0); // w5

		double expected0 = 0.0;
		double expected1 = 1.0;
		double expected2 = 1.0;
		double expected3 = 0.0;

		// Act
		x1.propagate();
		x2.propagate();
		double result0 = n2.evaluateOutput();

		x1.resetCaches();
		x2.resetCaches();
		x1.set(0.0);
		x2.set(1.0);
		x1.propagate();
		x2.propagate();
		double result1 = n2.evaluateOutput();

		x1.resetCaches();
		x2.resetCaches();
		x1.set(1.0);
		x2.set(0.0);
		x1.propagate();
		x2.propagate();
		double result2 = n2.evaluateOutput();

		x1.resetCaches();
		x2.resetCaches();
		x1.set(1.0);
		x2.set(1.0);
		x1.propagate();
		x2.propagate();
		double result3 = n2.evaluateOutput();

		// Assert
		assertEquals(expected0, result0);
		assertEquals(expected1, result1);
		assertEquals(expected2, result2);
		assertEquals(expected3, result3);
	}
}
