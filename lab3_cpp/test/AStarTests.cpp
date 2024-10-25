#include "doctest.hpp"
#include "Board.hpp"
#include "AStar.hpp"
#include "ContainerLayout.hpp"
#include <iostream>

TEST_CASE("Should solve container layout")
{
	// Arrange
	AStar A;
	ContainerLayout initial("A1C1 "
							"B1");

	ContainerLayout goal(	"A "
							"B "
							"C");

	double expectedCost = 1.0;

	// Act
	std::pair<AStar::bf_iter, AStar::bf_iter> itPair = A.solve(initial, goal);
	AStar::bf_iter end = itPair.second;

	const AStar::State* lastState = *(--end);
	ContainerLayout last = *dynamic_cast<const ContainerLayout*>(lastState->layout());

	// Assert
	CHECK(goal == last);
	CHECK(expectedCost == lastState->g());
}

TEST_CASE("Should solve container layout when initial is the same as goal")
{
	// Arrange
	AStar A;
	ContainerLayout initial("A1B1C1");

	ContainerLayout goal("A1B1C1");

	double expectedCost = 0.0;

	// Act
	std::pair<AStar::bf_iter, AStar::bf_iter> itPair = A.solve(initial, goal);
	AStar::bf_iter end = itPair.second;

	const AStar::State* lastState = *(--end);
	ContainerLayout last = *dynamic_cast<const ContainerLayout*>(lastState->layout());

	// Assert
	CHECK(goal == last);
	CHECK(expectedCost == lastState->g());
}

TEST_CASE("Should solve container layout 2")
{
	// Arrange
	AStar A;
	ContainerLayout initial("A1B1 "
							"C3D1");

	ContainerLayout goal(	"DA "
							"BC");

	ContainerLayout expected1(	"A "
								"B "
								"CD");

	ContainerLayout expected2(	"A "
								"B "
								"C "
								"D");

	ContainerLayout expected3(	"B "
								"C "
								"DA");

	double expectedCost = 6.0;

	// Act
	std::pair<AStar::bf_iter, AStar::bf_iter> itPair = A.solve(initial, goal);
	AStar::bf_iter curr = itPair.first;
	AStar::bf_iter end = itPair.second;

	const AStar::State* lastState = *(--end);
	ContainerLayout last = *dynamic_cast<const ContainerLayout*>(lastState->layout());

	// Assert
	CHECK(*dynamic_cast<const ContainerLayout*>((*curr++)->layout()) == initial);
	CHECK(*dynamic_cast<const ContainerLayout*>((*curr++)->layout()) == expected1);
	CHECK(*dynamic_cast<const ContainerLayout*>((*curr++)->layout()) == expected2);
	CHECK(*dynamic_cast<const ContainerLayout*>((*curr++)->layout()) == expected3);
	CHECK(*dynamic_cast<const ContainerLayout*>((*curr++)->layout()) == goal);
	CHECK(goal == last);
	CHECK(expectedCost == lastState->g());
}

TEST_CASE("Should work with lowercase ContainerLayout")
{
	// Arrange
	AStar A;
	ContainerLayout initial("a1B7 c3d2 A14");
	ContainerLayout goal("aBA dc");

	double expectedCost = 19.0;

	// Act
	std::pair<AStar::bf_iter, AStar::bf_iter> itPair = A.solve(initial, goal);
	AStar::bf_iter end = itPair.second;
	const AStar::State* lastState = *(--end);
	ContainerLayout last = *dynamic_cast<const ContainerLayout*>(lastState->layout());

	// Assert
	CHECK(goal == last);
	CHECK(expectedCost == lastState->g());
}

TEST_CASE("Should be efficient with reversed containers")
{
	// Arrange
	AStar A;
	ContainerLayout initial("O6l3p5m1x2E9 M5u7 e6 o2t1s6I7 a2 A2");
	ContainerLayout goal("Isto e uM ExmplO a A");

	double expectedCost = 54.0;

	// Act
	std::pair<AStar::bf_iter, AStar::bf_iter> itPair = A.solve(initial, goal);
	AStar::bf_iter end = itPair.second;
	const AStar::State* lastState = *(--end);
	ContainerLayout last = *dynamic_cast<const ContainerLayout*>(lastState->layout());

	// Assert
	CHECK(goal == last);
	CHECK(expectedCost == lastState->g());
}

TEST_CASE("mooshak5")
{
	// Arrange
	AStar A;
	ContainerLayout initial("A9I8Q7Y6 B5J4R3Z2 C1K2S3 D4L5T6 E7M8U9 F1N2V3 G1O2W3 H3P2X1");
	ContainerLayout goal("YZXW OQSTUV NMLKRI AJCDEF G B HP");

	double expectedCost = 87.0;

	// Act
	std::pair<AStar::bf_iter, AStar::bf_iter> itPair = A.solve(initial, goal);
	AStar::bf_iter end = itPair.second;
	const AStar::State* lastState = *(--end);
	ContainerLayout last = *dynamic_cast<const ContainerLayout*>(lastState->layout());

	// Assert
	CHECK(goal == last);
	CHECK(expectedCost == lastState->g());
}

TEST_CASE("mooshak6")
{
	// Arrange
	AStar A;
	ContainerLayout initial("B9b5A2a7 E3 e3 G1H5L9 M8m4N3O7o5 S3s1T5 R1 V9C4c5");
	ContainerLayout goal("BOM TRAbaLH VCS coNsEGem");

	double expectedCost = 84.0;

	// Act
	std::pair<AStar::bf_iter, AStar::bf_iter> itPair = A.solve(initial, goal);
	AStar::bf_iter end = itPair.second;
	const AStar::State* lastState = *(--end);
	ContainerLayout last = *dynamic_cast<const ContainerLayout*>(lastState->layout());

	// Assert
	CHECK(goal == last);
	CHECK(expectedCost == lastState->g());
}

TEST_CASE("mooshak7")
{
	// Arrange
	AStar A;
	ContainerLayout initial("A1B1C1D1E1F1G1H1I1J1K1L1M1N1O1P1Q1R1S1T1U1V1W1X1Y1Z1a1b1c1d1e1f1g1h1i1j1k1l1m1n1o1p1q1r1s1t1u1v1w1x1y1z1");
	ContainerLayout goal("zyxwvutsrqEDCBA cab edQPONMLKJI ZYXWVUTSRH gfGF kjih ponml");

	double expectedCost = 53.0;

	// Act
	std::pair<AStar::bf_iter, AStar::bf_iter> itPair = A.solve(initial, goal);
	AStar::bf_iter end = itPair.second;
	const AStar::State* lastState = *(--end);
	ContainerLayout last = *dynamic_cast<const ContainerLayout*>(lastState->layout());

	// Assert
	CHECK(goal == last);
	CHECK(expectedCost == lastState->g());
}

TEST_CASE("mooshak9")
{
	// Arrange
	AStar A;
	ContainerLayout initial("A8B7C6D5E4F3G2H1I9J8K7L6M5N4O3P2Q1R2S3T4U5V6W7X8Y9Z1");
	ContainerLayout goal("ZYXWVU MNOPQRS LKJIHGF ABCDET");

	double expectedCost = 115.0;

	// Act
	std::pair<AStar::bf_iter, AStar::bf_iter> itPair = A.solve(initial, goal);
	AStar::bf_iter end = itPair.second;
	const AStar::State* lastState = *(--end);
	ContainerLayout last = *dynamic_cast<const ContainerLayout*>(lastState->layout());

	// Assert
	CHECK(goal == last);
	CHECK(expectedCost == lastState->g());
}
