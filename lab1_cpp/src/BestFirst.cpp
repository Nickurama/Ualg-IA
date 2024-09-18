#include "BestFirst.hpp"
#include <iostream>
#include <sstream>

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
	// : m_layout(s.m_layout),
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

// BestFirst::State& BestFirst::State::operator=(const State& rhs) 
// {
// 	m_layout = std::move(rhs.m_layout);
// 	m_father = rhs.m_father;
// 	m_cost = rhs.m_cost;
// }

bool BestFirst::State::operator==(const State& rhs) const
{
	return m_layout == rhs.m_layout;
}

BestFirst::BestFirst()
	: m_closed(),
	m_open()
{
}

const ILayout* BestFirst::State::layout() const {
	return m_layout.get();
}

std::list<std::unique_ptr<BestFirst::State>> BestFirst::sucessors(const BestFirst::State& state)
{
	// std::cout << "making sucessors" << std::endl;
	std::list<std::unique_ptr<State>> sucessors;
	std::list<std::unique_ptr<ILayout>> children = state.layout()->children();
	// std::cout << "the parent address:\n" << state.father() << std::endl;
	// if (state.father() != nullptr)
	// {
	// 	std::cout << "the parent layout address:\n" << state.father()->layout() << std::endl;
	// 	std::cout << "the parent cost:\n" << state.father()->getCost() << std::endl;
	// 	// ERROR!
	// 	std::cout << "the parent layout:\n" << *state.father()->layout() << std::endl;
	// }
	// else
	// {
	// 	std::cout << "no parent" << std::endl;
	// }

	// for (std::unique_ptr<ILayout>& child : children)
	// {
	// 	std::cout << "the sucessor layout:" << std::endl;
	// 	std::cout << *child << std::endl;
	// }

	std::list<std::unique_ptr<ILayout>>::iterator it;
	// std::cout << "before loop" << std::endl;
	for (it = children.begin(); it != children.end(); it++)
	{
		// std::cout << "start loop" << std::endl;
		// std::cout << "father:" << state.father() << std::endl;
		// if (state.father() != nullptr)
			// std::cout << "layout:" << *state.father()->layout() << std::endl;
		if (state.father() == nullptr || **it != *state.father()->layout())
		{
			// std::cout << "in if" << std::endl;
			// State* st = new State(*it, &state);
			// std::unique_ptr<State> pst(st);
			// sucessors.emplace_front(std::move(pst));
			sucessors.emplace_front(std::make_unique<State>(std::move(*it), &state));
		}
	}
	return sucessors;
}

void BestFirst::debugPrint(std::string text, int indent)
{
	std::string remaining = text;
	std::string result = text;

	for (int i = 0; i < indent; i++)
		result = '\t' + result;

	size_t n = remaining.find('\n');
	if (n != std::string::npos)
	{
		result = remaining.substr(0, n);
		remaining = remaining.substr(n + 1);
		for (int i = 0; i < indent; i++)
			result = '\t' + result;
		while (n != std::string::npos)
		{
			result += '\n';
			for (int i = 0; i < indent; i++)
				result += '\t';
			result += remaining.substr(0, n);
			remaining = remaining.substr(n + 1);
			n = remaining.find('\n');
		}
		result += remaining;
	}
	std::cout << result << std::endl;
}

void BestFirst::debugPrintState(const State *state, int indent)
{
	if (state == nullptr)
	{
		debugPrint("Null", indent);
		return;
	}

	std::stringstream ss;

	debugPrint("[Layout]", indent);
		ss = std::stringstream();
		ss << state;
		debugPrint(ss.str(), indent + 1);
		ss = std::stringstream();
	state->layout()->print(ss);
	debugPrint(ss.str(), indent + 1);

	debugPrint("[Cost]", indent);
	debugPrint(std::to_string(state->getCost()), indent + 1);

	debugPrint("[Father]", indent);
	if (state->father() == nullptr)
		debugPrint("Null", indent + 1);
	else
	{
		ss = std::stringstream();
		ss << state->father();
		debugPrint(ss.str(), indent + 1);

		ss = std::stringstream();
		state->father()->layout()->print(ss);
		debugPrint(ss.str(), indent + 1);
	}
}

