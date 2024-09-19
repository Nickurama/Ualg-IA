cmake_minimum_required(VERSION 3.10)
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# project
project(ia VERSION 1.0)
file(GLOB_RECURSE SRC_FILES src/*.cpp)
add_executable(ia ${SRC_FILES})
target_include_directories(ia PUBLIC ${CMAKE_CURRENT_SOURCE_DIR}/include)
target_compile_options(ia PRIVATE -Wall -Werror -Wpedantic -O5)



# testing
# project(tests VERSION 1.0)
# file(GLOB_RECURSE TEST_FILES test/*.cpp src/*.cpp)
# list(FILTER TEST_FILES EXCLUDE REGEX ".*src/Main.cpp$")
# add_executable(tests ${TEST_FILES})
# target_include_directories(tests PUBLIC ${CMAKE_CURRENT_SOURCE_DIR}/include)



project(cmake_test VERSION 0.0.1)
file(GLOB_RECURSE TEST_FILES test/*.cpp src/*.cpp)
list(FILTER TEST_FILES EXCLUDE REGEX ".*src/Main.cpp$")
add_executable(tests ${TEST_FILES})
target_include_directories(tests PUBLIC ${CMAKE_CURRENT_SOURCE_DIR}/include) # includes

