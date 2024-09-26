#include "doctest.hpp"
#include "ContainerLayout.hpp"
#include <string>
#include <sstream>

TEST_CASE("Test constructor")
{
	// Arrange
	std::string expected = 	"[A, C]\n"
							"[B]\n";

	// Act
	ContainerLayout b("A1C1 B2");
	std::stringstream ss;
	ss << b;
	std::string bStr = ss.str();

	// Assert
	CHECK(bStr == expected);
}

TEST_CASE("Test constructor 2")
{
	// Arrange
	std::string expected = 	"[B]\n"
							"[C, A]\n"
							"[D]\n"
							"[E, F, G]\n"
							"[H, I, J, K, L, M, N, O, P]\n";

	// Act
	ContainerLayout b("C1A1 B2 D9 E14F9927G1 H1I1J1K1L1M1N1O1P1");
	std::stringstream ss;
	ss << b;
	std::string bStr = ss.str();

	// Assert
	CHECK(bStr == expected);
}

TEST_CASE("Test constructor 3")
{
	// Arrange
	std::string expected = 	"[A, C]\n"
							"[B]\n";

	// Act
	ContainerLayout b("AC B");
	std::stringstream ss;
	ss << b;
	std::string bStr = ss.str();

	// Assert
	CHECK(bStr == expected);
}

TEST_CASE("Should throw when constructing empty string")
{
	// Arrange
	bool threw = false;

	// Act
	try
	{
		ContainerLayout cs("");
	}
	catch (std::exception& e)
	{
		threw = true;
	}

	// Assert
	CHECK(threw);
}

TEST_CASE("Should throw when invalid cost")
{
	// Arrange
	bool threw = false;

	// Act
	try
	{
		ContainerLayout cs("A1 B-3");
	}
	catch (std::exception& e)
	{
		threw = true;
	}

	// Assert
	CHECK(threw);
}

TEST_CASE("Should throw when duplicate container")
{
	// Arrange
	bool threw = false;

	// Act
	try
	{
		ContainerLayout cs("A1 A2");
	}
	catch (std::exception& e)
	{
		threw = true;
	}

	// Assert
	CHECK(threw);
}

TEST_CASE("Should throw when mixing cost with no cost (1)")
{
	// Arrange
	bool threw = false;

	// Act
	try
	{
		ContainerLayout cs("A B1C2");
	}
	catch (std::exception& e)
	{
		threw = true;
	}

	// Assert
	CHECK(threw);
}

TEST_CASE("Should throw when mixing cost with cost (2)")
{
	// Arrange
	bool threw = false;

	// Act
	try
	{
		ContainerLayout cs("A1 CB1");
	}
	catch (std::exception& e)
	{
		threw = true;
	}

	// Assert
	CHECK(threw);
}

TEST_CASE("Should throw when mixing cost with cost (3)")
{
	// Arrange
	bool threw = false;

	// Act
	try
	{
		ContainerLayout cs("A1 B C1");
	}
	catch (std::exception& e)
	{
		threw = true;
	}

	// Assert
	CHECK(threw);
}

TEST_CASE("Get cost should return cost")
{
	// Arrange
	ContainerLayout cs("B1A1 E14F9927");
	std::vector<std::unique_ptr<ILayout>> children = cs.children();
	double expected0 = 0.0;
	double expected1 = 1.0;
	double expected2 = 9927.0;

	// Act
	double cost0 = cs.getCost();

	// Assert
	CHECK(expected0 == cost0);
	int count = 0;
	for (std::unique_ptr<ILayout>& child : children)
	{
		if (count++ <= 1)
			CHECK(expected1 == child->getCost());
		else
			CHECK(expected2 == child->getCost());
	}
}

TEST_CASE("getCost should throw when no cost is given")
{
	// Arrange
	ContainerLayout cs("BA EF C");
	bool threw = false;

	// Act
	try
	{
		cs.getCost();
	}
	catch (std::exception& e)
	{
		threw = true;
	}

	// Assert
	CHECK(threw);
}

TEST_CASE("Should equals")
{
	// Arrange
	ContainerLayout b0("B1A1 E14F9927");
	ContainerLayout b1("B1A1 E14F9927");
	ContainerLayout b2("F3 E14B4");
	ContainerLayout b3("F5 E4B7");
	ContainerLayout b4("A1 B1 C1 D1 E1 F3");
	ContainerLayout b5("B1 E1 D1 A1 F3 C1");
	
	// Act
	bool simmetry0 = b0 == b1 && b1 == b0;
	bool simmetry1 = b2 == b3 && b3 == b2;
	bool simmetry2 = b4 == b5 && b5 == b4;

	// Arrange
	CHECK(simmetry0);
	CHECK(simmetry1);
	CHECK(simmetry2);
}

TEST_CASE("Should equals (no cost)")
{
	// Arrange
	ContainerLayout b0("F3 E14B4");
	ContainerLayout b1("F EB");
	ContainerLayout b2("A1 B1 C1 D1 E1 F3");
	ContainerLayout b3("F EB");
	
	// Act
	bool simmetry0 = b0 == b1 && b1 == b0;
	bool simmetry1 = b0 == b2 && b2 == b0;
	bool simmetry2 = b1 == b2 && b2 == b1;
	bool simmetry3 = b1 == b3 && b3 == b1;

	// Arrange
	CHECK(simmetry0);
	CHECK(!simmetry1);
	CHECK(!simmetry2);
	CHECK(simmetry3);
}

