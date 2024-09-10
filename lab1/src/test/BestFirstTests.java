import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;

class BestFirstTests
{
	@Test
	public void shouldSolve()
	{
		// Arrange
		BestFirst bestFirst = new BestFirst();
		Board initial = new Board(	"023" +
									"145" +
									"678");

		Board goal = new Board(		"123" +
									"405" +
									"678");

		Board middle = new Board(	"123" +
									"045" +
									"678");

		double expectedCost = 2.0;

		// Act
		Iterator<BestFirst.State> it = bestFirst.solve(initial, goal);
		BestFirst.State state0 = it.next();
		BestFirst.State state1 = it.next();
		BestFirst.State state2 = it.next();

		// Arrange
		assertEquals(initial, state0.layout());
		assertEquals(middle, state1.layout());
		assertEquals(goal, state2.layout());
		assertEquals(expectedCost, state2.getCost());
	}

	@Test
	public void shouldSolve2()
	{
		// Arrange
		BestFirst bestFirst = new BestFirst();
		Board initial = new Board(	"123" +
									"456" +
									"780");

		Board goal = new Board(		"436" +
									"718" +
									"520");

		double expectedCost = 12.0;

		// Act
		Iterator<BestFirst.State> it = bestFirst.solve(initial, goal);
		BestFirst.State currState = it.next();
		while(it.hasNext())
			currState = it.next();

		// Arrange
		assertEquals(goal, currState.layout());
		assertEquals(expectedCost, currState.getCost());
	}
}
