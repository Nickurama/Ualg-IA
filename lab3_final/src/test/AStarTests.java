import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;

class AStarTests
{
	@Test
	public void shouldSolveContainerLayout()
	{
		// Arrange
		AStar A = new AStar();
		ContainerLayout initial = new ContainerLayout(	"A1C1 " +
														"B1");

		ContainerLayout goal = new ContainerLayout(		"A " +
														"C " +
														"B");

		double expectedCost = 1.0;

		// Act
		Iterator<AStar.State> it = A.solve(initial, goal);
		AStar.State state0 = it.next();
		AStar.State state1 = it.next();

		// Assert
		assertEquals(initial, state0.layout());
		assertEquals(goal, state1.layout());
		assertEquals(expectedCost, state1.g());
	}

	@Test
	public void shouldSolveContainerLayoutWhenInitialSameAsGoal()
	{
		// Arrange
		AStar A = new AStar();
		ContainerLayout initial = new ContainerLayout("A1B2C3");

		ContainerLayout goal = new ContainerLayout("A1B2C3");

		double expectedCost = 0.0;

		// Act
		Iterator<AStar.State> it = A.solve(initial, goal);
		AStar.State currState = it.next();
		while(it.hasNext())
			currState = it.next();

		// Assert
		assertEquals(goal, currState.layout());
		assertEquals(expectedCost, currState.g());
	}

	@Test
	public void shouldWorkWithLowercaseContainerLayout()
	{
		// Arrange
		AStar A = new AStar();
		ContainerLayout initial = new ContainerLayout("a1B7 c3d2 A14");
		ContainerLayout goal = new ContainerLayout("aBA dc");

		double expectedCost = 19.0;

		// Act
		Iterator<AStar.State> it = A.solve(initial, goal);
		AStar.State currState = it.next();
		while(it.hasNext())
			currState = it.next();

		// Assert
		assertEquals(goal, currState.layout());
		assertEquals(expectedCost, currState.g());
	}

	@Test
	public void shouldBeEfficientWithReversedContainers()
	{
		// Arrange
		AStar A = new AStar();
		ContainerLayout initial = new ContainerLayout("O6l3p5m1x2E9 M5u7 e6 o2t1s6I7 a2 A2");
		ContainerLayout goal = new ContainerLayout("Isto e uM ExmplO a A");
		double expectedCost = 54.0;

		// Act
		Iterator<AStar.State> it = A.solve(initial, goal);
		AStar.State currState = it.next();
		while(it.hasNext())
			currState = it.next();

		// Assert
		assertEquals(goal, currState.layout());
		assertEquals(expectedCost, currState.g());
	}

	@Test
	public void mooshak5()
	{
		// Arrange
		AStar A = new AStar();
		ContainerLayout initial = new ContainerLayout("A9I8Q7Y6 B5J4R3Z2 C1K2S3 D4L5T6 E7M8U9 F1N2V3 G1O2W3 H3P2X1");
		ContainerLayout goal = new ContainerLayout("YZXW OQSTUV NMLKRI AJCDEF G B HP");
		double expectedCost = 87.0;

		// Act
		Iterator<AStar.State> it = A.solve(initial, goal);
		AStar.State currState = it.next();
		while(it.hasNext())
			currState = it.next();

		// Assert
		assertEquals(goal, currState.layout());
		assertEquals(expectedCost, currState.g());
	}

	@Test
	public void mooshak6()
	{
		// Arrange
		AStar A = new AStar();
		ContainerLayout initial = new ContainerLayout("B9b5A2a7 E3 e3 G1H5L9 M8m4N3O7o5 S3s1T5 R1 V9C4c5");
		ContainerLayout goal = new ContainerLayout("BOM TRAbaLH VCS coNsEGem");
		double expectedCost = 84.0;

		// Act
		Iterator<AStar.State> it = A.solve(initial, goal);
		AStar.State currState = it.next();
		while(it.hasNext())
			currState = it.next();

		// Assert
		assertEquals(goal, currState.layout());
		assertEquals(expectedCost, currState.g());
	}
	@Test
	public void mooshak7()
	{
		// Arrange
		AStar A = new AStar();
		ContainerLayout initial = new ContainerLayout("A1B1C1D1E1F1G1H1I1J1K1L1M1N1O1P1Q1R1S1T1U1V1W1X1Y1Z1a1b1c1d1e1f1g1h1i1j1k1l1m1n1o1p1q1r1s1t1u1v1w1x1y1z1");
		ContainerLayout goal = new ContainerLayout("zyxwvutsrqEDCBA cab edQPONMLKJI ZYXWVUTSRH gfGF kjih ponml");
		double expectedCost = 53.0;

		// Act
		Iterator<AStar.State> it = A.solve(initial, goal);
		AStar.State currState = it.next();
		while(it.hasNext())
			currState = it.next();

		// Assert
		assertEquals(goal, currState.layout());
		assertEquals(expectedCost, currState.g());
	}
	@Test
	public void mooshak9()
	{
		// Arrange
		AStar A = new AStar();
		ContainerLayout initial = new ContainerLayout("A8B7C6D5E4F3G2H1I9J8K7L6M5N4O3P2Q1R2S3T4U5V6W7X8Y9Z1");
		ContainerLayout goal = new ContainerLayout("ZYXWVU MNOPQRS LKJIHGF ABCDET");
		double expectedCost = 115.0;

		// Act
		Iterator<AStar.State> it = A.solve(initial, goal);
		AStar.State currState = it.next();
		while(it.hasNext())
			currState = it.next();

		// Assert
		assertEquals(goal, currState.layout());
		assertEquals(expectedCost, currState.g());
	}
}
