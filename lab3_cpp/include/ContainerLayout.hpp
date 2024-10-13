#pragma once
#include "ILayout.hpp"
#include <string>

class ContainerLayout : public ILayout
{
	public:
		ContainerLayout(const std::string& str);
		ContainerLayout(const ContainerLayout& other);
		~ContainerLayout();
		std::vector<std::unique_ptr<ILayout>> children() const override;
		bool isGoal(const ILayout& layout) const override;
		double getCost() const override;
		void print(std::ostream& stream) const override;
		std::unique_ptr<ILayout> copy() const override;
		std::size_t hash() const override;
		bool operator==(const ILayout& rhs) const override;

	private:
		static const int BUCKET_SIZE = 26;

		static const std::size_t HASH_CONTAINS_POWER = 11;
		static const std::size_t HASH_BOTTOM_POWER = 23;
		static const std::size_t HASH_TOP_POWER = 37;

		mutable std::size_t m_hash;
		mutable bool m_hasHash;
		bool m_hasCost;
		double m_cost;
		int m_containersCost[BUCKET_SIZE]; // -1 when doesn't exit
		int m_containerOnTopIndex[BUCKET_SIZE]; // -1 when container is on top
		int m_containerOnBottomIndex[BUCKET_SIZE]; // -1 when container is on bottom
		std::vector<int> m_topmostContainersIndex;

		inline static bool isNumerical(char c);
		inline static bool isAlphabetical(char c);
		inline static int getKey(char c);
		inline static char getCharFromKey(int i);
		static void split(const std::string& str, std::vector<std::string>& tokens);

		inline bool contains(char c) const;
		inline bool contains(int i) const;
		inline bool hasContainerOnTop(char c) const;
		inline bool hasContainerOnTop(int i) const;
		inline bool hasContainerOnBottom(char c) const;
		inline bool hasContainerOnBottom(int i) const;
		inline bool isOnBottom(char c) const;
		inline bool isOnBottom(int i) const;
		inline bool isOnTop(char c) const;
		inline bool isOnTop(int i) const;
		inline int getContainerOnTopIndex(char c) const;
		inline int getContainerOnTopIndex(int i) const;
		inline int getContainerOnBottomIndex(char c) const;
		inline int getContainerOnBottomIndex(int i) const;
		inline static std::size_t fastPow(std::size_t base, std::size_t exponent);
		inline static void updateHash(ContainerLayout& layout, long long oldValue, long long newValue, std::size_t constant, std::size_t power);

		bool shouldHaveCost(const std::string& str) const;
		void populateFromString(const std::string& str);
		void populateContainersFromString(const std::string& str);
		void populateContainersFromStringWithCost(const std::string& str);
		void populateContainersFromStringWithoutCost(const std::string& str);
		void addNextContainer(const std::string& str, std::size_t index, std::size_t previous);
		void validateContainer(const std::string& str, std::size_t index) const;
		int addContainer(int key, int cost, int next, int previous);
		std::unique_ptr<ContainerLayout> moveContainerGround(int from) const;
		std::unique_ptr<ContainerLayout> moveContainer(int from, int to) const;
};
