#include "ContainerLayout.hpp"
#include <cassert>
#include <iostream>
#include <cstring>

ContainerLayout::ContainerLayout(const std::string& str)
	: m_hasHash(false),
	m_hash(-1),
	m_hasCost(shouldHaveCost(str)),
	m_cost(0.0),
	m_topmostContainersIndex()
{
	if (str.empty())
		throw std::invalid_argument("String cannot be empty.");

	std::fill_n(m_containersCost, ContainerLayout::BUCKET_SIZE, -1);
	std::fill_n(m_containerOnTopIndex, ContainerLayout::BUCKET_SIZE, -1);
	std::fill_n(m_containerOnBottomIndex, ContainerLayout::BUCKET_SIZE, -1);
	m_topmostContainersIndex.reserve(BUCKET_SIZE);

	populateFromString(str);
}

ContainerLayout::ContainerLayout(const ContainerLayout& other)
	: m_hasHash(other.m_hasHash),
	m_hash(other.m_hash),
	m_hasCost(other.m_hasCost),
	m_cost(other.m_cost),
	m_topmostContainersIndex()
{
	memcpy(m_containersCost, other.m_containersCost, BUCKET_SIZE * sizeof(int));
	memcpy(m_containerOnTopIndex, other.m_containerOnTopIndex, BUCKET_SIZE * sizeof(int));
	memcpy(m_containerOnBottomIndex, other.m_containerOnBottomIndex, BUCKET_SIZE * sizeof(int));

	for (int topmost : other.m_topmostContainersIndex)
		if (other.isOnTop(topmost))
			m_topmostContainersIndex.push_back(topmost);
}

ContainerLayout::~ContainerLayout() { }

bool ContainerLayout::shouldHaveCost(const std::string& str) const
{
	if (str.size() == 1)
		return false;
	else if (ContainerLayout::isNumerical(str[1]))
		return true;
	return false;
}

void ContainerLayout::populateFromString(const std::string& str)
{
	std::vector<std::string> tokens;
	tokens.reserve(ContainerLayout::BUCKET_SIZE);
	split(str, tokens);
	for (std::string token : tokens)
		populateContainersFromString(token);
}

void ContainerLayout::split(const std::string& str, std::vector<std::string>& tokens)
{
	std::size_t oldPos = 0;
	std::size_t pos = str.find(' ');
	while (pos != std::string::npos)
	{
		tokens.push_back(str.substr(oldPos, pos - oldPos));
		oldPos = pos + 1;
		pos = str.find(' ', oldPos);
	}
	tokens.push_back(str.substr(oldPos, str.size()));
}

void ContainerLayout::populateContainersFromString(const std::string& str)
{
	if (m_hasCost)
		populateContainersFromStringWithCost(str);
	else
		populateContainersFromStringWithoutCost(str);
}

void ContainerLayout::populateContainersFromStringWithCost(const std::string& str)
{
	addNextContainer(str, 0, -1);
}

void ContainerLayout::addNextContainer(const std::string& str, std::size_t i, std::size_t previous)
{
	if (i >= str.size())
		return;

	validateContainer(str, i);
	int key = getKey(str[i]);

	int cost = 0;
	i++;
	int next = -1;
	for (; i < str.length(); i++)
	{
		if (!isNumerical(str[i]))
		{
			if (!isAlphabetical(str[i]))
				throw std::invalid_argument("Invalid container stack.");
			next = (char)getKey(str[i]);
			break;
		}

		cost *= 10;
		cost += str[i] - '0';
	}

	int nextPrevious = addContainer(key, cost, next, previous);
	addNextContainer(str, i, nextPrevious);
}

void ContainerLayout::validateContainer(const std::string& str, std::size_t i) const
{
	if (!isAlphabetical(str[i]))
		throw std::invalid_argument("Expected alphabetic character.");
	if ((str.size() - i) < 2 || !isNumerical(str[i + 1]))
		throw std::invalid_argument("Expected numeric character.");
}

// returns the next container's "bottom" container
int ContainerLayout::addContainer(int key, int cost, int next, int previous)
{
	if (contains(key))
		throw std::invalid_argument("Duplicate container.");

	if (previous >= 0)
		m_containerOnBottomIndex[key] = previous;

	int nextPrevious = -1;
	if (next < 0)
	{
		m_topmostContainersIndex.push_back(key);
	}
	else
	{
		nextPrevious = key;
		m_containerOnTopIndex[key] = next;
	}
	m_containersCost[key] = cost;
	return nextPrevious;
}

void ContainerLayout::populateContainersFromStringWithoutCost(const std::string& str)
{
	for (int i = 0; i < (int)str.size(); i++)
	{
		if (!isAlphabetical(str[i]))
			throw std::invalid_argument("Expected alphabetic character.");

		int key = getKey(str[i]);
		if (i + 1 < (int)str.size())
			m_containerOnTopIndex[key] = getKey(str[i + 1]);
		else
			m_topmostContainersIndex.push_back(key);
		if (i - 1 >= 0)
			m_containerOnBottomIndex[key] = getKey(str[i - 1]);
		if (contains(key))
			throw std::invalid_argument("Duplicate container.");
		m_containersCost[key] = 0;
	}
}

inline bool ContainerLayout::contains(char c) const { return contains(getKey(c)); }
inline bool ContainerLayout::contains(int i) const

{
	return m_containersCost[i] >= 0;
}

inline bool ContainerLayout::hasContainerOnTop(char c) const { return hasContainerOnTop(getKey(c)); }
inline bool ContainerLayout::hasContainerOnTop(int i) const

