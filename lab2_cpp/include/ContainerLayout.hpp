#pragma once
#include "ILayout.hpp"

class ContainerLayout : public ILayout
{
	public:
		ContainerLayout(std::string str);
		~ContainerLayout();
		std::vector<std::unique_ptr<ILayout>> children() const override;
		bool isGoal(const ILayout& layout) const override;
		double getCost() const override;
		void print(std::ostream& stream) const override;
		std::unique_ptr<ILayout> copy() const override;
		std::size_t hash() const override;
		bool operator==(const ILayout& rhs) const override;

	private:
};
