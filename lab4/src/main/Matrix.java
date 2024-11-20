import java.util.function.Function;

public class Matrix //<T extends Number>
{
	private double matrix[][];
	private int columns;
	private int rows;

	public Matrix(int rows, int columns)
	{
		this.matrix = new double[rows][columns];
		this.rows = rows;
		this.columns = columns;
	}

	public Matrix(int rows, int columns, double fill)
	{
		this(rows, columns);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				this.matrix[i][j] = fill;
	}

	public Matrix(double matrix[][])
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

	public Matrix(Matrix copy)
	{
		this(copy.rows, copy.columns);
		copy(copy.matrix, this.matrix, rows, columns);
	}

	private static void copy(double[][] from, double[][] to, int rows, int columns)
	{
		if (columns == 0)
			return;
		for (int i = 0; i < rows; i++)
			System.arraycopy(from[i], 0, to[i], 0, columns);
	}

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

	public Matrix multiply(double scalar)
	{
		Matrix result = new Matrix(rows, columns);
		for (int row = 0; row < rows; row++)
			for (int column = 0; column < columns; column++)
				result.matrix[row][column] = scalar * this.matrix[row][column];
		return result;
	}

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

	public Matrix makeDiagonal()
	{
		if (this.rows != 1)
			throw new IllegalArgumentException("Matrix must be a single row to transform into a diagonal matrix");

		Matrix result = new Matrix(columns, columns, 0);
		for (int i = 0; i < columns; i++)
			result.matrix[i][i] = this.matrix[0][i];
		return result;
	}

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

	public Matrix appendAsRows(Matrix that)
	{
		if (this.columns != that.columns)
			throw new IllegalArgumentException("Impossible to append by rows, matrices do not have the same ammount of columns.");

		Matrix result = new Matrix(this.rows + that.rows, this.columns);
		copy(this.matrix, result.matrix, 0, 0, this.rows, this.columns);
		copy(that.matrix, result.matrix, this.rows, 0, that.rows, that.columns);

		return result;
	}

	public Matrix apply(Function<Double, Double> func)
	{
		Matrix result = new Matrix(this);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				result.matrix[i][j] = func.apply(result.matrix[i][j]);
		return result;
	}

	public double parse()
	{
		if (this.rows != 1 || this.columns != 1)
			throw new IllegalCallerException("Cannot parse a matrix bigger than 1x1");
		return this.matrix[0][0];
	}

	public double get(int row, int column)
	{
		return this.matrix[row][column];
	}

	public int rows()
	{
		return this.rows;
	}

	public int columns()
	{
		return this.columns;
	}
}
