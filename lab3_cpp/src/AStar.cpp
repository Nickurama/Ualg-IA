#include "AStar.hpp"
#include <iostream>

// accepts null father
AStar::State::State(std::unique_ptr<ILayout> layout, const State* father)
	: m_layout(std::move(layout)),
	m_father(father),
	m_g(0.0)
{
	if (father != nullptr)
		m_g = father->g() + m_layout->getCost();
}

AStar::State::State(State&& s)
	: m_layout(std::move(s.m_layout)),
	m_father(s.m_father),
	m_g(s.m_g)
{
	s.m_father = nullptr;
}

std::ostream& operator<<(std::ostream& os, const AStar::State& state)
{
	return os << *state.layout();
}

const AStar::State* AStar::State::father() const { return m_father; }

double AStar::State::g() const { return m_g; }

bool AStar::State::operator==(const State& rhs) const
{
	return m_layout == rhs.m_layout;
}

AStar::AStar()
	: m_closed(),
	m_open(),
	m_openMap()
{
}

const ILayout* AStar::State::layout() const {
	return m_layout.get();
}

std::vector<std::unique_ptr<AStar::State>> AStar::sucessors(const AStar::State& state)
{
	std::vector<std::unique_ptr<ILayout>> children = state.layout()->children();

	std::vector<std::unique_ptr<State>> sucessors;
	sucessors.reserve(10);
	for (std::unique_ptr<ILayout>& child : children)
		if (state.father() == nullptr || *child != *state.father()->layout())
			sucessors.emplace_back(std::make_unique<State>(std::move(child), &state));

	return sucessors;
}

std::pair<AStar::bf_iter, AStar::bf_iter> AStar::solve(const ILayout& start, const ILayout& goal)
{
	std::shared_ptr<State> first(std::make_shared<State>(start.copy(), nullptr));
	m_open.push(first);
	m_openMap[first->layout()] = first;
	std::vector<std::unique_ptr<State>> sucessors;
	m_closed.reserve(10000000); // HACK
	m_openMap.reserve(10000000); // HACK

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
			return std::pair<AStar::bf_iter, AStar::bf_iter>(m_solutionList.begin(), m_solutionList.end());
		}
		// solution not found

		// WARNING: very dangerous cast, but std::priority_queue does not allow
		// moving out of the queue. Do NOT use either the queue or attempt to
		// modify the variable from the old pointer after this.
		sucessors = AStar::sucessors(*curr);
		m_openMap.erase(curr->layout());
		m_closed[curr->layout()] = std::move(const_cast<std::shared_ptr<State>&>(curr));
		// WARNING: CANNOT USE CURR AFTER HERE
		m_open.pop();

		for (std::unique_ptr<State>& sucessor : sucessors)
		{
			if (!m_closed.contains(sucessor->layout()) && !m_openMap.contains(sucessor->layout()))
			{
				std::shared_ptr<State> sharedSucc = std::move(sucessor);
				m_openMap[sharedSucc->layout()] = sharedSucc;
				m_open.push(sharedSucc);
			}
		}
	}
	return std::pair<AStar::bf_iter, AStar::bf_iter>(nullptr, nullptr);
}

bool AStar::QueueCmp::operator()(const std::shared_ptr<State>& x, const std::shared_ptr<State>& y) const
{
	return x->g() > y->g();
}

std::size_t AStar::MapHash::operator()(const ILayout* ptr) const
{
	return ptr->hash();
}

bool AStar::MapEqual::operator()(const ILayout* x, const ILayout* y) const
{
	return *x == *y;
}
