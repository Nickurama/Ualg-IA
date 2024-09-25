#include "BestFirst.hpp"
#include "Board.hpp"
#include <cmath>
#include <iostream>

int main()
{
	BestFirst bf;
	
	std::string startStr, goalStr;
	std::cin >> startStr >> goalStr;
	Board start(startStr), goal(goalStr);

	std::pair<BestFirst::bf_iter, BestFirst::bf_iter> itPair = bf.solve(start, goal);
	BestFirst::bf_iter startIt = itPair.first;
	BestFirst::bf_iter endIt = itPair.second;

	if (startIt == endIt)
	{
		std::cout << "no solution found" << std::endl;
		return 0;
	}

	for (BestFirst::bf_iter it = startIt; it != endIt; it++)
		std::cout << **it << '\n';
	const BestFirst::State *lastState = *(--endIt);
	std::cout << static_cast<int>(std::round(lastState->getCost())) << std::endl;

	return 0;
}
