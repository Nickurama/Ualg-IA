import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class MatrixTests
{
	@Test
	public void shouldTranspose()
	{
		// Arrange
		double[][] m0arr = {
			{ 1, 2, 3, 4, 5 },
			{ 7, 2, 7, 0, 1 },
			{ 7, 1, 3, 7, 2 },
		};
		Matrix m0 = new Matrix(m0arr);
		double[][] m0expArr = {
			{ 1, 7, 7 },
			{ 2, 2, 1 },
			{ 3, 7, 3 },
			{ 4, 0, 7 },
			{ 5, 1, 2 },
		};
		Matrix m0exp = new Matrix(m0expArr);

		double[][] m1arr = {};
		Matrix m1 = new Matrix(m1arr);
		double[][] m1expArr = {};
		Matrix m1exp = new Matrix(m1expArr);

		// Act
		Matrix m0T = m0.transpose();
		Matrix m1T = m1.transpose();

		// Assert
		assertEquals(m0T, m0exp);
		assertEquals(m1T, m1exp);
	}

	@Test
	public void shouldEquals()
	{
		// Arrange
		double[][] m0arr = {
			{ 1, 2, 3, 4, 5 },
			{ 7, 2, 7, 0, 1 },
			{ 7, 1, 3, 7, 2 },
		};
		Matrix m0 = new Matrix(m0arr);

		double[][] m1arr = {
			{ 1, 2, 3, 4, 5 },
			{ 7, 2, 7, 0, 1 },
			{ 7, 1, 3, 7, 2 },
		};
		Matrix m1 = new Matrix(m1arr);

		double[][] m2arr = {
			{ 1, 7, 7 },
			{ 2, 2, 1 },
			{ 3, 7, 3 },
			{ 4, 0, 7 },
			{ 5, 1, 2 },
		};
		Matrix m2 = new Matrix(m2arr);

		// Act
		boolean isEquals0 = m0.equals(m1);
		boolean expected0 = true;
		boolean isEquals1 = m1.equals(m0);
		boolean expected1 = true;
		boolean isEquals2 = m0.equals(m2);
		boolean expected2 = false;
		boolean isEquals3 = m2.equals(m0);
		boolean expected3 = false;

		// Assert
		assertEquals(expected0, isEquals0);
		assertEquals(expected1, isEquals1);
		assertEquals(expected2, isEquals2);
		assertEquals(expected3, isEquals3);
	}

	@Test
	public void shouldApplyScalar()
	{
		// Arrange
		double[][] m0arr = {
			{ 1, 2, 3, 4, 5 },
			{ 7, 2, 7, 0, 1 },
			{ 7, 1, 3, 7, 2 },
		};
		Matrix m0 = new Matrix(m0arr);

		double[][] m0expArr = {
			{ 7, 14, 21, 28, 35 },
			{ 49, 14, 49, 0, 7 },
			{ 49, 7, 21, 49, 14 },
		};
		Matrix exp0 = new Matrix(m0expArr);

		double scalar0 = 7.0;

		// Act
		Matrix obtained0 = m0.multiply(scalar0);

		// Assert
		assertEquals(obtained0, exp0);
	}

	@Test
	public void shouldNotApplyDotProduct()
	{
		// Arrange
		Matrix m0 = new Matrix(new double[][]{
			{ 1, 2, 3, 4, 5 },
			{ 7, 2, 7, 0, 1 },
			{ 7, 1, 3, 7, 2 },
		});

		Matrix m1 = new Matrix(new double[][]{
			{ 1, 3, 7 },
			{ 7, 3, 1 },
			{ 0, 2, 5 },
			{ 0, 0, 9 },
			{ 4, 3, 1 },
		});

		Matrix m2 = new Matrix(new double[][]{
			{ 1, 3, 7, 2, 7 },
		});

		Matrix m3 = new Matrix(new double[][]{
			{ 1 },
			{ 3 },
			{ 5 },
			{ 7 },
			{ 11 },
		});

		// Act
		boolean threw0 = false;
		boolean threw1 = false;
		boolean notThrew0 = true;
		boolean notThrew1 = true;

		try {
			m0.dot(m3);
		}
		catch (Exception e)
		{
			notThrew0 = false;
		}

		try {
			m2.dot(m1);
		}
		catch (Exception e)
		{
			notThrew1 = false;
		}

		try {
			m1.dot(m2);
		}
		catch (Exception e)
		{
			threw0 = true;
		}

		try {
			m1.dot(m3);
		}
		catch (Exception e)
		{
			threw1 = true;
		}

		// Assert
		assertTrue(threw0);
		assertTrue(threw1);
		assertTrue(notThrew0);
		assertTrue(notThrew1);
	}

	@Test
	public void shouldApplyDotProduct()
	{
		// Arrange
		Matrix m0 = new Matrix(new double[][]{
			{ 1, 2, 3, 4, 5 },
			{ 7, 2, 7, 0, 1 },
			{ 7, 1, 3, 7, 2 },
		});

		Matrix m1 = new Matrix(new double[][]{
			{ 1, 3, 7 },
			{ 7, 3, 1 },
			{ 0, 2, 5 },
			{ 0, 0, 9 },
			{ 4, 3, 1 },
		});

		Matrix m2 = new Matrix(new double[][]{
			{ 1, 3, 7, 2, 7 },
		});

		Matrix m3 = new Matrix(new double[][]{
			{ 1 },
			{ 3 },
			{ 5 },
			{ 7 },
			{ 11 },
		});

		// Act
		Matrix obtained0 = m0.dot(m1);
		Matrix exp0 = new Matrix(new double[][]{
			{ 35, 30, 65 },
			{ 25, 44, 87 },
			{ 22, 36, 130 },
		});

		Matrix obtained1 = m2.dot(m3);
		Matrix exp1 = new Matrix(new double[][]{
			{ 136 },
		});

		// Assert
		assertEquals(obtained0, exp0);
		assertEquals(obtained1, exp1);
	}

	@Test
	public void shouldParse()
	{
		// Arrange
		Matrix m0 = new Matrix(new double[][]{
			{ 1, 3, 7, 2, 7 },
		});

		Matrix m1 = new Matrix(new double[][]{
			{ 1 },
			{ 3 },
			{ 5 },
			{ 7 },
			{ 11 },
		});

		Matrix m2 = new Matrix(new double[][]{
			{ 727.0 }
		});

		// Act
		double obtained0 = m0.dot(m1).parse();
		double expected0 = 136.0;
		double obtained1 = m2.parse();
		double expected1 = 727.0;

		// Assert
		assertEquals(obtained0, expected0);
		assertEquals(obtained1, expected1);
	}

	@Test
	public void shouldNotParse()
	{
		// Arrange
		Matrix m0 = new Matrix(new double[][]{
			{ 1, 3, 7, 2, 7 },
		});

		Matrix m1 = new Matrix(new double[][]{
			{ 1 },
			{ 3 },
			{ 5 },
			{ 7 },
			{ 11 },
		});

		// Act
		boolean threw0 = false;
		boolean threw1 = false;

		try {
			m0.parse();
		}
		catch (Exception e)
		{
			threw0 = true;
		}

		try {
			m1.parse();
		}
		catch (Exception e)
		{
			threw1 = true;
		}

		// Assert
		assertTrue(threw0);
		assertTrue(threw1);
	}
}
