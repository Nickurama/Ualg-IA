import java.util.Iterator;
import java.util.Scanner;

public class Main
{
	public static void main(String[] args)
	{
		Scanner sc = new Scanner(System.in);
		BestFirst s = new BestFirst();

		long time_start = System.nanoTime();
		Iterator<BestFirst.State> it = s.solve(new ContainerLayout(sc.nextLine()), new ContainerLayout(sc.nextLine()));
		long time_stop = System.nanoTime();
		long time_ns = time_stop - time_start;
		double time_ms = (double)time_ns / 1000000;
		System.out.println(time_ms + "ms");
		sc.close();

		if (it == null)
		{
			System.out.println("no solution found");
			return;
		}

		while(it.hasNext())
		{
			BestFirst.State i = it.next();
			System.out.println(i);
			if (!it.hasNext())
				System.out.println((int)Math.round(i.getCost()));
		}
	}
}
