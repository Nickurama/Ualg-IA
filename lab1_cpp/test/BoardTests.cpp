#include "doctest.hpp"
#include "Board.hpp"
#include <sstream>
#include <algorithm>

TEST_CASE("testConstructor")
{
	// Arrange
	std::string expected = 	" 23\n"
							"145\n"
							"678\n";

	// Act
	Board b("023145678");
	std::stringstream ss;
	ss << b;
	std::string bStr = ss.str();

	// Assert
	CHECK(bStr == expected);
}

TEST_CASE("testConstructor2")
{
	// Arrange
	std::string expected = 	"123\n"
							"485\n"
							"67 \n";

	// Act
	Board b("123485670");
	std::stringstream ss;
	ss << b;
	std::string bStr = ss.str();

	// Assert
	CHECK(bStr == expected);
}

TEST_CASE("getCost should return 1")
{
	// Arrange
	Board b0("123456780");
	Board b1("087654321");
	double expected = 1.0;

	// Act
	double cost0 = b0.getCost();
	double cost1 = b1.getCost();

	// Assert
	CHECK(cost0 == expected);
	CHECK(cost1 == expected);
}

TEST_CASE("should be equal")
{
	// Arrange
	Board b0("123456780");
	Board b1("123456780");
	Board b2("137245086");
	Board b3("137245086");

	// Act
	bool simmetry0 = (b0 == b1 && b1 == b0);
	bool simmetry1 = (b2 == b3 && b3 == b2);

	// Arrange
	CHECK(simmetry0);
	CHECK(simmetry1);
}

TEST_CASE("should not be equal")
{
	// Arrange
	Board b0("123456780");
	Board b1("137245086");
	Board b2("137245680");

	// Act
	bool simmetry0 = (b0 == b1 || b1 == b0);
	bool simmetry1 = (b0 == b2 || b2 == b0);
	bool simmetry2 = (b1 == b2 || b2 == b1);

	// Arrange
	CHECK(!simmetry0);
	CHECK(!simmetry1);
	CHECK(!simmetry2);
}

TEST_CASE("should have hash properties")
{
	// Arrange
	Board b0("123456780");
	Board b1("123456780");
	Board b2("137245086");
	Board b3("137245086");
	
	// Act
	bool simmetry0 = (b0 == b1 && b1 == b0);
	bool simmetry1 = (b2 == b3 && b3 == b2);
	std::size_t hash0 = b0.hash();
	std::size_t hash1 = b1.hash();
	std::size_t hash2 = b2.hash();
	std::size_t hash3 = b3.hash();

	// Arrange
	CHECK(simmetry0);
	CHECK(simmetry1);
	CHECK(hash0 == hash1);
	CHECK(hash2 == hash3);
	CHECK(hash0 != hash2);
}

TEST_CASE("should be goal when is equal")
{
	// Arrange
	Board b0("123456780");
	Board b1("123456780");
	Board b2("137245086");
	Board b3("137245086");
	
	// Act
	bool isGoal0 = b0.isGoal(b1);
	bool isGoal1 = b1.isGoal(b2);
	bool isGoal2 = b2.isGoal(b3);

	// Arrange
	CHECK(isGoal0);
	CHECK(!isGoal1);
	CHECK(isGoal2);
}

bool test_find(std::vector<std::unique_ptr<ILayout>>::iterator begin, std::vector<std::unique_ptr<ILayout>>::iterator end, const ILayout& expected)
{
	for (std::vector<std::unique_ptr<ILayout>>::iterator it = begin; it != end; it++)
		if (**it == expected)
			return true;
	return false;
}

TEST_CASE("should get 4 children")
{
	// Arrange
	Board b("134208657");
	Board expectedChild0("104238657");
	Board expectedChild1("134280657");
	Board expectedChild2("134028657");
	Board expectedChild3("134258607");

	// Act
	std::vector<std::unique_ptr<ILayout>> children = b.children();

	// Arrange
	CHECK(children.size() == 4);
	CHECK(test_find(children.begin(), children.end(), expectedChild0));
	CHECK(test_find(children.begin(), children.end(), expectedChild1));
	CHECK(test_find(children.begin(), children.end(), expectedChild2));
	CHECK(test_find(children.begin(), children.end(), expectedChild3));
}

TEST_CASE("should get 2 children on bottom left")
{
	// Arrange
	Board b("634218057");
	Board expectedChild0("634018257");
	Board expectedChild1("634218507");

	// Act
	std::vector<std::unique_ptr<ILayout>> children = b.children();

	// Arrange
	CHECK(children.size() == 2);
	CHECK(test_find(children.begin(), children.end(), expectedChild0));
	CHECK(test_find(children.begin(), children.end(), expectedChild1));
}

TEST_CASE("should get 2 children on top right")
{
	// Arrange
	Board b("630218357");
	Board expectedChild0("603218357");
	Board expectedChild1("638210357");

	// Act
	std::vector<std::unique_ptr<ILayout>> children = b.children();

	// Arrange
	CHECK(children.size() == 2);

	CHECK(test_find(children.begin(), children.end(), expectedChild0));
	CHECK(test_find(children.begin(), children.end(), expectedChild1));
}
