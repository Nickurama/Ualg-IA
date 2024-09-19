#pragma once
#include <list>
#include <memory>
#include <vector>

class ILayout
{
public:
	virtual ~ILayout();
	virtual std::vector<std::unique_ptr<ILayout>> children() const = 0;
	virtual bool isGoal(const ILayout& layout) const = 0;
	virtual double getCost() const = 0;
	virtual void print(std::ostream& stream) const = 0;
	virtual std::unique_ptr<ILayout> copy() const = 0;
	bool operator!=(const ILayout& rhs) const;
	virtual bool operator==(const ILayout& rhs) const = 0;
	virtual std::size_t hash() const = 0;
	friend std::ostream& operator<<(std::ostream& os, const ILayout& layout);
};
