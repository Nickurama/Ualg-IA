import java.io.BufferedReader;
import java.io.Externalizable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Represents a matrix.
 */
public class Matrix /* <T extends Number> */
{
	// serializable
	private static final long serialVersionUID = 138L;

	private double matrix[][];
	private int columns;
	private int rows;

	/**
	 * instantiates an empty matrix with the desired rows and columns.
	 * @param rows the number of rows
	 * @param columns the number of columns
	 */
	public Matrix(int rows, int columns)
	{
		this.matrix = new double[rows][columns];
		this.rows = rows;
		this.columns = columns;
	}

	/**
	 * instantiates a matrix with the desired number.
	 * @param rows the number of rows
	 * @param columns the number of columns
	 * @param fill the number to fill the matrix with
	 */
	public Matrix(int rows, int columns, double fill)
	{
		this(rows, columns);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				this.matrix[i][j] = fill;
	}

	/**
	 * instantiates a matrix from a two-dimentional array.
	 * @param matrix the array to initialize the matrix from
	 */
	public Matrix(double matrix[][])
	{
		this.init(matrix);
	}

	/**
	 * initializes a matrix
	 * @param matrix the two-dimentional array to initialize the matrix from
	 */
	private final void init(double matrix[][])
	{
		this.rows = matrix.length;
		if (matrix.length == 0)
		{
			this.columns = 0;
			this.rows = 0;
		}
		else
		{
			this.columns = matrix[0].length;
			for (int row = 0; row < matrix.length; row++)
				if (matrix[row].length != this.columns)
					throw new IllegalArgumentException("Matrix had differing sizes of rows");
		}
		this.matrix = matrix;
	}

	/**
	 * Matrix copy constructor
	 * @param copy the matrix to copy
	 */
	public Matrix(Matrix copy)
	{
		this(copy.rows, copy.columns);
		copy(copy.matrix, this.matrix, rows, columns);
	}

	/**
	 * Instantiates a matrix from a file where each line is a row
	 * @param file the file path
	 * @param separator the separator for each column
	 * @return the matrix created from the file data
	 */
	// public static Matrix parseFromFile(String file, String separator) throws IOException
	// {
	// 	Matrix result = new Matrix(new double[][]{{}});
	// 	File f = new File(file);
	// 	BufferedReader reader = new BufferedReader(new FileReader(f));
	//
	// 	String line;
	// 	while ((line = reader.readLine()) != null)
	// 	{
	// 		String[] tokens = line.split(separator);
	// 		double[] row = new double[tokens.length];
	// 		for (int i = 0; i < tokens.length; i++)
	// 			row[i] = Double.parseDouble(tokens[i]);
	// 		result = result.addRow(row);
	// 	}
	//
	// 	reader.close();
	// 	return result;
	// }

	/**
	 * copies a section of a two dimentional array into another, starting at the origin
	 * @param from the array to copy from
	 * @param to the array to paste to
	 * @param rows the number of rows to copy
	 * @param columns the number of columns to copy
	 */
	private static void copy(double[][] from, double[][] to, int rows, int columns)
	{
		if (columns == 0)
			return;
		for (int i = 0; i < rows; i++)
			System.arraycopy(from[i], 0, to[i], 0, columns);
	}

	/**
	 * copies a section of a two dimentional array into another
	 * @param from the array to copy from
	 * @param to the array to copy to
	 * @param startRowTo the row to start pasting to
	 * @param startColumnTo the column to start pasting to
	 * @param rows the number of rows to copy
	 * @param columns the number of columns to copy
	 */
	private static void copy(double[][] from, double[][] to, int startRowTo, int startColumnTo, int rows, int columns)
	{
		if (columns == 0)
			return;
		for (int i = 0; i < rows; i++)
			System.arraycopy(from[i], 0, to[startRowTo + i], startColumnTo, columns);
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == this) return true;
		if (other == null) return false;
		if (!Matrix.class.isInstance(other)) return false;
		Matrix that = (Matrix) other;