{
	return m_containerOnTopIndex[i] >= 0;
}

inline bool ContainerLayout::hasContainerOnBottom(char c) const { return hasContainerOnBottom(getKey(c)); }
inline bool ContainerLayout::hasContainerOnBottom(int i) const

{
	return m_containerOnBottomIndex[i] >= 0;
}

inline bool ContainerLayout::isOnBottom(char c) const { return isOnBottom(getKey(c)); }
inline bool ContainerLayout::isOnBottom(int i) const

{
	return !hasContainerOnBottom(i);
}

inline bool ContainerLayout::isOnTop(char c) const { return isOnTop(getKey(c)); }
inline bool ContainerLayout::isOnTop(int i) const
{
	return !hasContainerOnTop(i);
}

inline int ContainerLayout::getContainerOnTopIndex(char c) const { return getContainerOnTopIndex(getKey(c)); }
inline int ContainerLayout::getContainerOnTopIndex(int i) const
{
	return m_containerOnTopIndex[i];
}

inline int ContainerLayout::getContainerOnBottomIndex(char c) const { return getContainerOnBottomIndex(getKey(c)); }
inline int ContainerLayout::getContainerOnBottomIndex(int i) const
{
	return m_containerOnBottomIndex[i];
}

inline int ContainerLayout::getKey(char c)
{
	return c - 'A';
}
inline char ContainerLayout::getCharFromKey(int i)
{
	return i + 'A';
}

inline bool ContainerLayout::isNumerical(char c)
{
	return c >= '0' && c <= '9';
}

inline bool ContainerLayout::isAlphabetical(char c)
{
	return c >= 'A' && c <= 'Z';
}

void ContainerLayout::print(std::ostream& stream) const
{
	for (int i = 0; i < BUCKET_SIZE; i++)
	{
		if (!isOnBottom(i) || !contains(i))
			continue;

		stream << "[" << getCharFromKey(i);
		int next = getContainerOnTopIndex(i);
		while (next >= 0)
		{
			stream << ", " << getCharFromKey(next);
			next = m_containerOnTopIndex[next];
		}
		stream << "]\n";
	}
}

std::vector<std::unique_ptr<ILayout>> ContainerLayout::children() const
{
	if (!m_hasCost)
		throw std::invalid_argument("This layout doesn't support cost.");

	std::vector<std::unique_ptr<ILayout>> children;

	for (int topmost : m_topmostContainersIndex)
	{
		if (!isOnTop(topmost))
			continue;

		if (!isOnBottom(topmost))
			children.push_back(moveContainerGround(topmost));
		for (int innerTopmost : m_topmostContainersIndex)
		{
			if (!isOnTop(innerTopmost))
				continue;
			if (topmost == innerTopmost)
				continue;

			children.push_back(moveContainer(topmost, innerTopmost));
		}
	}
	return children;
}

std::unique_ptr<ContainerLayout> ContainerLayout::moveContainerGround(int from) const
{
	return moveContainer(from, -1);
}

// from/to mustu be top, can also be mutually bottom
// to can be -1 to place on ground
std::unique_ptr<ContainerLayout> ContainerLayout::moveContainer(int from, int to) const
{
	// must be top
	std::unique_ptr<ContainerLayout> moved = std::make_unique<ContainerLayout>(*this);
	moved->m_cost = m_containersCost[from];
	if (isOnBottom(from))
	{
		// to will never be negative when this function is called correctly from
		// children(), because if the current container is on the ground, it means
		// it can only be placed TO the top of a container (thus not -1)
		assert(to >= 0);
		moved->m_containerOnBottomIndex[from] = to;
		moved->m_containerOnTopIndex[to] = from;
	}
	else
	{
		int belowFrom = getContainerOnBottomIndex(from);
		moved->m_containerOnTopIndex[belowFrom] = -1;
		moved->m_topmostContainersIndex.push_back(belowFrom);

		moved->m_containerOnBottomIndex[from] = to; // -1 if ground
		if (to >= 0) // place on top of container
			moved->m_containerOnTopIndex[to] = from;
	}
	return moved;
}

bool ContainerLayout::isGoal(const ILayout& that) const
{
	return *this == that;
}

double ContainerLayout::getCost() const
{
	if (!m_hasCost)
		throw std::logic_error("This layout doesn't support cost.");
	return m_cost;
}

std::unique_ptr<ILayout> ContainerLayout::copy() const
{
	return std::make_unique<ContainerLayout>(*this);
}

std::size_t ContainerLayout::hash() const
{
	if (m_hasHash)
		return m_hash;

	m_hasHash = true;
	m_hash = 0;
	for (int i = 0; i < BUCKET_SIZE; i++)
		if (contains(i))
			m_hash = m_hash * 97 + i + 1;
	return m_hash;
}

bool ContainerLayout::operator==(const ILayout& rhs) const
{
	if (this == &rhs) return true;
	const ContainerLayout* other = dynamic_cast<const ContainerLayout*>(&rhs);
	if (other == nullptr)
		return false;

	if (!(std::memcmp(m_containerOnTopIndex, other->m_containerOnTopIndex, BUCKET_SIZE * sizeof(int)) == 0))
		return false;

	if (!(std::memcmp(m_containerOnBottomIndex, other->m_containerOnBottomIndex, BUCKET_SIZE * sizeof(int)) == 0))
		return false;
	for (int i = 0; i < BUCKET_SIZE; i++)
	{
		if (this->contains(i) && !other->contains(i))
			return false;
		if (!this->contains(i) && other->contains(i))
			return false;
	}
	return true;
}
