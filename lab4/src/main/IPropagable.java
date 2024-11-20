import java.util.ArrayList;

public interface IPropagable
{
	public Matrix output();
	// FIXME
	// public double errorTerm();
	public void connect(IPropagable that, double weight);
	public void resetCaches(); // propagates forward throughout network; must be called after training for further usage
	public void propagate(); // propagates forward throughout network
	public void backpropagate(Matrix deltas, double learningRate); // errorDifference = (t - y); propagates back throughout network; resets caches automatically except for the ones for training
	// TODO
	// public void train(double learningRate, double numTrainingSets); // propagates forward throughout network
	// TODO
	// public void train(double quotient); // propagates forward throughout network

	public void backConnect(IPropagable that, double connWeight);
	public void fwrdConnect(IPropagable that);
	// public void addInput(Double in);
	// public void addInput(Matrix in);
	public Matrix weights();
	public double bias();
}
