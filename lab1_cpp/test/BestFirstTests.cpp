#include "doctest.hpp"
#include "Board.hpp"
#include "BestFirst.hpp"
#include <iostream>

// TEST_CASE("should solve")
// {
// 	// Arrange
// 	BestFirst bestFirst;
// 	Board initial(	"023"
// 					"145"
// 					"678");
//
// 	Board goal(		"123"
// 					"405"
// 					"678");
//
// 	Board middle(	"123"
// 					"045"
// 					"678");
//
// 	double expectedCost = 2.0;
//
// 	// Act
// 	std::pair<BestFirst::bf_iter, BestFirst::bf_iter> itPair = bestFirst.solve(initial, goal);
// 	BestFirst::bf_iter curr = itPair.first;
// 	// BestFirst::bf_iter end = itPair.second;
//
// 	const BestFirst::State* state0 = *curr++;
// 	const BestFirst::State* state1 = *curr++;
// 	const BestFirst::State* state2 = *curr++;
//
// 	// Assert
// 	CHECK(initial == *dynamic_cast<const Board*>(state0->layout()));
// 	CHECK(middle == *dynamic_cast<const Board*>(state1->layout()));
// 	CHECK(goal == *dynamic_cast<const Board*>(state2->layout()));
// 	CHECK(expectedCost == state2->getCost());
// }

TEST_CASE("should solve 2")
{
	// Arrange
	BestFirst bestFirst;
	Board initial(	"123"
					"456"
					"780");

	Board goal(		"436"
					"718"
					"520");

	double expectedCost = 12.0;

	// Act
	std::pair<BestFirst::bf_iter, BestFirst::bf_iter> itPair = bestFirst.solve(initial, goal);
	// BestFirst::bf_iter curr = itPair.first;
	BestFirst::bf_iter end = itPair.second;

	const BestFirst::State* lastState = *(--end);
	Board last = *dynamic_cast<const Board*>(lastState->layout());

	// Assert
	CHECK(goal == last);
	CHECK(expectedCost == lastState->getCost());
}
