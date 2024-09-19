#include "Board.hpp"
#include <chrono>
#include <cstring>
#include <ostream>
#include <iostream>
#include <stdexcept>
#include <sstream>

Board::Board() : m_hash(-1), m_hasHash(false) { }

Board::~Board() { }

// Board::Board(Board&& b)
// 	: m_hash(b.m_hash)
// {
// }

Board::Board(const std::string& str)
	: m_hash(-1),
	m_hasHash(false)
{
	if (str.length() != DIM * DIM)
	{
		throw std::invalid_argument("Invalid string in Board constructor.");
		m_hash = -1;
	}

	int strIndex = 0;
	for (int i = 0; i < DIM; i++)
		for (int j = 0; j < DIM; j++)
			m_board[i][j] = str[strIndex++] - '0';
	// m_hash = calcHash();
}

Board::Board(const Board& b)
{
	*this = b;
}

Board& Board::operator=(const Board& rhs)
{
	if (this == &rhs) // self assignment check
		return *this;

	std::memcpy(m_board, rhs.m_board, DIM * DIM * sizeof(int));
	// for (int i = 0; i < DIM; i++)
	// 	for (int j = 0; j < DIM; j++)
	// 		m_board[i][j] = rhs.m_board[i][j];

	m_hash = rhs.m_hash;
	m_hasHash = rhs.m_hasHash;

	return *this;
}

std::unique_ptr<ILayout> Board::copy() const
{
	return std::make_unique<Board>(*this);
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

	return std::memcmp(m_board, other.m_board, DIM * DIM * sizeof(int)) == 0;
	// for (int i = 0; i < DIM; i++)
	// 	for (int j = 0; j < DIM; j++)
	// 		if (m_board[i][j] != other.m_board[i][j])
	// 			return false;
	return true;
}

size_t Board::calcHash() const
{
	// std::stringstream ss;
	// ss << *this;
	// size_t hash = std::hash<std::string>()(ss.str());

	std::size_t hash = 0;
	for (int i = 0; i < DIM; i++)
		for (int j = 0; j < DIM; j++)
			hash = hash * 97 + std::hash<int>()(m_board[i][j]);
	return hash;
}

size_t Board::hash() const
{
	if (!m_hasHash)
	{
		m_hash = calcHash();
		m_hasHash = true;
	}
	return m_hash;
	// static std::chrono::nanoseconds total(0);
	//
	// auto start = std::chrono::high_resolution_clock::now();
	//
	// std::stringstream ss;
	// ss << *this;
	// size_t hash = std::hash<std::string>()(ss.str());
	//
	// auto end = std::chrono::high_resolution_clock::now();
	// total += std::chrono::duration_cast<std::chrono::nanoseconds>(end - start);
	// std::cout << ": " << std::chrono::duration_cast<std::chrono::milliseconds>(total).count() << "ms\n";
	// return hash;
}

void Board::findZero(int& zeroRow, int& zeroColumn) const
{
	for (int i = 0; i < DIM; i++)
	{
		for (int j = 0; j < DIM; j++)
		{
			if (m_board[i][j] == 0)
			{
				zeroRow = i;
				zeroColumn = j;
				return;
			}
		}
	}
}

std::vector<std::unique_ptr<ILayout>> Board::children() const
{
	int zeroRow = -1;
	int zeroColumn = -1;
	findZero(zeroRow, zeroColumn);
	if (zeroRow < 0 || zeroColumn < 0)
		throw std::runtime_error("Zero was not found.");

	return children(zeroRow, zeroColumn);
}

std::vector<std::unique_ptr<ILayout>> Board::children(int row, int column) const
{
	std::vector<std::unique_ptr<ILayout>> children;
	children.reserve(4);

	if (row > 0)
		children.emplace_back(moveUp(row, column));
	if (row < DIM - 1)
		children.emplace_back(moveDown(row, column));
	if (column > 0)
		children.emplace_back(moveLeft(row, column));
	if (column < DIM - 1)
		children.emplace_back(moveRight(row, column));

	return children;
}

std::unique_ptr<Board> Board::moveUp(int row, int column) const
{
	// timing
	// static std::chrono::nanoseconds time_total(0);
	// auto start_time = std::chrono::high_resolution_clock::now();
	// timing

	if (row <= 0)
		throw std::invalid_argument("Cannot move up from row 0.");

	std::unique_ptr result = std::make_unique<Board>(*this);

	int tmp = result->m_board[row][column];
	result->m_board[row][column] = result->m_board[row - 1][column];
	result->m_board[row - 1][column] = tmp;

	result->m_hasHash = false;
	// result->m_hash = result->calcHash();

	// timing
	// auto end_time = std::chrono::high_resolution_clock::now();
	// time_total += std::chrono::duration_cast<std::chrono::nanoseconds>(end_time - start_time);
	// std::cout << "children: " << std::chrono::duration_cast<std::chrono::milliseconds>(time_total).count() << "ms\n";
	// timing

	return result;
}

std::unique_ptr<Board> Board::moveDown(int row, int column) const
{
	if (row >= DIM - 1)
		throw std::invalid_argument("Cannot move down from row " + std::to_string(DIM - 1) + ".");

	std::unique_ptr result = std::make_unique<Board>(*this);

	int tmp = result->m_board[row][column];
	result->m_board[row][column] = result->m_board[row + 1][column];
	result->m_board[row + 1][column] = tmp;

	result->m_hasHash = false;

	// result->m_hash = result->calcHash();

	return result;
}

std::unique_ptr<Board> Board::moveLeft(int row, int column) const
{
	if (column <= 0)
		throw std::invalid_argument("Cannot move left from column 0.");

	std::unique_ptr result = std::make_unique<Board>(*this);

	int tmp = result->m_board[row][column];
	result->m_board[row][column] = result->m_board[row][column - 1];
	result->m_board[row][column - 1] = tmp;

	result->m_hasHash = false;

	// result->m_hash = result->calcHash();

	return result;
}

std::unique_ptr<Board> Board::moveRight(int row, int column) const
{
	if (column >= DIM - 1)
		throw std::invalid_argument("Cannot move right from column " + std::to_string(DIM - 1) + ".");

	std::unique_ptr result = std::make_unique<Board>(*this);

	int tmp = result->m_board[row][column];
	result->m_board[row][column] = result->m_board[row][column + 1];
	result->m_board[row][column + 1] = tmp;

	result->m_hasHash = false;

	// result->m_hash = result->calcHash();

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
