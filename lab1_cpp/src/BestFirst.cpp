#include "BestFirst.hpp"
#include <iostream>
#include <sstream>
#include <chrono>

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
	m_open(),
	m_openMap()
{
}

const ILayout* BestFirst::State::layout() const {
	return m_layout.get();
}

std::vector<std::unique_ptr<BestFirst::State>> BestFirst::sucessors(const BestFirst::State& state)
{
	// timing
	// static std::chrono::nanoseconds time_total(0);
	// static std::chrono::nanoseconds before_time(0);
	// static std::chrono::nanoseconds after_time(0);
	// static int count = 0;
	// auto start_time = std::chrono::high_resolution_clock::now();
	// timing
	
	std::vector<std::unique_ptr<ILayout>> children = state.layout()->children();

	// timing
	// auto mid_time = std::chrono::high_resolution_clock::now();
	// timing

	// std::list<std::unique_ptr<State>> sucessors;
	std::vector<std::unique_ptr<State>> sucessors;
	sucessors.reserve(10);
	for (std::unique_ptr<ILayout>& child : children)
		if (state.father() == nullptr || *child != *state.father()->layout())
			sucessors.emplace_back(std::make_unique<State>(std::move(child), &state));

	// timing
	// auto end_time = std::chrono::high_resolution_clock::now();
	// time_total += std::chrono::duration_cast<std::chrono::nanoseconds>(end_time - start_time);
	// before_time += std::chrono::duration_cast<std::chrono::nanoseconds>(mid_time - start_time);
	// after_time += std::chrono::duration_cast<std::chrono::nanoseconds>(end_time - mid_time);
	// if (count++ > 181438)
	// {
	// 	std::cout << "sucessors: " << std::chrono::duration_cast<std::chrono::milliseconds>(time_total).count() << "ms\n";
	// 	std::cout << "before: " << std::chrono::duration_cast<std::chrono::milliseconds>(before_time).count() << "ms\n";
	// 	std::cout << "after: " << std::chrono::duration_cast<std::chrono::milliseconds>(after_time).count() << "ms\n";
	// }
	// timing

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
void BestFirst::debugPrintOpen(std::priority_queue<std::shared_ptr<State>, std::vector<std::shared_ptr<State>>, QueueCmp>& queue, int indent)
{
	if (queue.empty())
		debugPrint("Empty.", indent);

	std::vector<std::shared_ptr<State>> vector;
	int n = 0;
	while (!queue.empty())
	{
		const std::shared_ptr<State>& currState = queue.top();
		debugPrint("[Open Entry " + std::to_string(n++) + "]", indent);
		debugPrintState(currState.get(), indent + 1);
		vector.emplace_back(std::move(const_cast<std::shared_ptr<State>&>(currState)));
		queue.pop();
	}
	for (size_t i = 0; i < vector.size(); i++)
		queue.push(std::move(vector[i]));
}

void BestFirst::debugPrintClosed(const std::unordered_map<const ILayout *, std::shared_ptr<State>, MapHash, MapEqual>& map, int indent)
{
	if (map.size() <= 0)
	{
		debugPrint("Empty.", indent);
		return;
	}

	int n = 0;
	for (const std::pair<const ILayout* const, std::shared_ptr<State>>& pair : map)
	{
		debugPrint("[Closed Entry " + std::to_string(n++) + "]", indent);
		debugPrintState(pair.second.get(), indent + 1);
	}
}

std::pair<BestFirst::bf_iter, BestFirst::bf_iter> BestFirst::solve(const ILayout& start, const ILayout& goal)
{
	std::chrono::nanoseconds time_total_full(0);
	// std::chrono::nanoseconds time_total_sucessors(0);
	// std::chrono::nanoseconds time_total_map_operations(0);
	// std::chrono::nanoseconds time_total_state_sucessors(0);


	auto start_full = std::chrono::high_resolution_clock::now();

	std::shared_ptr<State> first(std::make_shared<State>(start.copy(), nullptr));
	m_open.push(first);
	m_openMap[first->layout()] = first;
	std::vector<std::unique_ptr<State>> sucessors;

	// int count = 0;

	while (!m_open.empty())
	{
		const std::shared_ptr<State>& curr = m_open.top(); // should be m_current

		// debugPrint("----------------------------------------------", 0);
		// debugPrint("[Iteration]", 0);
		// debugPrint(std::to_string(count++), 1);
		// debugPrint("[Current State]", 0);
		// debugPrintState(curr.get(), 1);
		// debugPrint("[Open]", 0);
		// debugPrintOpen(m_open, 1);
		// debugPrint("[Closed]", 0);
		// debugPrintClosed(m_closed, 1);

		if (*curr->layout() == goal)
		{
			// debugPrint("--- GOAL FOUND ---", 0);
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
		// auto start_sucessors = std::chrono::high_resolution_clock::now();
		sucessors = BestFirst::sucessors(*curr);
		// auto end_sucessors = std::chrono::high_resolution_clock::now();
		// time_total_sucessors += std::chrono::duration_cast<std::chrono::nanoseconds>(end_sucessors - start_sucessors);

		// auto start_map_operations = std::chrono::high_resolution_clock::now();
		m_openMap.erase(curr->layout());
		m_closed[curr->layout()] = std::move(const_cast<std::shared_ptr<State>&>(curr));
		// WARNING: CANNOT USE CURR AFTER HERE
		m_open.pop();
		// auto end_map_operations = std::chrono::high_resolution_clock::now();
		// time_total_map_operations += std::chrono::duration_cast<std::chrono::nanoseconds>(end_map_operations - start_map_operations);

		// debugPrint("[Sucessors]", 0);
		// int n = 0;
		// auto start_state_sucessors = std::chrono::high_resolution_clock::now();
		for (std::unique_ptr<State>& sucessor : sucessors)
		{
			// debugPrint("[Sucessor " + std::to_string(n++) + "]", 1);
			// debugPrintState(sucessor.get(), 2);
			// debugPrint("[Hash]", 2);
			// debugPrint(std::to_string(sucessor->layout()->hash()), 3);
			// debugPrint("[Found in Closed]", 2);
			if (m_closed.find(sucessor->layout()) == m_closed.end() &&
				m_openMap.find(sucessor->layout()) == m_openMap.end())
			{
				// debugPrint("False", 3);
				std::shared_ptr<State> sharedSucc = std::move(sucessor);
				m_openMap[sharedSucc->layout()] = sharedSucc;
				m_open.push(sharedSucc);
			}
			// else
			// {
			// 	debugPrint("True", 3);
			// }
		}
	}
	auto end_full = std::chrono::high_resolution_clock::now();
	time_total_full += std::chrono::duration_cast<std::chrono::nanoseconds>(end_full - start_full);

	std::cout << "Full time: " << std::chrono::duration_cast<std::chrono::milliseconds>(time_total_full).count() << "ms\n";
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
