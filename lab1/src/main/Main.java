import java.util.Iterator;
import java.util.Scanner;

public class Main
{
	public static void main(String[] args)
	{
		Scanner sc = new Scanner(System.in);
		BestFirst s = new BestFirst();

		Iterator<BestFirst.State> it = s.solve(new Board(sc.next()), new Board(sc.next()));
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
				System.out.println(i.getCost());
		}
	}
}
