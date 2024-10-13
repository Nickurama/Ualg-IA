#include "BestFirst.hpp"
#include "Board.hpp"
#include "ContainerLayout.hpp"
#include <cmath>
#include <iostream>
#include <chrono>

int main()
{
	BestFirst bf;
	
	std::string startStr, goalStr;
	// std::cin >> startStr >> goalStr;
	// Board start(startStr), goal(goalStr);

	getline(std::cin, startStr);
	getline(std::cin, goalStr);
	ContainerLayout start(startStr), goal(goalStr);

	auto time_start = std::chrono::high_resolution_clock::now();
	std::pair<BestFirst::bf_iter, BestFirst::bf_iter> itPair = bf.solve(start, goal);
	auto time_stop = std::chrono::high_resolution_clock::now();
	auto start_ns = std::chrono::time_point_cast<std::chrono::nanoseconds>(time_start);
	auto stop_ns = std::chrono::time_point_cast<std::chrono::nanoseconds>(time_stop);
	auto total_ns = stop_ns - start_ns;
	double total_ms = (double)total_ns.count() / 1000000;
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

	std::cout << total_ms << "ms" << std::endl;
	return 0;
}
