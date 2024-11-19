public class Matrix //<T extends Number>
{
	double matrix[][];
	int columns;
	int rows;

	private Matrix(int rows, int columns)
	{
		this.matrix = new double[rows][columns];
		this.rows = rows;
		this.columns = columns;
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

	public Matrix dot(Matrix that)
	{
		if (this.columns != that.rows)
			throw new IllegalArgumentException("Cannot multiply matrices");

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

	public double parse()
	{
		if (this.rows != 1 || this.columns != 1)
			throw new IllegalCallerException("Cannot parse a matrix bigger than 1x1");
		return this.matrix[0][0];
	}
}
