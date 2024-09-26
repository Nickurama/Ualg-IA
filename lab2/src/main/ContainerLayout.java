import java.util.*;

class ContainerLayout implements ILayout
{
	private final int BUCKET_SIZE = 26;

	private boolean hasCost;
	private double cost;
	private int[] containersCost; // -1 when doesn't exist
	private int[] containerOnTopIndex; // -1 when container is on top
	private int[] containerOnBottomIndex; // -1 when container is on bottom
	private List<Integer> topmostContainersIndex;

	public ContainerLayout(String str) throws IllegalArgumentException
	{
		if (str.isEmpty())
			throw new IllegalArgumentException("String cannot be empty.");

		this.cost = 0.0;
		
		this.containersCost = new int[BUCKET_SIZE];
		for (int i = 0; i < BUCKET_SIZE; i++)
			containersCost[i] = -1;
		this.containerOnTopIndex = new int[BUCKET_SIZE];
		for (int i = 0; i < BUCKET_SIZE; i++)
			containerOnTopIndex[i] = -1;
		this.containerOnBottomIndex = new int[BUCKET_SIZE];
		for (int i = 0; i < BUCKET_SIZE; i++)
			containerOnBottomIndex[i] = -1;

		this.topmostContainersIndex = new LinkedList<Integer>(); // lazy removal, may not actually be a top
		this.hasCost = shouldHaveCost(str);

		populateFromString(str);
	}

	private ContainerLayout(ContainerLayout other, double cost)
	{
		this.cost = cost;

		this.containersCost = new int[BUCKET_SIZE];
		System.arraycopy(other.containersCost, 0, this.containersCost, 0, BUCKET_SIZE);
		this.containerOnTopIndex = new int[BUCKET_SIZE];
		System.arraycopy(other.containerOnTopIndex, 0, this.containerOnTopIndex, 0, BUCKET_SIZE);
		this.containerOnBottomIndex = new int[BUCKET_SIZE];
		System.arraycopy(other.containerOnBottomIndex, 0, this.containerOnBottomIndex, 0, BUCKET_SIZE);

		this.topmostContainersIndex = new LinkedList<Integer>();
		for (int topmost : other.topmostContainersIndex)
			if (other.isOnTop(topmost))
				this.topmostContainersIndex.add(topmost);
		this.hasCost = other.hasCost;
	}

	private boolean shouldHaveCost(String str)
	{
		if (str.length() == 1)
			return false;
		else if (isNumerical(str.charAt(1)))
			return true;
		return false;
	}

	private void populateFromString(String str) throws IllegalArgumentException
	{
		String[] tokens = str.split(" ");
		for (String token : tokens)
			populateContainersFromString(token);
	}

	private void populateContainersFromString(String str) throws IllegalArgumentException
	{
		if (this.hasCost)
			populateContainersFromStringWithCost(str);
		else
			populateContainersFromStringWithoutCost(str);
	}

	private void populateContainersFromStringWithCost(String str) throws IllegalArgumentException
	{
		addNextContainer(str, 0, -1);
	}

	// assumes that string has cost
	// i has to be a valid index at str
	private void addNextContainer(String str, int index, int previous) throws IllegalArgumentException
	{
		if (index >= str.length())
			return;

		validateContainer(str, index);
		int key = getKey(str.charAt(index));

		int cost = 0;
		index++;
		int next = -1;
		for (; index < str.length(); index++)
		{
			if (!isNumerical(str.charAt(index)))
			{
				if (!isAlphabetical(str.charAt(index)))
					throw new IllegalArgumentException("Invalid container stack.");
				next = (char)getKey(str.charAt(index));
				break;
			}

			cost *= 10;
			cost += str.charAt(index) - '0';
		}

		int nextPrevious = addContainer(key, cost, next, previous);
		addNextContainer(str, index, nextPrevious);
	}

	private void validateContainer(String str, int index) throws IllegalArgumentException
	{
		if (!isAlphabetical(str.charAt(index)))
			throw new IllegalArgumentException("Expected alphabetic character.");
		if ((str.length() - index) < 2 || !isNumerical(str.charAt(index + 1)))
			throw new IllegalArgumentException("Expected numeric character.");
	}

	// returns the next container's "bottom" container
	private int addContainer(int key, int cost, int next, int previous) throws IllegalArgumentException
	{
		if (this.contains(key))
			throw new IllegalArgumentException("Duplicate container.");

		if (previous >= 0)
			containerOnBottomIndex[key] = previous;

		int nextPrevious = -1;
		if (next < 0)
		{
			this.topmostContainersIndex.add((int)key);
		}
		else
		{
			nextPrevious = key;
			this.containerOnTopIndex[key] = next;
		}
		this.containersCost[key] = cost;
		return nextPrevious;
	}

	private void populateContainersFromStringWithoutCost(String str) throws IllegalArgumentException
	{
		for (int i = 0; i < str.length(); i++)
		{
			if (!isAlphabetical(str.charAt(i)))
				throw new IllegalArgumentException("Expected alphabetic character.");

			int key = getKey(str.charAt(i));
			if (i + 1 < str.length()) // no need to check for alphabetical on next, it will break on the following iteration
				this.containerOnTopIndex[key] = getKey(str.charAt(i + 1));
			else
				this.topmostContainersIndex.add(key);
			if (i - 1 >= 0)
				this.containerOnBottomIndex[key] = getKey(str.charAt(i - 1));
			if (this.contains(key))
				throw new IllegalArgumentException("Duplicate container.");
			this.containersCost[key] = 0;
		}
	}

