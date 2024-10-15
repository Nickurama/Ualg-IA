import java.util.*;

class BestFirst
{
	protected Queue<State> open;
	private Map<ILayout, State> openMap;
	private Map<ILayout, State> closed;
	private State current;
	private ILayout objective;

	static class State
	{
		private ILayout layout;
		private State father;
		private double cost;

		public State(ILayout layout, State father)
		{
			this.layout = layout;
			this.father = father;

			this.cost = 0.0;
			if (father != null)
				this.cost = father.cost + layout.getCost();
		}

		public String toString() { return layout.toString(); }

		public double getCost() { return cost; }

		public int hashCode() { return layout.hashCode(); }

		public boolean equals (Object other)
		{
			if (this == other) return true;
			if (other == null) return false;
			if (this.getClass() != other.getClass()) return false;

			State that = (State) other;
			return this.layout.equals(that.layout);
		}

		public ILayout layout() { return layout; }
	}

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

	private void openAdd(State state)
	{
		open.add(state);
		openMap.put(state.layout, state);
	}

	private State openPop()
	{
		State result = open.remove();
		openMap.remove(result.layout);
		return result;
	}

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
