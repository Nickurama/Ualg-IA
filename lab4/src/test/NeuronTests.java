import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

class NeuronTests
{
	// @Test
	// public void shouldThrowWhenUnmatchedInputAndWeight()
	// {
	// 	// Arrange
	// 	ArrayList<Double> inputs = new ArrayList<>();
	// 	inputs.add(1.0);
	// 	inputs.add(1.5);
	// 	inputs.add(-2.0);
	//
	// 	ArrayList<Double> weights = new ArrayList<>();
	// 	weights.add(3.7);
	// 	weights.add(2.1);
	//
	// 	double bias = 1.6;
	//
	// 	Neuron n = new Neuron(weights, bias);
	//
	// 	// Act
	// 	boolean threw = false;
	// 	try
	// 	{
	// 		n.output(inputs);
	// 	}
	// 	catch (Exception e)
	// 	{
	// 		threw = true;
	// 	}
	//
	// 	// Assert
	// 	assertTrue(threw);
	// }
	//
	// @Test
	// public void shouldMakeLogicAnd()
	// {
	// 	// Arrange
	// 	ArrayList<Double> weights = new ArrayList<>();
	// 	weights.add(1.0);
	// 	weights.add(1.0);
	// 	double bias = -1.5;
	// 	Neuron n = new Neuron(weights, bias);
	//
	// 	ArrayList<Double> inputs0 = new ArrayList<>();
	// 	inputs0.add(0.0);
	// 	inputs0.add(0.0);
	// 	ArrayList<Double> inputs1 = new ArrayList<>();
	// 	inputs1.add(0.0);
	// 	inputs1.add(1.0);
	// 	ArrayList<Double> inputs2 = new ArrayList<>();
	// 	inputs2.add(1.0);
	// 	inputs2.add(0.0);
	// 	ArrayList<Double> inputs3 = new ArrayList<>();
	// 	inputs3.add(1.0);
	// 	inputs3.add(1.0);
	//
	// 	double expected0 = 0.0;
	// 	double expected1 = 0.0;
	// 	double expected2 = 0.0;
	// 	double expected3 = 1.0;
	//
	// 	// Act
	// 	double result0 = n.binaryOutput(inputs0);
	// 	double result1 = n.binaryOutput(inputs1);
	// 	double result2 = n.binaryOutput(inputs2);
	// 	double result3 = n.binaryOutput(inputs3);
	//
	// 	// Assert
	// 	assertEquals(expected0, result0);
	// 	assertEquals(expected1, result1);
	// 	assertEquals(expected2, result2);
	// 	assertEquals(expected3, result3);
	// }
	//
	// @Test
	// public void shouldMakeLogicXor()
	// {
	// 	// Arrange
	// 	ArrayList<Double> weights0 = new ArrayList<>();
	// 	weights0.add(1.0);
	// 	weights0.add(1.0);
	// 	double bias0 = -0.5;
	// 	Neuron n0 = new Neuron(weights0, bias0);
	//
	// 	ArrayList<Double> weights1 = new ArrayList<>();
	// 	weights1.add(-1.0);
	// 	weights1.add(-1.0);
	// 	weights1.add(3.0);
	// 	double bias1 = -1.5;
	// 	Neuron n1 = new Neuron(weights1, bias1);
	//
	//
	// 	ArrayList<Double> inputs0 = new ArrayList<>();
	// 	inputs0.add(0.0);
	// 	inputs0.add(0.0);
	// 	ArrayList<Double> inputs1 = new ArrayList<>();
	// 	inputs1.add(0.0);
	// 	inputs1.add(1.0);
	// 	ArrayList<Double> inputs2 = new ArrayList<>();
	// 	inputs2.add(1.0);
	// 	inputs2.add(0.0);
	// 	ArrayList<Double> inputs3 = new ArrayList<>();
	// 	inputs3.add(1.0);
	// 	inputs3.add(1.0);
	//
	// 	inputs0.add(n0.binaryOutput(inputs0));
	// 	inputs1.add(n0.binaryOutput(inputs1));
	// 	inputs2.add(n0.binaryOutput(inputs2));
	// 	inputs3.add(n0.binaryOutput(inputs3));
	//
	//
	// 	double expected0 = 0.0;
	// 	double expected1 = 1.0;
	// 	double expected2 = 1.0;
	// 	double expected3 = 0.0;
	//
	// 	// Act
	// 	double result0 = n1.binaryOutput(inputs0);
	// 	double result1 = n1.binaryOutput(inputs1);
	// 	double result2 = n1.binaryOutput(inputs2);
	// 	double result3 = n1.binaryOutput(inputs3);
	//
	// 	// Assert
	// 	assertEquals(expected0, result0);
	// 	assertEquals(expected1, result1);
	// 	assertEquals(expected2, result2);
	// 	assertEquals(expected3, result3);
	// }
}
