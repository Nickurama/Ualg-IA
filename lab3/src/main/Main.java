import java.util.Iterator;
import java.util.Scanner;

public class Main
{
	public static void main(String[] args)
	{
		Scanner sc = new Scanner(System.in);
		BestFirst s = new BestFirst();
		String start = sc.nextLine();
		String goal = sc.nextLine();
		sc.close();

		StateSpaceStats.startTimer();
		Iterator<BestFirst.State> it = s.solve(new ContainerLayout(start), new ContainerLayout(goal));
		StateSpaceStats.stopTimer();

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

		StateSpaceStats.print();
	}
}
