#pragma once
#include "ILayout.hpp"
#include <ostream>
#include <queue>
#include <unordered_map>

class BestFirst
{
public:
	class State
	{
	public:
		explicit State(std::unique_ptr<ILayout>& layout, const State* father);
		State(State&& s);
		double getCost() const;
		friend std::ostream& operator<<(std::ostream& os, const State& state);
		const ILayout* layout() const; // WARNING! careful not to get outlived
		const State* father() const;
		// State& operator=(const State& rhs);
		bool operator==(const State& rhs) const;
		// hash?
	private:
		std::unique_ptr<ILayout> m_layout; // state should have ownership of layout
		// const ILayout& m_layout;
		const State* m_father; // m_father isn't owned
		double m_cost;
	};

	typedef std::list<const BestFirst::State* >::iterator bf_iter;
	BestFirst();
	BestFirst(const BestFirst&) = delete;
	BestFirst& operator=(const BestFirst& rhs) = delete;
	std::pair<bf_iter, bf_iter> solve(const ILayout& start, const ILayout& goal);

private:
	
	struct MapHash { std::size_t operator()(const ILayout* ptr) const; };
	struct MapEqual { bool operator()(const ILayout* x, const ILayout* y) const; };
	// the map needs pointer for polymorphism
	// it also has ownership of (part of) states
	std::unordered_map<const ILayout *, std::unique_ptr<State>, MapHash, MapEqual> m_closed; // needs pointer for polymorphism
	// State& m_current;
	// ILayout& m_solution;
	std::list<const State *> m_solutionList;
	static std::list<std::unique_ptr<State>> sucessors(const State& state);
	// void init();

protected:
	// also has ownership of (part of) states
	struct QueueCmp{ bool operator()(const std::unique_ptr<State>& x, const std::unique_ptr<State>& y) const; };
	std::priority_queue<std::unique_ptr<State>, std::vector<std::unique_ptr<State>>, QueueCmp> m_open;
	// std::priority_queue<std::unique_ptr<State>, std::vector<std::unique_ptr<State>>, bool(*)(const State&, const State&)> m_open;
};