TEST_CASE("Should not equals")
{
	// Arrange
	ContainerLayout b0("B1A1 E14F9927");
	ContainerLayout b1("F3 E14B4");
	
	// Act
	bool simmetry0 = b0 == b1 || b1 == b0;

	// Arrange
	CHECK(!simmetry0);
}

TEST_CASE("Should have hash properties")
{
	// Arrange
	ContainerLayout b0("B1A1 E14F9927");
	ContainerLayout b1("B1A1 E14F9927");
	ContainerLayout b2("F3 E14B4");
	ContainerLayout b3("F3 E14B4");
	ContainerLayout b4("F5 E4B33");
	ContainerLayout b5("F EB");
	ContainerLayout b6("EB F");
	ContainerLayout b7("FE B");
	
	// Act
	bool simmetry0 = b0 == b1 && b1 == b0;
	bool simmetry1 = b2 == b3 && b3 == b2;
	bool simmetry2 = b3 == b4 && b4 == b3;
	bool simmetry3 = b3 == b5 && b5 == b3;
	bool simmetry4 = b3 == b6 && b6 == b3;
	bool simmetry5 = b5 == b7 && b7 == b5;
	size_t hash0 = b0.hash();
	size_t hash1 = b1.hash();
	size_t hash2 = b2.hash();
	size_t hash3 = b3.hash();
	size_t hash4 = b4.hash();
	size_t hash5 = b5.hash();
	size_t hash6 = b6.hash();
	size_t hash7 = b7.hash();

	// Arrange
	CHECK(simmetry0);
	CHECK(simmetry1);
	CHECK(simmetry2);
	CHECK(simmetry3);
	CHECK(simmetry4);
	CHECK(!simmetry5);

	CHECK(hash0 == hash1);
	CHECK(hash2 == hash3);
	CHECK(hash0 != hash2);
	CHECK(hash3 == hash4);
	CHECK(hash3 == hash5);
	CHECK(hash3 == hash6);
	CHECK(hash5 != hash7);
}

TEST_CASE("Should be goal when equals")
{
	// Arrange
	ContainerLayout b0("B1A1 E14F9927");
	ContainerLayout b1("B1A1 E14F9927");
	ContainerLayout b2("F3 E14B4");
	ContainerLayout b3("F EB");
	
	// Act
	bool isGoal0 = b0.isGoal(b1);
	bool isGoal1 = b1.isGoal(b2);
	bool isGoal2 = b2.isGoal(b3);

	// Arrange
	CHECK(isGoal0);
	CHECK(!isGoal1);
	CHECK(isGoal2);
}

TEST_CASE("Should get one child, same as parent")
{
	// Arrange
	ContainerLayout cs("A1");

	// Act
	std::vector<std::unique_ptr<ILayout>> children = cs.children();

	// Assert
	CHECK(children.empty());
}

bool test_find(const std::vector<std::unique_ptr<ILayout>>& vect, const ILayout& expected)
{
	for (const std::unique_ptr<ILayout>& unit : vect)
		if (*unit == expected)
			return true;
	return false;
}

TEST_CASE("Shuld get 4 children")
{
	// Arrange
	ContainerLayout cs("B1A1 E14F9927");
	ContainerLayout expectedChild0("A1 B1 E14F9927");
	ContainerLayout expectedChild1("B1 E14F9927A1");
	ContainerLayout expectedChild2("F9927 B1A1 E14");
	ContainerLayout expectedChild3("B1A1F9927 E14");

	// Act
	std::vector<std::unique_ptr<ILayout>> children = cs.children();

	// Arrange
	CHECK(children.size() == 4);
	CHECK(test_find(children, expectedChild0));
	CHECK(test_find(children, expectedChild1));
	CHECK(test_find(children, expectedChild2));
	CHECK(test_find(children, expectedChild3));
}

TEST_CASE("Should get 8 children")
{
	// Arrange
	ContainerLayout cs("B1A1 E14F9927 C7");

	ContainerLayout expectedChild0("A1 B1 E14F9927 C7");
	ContainerLayout expectedChild1("B1 E14F9927A1 C7");
	ContainerLayout expectedChild2("B1 E14F9927 C7A1");

	ContainerLayout expectedChild3("F9927 B1A1 E14 C7");
	ContainerLayout expectedChild4("B1A1F9927 E14 C7");
	ContainerLayout expectedChild5("B1A1 E14 C7F9927");

	ContainerLayout expectedChild6("B1A1C7 E14F9927");
	ContainerLayout expectedChild7("B1A1 E14F9927C7");

	// Act
	std::vector<std::unique_ptr<ILayout>> children = cs.children();

	// Arrange
	CHECK(children.size() == 8);
	CHECK(test_find(children, expectedChild0));
	CHECK(test_find(children, expectedChild1));
	CHECK(test_find(children, expectedChild2));
	CHECK(test_find(children, expectedChild3));
	CHECK(test_find(children, expectedChild4));
	CHECK(test_find(children, expectedChild5));
	CHECK(test_find(children, expectedChild6));
	CHECK(test_find(children, expectedChild7));
}
