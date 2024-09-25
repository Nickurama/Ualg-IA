#include "BestFirst.hpp"
#include <iostream>

// accepts null father
BestFirst::State::State(std::unique_ptr<ILayout> layout, const State* father)
	: m_layout(std::move(layout)),
	m_father(father),
	m_cost(0.0)
{
	if (father != nullptr)
		m_cost = father->getCost() + m_layout->getCost();
}

BestFirst::State::State(State&& s)
	: m_layout(std::move(s.m_layout)),
	m_father(s.m_father),
	m_cost(s.m_cost)
{
	s.m_father = nullptr;
}

std::ostream& operator<<(std::ostream& os, const BestFirst::State& state)
{
	return os << *state.layout();
}

const BestFirst::State* BestFirst::State::father() const { return m_father; }

double BestFirst::State::getCost() const { return m_cost; }

bool BestFirst::State::operator==(const State& rhs) const
{
	return m_layout == rhs.m_layout;
}

BestFirst::BestFirst()
	: m_closed(),
	m_open(),
	m_openMap()
{
}

const ILayout* BestFirst::State::layout() const {
	return m_layout.get();
}

std::vector<std::unique_ptr<BestFirst::State>> BestFirst::sucessors(const BestFirst::State& state)
{
	std::vector<std::unique_ptr<ILayout>> children = state.layout()->children();

	std::vector<std::unique_ptr<State>> sucessors;
	sucessors.reserve(10);
	for (std::unique_ptr<ILayout>& child : children)
		if (state.father() == nullptr || *child != *state.father()->layout())
			sucessors.emplace_back(std::make_unique<State>(std::move(child), &state));

	return sucessors;
}

std::pair<BestFirst::bf_iter, BestFirst::bf_iter> BestFirst::solve(const ILayout& start, const ILayout& goal)
{
	std::shared_ptr<State> first(std::make_shared<State>(start.copy(), nullptr));
	m_open.push(first);
	m_openMap[first->layout()] = first;
	std::vector<std::unique_ptr<State>> sucessors;

	// int count = 0;

	while (!m_open.empty())
	{
		const std::shared_ptr<State>& curr = m_open.top(); // should be m_current

		if (*curr->layout() == goal)
		{
			// solution found
			const State* tmp = curr.get(); // dangerous
			m_solutionList.emplace_back(tmp);
			while (tmp->father() != nullptr)
			{
				m_solutionList.emplace_back(tmp->father());
				tmp = tmp->father();
			}
			m_solutionList.reverse();
			return std::pair<BestFirst::bf_iter, BestFirst::bf_iter>(m_solutionList.begin(), m_solutionList.end());
		}
		// solution not found

		// WARNING: very dangerous cast, but std::priority_queue does not allow
		// moving out of the queue. Do NOT use either the queue or attempt to
		// modify the variable from the old pointer after this.
		sucessors = BestFirst::sucessors(*curr);
		m_openMap.erase(curr->layout());
		m_closed[curr->layout()] = std::move(const_cast<std::shared_ptr<State>&>(curr));
		// WARNING: CANNOT USE CURR AFTER HERE
		m_open.pop();

		for (std::unique_ptr<State>& sucessor : sucessors)
		{
			if (m_closed.find(sucessor->layout()) == m_closed.end() &&
				m_openMap.find(sucessor->layout()) == m_openMap.end())
			{
				std::shared_ptr<State> sharedSucc = std::move(sucessor);
				m_openMap[sharedSucc->layout()] = sharedSucc;
				m_open.push(sharedSucc);
			}
		}
	}
	return std::pair<BestFirst::bf_iter, BestFirst::bf_iter>(nullptr, nullptr);
}

bool BestFirst::QueueCmp::operator()(const std::shared_ptr<State>& x, const std::shared_ptr<State>& y) const
{
	return x->getCost() > y->getCost();
}

std::size_t BestFirst::MapHash::operator()(const ILayout* ptr) const
{
	return ptr->hash();
}

bool BestFirst::MapEqual::operator()(const ILayout* x, const ILayout* y) const
{
	return *x == *y;
}
