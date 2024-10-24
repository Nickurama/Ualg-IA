import java.util.Iterator;
import java.util.Scanner;

public class Main
{
	public static void main(String[] args)
	{
		Scanner sc = new Scanner(System.in);
		String start = sc.nextLine();
		String goal = sc.nextLine();
		sc.close();

		// solveBestFirst(start, goal);
		solveAStar(start, goal);
	}

	private static void solveBestFirst(String start, String goal)
	{
		BestFirst s = new BestFirst();
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
			// System.out.println(i);
			if (!it.hasNext())
			{
				System.out.println(i);
				System.out.println((int)Math.round(i.getCost()));
			}
		}

		StateSpaceStats.print();
	}

	private static void solveAStar(String start, String goal)
	{
		AStar s = new AStar();
		StateSpaceStats.startTimer();
		Iterator<AStar.State> it = s.solve(new ContainerLayout(start), new ContainerLayout(goal));
		StateSpaceStats.stopTimer();

		if (it == null)
		{
			System.out.println("no solution found");
			return;
		}

		while(it.hasNext())
		{
			AStar.State i = it.next();
			// System.out.println("Trying heuristic...");
			// i.layout().heuristic(new ContainerLayout(goal));
			// System.out.println("Expanding node of f: " + i.f() + " (g: " + i.g() + " h: " + i.h() + ")");
			// System.out.println(i);
			if (!it.hasNext())
			{
				System.out.println(i);
				System.out.println((int)Math.round(i.g()));
			}
		}

		StateSpaceStats.print();
	}
}
