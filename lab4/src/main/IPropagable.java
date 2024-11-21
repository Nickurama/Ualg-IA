import java.util.ArrayList;

public interface IPropagable
{
	public Matrix output();
	public void connect(IPropagable that, double weight);
	public void connect(IPropagable that); // rng weight
	public void resetCaches(); // propagates forward throughout network
	public void propagate(); // propagates forward throughout network
	public void backpropagate(Matrix deltas, double learningRate, int numOutputs); // propagates back throughout network; resets caches automatically
	public void backConnect(IPropagable that, double connWeight);
	public void fwrdConnect(IPropagable that);
	public Matrix weights();
	public double bias();

	public String name();
	public ArrayList<String> getWeightInfo(ArrayList<String> previousInfo); // propagates forward
}
