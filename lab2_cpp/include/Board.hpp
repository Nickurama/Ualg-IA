#pragma once
#include <string>
#include "ILayout.hpp"

class Board : public ILayout
{
public:
	Board();
	~Board();
	// Board(Board&& b);
	explicit Board(const std::string& str);
	Board(const Board& b);
	double getCost() const override;
	bool isGoal(const ILayout& that) const override;
	std::vector<std::unique_ptr<ILayout>> children() const override;
	std::unique_ptr<ILayout> copy() const override;
	void print(std::ostream& stream) const override;
	std::size_t hash() const override;
	Board& operator=(const Board& rhs);
	bool operator==(const ILayout& rhs) const override;

private:
	static const int DIM = 3;
	int m_board[DIM][DIM];
	mutable size_t m_hash;
	mutable bool m_hasHash;

	std::vector<std::unique_ptr<ILayout>> children(int row, int column) const;
	void findZero(int& zeroRow, int& zeroColumn) const;
	size_t calcHash() const;
	std::unique_ptr<Board> moveUp(int row, int column) const;
	std::unique_ptr<Board> moveDown(int row, int column) const;
	std::unique_ptr<Board> moveLeft(int row, int column) const;
	std::unique_ptr<Board> moveRight(int row, int column) const;
};
