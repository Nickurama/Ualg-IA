import java.util.ArrayList;

public class Neuron
{
	private ArrayList<Double> weights;
	private Double bias;

	public Neuron(ArrayList<Double> weights, Double bias)
	{
		// shallow copy
		this.weights = weights;
		this.bias = bias;
	}

	private double sigmoid(double z) { return 1 / (1 + Math.exp(-z)); }

	private double summation(ArrayList<Double> inputs)
	{
		if (inputs.size() != this.weights.size())
			throw new IllegalArgumentException("Inputs and weights do not have the same size");

		double result = bias;

		for (int i = 0; i < inputs.size(); i++)
			result += inputs.get(i) * weights.get(i);

		return result;
	}

	public double output(ArrayList<Double> inputs) { return sigmoid(summation(inputs)); }

	public double binaryOutput(ArrayList<Double> inputs) { return output(inputs) >= 0.5 ? 1 : 0; }
}
