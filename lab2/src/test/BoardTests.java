import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class BoardTests
{
	@Test
	public void testConstructor()
	{
		Board b = new Board("023145678");
		StringWriter writer = new StringWriter();
		PrintWriter pw = new PrintWriter ( writer ) ;
		pw.println(" 23");
		pw.println("145");
		pw.println("678");
		assertEquals(writer.toString(), b.toString());
		pw.close();
	}

	@Test
	public void testConstructor2()
	{
		Board b = new Board("123485670");
		StringWriter writer = new StringWriter();
		PrintWriter pw = new PrintWriter (writer) ;
		pw.println("123");
		pw.println("485");
		pw.println("67 ");
		assertEquals(writer.toString(), b.toString());
		pw.close();
	}

	@Test
	public void getCostShoultReturnOne()
	{
		// Arrange
		Board b0 = new Board("123456780");
		Board b1 = new Board("087654321");
		double expected = 1.0d;

		// Act
		double cost0 = b0.getCost();
		double cost1 = b1.getCost();

		// Assert
		assertEquals(expected, cost0);
		assertEquals(expected, cost1);
	}

	@Test
	public void shouldEquals()
	{
		// Arrange
		Board b0 = new Board("123456780");
		Board b1 = new Board("123456780");
		Board b2 = new Board("137245086");
		Board b3 = new Board("137245086");
		
		// Act
		boolean simmetry0 = b0.equals(b1) && b1.equals(b0);
		boolean simmetry1 = b2.equals(b3) && b3.equals(b2);

		// Arrange
		assertTrue(simmetry0);
		assertTrue(simmetry1);
	}

	@Test
	public void shouldNotEquals()
	{
		// Arrange
		Board b0 = new Board("123456780");
		Board b1 = new Board("137245086");
		Board b2 = new Board("137245680");
		
		// Act
		boolean simmetry0 = b0.equals(b1) || b1.equals(b0);
		boolean simmetry1 = b0.equals(b2) || b2.equals(b0);
		boolean simmetry2 = b1.equals(b2) || b2.equals(b1);

		// Arrange
		assertFalse(simmetry0);
		assertFalse(simmetry1);
		assertFalse(simmetry2);
		assertNotEquals(b0, "123456780");
	}

	@Test
	public void shouldHaveHashProperties()
	{
		// Arrange
		Board b0 = new Board("123456780");
		Board b1 = new Board("123456780");
		Board b2 = new Board("137245086");
		Board b3 = new Board("137245086");
		
		// Act
		boolean simmetry0 = b0.equals(b1) && b1.equals(b0);
		boolean simmetry1 = b2.equals(b3) && b3.equals(b2);
		int hash0 = b0.hashCode();
		int hash1 = b1.hashCode();
		int hash2 = b2.hashCode();
		int hash3 = b3.hashCode();

		// Arrange
		assertTrue(simmetry0);
		assertTrue(simmetry1);
		assertEquals(hash0, hash1);
		assertEquals(hash2, hash3);
		assertNotEquals(hash0, hash2);
	}

	@Test
	public void shouldBeGoalWhenEquals()
	{
		// Arrange
		Board b0 = new Board("123456780");
		Board b1 = new Board("123456780");
		Board b2 = new Board("137245086");
		Board b3 = new Board("137245086");
		
		// Act
		boolean isGoal0 = b0.isGoal(b1);
		boolean isGoal1 = b1.isGoal(b2);
		boolean isGoal2 = b2.isGoal(b3);

		// Arrange
		assertTrue(isGoal0);
		assertFalse(isGoal1);
		assertTrue(isGoal2);
	}

	@Test
	public void shouldGetFourChildren()
	{
		// Arrange
		Board b = new Board("134208657");
		Board expectedChild0 = new Board("104238657");
		Board expectedChild1 = new Board("134280657");
		Board expectedChild2 = new Board("134028657");
		Board expectedChild3 = new Board("134258607");

		// Act
		List<ILayout> children = b.children();

		// Arrange
		assertEquals(children.size(), 4);
		assertTrue(children.contains(expectedChild0));
		assertTrue(children.contains(expectedChild1));
		assertTrue(children.contains(expectedChild2));
		assertTrue(children.contains(expectedChild3));
	}

	@Test
	public void shouldGetTwoChildrenOnBottomLeft()
	{
		// Arrange
		Board b = new Board("634218057");
		Board expectedChild0 = new Board("634018257");
		Board expectedChild1 = new Board("634218507");

		// Act
		List<ILayout> children = b.children();

		// Arrange
		assertEquals(children.size(), 2);
		assertTrue(children.contains(expectedChild0));
		assertTrue(children.contains(expectedChild1));
	}

	@Test
	public void shouldGetTwoChildrenOnTopRight()
	{
		// Arrange
		Board b = new Board("630218357");
		Board expectedChild0 = new Board("603218357");
		Board expectedChild1 = new Board("638210357");

		// Act
		List<ILayout> children = b.children();

		// Arrange
		assertEquals(children.size(), 2);
		assertTrue(children.contains(expectedChild0));
		assertTrue(children.contains(expectedChild1));
	}
}
