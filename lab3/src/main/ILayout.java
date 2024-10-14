import java.util.List;

interface ILayout
{
	/**
	 * @return the children of the receiver.
	*/
	List<ILayout> children();

	/**
	 * @return true if the receiver equals the argument. return false otherwise.
	*/
	boolean isGoal(ILayout layout);

	/**
	 * @return the cost from the receiver to a successor
	*/
	double getCost();

	double heuristic(ILayout goal);
}
