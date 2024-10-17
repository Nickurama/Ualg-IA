class StateSpaceStats
{
	private static long expanded = 0;
	private static long generated = 0;
	private static long length = 0;

	private static long timeStart = 0;
	private static long timeStop = 0;

	/**
	 * resets all the stat counting
	 */
	public static void reset()
	{
		expanded = 0;
		generated = 0;
		length = 0;
	}

	/**
	 * logs an expansion
	 */
	public static void logExpand() { expanded++; }

	/**
	 * logs a generation of a node
	 */
	public static void logGenerate() { generated++; }

	/**
	 * logs n generations of nodes
	 * @param n the number of generations
	 */
	public static void logGenerate(int n) { generated += n; }

	/**
	 * logs an increase in solution length
	 */
	public static void logLengthen() { length++; }

	/**
	 * @return the number of expanded nodes
	 */
	public static long expanded() { return expanded; }

	/**
	 * @return the number of generated nodes
	 */
	public static long generated() { return generated; }

	/**
	 * @return the length of the solution
	 */
	public static long length() { return length; }

	/**
	 * @return the penetrance achieved
	 */
	public static double penetrance() { return (double)length / generated; } // doesn't check for division by 0

	/**
	 * starts the timer
	 */
	public static void startTimer() { timeStart = System.nanoTime(); }

	/**
	 * stops the timer
	 */
	public static void stopTimer() { timeStop = System.nanoTime(); }

	/**
	 * @return gets the time elapsed of the timer
	 */
	public static double timeElapsed() { return (timeStop - timeStart) / 1000000; }


	/**
	 * prints the stats to the screen
	 */
	public static void print()
	{
		System.out.println("---------- STATS ----------");
		System.out.println("time elapsed:\t\t" + timeElapsed() + "ms");
		System.out.println("nodes expanded:\t\t" + expanded());
		System.out.println("nodes generated:\t" + generated());
		System.out.println("solution length:\t" + length());
		System.out.println("penetrance:\t\t" + penetrance());
		System.out.println("---------------------------");
	}
}
