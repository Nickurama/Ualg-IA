import java.util.Random;

/**
 * small wrapper-like class for Random, in order to have a project-scoped rng with the same seed
 */
public class RandomNumberGenerator
{
	private static Random rng;
	private static long seed;

	/**
	 * @return the random number generator.
	 */
	public static Random get()
	{
		if (rng == null)
		{
			rng = new Random();
			seed = rng.nextLong();
			rng.setSeed(seed);
		}
		return rng;
	}

	/**
	 * sets the seed of the random number generator used.
	 * @param seed the seed to use
	 */
	public static void setSeed(long seed)
	{
		RandomNumberGenerator.seed = seed;
		RandomNumberGenerator.get().setSeed(seed);
	}

	/**
	 * @return the seed being used
	 */
	public static long seed()
	{
		return seed;
	}

	/**
	 * @return a random value between -bound and bound.
	 */
	public static double getRandomBounded(double bound)
	{
		Random rng = RandomNumberGenerator.get();
		double num = rng.nextDouble() * bound;
		return rng.nextBoolean() ? num : -num;
	}
}
