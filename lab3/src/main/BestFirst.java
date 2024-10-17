import java.util.*;

/**
 * Represents an uniform cost search of ILayout states.
 */
class BestFirst
{
	protected Queue<State> open;
	private Map<ILayout, State> openMap;
	private Map<ILayout, State> closed;
	private State current;
	private ILayout objective;

	/**
	 * Represents a state in the uniform cost search
	 */
	static class State
	{
		private ILayout layout;
		private State father;
		private double cost;

		/**
		 * State constructor
		 * @param layout the layout of the state
		 * @param father the father of the state being constructed (can be null)
		 */
		public State(ILayout layout, State father)
		{
			this.layout = layout;
			this.father = father;

			this.cost = 0.0;
			if (father != null)
				this.cost = father.cost + layout.getCost();
		}

		@Override
		public String toString() { return layout.toString(); }

		/**
		 * @return the cost from the start to this node
		 */
		public double getCost() { return cost; }

		@Override
		public int hashCode() { return layout.hashCode(); }

		@Override
		public boolean equals (Object other)
		{
			if (this == other) return true;
			if (other == null) return false;
			if (this.getClass() != other.getClass()) return false;

			State that = (State) other;
			return this.layout.equals(that.layout);
		}

		/**
		 * @return the layout associated with this state
		 */
		public ILayout layout() { return layout; }
	}

	/**
	 * Generates the sucessors of the provided state
	 * @param state the state to generate the sucessors of
	 * @return the sucessors of the state
	 */
	final private List<State> sucessors(State state)
	{
		List<State> sucessors = new ArrayList<>(4);
		List<ILayout> children = state.layout.children();

		for (ILayout child : children)
		{
			if (state.father == null || !child.equals(state.father.layout))
			{
				State nextState = new State(child, state);
				sucessors.add(nextState);
			}
		}
		return sucessors;
	}

	/**
	 * Adds a state to "open"
	 * @param state the state to add to "open"
	 */
	private void openAdd(State state)
	{
		open.add(state);
		openMap.put(state.layout, state);
	}

	/**
	 * Pops the next state from "open"
	 * @return the next state from "open"
	 */
	private State openPop()
	{
		State result = open.remove();
		openMap.remove(result.layout);
		return result;
	}

	/**
	 * Generates the shortest path from start to goal
	 * @param start the starting layout
	 * @param goal the goal layout
	 * @return the steps from start to goal
	 */
	final public Iterator<State> solve(ILayout start, ILayout goal)
	{
		StateSpaceStats.logGenerate(); // initial counts as generated
		objective = goal;
		open = new PriorityQueue<State>(10, (s1, s2) -> (int) Math.signum(s1.getCost() - s2.getCost()));
		closed = new HashMap<ILayout, State>();
		openMap = new HashMap<ILayout, State>();
		openAdd(new State(start, null));
		List<State> sucessors;

		while (!open.isEmpty())
		{
			StateSpaceStats.logExpand();
			current = openPop();
			if (current.layout.equals(goal))
				return getCurrentPathIterator();
			closed.put(current.layout, current);

			sucessors = sucessors(current);
			StateSpaceStats.logGenerate(sucessors.size());
			for (State sucessor : sucessors)
				if (!closed.containsKey(sucessor.layout) && !openMap.containsKey(sucessor.layout))
					openAdd(sucessor);
		}
		return null;
	}

	/**
	 * @return the path from start to the current node
	 */
	private Iterator<State> getCurrentPathIterator()
	{
		List<State> solution = new LinkedList<State>();
		State tmp = current;
		StateSpaceStats.logLengthen(); // first counts as length
		solution.add(tmp);
		while (tmp.father != null)
		{
			StateSpaceStats.logLengthen();
			solution.add(tmp.father);
			tmp = tmp.father;
		}
		return solution.reversed().iterator();
	}
}
