class StateSpaceStats
{
	private static long expanded = 0;
	private static long generated = 0;
	private static long length = 0;

	private static long timeStart = 0;
	private static long timeStop = 0;

	public static void reset()
	{
		expanded = 0;
		generated = 0;
		length = 0;
	}

	public static void logExpand() { expanded++; }
	public static void logGenerate() { generated++; }
	public static void logGenerate(int n) { generated += n; }
	public static void logLengthen() { length++; }

	public static long expanded() { return expanded; }
	public static long generated() { return generated; }
	public static long length() { return length; }
	public static double penetrance() { return (double)length / generated; } // doesn't check for division by 0

	public static void startTimer() { timeStart = System.nanoTime(); }
	public static void stopTimer() { timeStop = System.nanoTime(); }
	public static double timeElapsed() { return (timeStop - timeStart) / 1000000; }


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
