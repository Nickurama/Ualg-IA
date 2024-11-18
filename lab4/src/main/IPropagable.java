import java.util.ArrayList;

public interface IPropagable
{
	public double output();
	public double errorTerm();
	public void connect(IPropagable that, Double weight);
	public void resetCaches(); // propagates forward throughout network; must be called after training for further usage
	public void propagate(); // propagates forward throughout network
	public void backpropagate(double errorDifference); // errorDifference = (t - y); propagates back throughout network; resets caches automatically except for the ones for training
	public void train(double learningRate, double numTrainingSets); // propagates forward throughout network
	public void train(double quotient); // propagates forward throughout network

	public void backConnect(IPropagable that);
	public void fwrdConnect(IPropagable that);
	public void addInput(Double in);
	public ArrayList<Double> weights();
	public double bias();
}
