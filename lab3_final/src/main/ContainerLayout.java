import java.util.*;

/**
 * A representation of a configuration (layout) of containers
 * A container can have an associated cost, or not
 */
public class ContainerLayout implements ILayout
{
	private static final int BUCKET_SIZE = 52; // 2 * 26
	private static final int MID_INDEX = 26;

	static final long CONTAINS_POWER_BASE = 11;
	static final long BOTTOM_POWER_BASE = 23;
	static final long TOP_POWER_BASE = 37;

	private boolean hasCost;
	private double cost;
	private int[] containersCost; // -1 when doesn't exist
	private int[] containerOnTopIndex; // -1 when container is on top
	private int[] containerOnBottomIndex; // -1 when container is on bottom
	private List<Integer> topmostContainersIndex;
	private List<Integer> bottommostContainersIndex;

	/**
	 * Instantiates a ContainerLayout from a string
	 * @param str the string to parse into a ContainerLayout
	 * @throws IllegalArgumentException
	 */
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

		this.topmostContainersIndex = new ArrayList<Integer>(); // lazy removal, may not actually be a top
		this.bottommostContainersIndex = new ArrayList<Integer>(); // lazy removal, may not actually be a bottom
		this.hasCost = shouldHaveCost(str);

		populateFromString(str);
	}

	/**
	 * Copies a ContainerLayout and assigns a different cost to it
	 * @param other the container to copy
	 * @param cost the new cost
	 */
	private ContainerLayout(ContainerLayout other, double cost)
	{
		this.cost = cost;

		this.containersCost = new int[BUCKET_SIZE];
		System.arraycopy(other.containersCost, 0, this.containersCost, 0, BUCKET_SIZE);
		this.containerOnTopIndex = new int[BUCKET_SIZE];
		System.arraycopy(other.containerOnTopIndex, 0, this.containerOnTopIndex, 0, BUCKET_SIZE);
		this.containerOnBottomIndex = new int[BUCKET_SIZE];
		System.arraycopy(other.containerOnBottomIndex, 0, this.containerOnBottomIndex, 0, BUCKET_SIZE);

		this.topmostContainersIndex = new ArrayList<Integer>();
		for (int topmost : other.topmostContainersIndex)
			if (other.isOnTop(topmost))
				this.topmostContainersIndex.add(topmost);
		this.bottommostContainersIndex = new ArrayList<Integer>();
		for (int bottommost : other.bottommostContainersIndex)
			if (other.isOnBottom(bottommost))
				this.bottommostContainersIndex.add(bottommost);

		this.hasCost = other.hasCost;
	}

	/**
	 * Checks if the container layout described in the string
	 * has costs associated with the containers or not
	 * @param str the string to check
	 * @return if the container layout described in the string has costs associated with the containers
	 */
	private boolean shouldHaveCost(String str)
	{
		if (str.length() == 1)
			return false;
		else if (isNumerical(str.charAt(1)))
			return true;
		return false;
	}

	/**
	 * Populates the class from a string
	 * @param str the string to parse to containers
	 * @throws IllegalArgumentException
	 */
	private void populateFromString(String str) throws IllegalArgumentException
	{
		String[] tokens = str.split(" ");
		for (String token : tokens)
			populateContainersFromString(token);
	}

	/**
	 * Populates a stack from a string
	 * @param str the string to parse into a stack of containers
	 * @throws IllegalArgumentException
	 */
	private void populateContainersFromString(String str) throws IllegalArgumentException
	{
		if (this.hasCost)
			populateContainersFromStringWithCost(str);
		else
			populateContainersFromStringWithoutCost(str);
	}

	/**
	 * Populates a stack of containers that have costs from a string
	 * @param str a string with containers with a cost
	 * @throws IllegalArgumentException
	 */
	private void populateContainersFromStringWithCost(String str) throws IllegalArgumentException
	{
		addNextContainer(str, 0, -1);
	}

	/**
	 * Adds containers recursively
	 * Assumes that the string has cost
	 * @param str the string with the container to add
	 * @param index the index of the container in the string to add
	 * @param previous the container below the one to be interpreted in the string
	 * @throws IllegalArgumentException
	 */
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

	/**
	 * Checks if the container at index is a valid container
	 * @param str the string where the container is
	 * @param index the index of the container to check
	 * @throws IllegalArgumentException
	 */
	private void validateContainer(String str, int index) throws IllegalArgumentException
	{
		if (!isAlphabetical(str.charAt(index)))
			throw new IllegalArgumentException("Expected alphabetic character.");
		if ((str.length() - index) < 2 || !isNumerical(str.charAt(index + 1)))
			throw new IllegalArgumentException("Expected numeric character.");
	}

	// returns the next container's "bottom" container
	/**
	 * Adds a container.
	 * @param key the index of the container
	 * @param cost the cost of the container
	 * @param next the container above it
	 * @param previous the container below it
	 * @return the container that was added
	 * @throws IllegalArgumentException
	 */
	private int addContainer(int key, int cost, int next, int previous) throws IllegalArgumentException
	{
		if (this.contains(key))
			throw new IllegalArgumentException("Duplicate container.");

		if (previous >= 0)
			containerOnBottomIndex[key] = previous;
		else
			this.bottommostContainersIndex.add(key);

		int nextPrevious = -1;
		if (next < 0)
		{
			this.topmostContainersIndex.add(key);
		}
		else
		{
			nextPrevious = key;
			this.containerOnTopIndex[key] = next;
		}
		this.containersCost[key] = cost;
		return nextPrevious;
	}

	/**
	 * Populates containers from a string that doesn't have the cost of
	 * each container
	 * @param str the string that has the containers
	 * @throws IllegalArgumentException
	 */
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
			else
				this.bottommostContainersIndex.add(key);
			if (this.contains(key))
				throw new IllegalArgumentException("Duplicate container.");
			this.containersCost[key] = 0;
		}
	}

	/**
	 * @param i the index of the container
	 * @return if the container exists
	 */
	private boolean contains(int i)
	{
		if (i < 0)
			return false;
		return containersCost[i] >= 0;
	}

	/**
	 * @param i the index of the container
	 * @return if the container has a container on top of it
	 */
	private boolean hasContainerOnTop(int i) { return containerOnTopIndex[i] >= 0; }

	/**
	 * @param i the index of the container
	 * @return if the container has a container below it
	 */
	private boolean hasContainerOnBottom(int i) { return containerOnBottomIndex[i] >= 0; }

	/**
	 * @param i the index of the container
	 * @return if the container is on the bottom of it's stack
	 */
	private boolean isOnBottom(int i) { return !hasContainerOnBottom(i); }
 
	/**
	 * @param i the index of the container
	 * @return if the container is on top of it's stack
	 */
	private boolean isOnTop(int i) { return !hasContainerOnTop(i); }

	/**
	 * @param i the index of the container
	 * @return the index of the container on top of it or -1 if there isn't one
	 */
	private int getContainerOnTopIndex(int i) { return containerOnTopIndex[i]; }

	/**
	 * @param i the index of the container
	 * @return the index of the container below it or -1 if there isn't one
	 */
	private int getContainerOnBottomIndex(int i) { return containerOnBottomIndex[i]; }

	/**
	 * @param c the character/container to get the index from
	 * @return the index of the container
	 */
	static private int getKey(char c) { return c - 'A' >= MID_INDEX ? c - 'a' + MID_INDEX : c - 'A'; }

	/**
	 * @param i the index of the contaier
	 * @return the character of the container
	 */
	static private char getCharFromKey(int i) { return i >= MID_INDEX ? (char)(i - MID_INDEX + 'a') : (char)(i + 'A'); }

	/**
	 * @return if the character is alphabetical
	 */
	static private boolean isAlphabetical(char c) { return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'); }

	/**
	 * @return if the character is a number
	 */
	static private boolean isNumerical(char c) { return c >= '0' && c <= '9'; }

	@Override
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

	/**
	 * Checks if the two container layouts are equal at a given index
	 * @param that the container to compare to
	 * @param i the index to compare
	 * @return if the two container layouts are equal at that index
	 */
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

		long containsPower = 1;
		long bottomPower = 1;
		long topPower = 1;
		for (int i = 0; i < BUCKET_SIZE; i++)
		{
			containsPower *= CONTAINS_POWER_BASE;
			bottomPower *= BOTTOM_POWER_BASE;
			topPower *= TOP_POWER_BASE;
			if (this.contains(i))
			{
				hash += (int)containsPower;
				hash += containerOnBottomIndex[i] * (int)bottomPower;
				hash += containerOnTopIndex[i] * (int)topPower;
			}
		}
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
			if (!isOnTop(topmost)) // might not be on top because lazy removal
				continue;

			if (!isOnBottom(topmost))
				children.add(moveContainerGround(topmost));
			for (int innerTopmost : topmostContainersIndex)
			{
				if (!isOnTop(innerTopmost)) // might not be on top because lazy removal
					continue;
				if (topmost == innerTopmost)
					continue;

				children.add(moveContainer(topmost, innerTopmost));
			}
		}

		return children;
	}

	/**
	 * Generates a container layout where the given container will be moved to the ground
	 * @param from the container to move to the ground
	 * @return a new container layout where the given container was moved to the ground
	 * @pre the container must not already be on the ground
	 */
	private ContainerLayout moveContainerGround(int from)
	{
		return moveContainer(from, -1);
	}

	/**
	 * Generates a container layout where the given container was moved to the top of another container
	 * @param from the container to move
	 * @param to the container to move from on top of (can be -1 to move a container to the ground)
	 * @return a container layout where the given container was moved on top of the other container
	 * @pre from and to must be at the top of their stacks
	 */
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
			else
				moved.bottommostContainersIndex.add(from);

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

	@Override
	public double heuristic(ILayout goalLayout)
	{ 
		return h2(goalLayout);
	}

	/**
	 * heuristic that acts as a best-first search, returning always 0
	 * @param goalLayout the goal layout
	 * @return 0
	 */
	private int h0(ILayout goalLayout)
	{
		if (this.getClass() != goalLayout.getClass())
			throw new Error("Should never check the heuristic for a different class Goal!");

		return 0;
	}

	/**
	 * Simple heuristic checking only what containers aren't in their place
	 * @param goalLayout the goal layout
	 * @return the sum of the weights of the containers that aren't in their place
	 */
	private int h1(ILayout goalLayout)
	{
		if (this.getClass() != goalLayout.getClass())
			throw new Error("Should never check the heuristic for a different class Goal!");
		ContainerLayout goal = (ContainerLayout) goalLayout;

		int h = 0;
		for (Integer thisBottom : bottommostContainersIndex)
		{
			if (!isOnBottom(thisBottom))
				continue;
			if (goal.isOnBottom(thisBottom))
			{
				Integer currThis = thisBottom;
				Integer currGoal = thisBottom;
				boolean currHasRunOut = false;
				while (currThis == currGoal)
				{
					if (this.hasContainerOnTop(currThis))
						currThis = this.getContainerOnTopIndex(currThis);
					else
					{
						currHasRunOut = true;
						break;
					}

					if (goal.hasContainerOnTop(currGoal))
						currGoal = goal.getContainerOnTopIndex(currGoal);
					else
						break;
				}
				if (!currHasRunOut)
					h += getStackLengthFrom(currThis);
			}
			else
				h += getStackLengthFrom(thisBottom);
		}

		return h;
	}

	/**
	 * does what h1 does, and checks for cases where containers must be moved twice
	 * @param goalLayout the goal layout
	 * @return the calculated heuristic
	 */
	private int h2(ILayout goalLayout)
	{
		if (this.getClass() != goalLayout.getClass())
			throw new Error("Should never check the heuristic for a different class Goal!");
		ContainerLayout goal = (ContainerLayout) goalLayout;

		int h = 0;
		for (Integer thisBottom : this.bottommostContainersIndex)
		{
			if (!isOnBottom(thisBottom)) // lazy removal
				continue;

			h += hStack(thisBottom, goal);
		}

		return h;
	}

	/**
	 * gets the h2 but for a single stack
	 * @param stackBottom the bottom of the stack to get the heuristic out of
	 * @param goal the goal state
	 * @return h2 for the given stack
	 */
	private int hStack(int stackBottom, ContainerLayout goal)
	{
		int h = 0;
		if (goal.isOnBottom(stackBottom))
		{
			int currThis = stackBottom;
			int currGoal = stackBottom;
			boolean currHasRunOut = false;
			while (currThis == currGoal)
			{
				if (this.hasContainerOnTop(currThis))
					currThis = this.getContainerOnTopIndex(currThis);
				else
				{
					currHasRunOut = true;
					break;
				}

				if (goal.hasContainerOnTop(currGoal))
					currGoal = goal.getContainerOnTopIndex(currGoal);
				else
					break;
			}
			if (!currHasRunOut)
			{
				h += getStackLengthFrom(currThis);
				h += hNonGoalStackLeftovers(currThis, goal);
			}
		}
		else
		{
			h += getStackLengthFrom(stackBottom);
			h += hNonGoalStackLeftovers(stackBottom, goal);
		}
		return h;
	}

	/**
	 * gets h2 but from the middle of a stack to the top
	 * @param stackPos the position in the stack to start applying h2 to
	 * @param goal the goal layout
	 * @return h2 from the given container to the top of it's stack
	 */
	private int hNonGoalStackLeftovers(int stackPos, ContainerLayout goal)
	{
		int h = 0;
		for ( ; stackPos >= 0; stackPos = this.getContainerOnTopIndex(stackPos))
		{
			boolean hasFound = false;
			for (int curr = this.getContainerOnBottomIndex(stackPos); curr >= 0; curr = this.getContainerOnBottomIndex(curr))
			{
				// check if curr is below stackPos in goal
				for (int onGoal = goal.getContainerOnBottomIndex(stackPos); onGoal >= 0; onGoal = goal.getContainerOnBottomIndex(onGoal))
				{
					if (curr == onGoal)
					{
						h += containersCost[stackPos];
						hasFound = true;
						break;
					}
				}

				if (hasFound)
					break;
			}
		}
		return h;
	}

	/**
	 * Gets the sum of the weights of all the container above i (inclusive)
	 * @param i the container to start summing all the weights from
	 * @return the sum of the weights of all the containers above i (inclusive)
	 */
	private int getStackLengthFrom(int i)
	{
		int result = 0;
		while (hasContainerOnTop(i))
		{
			result += containersCost[i];
			i = getContainerOnTopIndex(i);
		}
		result += containersCost[i];
		return result;
	}
}