	private boolean contains(char c) { return contains(getKey(c)); }
	private boolean contains(int i)
	{
		return containersCost[i] >= 0;
	}

	private boolean hasContainerOnTop(char c) { return hasContainerOnTop(getKey(c)); }
	private boolean hasContainerOnTop(int i)
	{
		return containerOnTopIndex[i] >= 0;
	}

	private boolean hasContainerOnBottom(char c) { return hasContainerOnBottom(getKey(c)); }
	private boolean hasContainerOnBottom(int i)
	{
		return containerOnBottomIndex[i] >= 0;
	}

	private boolean isOnBottom(char c) { return isOnBottom(getKey(c)); }
	private boolean isOnBottom(int i)
	{
		return !hasContainerOnBottom(i);
	}

	private boolean isOnTop(char c) { return isOnTop(getKey(c)); }
	private boolean isOnTop(int i)
	{
		return !hasContainerOnTop(i);
	}

	private int getContainerOnTopIndex(char c) { return getContainerOnTopIndex(getKey(c)); }
	private int getContainerOnTopIndex(int i)
	{
		return containerOnTopIndex[i];
	}

	private int getContainerOnBottomIndex(char c) { return getContainerOnBottomIndex(getKey(c)); }
	private int getContainerOnBottomIndex(int i)
	{
		return containerOnBottomIndex[i];
	}

	static private int getKey(char c)
	{
		return c - 'A';
	}

	static private char getCharFromKey(int i)
	{
		return (char)(i + 'A');
	}

	static private boolean isAlphabetical(char c)
	{
		return c >= 'A' && c <= 'Z';
	}

	static private boolean isNumerical(char c)
	{
		return c >= '0' && c <= '9';
	}

	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		// essentially a DFS
		for (int i = 0; i < BUCKET_SIZE; i++)
		{
			if (isOnBottom(i) && this.contains(i))
			{
				builder.append("[");
				builder.append(getCharFromKey(i));
				int next = containerOnTopIndex[i];
				while (next >= 0)
				{
					builder.append(", ");
					builder.append(getCharFromKey(next));
					next = containerOnTopIndex[next];
				}
				builder.append("]\n");
			}
		}

		return builder.toString();
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == this) return true;
		if (other == null) return false;
		if (!ContainerLayout.class.isInstance(other)) return false;
		ContainerLayout that = (ContainerLayout) other;

		for (int i = 0; i < BUCKET_SIZE; i++)
			if (!equalsIndex(that, i))
				return false;
		return true;
	}

	private boolean equalsIndex(ContainerLayout that, int i)
	{
		if (this.contains(i) && that.contains(i) &&
			this.getContainerOnTopIndex(i) == that.getContainerOnTopIndex(i) &&
			this.getContainerOnBottomIndex(i) == that.getContainerOnBottomIndex(i))
			{
				return true;
			}
		if (!this.contains(i) && !that.contains(i))
			return true;
		return false;
	}

	@Override
	public int hashCode()
	{
		int hash = 0;
		for (int i = 0; i < BUCKET_SIZE; i++)
			if (this.contains(i))
				hash = hash * 97 + i + 1;
		return hash;
	}

	@Override
	public List<ILayout> children()
	{
		if (!this.hasCost)
			throw new IllegalStateException("This layout doesn't support cost.");

		List<ILayout> children = new ArrayList<ILayout>();

		for (int topmost : topmostContainersIndex)
		{
			if (!isOnTop(topmost))
				continue;

			if (!isOnBottom(topmost))
				children.add(moveContainerGround(topmost));
			for (int innerTopmost : topmostContainersIndex)
			{
				if (!isOnTop(innerTopmost))
					continue;
				if (topmost == innerTopmost)
					continue;

				children.add(moveContainer(topmost, innerTopmost));
			}
		}

		return children;
	}

	private ContainerLayout moveContainerGround(int from)
	{
		return moveContainer(from, -1);
	}

	// from/to must be top, can also be mutually bottom
	// to can be -1 to place on ground
	private ContainerLayout moveContainer(int from, int to)
	{
		// must be top
		ContainerLayout moved = new ContainerLayout(this, containersCost[from]);
		if (isOnBottom(from))
		{
			moved.containerOnBottomIndex[from] = to;
			moved.containerOnTopIndex[to] = from;
		}
		else
		{
			int belowFrom = getContainerOnBottomIndex(from);
			moved.containerOnTopIndex[belowFrom] = -1;
			moved.topmostContainersIndex.add(belowFrom);

			moved.containerOnBottomIndex[from] = to; // -1 if ground
			if (to >= 0) // place on top of container
				moved.containerOnTopIndex[to] = from;

		}
		return moved;
	}

	@Override
	public boolean isGoal(ILayout that)
	{
		return this.equals(that);
	}

	@Override
	public double getCost()
	{
		if (!this.hasCost)
			throw new IllegalStateException("This layout doesn't support cost.");
		return this.cost;
	}
}