// passes by value!
void BestFirst::debugPrintOpen(std::priority_queue<std::unique_ptr<State>, std::vector<std::unique_ptr<State>>, QueueCmp>& queue, int indent)
{
	if (queue.empty())
		debugPrint("Empty.", indent);

	std::vector<std::unique_ptr<State>> vector;
	int n = 0;
	while (!queue.empty())
	{
		const std::unique_ptr<State>& currState = queue.top();
		debugPrint("[Open Entry " + std::to_string(n++) + "]", indent);
		debugPrintState(currState.get(), indent + 1);
		vector.emplace_back(std::move(const_cast<std::unique_ptr<State>&>(currState)));
		queue.pop();
	}
	for (size_t i = 0; i < vector.size(); i++)
		queue.push(std::move(vector[i]));
}

void BestFirst::debugPrintClosed(const std::unordered_map<const ILayout *, std::unique_ptr<State>, MapHash, MapEqual>& map, int indent)
{
	if (map.size() <= 0)
	{
		debugPrint("Empty.", indent);
		return;
	}

	int n = 0;
	for (const std::pair<const ILayout* const, std::unique_ptr<State>>& pair : map)
	{
		debugPrint("[Closed Entry " + std::to_string(n++) + "]", indent);
		debugPrintState(pair.second.get(), indent + 1);
	}
}

std::pair<BestFirst::bf_iter, BestFirst::bf_iter> BestFirst::solve(const ILayout& start, const ILayout& goal)
{
	m_open.push(std::make_unique<State>(State(start.copy(), nullptr)));
	std::list<std::unique_ptr<State>> sucessors;

	int count = 0;

	while (!m_open.empty())
	{
		const std::unique_ptr<State>& curr = m_open.top(); // should be m_current

		debugPrint("----------------------------------------------", 0);
		debugPrint("[Iteration]", 0);
		debugPrint(std::to_string(count++), 1);
		debugPrint("[Current State]", 0);
		debugPrintState(curr.get(), 1);
		debugPrint("[Open]", 0);
		debugPrintOpen(m_open, 1);
		debugPrint("[Closed]", 0);
		debugPrintClosed(m_closed, 1);

		if (*curr->layout() == goal)
		{
			debugPrint("--- GOAL FOUND ---", 0);
			// solution found
			// std::list<const State *> solution;
			const State* tmp = curr.get(); // dangerous
			m_solutionList.emplace_back(tmp);
			while (tmp->father() != nullptr)
			{
				m_solutionList.emplace_back(tmp->father());
				tmp = tmp->father();
			}
			m_solutionList.reverse();
			return std::pair<BestFirst::bf_iter, BestFirst::bf_iter>( m_solutionList.begin(), m_solutionList.end());
		}
		// solution not found

		// WARNING: very dangerous cast, but std::priority_queue does not allow
		// moving out of the queue. Do NOT use either the queue or attempt to
		// modify the variable from the old pointer after this.
		// std::cout << "--- owo ---" << std::endl;
		sucessors = BestFirst::sucessors(*curr);
		// std::cout << "--- owo2 ---" << std::endl;

		// std::cout << "--- owo curr ---\n" << *curr->layout() << std::endl;
		// std::cout << "current cost: " << curr->getCost() << std::endl;
		m_closed[curr->layout()] = std::move(const_cast<std::unique_ptr<State>&>(curr));
		// std::cout << "--- owo curr 2.5 ---" << std::endl;
		m_open.pop();

		// std::cout << "--- owo curr 3 ---" << std::endl;
		debugPrint("[Sucessors]", 0);
		int n = 0;
		for (std::unique_ptr<State>& sucessor : sucessors)
		{
			debugPrint("[Sucessor " + std::to_string(n++) + "]", 1);
			debugPrintState(sucessor.get(), 2);
			debugPrint("[Hash]", 2);
			debugPrint(std::to_string(sucessor->layout()->hash()), 3);
			debugPrint("[Found in Closed]", 2);
			if (m_closed.find(sucessor->layout()) == m_closed.end())
			{
				debugPrint("False", 3);
				m_open.push(std::move(sucessor));
			}
			else
			{
				debugPrint("True", 3);
			}
		}
	}
	std::cout << "hewwo" << std::endl;
	return std::pair<BestFirst::bf_iter, BestFirst::bf_iter>(nullptr, nullptr);
}

bool BestFirst::QueueCmp::operator()(const std::unique_ptr<State>& x, const std::unique_ptr<State>& y) const
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
