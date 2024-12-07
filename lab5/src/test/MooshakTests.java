import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.ArrayList;

public class MooshakTests
{
	private static final String testsFolder = "src/test/mooshak_tests/";

	@Test
	public void test1() throws ClassNotFoundException, IOException
	{
		test("T01/", "dataset1.csv", "labels1.csv");
	}
	@Test
	public void test2() throws ClassNotFoundException, IOException
	{
		test("T02/", "dataset2.csv", "labels2.csv");
	}
	@Test
	public void test3() throws ClassNotFoundException, IOException
	{
		test("T03/", "dataset3.csv", "labels3.csv");
	}
	@Test
	public void test4() throws ClassNotFoundException, IOException
	{
		test("T04/", "dataset4.csv", "labels4.csv");
	}
	@Test
	public void test5() throws ClassNotFoundException, IOException
	{
		test("T05/", "dataset5.csv", "labels5.csv");
	}
	@Test
	public void test6() throws ClassNotFoundException, IOException
	{
		test("T06/", "dataset6.csv", "labels6.csv");
	}
	@Test
	public void test7() throws ClassNotFoundException, IOException
	{
		test("T07/", "dataset7.csv", "labels7.csv");
	}
	@Test
	public void test8() throws ClassNotFoundException, IOException
	{
		test("T08/", "dataset8.csv", "labels8.csv");
	}
	@Test
	public void test9() throws ClassNotFoundException, IOException
	{
		test("T09/", "dataset9.csv", "labels9.csv");
	}
	@Test
	public void test10() throws ClassNotFoundException, IOException
	{
		test("T10/", "dataset10.csv", "labels10.csv");
	}

	private void test(String testFolder, String datasetFile, String labelFile) throws ClassNotFoundException, IOException
	{
		// Arrange
		String input = readLine(testsFolder + testFolder + datasetFile);
		String expectedStr = readLine(testsFolder + testFolder + labelFile);
		boolean expected = Integer.parseInt(expectedStr) == 1 ? true : false;

		InputStream inBackup = System.in;
		ByteArrayInputStream newIn = new ByteArrayInputStream(input.getBytes());
		System.setIn(newIn);

		// Act
		boolean output = Main.mooshak();


		// Assert
		System.setIn(inBackup);
		assertEquals(expected, output);
	}

	private String readLine(String fileName) throws IOException
	{
		File file = new File(fileName);
		FileReader fReader = new FileReader(file);
		BufferedReader reader = new BufferedReader(fReader);
		String result = reader.readLine();
		reader.close();
		fReader.close();
		return result;
	}
}
