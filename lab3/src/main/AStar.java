import java.util.*;

/**
 * Represents the A* heuristic state space search
 */
class AStar
{
	protected Queue<State> open;
	private Map<ILayout, State> openMap;
	private Map<ILayout, State> closed;
	private State current;
	private ILayout goal;

	/**
	 * Represents a state in heuristic search
	 */
	static class State
	{
		private ILayout layout;
		private State father;
		private double g;
		private double h;
		private double f;

		/**
		 * State constructor
		 * @param layout the layout of the state
		 * @param father the father of the state being constructed (can be null)
		 * @param goal the goal layout
		 */
		public State(ILayout layout, State father, ILayout goal)
		{
			this.layout = layout;
			this.father = father;

			this.g = 0.0;
			this.h = layout.heuristic(goal);
			if (father != null)
				this.g = father.g + layout.getCost();
			this.f = this.g + this.h;
		}

		@Override
		public String toString() { return layout.toString(); }

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
		 * @return g - the estimated cost from start to this state
		 */
		public double g() { return g; }
		/**
		 * @return h - the estimated cost from this state to the goal
		 */
		public double h() { return h; }
		/**
		 * @return f - the estimated cost from start to goal
		 */
		public double f() { return f; }
		
		/**
		 * updates the current state with new information
		 * @param s the equivalent state that holds the current information
		 */
		public void update(State s)
		{
			this.g = s.g;
			this.h = s.h;
			this.f = s.f;
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
				State nextState = new State(child, state, goal);
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
		this.goal = goal;
		open = new PriorityQueue<State>(10, (s1, s2) -> (int) Math.signum(s1.f - s2.f));
		closed = new HashMap<ILayout, State>();
		openMap = new HashMap<ILayout, State>();
		openAdd(new State(start, null, goal));
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
			{
				State openState = openMap.get(sucessor.layout);
				State closedState = closed.get(sucessor.layout);

				if (closedState == null && openState == null)
					openAdd(sucessor);

				if (openState != null && sucessor.f < openState.f)
					openState.update(sucessor);

				if (closedState != null && sucessor.f < closedState.f)
					closedState.update(sucessor);
			}
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
