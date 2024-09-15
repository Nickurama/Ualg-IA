#include "ILayout.hpp"

ILayout::~ILayout() { }

bool ILayout::operator!=(const ILayout& rhs) const
{
	return !(*this == rhs);
}

std::ostream& operator<<(std::ostream& os, const ILayout& layout)
{
	layout.print(os);
	return os;
}

// bool operator==(const ILayout& lhs, const ILayout& rhs)
// {
// 	return lhs.equals(rhs);
// }
