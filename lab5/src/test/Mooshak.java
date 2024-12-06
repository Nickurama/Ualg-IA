import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class Mooshak
{
	@Test
	public void test1()
	{
		// read mooshak input/output from file
		// pipe input into stdin
		boolean output;
		try
		{
			output = Main.mooshak();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Error("test exited." + e.toString());
		}
		// check if output corresponds with output in file
	}
}