		if (this.rows != that.rows || this.columns != that.columns)
			return false;

		for (int row = 0; row < rows; row++)
			for (int col = 0; col < columns; col++)
				if (this.matrix[row][col] != that.matrix[row][col])
					return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @return the transpose of the matrix
	 */
	public Matrix transpose()
	{
		Matrix transposed = new Matrix(columns, rows);
		for (int row = 0; row < rows; row++)
			for (int column = 0; column < columns; column++)
				transposed.matrix[column][row] = this.matrix[row][column];
		return transposed;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int row = 0; row < rows; row++)
		{
			sb.append("[");
			for (int col = 0; col < columns; col++)
			{
				sb.append(this.matrix[row][col]);
				if (col + 1 < columns)
					sb.append(", ");
			}
			sb.append("]");
			if (row + 1 < rows)
				sb.append("\n");
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * multiplies the matrix by a scalar.
	 * @param scalar the scalar to multiply the matrix by
	 * @return the matrix multiplied by the scalar.
	 */
	public Matrix multiply(double scalar)
	{
		Matrix result = new Matrix(rows, columns);
		for (int row = 0; row < rows; row++)
			for (int column = 0; column < columns; column++)
				result.matrix[row][column] = scalar * this.matrix[row][column];
		return result;
	}

	/**
	 * subtract two matrices.
	 * @param that the matrix to subtract the current one
	 * @return a matrix resulting in the subtraction of the current by the other 
	 */
	public Matrix sub(Matrix that)
	{
		if (this.rows != that.rows || this.columns != that.columns)
			throw new IllegalArgumentException("Illegal matrix subtraction");

		Matrix result = new Matrix(rows, columns);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				result.matrix[i][j] = this.matrix[i][j] - that.matrix[i][j];
		return result;
	}

	/**
	 * add two matrices.
	 * @param that the matrix to add to the current one
	 * @return a matrix resulting in the sum of the current by the other
	 */
	public Matrix add(Matrix that)
	{
		if (this.rows != that.rows || this.columns != that.columns)
			throw new IllegalArgumentException("Illegal matrix sum");

		Matrix result = new Matrix(rows, columns);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				result.matrix[i][j] = this.matrix[i][j] + that.matrix[i][j];
		return result;
	}

	/**
	 * the dot product between two matrices.
	 * @param that the matrix to apply the dot product by
	 * @return the dot product between the current one and the other
	 */
	public Matrix dot(Matrix that)
	{
		if (this.columns != that.rows)
			throw new IllegalArgumentException("Invalid matrix multiplication");

		Matrix result = new Matrix(this.rows, that.columns);
		for (int row = 0; row < result.rows; row++)
		{
			for (int col = 0; col < result.columns; col++)
			{
				double curr = 0;
				for (int i = 0; i < this.columns; i++)
					curr += this.matrix[row][i] * that.matrix[i][col];
				result.matrix[row][col] = curr;
			}
		}
		return result;
	}

	/**
	 * Makes a matrix where all the values are 0, except for the diagonal,
	 * which holds the values of the current row matrix.
	 * @pre the matrix is a single row
	 * @return a diagonal version of the current row matrix
	 */
	public Matrix makeDiagonal()
	{
		if (this.rows != 1)
			throw new IllegalArgumentException("Matrix must be a single row to transform into a diagonal matrix");

		Matrix result = new Matrix(columns, columns, 0);
		for (int i = 0; i < columns; i++)
			result.matrix[i][i] = this.matrix[0][i];
		return result;
	}

	/**
	 * appends a row to the current matrix.
	 * @pre the new row has to have the same column size as the matrix
	 * @param newRow the new row to append
	 * @return a matrix resulting of the current one with the row appended to it
	 */
	public Matrix addRow(double[] newRow)
	{
		if (newRow.length != this.columns && this.columns != 0)
			throw new IllegalArgumentException("Cannot add a row of different column size");

		Matrix result;

		if (this.columns == 0)
			result = new Matrix(this.rows + 1, newRow.length);
		else
			result = new Matrix(this.rows + 1, this.columns);

		copy(this.matrix, result.matrix, this.rows, this.columns);
		result.matrix[this.rows] = newRow;
		return result;
	}

	/**
	 * appends the current matrix to another, as rows (below).
	 * @pre the matrices need to have the same ammount of columns
	 * @param that the matrix to append below the current one
	 * @return a matrix resulting in the the current matrix appended with the other below it
	 */
	public Matrix appendAsRows(Matrix that)
	{
		if (this.columns != that.columns)
			throw new IllegalArgumentException("Impossible to append by rows, matrices do not have the same ammount of columns.");

		Matrix result = new Matrix(this.rows + that.rows, this.columns);
		copy(this.matrix, result.matrix, 0, 0, this.rows, this.columns);
		copy(that.matrix, result.matrix, this.rows, 0, that.rows, that.columns);

		return result;
	}

	/**
	 * Applies a function to the matrix
	 * @param func the function to apply
	 * @return a matrix resulting in the current one, with the function applied to it
	 */
	public Matrix apply(Function<Double, Double> func)
	{
		Matrix result = new Matrix(this);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				result.matrix[i][j] = func.apply(result.matrix[i][j]);
		return result;
	}

	/**
	 * parses a matrix of a single number to a double
	 * @pre the matrix is a single number
	 * @return the double version of the matrix
	 */
	public double parse()
	{
		if (this.rows != 1 || this.columns != 1)
			throw new IllegalCallerException("Cannot parse a matrix bigger than 1x1");
		return this.matrix[0][0];
	}

	/**
	 * gets the a submatrix from a matrix (0-indexed)
	 * @param startRow the starting row (inclusive)
	 * @param endRow the ending row (inclusive)
	 * @param startColumn the starting column (inclusive)
	 * @param endColumn the ending column (inclusive)
	 * @return the submatrix in the bounds set
	 */
	public Matrix subMatrix(int startRow, int endRow, int startColumn, int endColumn)
	{
		double[][] result = new double[endRow - startRow + 1][endColumn - startColumn + 1];
		for (int i = startRow; i < endRow + 1; i++)
			System.arraycopy(this.matrix[i], startColumn, result[i - startRow], 0, endColumn - startColumn + 1);
		return new Matrix(result);
	}

	/**
	 * split a matrix into two by their rows.
	 * index 0 - from 0% to percentage.
	 * index 1 - from percentage to 100%.
	 * @param percentage the percentage at which to split the matrices row-wise
	 * @return an array with both of the resulting matrices
	 */
	public Matrix[] splitByRows(double percentage)
	{
		if (percentage < 0 || percentage > 1.0)
			throw new IllegalArgumentException("Percentage must be between 0 and 1.");
		Matrix[] result = new Matrix[2];

		int rows0 = (int)(Math.round((double)this.rows() * percentage));
		result[0] = this.subMatrix(0, rows0 - 1, 0, this.columns() - 1);
		result[1] = this.subMatrix(rows0, this.rows() - 1, 0, this.columns() - 1);

		return result;
	}

	/**
	 * returns the specified number on the matrix
	 * @param row the row of the number
	 * @param column the column of the number
	 * @return the number on the given row and column
	 */
	public double get(int row, int column)
	{
		return this.matrix[row][column];
	}

	/**
	 * returns a row of the matrix
	 * @param row the zero-indexed row of the matrix
	 * @return a matrix with a single row, compromised of the selected row
	 */
	public Matrix getRow(int row)
	{
		return new Matrix(new double[][] { this.matrix[row] });
	}

	/**
	 * @return the number of rows
	 */
	public int rows()
	{
		return this.rows;
	}

	/**
	 * @return the number of columns
	 */
	public int columns()
	{
		return this.columns;
	}

	// private void writeObject(ObjectOutputStream out) throws IOException
	// {
	// 	out.writeObject(this.matrix);
	// }
	//
	// private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException
	// {
	// 	this.init((double[][])in.readObject());
	// }
}
