#pragma once
#include <string>
#include "ILayout.hpp"

class Board : public ILayout
{
public:
	Board();
	~Board();
	// Board(Board&& b);
	explicit Board(const std::string str);
	Board(const Board& b);
	double getCost() const override;
	bool isGoal(const ILayout& that) const override;
	std::list<std::unique_ptr<ILayout>> children() const override;
	std::unique_ptr<ILayout> copy() const override;
	void print(std::ostream& stream) const override;
	std::size_t hash() const override;
	Board& operator=(const Board& rhs);
	bool operator==(const ILayout& rhs) const override;

private:
	static const int DIM = 3;
	int m_board[DIM][DIM];

	std::list<std::unique_ptr<ILayout>> children(int row, int column) const;
	Board moveUp(int row, int column) const;
	Board moveDown(int row, int column) const;
	Board moveLeft(int row, int column) const;
	Board moveRight(int row, int column) const;
};
