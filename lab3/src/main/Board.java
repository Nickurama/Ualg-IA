import java.util.LinkedList;
import java.util.List;

class Board implements ILayout
{
	private static final int dim = 3;
	private int board[][];

	public Board()
	{
		board = new int[dim][dim];
	}

	public Board(String str) throws IllegalStateException
	{
		if (str.length() != dim * dim)
			throw new IllegalStateException("Invalid arg in Board constructor");

		board = new int[dim][dim];
		int si = 0;
		for(int i = 0; i < dim; i++)
			for(int j = 0; j < dim; j++)
				board[i][j] = Character.getNumericValue(str.charAt(si++));
	}


	public Board(Board other)
	{
		board = new int[dim][dim];
		for(int i = 0; i < dim; i++)
			for(int j = 0; j < dim; j++)
				this.board[i][j] = other.board[i][j];
	}

	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < dim; i++)
		{
			for(int j = 0; j < dim; j++)
			{
				if (board[i][j] == 0)
					builder.append(' ');
				else
					builder.append(board[i][j]);
			}
			builder.append('\n');
		}
		return builder.toString();
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == this) return true;
		if (other == null) return false;
		if (!Board.class.isInstance(other)) return false;
		Board that = (Board) other;

		for(int i = 0; i < dim; i++)
			for(int j = 0; j < dim; j++)
				if (this.board[i][j] != that.board[i][j])
					return false;
		return true;
	}

	@Override
	public int hashCode() // assumes that the size is 3!!!!
	{
		// int[] rowQuotients = {7, 11, 13};
		// int[] columnQuotients = {17, 19, 23};
		//
		// int hash = 0;
		// for(int i = 0; i < dim; i++)
		// 	for(int j = 0; j < dim; j++)
		// 		hash += board[i][j] * rowQuotients[i] * columnQuotients[j];
		// return hash;
		return this.toString().hashCode();
	}

	@Override
	public double heuristic(ILayout goal) { throw new UnsupportedOperationException(); }

	@Override
	public List<ILayout> children()
	{
		int zeroRow = -1;
		int zeroColumn = -1;
		for(int i = 0; i < dim; i++)
		{
			for(int j = 0; j < dim; j++)
			{
				if(board[i][j] == 0)
				{
					zeroRow = i;
					zeroColumn = j;
				}
			}
		}
		if (zeroRow < 0 || zeroColumn < 0)
			throw new Error("Unexpected state. zero not found.");

		return children(zeroRow, zeroColumn);
	}

	private List<ILayout> children(int row, int column)
	{
		List<ILayout> children = new LinkedList<ILayout>();
		if (row > 0)
			children.add(moveUp(row, column));
		if (row < dim - 1)
			children.add(moveDown(row, column));
		if (column > 0)
			children.add(moveLeft(row, column));
		if (column < dim - 1)
			children.add(moveRight(row, column));
		return children;
	}

	private Board moveUp(int row, int column) throws IllegalArgumentException
	{
		if (row <= 0)
			throw new IllegalArgumentException("Cannot move up from row 0.");

		Board result = new Board(this);

		int tmp = result.board[row][column];
		result.board[row][column] = result.board[row - 1][column];
		result.board[row - 1][column] = tmp;

		return result;
	}

	private Board moveDown(int row, int column) throws IllegalArgumentException
	{
		if (row >= dim - 1)
			throw new IllegalArgumentException("Cannot move down from row " + (dim - 1) + ".");

		Board result = new Board(this);

		int tmp = result.board[row][column];
		result.board[row][column] = result.board[row + 1][column];
		result.board[row + 1][column] = tmp;

		return result;
	}

	private Board moveLeft(int row, int column) throws IllegalArgumentException
	{
		if (column <= 0)
			throw new IllegalArgumentException("Cannot move left from column 0.");

		Board result = new Board(this);

		int tmp = result.board[row][column];
		result.board[row][column] = result.board[row][column - 1];
		result.board[row][column - 1] = tmp;

		return result;
	}

	private Board moveRight(int row, int column) throws IllegalArgumentException
	{
		if (column >= dim - 1)
			throw new IllegalArgumentException("Cannot move right from column " + (dim - 1) + ".");

		Board result = new Board(this);

		int tmp = result.board[row][column];
		result.board[row][column] = result.board[row][column + 1];
		result.board[row][column + 1] = tmp;

		return result;
	}

	@Override
	public boolean isGoal(ILayout that)
	{
		return this.equals(that);
	}

	@Override
	public double getCost()
	{
		return 1;
	}
}
