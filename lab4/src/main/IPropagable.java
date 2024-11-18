public interface IPropagable
{
	public void propagate();
	public double output();
	public void connect(IPropagable that, Double weight);

	public void backConnect(IPropagable that);
	public void fwrdConnect(IPropagable that);
	public void addInput(Double in);
}
