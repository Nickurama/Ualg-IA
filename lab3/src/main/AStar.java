import java.util.*;

class AStar
{
	protected Queue<State> open;
	private Map<ILayout, State> openMap;
	private Map<ILayout, State> closed;
	private State current;
	private ILayout goal;

	static class State
	{
		private ILayout layout;
		private State father;
		private double g;
		private double h;
		private double fitness;

		public State(ILayout layout, State father, ILayout goal)
		{
			this.layout = layout;
			this.father = father;

			this.g = 0.0;
			this.h = layout.heuristic(goal);
			if (father != null)
				this.g = father.g + layout.getCost();
			this.fitness = this.g + this.h;

			// System.out.println(this);
			// System.out.println("g: " + g);
			// System.out.println("h: " + h);
			// System.out.println("f: " + fitness);
			// System.out.println("===========================");
		}

		public String toString() { return layout.toString(); }

		public int hashCode() { return layout.hashCode(); }

		public boolean equals (Object other)
		{
			if (this == other) return true;
			if (other == null) return false;
			if (this.getClass() != other.getClass()) return false;

			State that = (State) other;
			return this.layout.equals(that.layout);
		}

		public double g() { return g; }
		public double h() { return h; }
		public double fitness() { return fitness; }
		
		public void update(State s)
		{
			this.g = s.g;
			this.h = s.h;
			this.fitness = s.fitness;
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
				State nextState = new State(child, state, goal);
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
		this.goal = goal;
		open = new PriorityQueue<State>(10, (s1, s2) -> (int) Math.signum(s1.fitness - s2.fitness));
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

				if (openState != null && sucessor.fitness < openState.fitness)
					openState.update(sucessor);

				if (closedState != null && sucessor.fitness < closedState.fitness)
					closedState.update(sucessor);
			}
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
