import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class ContainerLayoutTests
{
	@Test
	public void testConstructor()
	{
		// Arrange
		ContainerLayout cs = new ContainerLayout("A1C1 B2");
		StringWriter writer = new StringWriter();
		PrintWriter pw = new PrintWriter ( writer ) ;
		pw.println("[A, C]");
		pw.println("[B]");
		String expected = writer.toString();
		pw.close();

		// Act
		String obtained = cs.toString();

		// Assert
		assertEquals(expected, obtained);
	}

	@Test
	public void testConstructor2()
	{
		// Arrange
		ContainerLayout cs = new ContainerLayout("C1A1 B2 D9 E14F9927G1 H1I1J1K1L1M1N1O1P1");
		StringWriter writer = new StringWriter();
		PrintWriter pw = new PrintWriter ( writer ) ;
		pw.println("[B]");
		pw.println("[C, A]");
		pw.println("[D]");
		pw.println("[E, F, G]");
		pw.println("[H, I, J, K, L, M, N, O, P]");
		String expected = writer.toString();
		pw.close();

		// Act
		String obtained = cs.toString();

		// Assert
		assertEquals(expected, obtained);
	}

	public void testConstructor3()
	{
		// Arrange
		ContainerLayout cs = new ContainerLayout("A C B");
		StringWriter writer = new StringWriter();
		PrintWriter pw = new PrintWriter ( writer ) ;
		pw.println("[A, C]");
		pw.println("[B]");
		String expected = writer.toString();
		pw.close();

		// Act
		String obtained = cs.toString();

		// Assert
		assertEquals(expected, obtained);
	}

	public void shouldThrowWhenConstructingEmptyString()
	{
		// Arrange
		boolean threw = false;

		// Act
		try
		{
			new ContainerLayout("");
		}
		catch (Exception e)
		{
			threw = true;
		}

		// Assert
		assertTrue(threw);
	}

	@Test
	public void shouldThrowWhenInvalidCost()
	{
		// Arrange
		boolean threw = false;

		// Act
		try
		{
			new ContainerLayout("A1 B-3");
		}
		catch (Exception e)
		{
			threw = true;
		}

		// Assert
		assertTrue(threw);
	}

	@Test
	public void shouldThrowWhenDuplicateContainer()
	{
		// Arrange
		boolean threw = false;

		// Act
		try
		{
			new ContainerLayout("A1 A2");
		}
		catch (Exception e)
		{
			threw = true;
		}

		// Assert
		assertTrue(threw);
	}

	@Test
	public void shouldThrowWhenMixingCostWithNoCost1()
	{
		// Arrange
		boolean threw = false;

		// Act
		try
		{
			new ContainerLayout("A B1C2");
		}
		catch (Exception e)
		{
			threw = true;
		}

		// Assert
		assertTrue(threw);
	}

	@Test
	public void shouldThrowWhenMixingCostWithNoCost2()
	{
		// Arrange
		boolean threw = false;

		// Act
		try
		{
			new ContainerLayout("A1 CB1");
		}
		catch (Exception e)
		{
			threw = true;
		}

		// Assert
		assertTrue(threw);
	}

	@Test
	public void shouldThrowWhenMixingCostWithNoCost3()
	{
		// Arrange
		boolean threw = false;

		// Act
		try
		{
			new ContainerLayout("A1 B C1");
		}
		catch (Exception e)
		{
			threw = true;
		}

		// Assert
		assertTrue(threw);
	}

	@Test
	public void getCostShoultReturnCost()
	{
		// Arrange
		ContainerLayout cs = new ContainerLayout("B1A1 E14F9927");
		List<ILayout> children = cs.children();
		double expected0 = 0.0;
		double expected1 = 1.0;
		double expected2 = 9927.0;

		// Act
		double cost0 = cs.getCost();

		// Assert
		assertEquals(expected0, cost0);
		int count = 0;
		for (ILayout child : children)
		{
			if (count++ <= 1)
				assertEquals(expected1, child.getCost());
			else
				assertEquals(expected2, child.getCost());
		}
	}

	@Test
	public void getCostShouldThrowWhenNoCostIsGiven()
	{
		// Arrange
		ContainerLayout cs = new ContainerLayout("BA EF C");
		boolean threw = false;

		// Act
		try
		{
			cs.getCost();
		}
		catch (Exception e)
		{
			threw = true;
		}

		// Assert
		assertTrue(threw);
	}

	@Test
	public void shouldEquals()
	{
		// Arrange
		ContainerLayout b0 = new ContainerLayout("B1A1 E14F9927");
		ContainerLayout b1 = new ContainerLayout("B1A1 E14F9927");
		ContainerLayout b2 = new ContainerLayout("F3 E14B4");
		ContainerLayout b3 = new ContainerLayout("F5 E4B7");
		ContainerLayout b4 = new ContainerLayout("A1 B1 C1 D1 E1 F3");
		ContainerLayout b5 = new ContainerLayout("B1 E1 D1 A1 F3 C1");
		
		// Act
		boolean simmetry0 = b0.equals(b1) && b1.equals(b0);
		boolean simmetry1 = b2.equals(b3) && b3.equals(b2);
		boolean simmetry2 = b4.equals(b5) && b5.equals(b4);

		// Arrange
		assertTrue(simmetry0);
		assertTrue(simmetry1);
		assertTrue(simmetry2);
	}

	@Test
	public void shouldEqualsNoCost()
	{
		// Arrange
		ContainerLayout b0 = new ContainerLayout("F3 E14B4");
		ContainerLayout b1 = new ContainerLayout("F EB");
		ContainerLayout b2 = new ContainerLayout("A1 B1 C1 D1 E1 F3");
		ContainerLayout b3 = new ContainerLayout("F EB");
		
		// Act
		boolean simmetry0 = b0.equals(b1) && b1.equals(b0);
		boolean simmetry1 = b0.equals(b2) && b2.equals(b0);
		boolean simmetry2 = b1.equals(b2) && b2.equals(b1);
		boolean simmetry3 = b1.equals(b3) && b3.equals(b1);

		// Arrange
		assertTrue(simmetry0);
		assertFalse(simmetry1);
		assertFalse(simmetry2);
		assertTrue(simmetry3);
	}

	@Test
	public void shouldNotEquals()
	{
		// Arrange
		ContainerLayout b0 = new ContainerLayout("B1A1 E14F9927");
		ContainerLayout b1 = new ContainerLayout("F3 E14B4");
		
		// Act
		boolean simmetry0 = b0.equals(b1) || b1.equals(b0);

		// Arrange
		assertFalse(simmetry0);
		assertNotEquals(b0, "B1A1 E14F9927");
	}

	@Test
	public void shouldHaveHashProperties()
	{
		// Arrange
		ContainerLayout b0 = new ContainerLayout("B1A1 E14F9927");
		ContainerLayout b1 = new ContainerLayout("B1A1 E14F9927");
		ContainerLayout b2 = new ContainerLayout("F3 E14B4");
		ContainerLayout b3 = new ContainerLayout("F3 E14B4");
		ContainerLayout b4 = new ContainerLayout("F5 E4B33");
		ContainerLayout b5 = new ContainerLayout("F EB");
		ContainerLayout b6 = new ContainerLayout("EB F");
		
		// Act
		boolean simmetry0 = b0.equals(b1) && b1.equals(b0);
		boolean simmetry1 = b2.equals(b3) && b3.equals(b2);
		boolean simmetry2 = b3.equals(b4) && b4.equals(b3);
		boolean simmetry3 = b3.equals(b5) && b5.equals(b3);
		boolean simmetry4 = b3.equals(b6) && b6.equals(b3);
		int hash0 = b0.hashCode();
		int hash1 = b1.hashCode();
		int hash2 = b2.hashCode();
		int hash3 = b3.hashCode();
		int hash4 = b4.hashCode();
		int hash5 = b5.hashCode();
		int hash6 = b6.hashCode();

		// Arrange
		assertTrue(simmetry0);
		assertTrue(simmetry1);
		assertTrue(simmetry2);
		assertTrue(simmetry3);
		assertTrue(simmetry4);

		assertEquals(hash0, hash1);
		assertEquals(hash2, hash3);
		assertNotEquals(hash0, hash2);
		assertEquals(hash3, hash4);
		assertEquals(hash3, hash5);
		assertEquals(hash3, hash6);
	}

	@Test
	public void shouldBeGoalWhenEquals()
	{
		// Arrange
		ContainerLayout b0 = new ContainerLayout("B1A1 E14F9927");
		ContainerLayout b1 = new ContainerLayout("B1A1 E14F9927");
		ContainerLayout b2 = new ContainerLayout("F3 E14B4");
		ContainerLayout b3 = new ContainerLayout("F EB");
		
		// Act
		boolean isGoal0 = b0.isGoal(b1);
		boolean isGoal1 = b1.isGoal(b2);
		boolean isGoal2 = b2.isGoal(b3);

		// Arrange
		assertTrue(isGoal0);
		assertFalse(isGoal1);
		assertTrue(isGoal2);
	}

	public void shouldGetOneChildSameAsParent()
	{
		// Arrange
		ContainerLayout cs = new ContainerLayout("A1");

		// Act
		List<ILayout> children = cs.children();

		// Arrange
		assertEquals(children.size(), 1);
		assertTrue(children.contains(cs));
	}

	@Test
	public void shouldGetFourChildren()
	{
		// Arrange
		ContainerLayout cs = new ContainerLayout("B1A1 E14F9927");
		ContainerLayout expectedChild0 = new ContainerLayout("A1 B1 E14F9927");
		ContainerLayout expectedChild1 = new ContainerLayout("B1 E14F9927A1");
		ContainerLayout expectedChild2 = new ContainerLayout("F9927 B1A1 E14");
		ContainerLayout expectedChild3 = new ContainerLayout("B1A1F9927 E14");

		// Act
		List<ILayout> children = cs.children();

		// Arrange
		assertEquals(children.size(), 4);
		assertTrue(children.contains(expectedChild0));
		assertTrue(children.contains(expectedChild1));
		assertTrue(children.contains(expectedChild2));
		assertTrue(children.contains(expectedChild3));
	}

	@Test
	public void shouldGetEightChildren()
	{
		// Arrange
		ContainerLayout cs = new ContainerLayout("B1A1 E14F9927 C7");

		ContainerLayout expectedChild0 = new ContainerLayout("A1 B1 E14F9927 C7");
		ContainerLayout expectedChild1 = new ContainerLayout("B1 E14F9927A1 C7");
		ContainerLayout expectedChild2 = new ContainerLayout("B1 E14F9927 C7A1");

		ContainerLayout expectedChild3 = new ContainerLayout("F9927 B1A1 E14 C7");
		ContainerLayout expectedChild4 = new ContainerLayout("B1A1F9927 E14 C7");
		ContainerLayout expectedChild5 = new ContainerLayout("B1A1 E14 C7F9927");

		ContainerLayout expectedChild6 = new ContainerLayout("B1A1C7 E14F9927");
		ContainerLayout expectedChild7 = new ContainerLayout("B1A1 E14F9927C7");

		// Act
		List<ILayout> children = cs.children();

		// Arrange
		assertEquals(children.size(), 8);
		assertTrue(children.contains(expectedChild0));
		assertTrue(children.contains(expectedChild1));
		assertTrue(children.contains(expectedChild2));
		assertTrue(children.contains(expectedChild3));
		assertTrue(children.contains(expectedChild4));
		assertTrue(children.contains(expectedChild5));
		assertTrue(children.contains(expectedChild6));
		assertTrue(children.contains(expectedChild7));
	}
}
