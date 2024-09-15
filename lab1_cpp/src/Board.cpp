#include "Board.hpp"
#include <ostream>
#include <stdexcept>
#include <sstream>

Board::Board() { }

Board::~Board() { }

// Board::Board(Board&& b)
// 	: m_board(std::move(b.m_board))
// {
// }

Board::Board(std::string str)
{
	if (str.length() != DIM * DIM)
		throw std::invalid_argument("Invalid string in Board constructor.");

	int strIndex = 0;
	for (int i = 0; i < DIM; i++)
		for (int j = 0; j < DIM; j++)
			m_board[i][j] = str[strIndex++] - '0';
}

Board::Board(const Board& b)
{
	*this = b;
}

Board& Board::operator=(const Board& rhs)
{
	if (this == &rhs) // self assignment check
		return *this;

	for (int i = 0; i < DIM; i++)
		for (int j = 0; j < DIM; j++)
			m_board[i][j] = rhs.m_board[i][j];

	return *this;
}

std::unique_ptr<ILayout> Board::copy() const
{
	return std::make_unique<Board>(Board(*this));
}


void Board::print(std::ostream& os) const
{
	for (int i = 0; i < DIM; i++)
	{
		for (int j = 0; j < DIM; j++)
		{
			if (m_board[i][j] == 0)
				os << ' ';
			else
				os << m_board[i][j];
		}
		os << '\n';
	}
}

bool Board::operator==(const ILayout& rhs) const
{
	if (this == &rhs) return true;
	const Board& other = dynamic_cast<const Board&>(rhs);
	// if (other == nullptr) return false;

	for (int i = 0; i < DIM; i++)
		for (int j = 0; j < DIM; j++)
			if (m_board[i][j] != other.m_board[i][j])
				return false;
	return true;
}

size_t Board::hash() const
{
	std::stringstream ss;
	ss << *this;
	return std::hash<std::string>()(ss.str());
}

std::list<std::unique_ptr<ILayout>> Board::children() const
{
	int zeroRow = -1;
	int zeroColumn = -1;
	for (int i = 0; i < DIM; i++)
	{
		for (int j = 0; j < DIM; j++)
		{
			if (m_board[i][j] == 0)
			{
				zeroRow = i;
				zeroColumn = j;
			}
		}
	}
	if (zeroRow < 0 || zeroColumn < 0)
		throw std::runtime_error("Zero was not found.");

	return children(zeroRow, zeroColumn);
}

std::list<std::unique_ptr<ILayout>> Board::children(int row, int column) const
{
	std::list<std::unique_ptr<ILayout>> children;
	if (row > 0)
		children.emplace_front(std::make_unique<Board>(moveUp(row, column)));
	if (row < DIM - 1)
		children.emplace_front(std::make_unique<Board>(moveDown(row, column)));
	if (column > 0)
		children.emplace_front(std::make_unique<Board>(moveLeft(row, column)));
	if (column < DIM - 1)
		children.emplace_front(std::make_unique<Board>(moveRight(row, column)));
	return children;
}

Board Board::moveUp(int row, int column) const
{
	if (row <= 0)
		throw std::invalid_argument("Cannot move up from row 0.");

	Board result(*this);

	int tmp = result.m_board[row][column];
	result.m_board[row][column] = result.m_board[row - 1][column];
	result.m_board[row - 1][column] = tmp;

	return result;
}

Board Board::moveDown(int row, int column) const
{
	if (row >= DIM - 1)
		throw std::invalid_argument("Cannot move down from row " + std::to_string(DIM - 1) + ".");

	Board result(*this);

	int tmp = result.m_board[row][column];
	result.m_board[row][column] = result.m_board[row + 1][column];
	result.m_board[row + 1][column] = tmp;

	return result;
}

Board Board::moveLeft(int row, int column) const
{
	if (column <= 0)
		throw std::invalid_argument("Cannot move left from column 0.");

	Board result(*this);

	int tmp = result.m_board[row][column];
	result.m_board[row][column] = result.m_board[row][column - 1];
	result.m_board[row][column - 1] = tmp;

	return result;
}

Board Board::moveRight(int row, int column) const
{
	if (column >= DIM - 1)
		throw std::invalid_argument("Cannot move right from column " + std::to_string(DIM - 1) + ".");

	Board result(*this);

	int tmp = result.m_board[row][column];
	result.m_board[row][column] = result.m_board[row][column + 1];
	result.m_board[row][column + 1] = tmp;

	return result;
}

bool Board::isGoal(const ILayout& that) const
{
	const Board& other = dynamic_cast<const Board&>(that);

	return *this == other;
}

double Board::getCost() const
{
	return 1;
}
