import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
	public static void main(String[] args) throws IOException, ClassNotFoundException
	{
		// testXOR();
		playground();

		// twoBitAdderNetwork();
		// trainingNetworkXOR();
		// runAND();
		// runXOR();

		// learningRateSamples();
	}

	private static void playground() throws IOException, ClassNotFoundException
	{
		String prefix = "../";
		String separator = ",";
		// DataPreprocessor.normalize("dataset/dataset.csv", "dataset/normalized_dataset.csv", separator);
		// RandomNumberGenerator.setSeed(7296176720875951778L); // learning rate = 0.7, trainingRatio = 0.8
		// RandomNumberGenerator.setSeed(2479559307156667474L); // learning rate = 0.1, trainingRatio = 0.66

		// tuning parameters
		int iterations = 100;
		double learningRate = 0.70;
		double trainingToTestingRatio = 0.66;

		// Read from file
		// String inSetFile = "dataset/dataset.csv";
		String saveNetworkToFile = prefix + "saved_networks/network.ser";
		String loadNetworkFromFile = saveNetworkToFile;
		String inSetFile = prefix + "dataset/normalized_dataset.csv";
		String targetOutFile = prefix + "dataset/labels.csv";
		String myInputFile = prefix + "dataset/my_inputs.csv";
		double[][] outputs = DataPreprocessor.readMatrix(targetOutFile, separator);
		double[][] inputs = DataPreprocessor.readMatrix(inSetFile, separator);
		DataPreprocessor.shuffleRowsPreserve(inputs, outputs);

		// split by training to test ratio
		Matrix[] targetOutputSets = new Matrix(outputs).splitByRows(trainingToTestingRatio); // needs to be transposed!
		Matrix[] inputSets = new Matrix(inputs).splitByRows(trainingToTestingRatio); // needs to be transposed!

		// my inputs
		Matrix tryingFile0 = new Matrix(DataPreprocessor.readMatrix(myInputFile, ",")).transpose();

		Matrix trying = new Matrix(new double[][] {{
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 1, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
		}});
		trying = trying.transpose();

		Matrix trying2 = new Matrix(new double[][] {{
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 1, 1, 1, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 1, 1, 1, 1, 1, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
			0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8,
		}});
		trying2 = trying2.transpose();

		Matrix trying3 = new Matrix(new double[][] {{
			0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08,
			0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08,
			0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08,
			0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.0, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08,
			0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.0, 1, 0.0, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08,
			0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.0, 1, 1, 0.0, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08,
			0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.0, 1, 1, 1, 0.0, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08,
			0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 1, 0.0, 1, 0.0, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08,
			0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 1, 0.08, 0.0, 1, 0.0, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08,
			0.08, 0.08, 0.08, 0.08, 0.08, 1, 1, 0.08, 0.08, 0.0, 1, 0.0, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08,
			0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.0, 1, 0.0, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08,
			0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.0, 1, 0.0, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08,
			0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.0, 1, 0.0, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08,
			0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.0, 1, 0.0, 0.0, 0.0, 0.0, 0.08, 0.08, 0.08, 0.08,
			0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.0, 0.0, 0.0, 0.0, 1, 0.0, 1, 1, 1, 1, 0.0, 0.08, 0.08,
			0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.0, 1, 1, 1, 1, 1, 1, 1, 0.08, 0.0, 0.0, 0.0, 0.08, 0.08,
			0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.0, 0.0, 0.0, 0.0, 0.0, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08,
			0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08,
			0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08,
			0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08,
		}});
		trying3 = trying3.transpose();

		Matrix trying4 = new Matrix(new double[][] {{
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		}});
		trying4 = trying4.transpose();

		Matrix trainingSet = inputSets[0].transpose();
		Matrix trainingTargetOutput = targetOutputSets[0].transpose();
		// Matrix trainingSet = inputSets[0].appendAsRows(tryingFile0.transpose().getRow(1)).transpose();
		// Matrix trainingTargetOutput = targetOutputSets[0].appendAsRows(new Matrix(new double[][]{{1}})).transpose();
		Matrix testingSet = inputSets[1].transpose();
		Matrix testingTargetOutput = targetOutputSets[1].transpose();

		System.out.println("training size: " + trainingSet.columns());
		System.out.println("testing size: " + testingSet.columns());

		ArrayList<Integer> layerSizes = new ArrayList<>();
		// layerSizes.add(5);
		// layerSizes.add(5);

		NeuralNetwork network = NeuralNetwork.layeredBuilder(400, 1, trainingSet, trainingTargetOutput, layerSizes);
		network.setTestingSet(testingSet, testingTargetOutput);
		network.setEarlyStopping(true);
		network.setPrintingTestingError(true);

		network.setExportingLoss(false);
		network.setPrettyPrint(true);
		network.setPrintOutputs(false);
		network.setShouldPrintWhileTraining(false);
		network.setShouldPrintWeights(false);

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int i = 100;

		// network read
		File readFile = new File(loadNetworkFromFile);
		if (readFile.exists())
			network = NeuralNetwork.loadFromFile(loadNetworkFromFile);

		System.out.println("EVALUATION (1): " + network.evaluate(trying));
		System.out.println("EVALUATION (0): " + network.evaluate(trying2));
		System.out.println("EVALUATION (1): " + network.evaluate(trying3));
		System.out.println("EVALUATION (1): " + network.evaluate(trying4));
		System.out.println("EVALUATION EXTRA (1): " + network.evaluate(tryingFile0));
		while(i-- > 0)
		{
			network.train(iterations, learningRate);
			network.saveToFile(saveNetworkToFile);

			System.out.println("EVALUATION (1): " + network.evaluate(trying));
			System.out.println("EVALUATION (0): " + network.evaluate(trying2));
			System.out.println("EVALUATION (1): " + network.evaluate(trying3));
			System.out.println("EVALUATION (1): " + network.evaluate(trying4));
			System.out.println("EVALUATION EXTRA (1): " + network.evaluate(tryingFile0));
			reader.readLine();
		}
		reader.close();
	}



	private static NeuralNetwork testXOR() { return testXOR(0); }
	private static NeuralNetwork testXOR(double learningRate)
	{
		RandomNumberGenerator.setSeed(3207636386306947792L);

		int iterations = 9800;
		double maxError = 0.001;
		learningRate = 1.0;

		Matrix trainingSet = new Matrix(new double[][] {
			{ 0, 1, 0, 1 },
			{ 0, 0, 1, 1 },
		});
		Matrix targetOutput = new Matrix(new double[][] {
			{ 0, 1, 1, 0 },
		});

		Matrix testingSet = new Matrix(new double[][] {
			{ 0.25 },
			{ 0.25 },
		});

		Matrix testingTargetOutput = new Matrix(new double[][] {
			{ 0.5 },
		});

		InputNode x1 = new InputNode(trainingSet.getRow(0));
		InputNode x2 = new InputNode(trainingSet.getRow(1));
		Neuron n1 = new Neuron();
		Neuron n2 = new Neuron();

		ArrayList<InputNode> inputNodes = new ArrayList<>();
		inputNodes.add(x1);
		inputNodes.add(x2);
		ArrayList<Neuron> outputNeurons = new ArrayList<>();
		outputNeurons.add(n2);

		NeuralNetwork network = new NeuralNetwork(inputNodes, outputNeurons, trainingSet, targetOutput);

		// setup connections
		x1.connect(n1);
		x1.connect(n2);
		x2.connect(n1);
		x2.connect(n2);
		n1.connect(n2);

		network.setTestingSet(testingSet, testingTargetOutput);
		network.setPrintingTestingError(true);

		network.setExportingLoss(true);
		network.setPrinting(true);
		network.setPrettyPrint(true);
		network.setShouldPrintWhileTraining(false);
		network.setShouldPrintWeights(true);

		network.train(iterations, learningRate);
		// network.train(maxError, learningRate);

		network.iterativePropagation();

		return network;
	}

	private static void twoBitAdderNetwork()
	{
		RandomNumberGenerator.setSeed(-682544829822166666l);

		int iterations = 100000;
		double maxError = 0.00001;
		double learningRate = 0.9;

		Matrix trainingSet = new Matrix(new double[][] {
			{ 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1 },

			{ 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1 },
			{ 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1 },
		});
		Matrix targetOutput = new Matrix(new double[][] {
			{ 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0 },
			{ 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 1 },
			{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 1 },
		});

		InputNode x1 = new InputNode(trainingSet.getRow(0));
		InputNode x2 = new InputNode(trainingSet.getRow(1));
		InputNode x3 = new InputNode(trainingSet.getRow(2));
		InputNode x4 = new InputNode(trainingSet.getRow(3));

		// hidden layer 1
		Neuron n1 = new Neuron();
		Neuron n2 = new Neuron();
		Neuron n3 = new Neuron();
		Neuron n4 = new Neuron();

		// hidden layer 2
		Neuron n5 = new Neuron();
		Neuron n6 = new Neuron();
		Neuron n7 = new Neuron();
		Neuron n8 = new Neuron();

		// hidden layer 3
		Neuron n9 = new Neuron();
		Neuron n10 = new Neuron();
		Neuron n11 = new Neuron();
		Neuron n12 = new Neuron();

		// output layer
		Neuron n13 = new Neuron();
		Neuron n14 = new Neuron();
		Neuron n15 = new Neuron();

		ArrayList<InputNode> inputNodes = new ArrayList<>();
		inputNodes.add(x1);
		inputNodes.add(x2);
		inputNodes.add(x3);
		inputNodes.add(x4);
		ArrayList<Neuron> outputNeurons = new ArrayList<>();
		outputNeurons.add(n13);
		outputNeurons.add(n14);
		outputNeurons.add(n15);

		NeuralNetwork network = new NeuralNetwork(inputNodes, outputNeurons, trainingSet, targetOutput);

		// input layer -> layer 1
		x1.connect(n1);
		x1.connect(n2);
		x1.connect(n3);
		x1.connect(n4);
		x2.connect(n1);
		x2.connect(n2);
		x2.connect(n3);
		x2.connect(n4);
		x3.connect(n1);
		x3.connect(n2);
		x3.connect(n3);
		x3.connect(n4);
		x4.connect(n1);
		x4.connect(n2);
		x4.connect(n3);
		x4.connect(n4);

		// hidden layer 1 -> hidden layer 2
		n1.connect(n5);
		n1.connect(n6);
		n1.connect(n7);
		n1.connect(n8);
		n2.connect(n5);
		n2.connect(n6);
		n2.connect(n7);
		n2.connect(n8);
		n3.connect(n5);
		n3.connect(n6);
		n3.connect(n7);
		n3.connect(n8);
		n4.connect(n5);
		n4.connect(n6);
		n4.connect(n7);
		n4.connect(n8);

		// hidden layer 2 -> hidden layer 3
		n5.connect(n9);
		n5.connect(n10);
		n5.connect(n11);
		n5.connect(n12);
		n6.connect(n9);
		n6.connect(n10);
		n6.connect(n11);
		n6.connect(n12);
		n7.connect(n9);
		n7.connect(n10);
		n7.connect(n11);
		n7.connect(n12);
		n8.connect(n9);
		n8.connect(n10);
		n8.connect(n11);
		n8.connect(n12);

		// hidden layer 3 -> output layer
		n9.connect(n13);
		n9.connect(n14);
		n9.connect(n15);
		n10.connect(n13);
		n10.connect(n14);
		n10.connect(n15);
		n11.connect(n13);
		n11.connect(n14);
		n11.connect(n15);
		n12.connect(n13);
		n12.connect(n14);
		n12.connect(n15);

		network.setExportingLoss(false);
		network.setPrettyPrint(true);
		network.setShouldPrintWhileTraining(false);
		network.setShouldPrintWeights(true);

		network.train(iterations, learningRate);
		// network.train(maxError, learningRate);

		network.iterativePropagation();
	}

	private static NeuralNetwork trainingNetworkXOR() { return trainingNetworkXOR(0); }
	private static NeuralNetwork trainingNetworkXOR(double learningRate)
	{
		RandomNumberGenerator.setSeed(3207636386306947792L);

		int iterations = 9800;
		double maxError = 0.001;
		learningRate = 1.0;

		Matrix trainingSet = new Matrix(new double[][] {
			{ 0, 1, 0, 1 },
			{ 0, 0, 1, 1 },
		});
		Matrix targetOutput = new Matrix(new double[][] {
			{ 0, 1, 1, 0 },
		});

		InputNode x1 = new InputNode(trainingSet.getRow(0));
		InputNode x2 = new InputNode(trainingSet.getRow(1));
		Neuron n1 = new Neuron();
		Neuron n2 = new Neuron();

		ArrayList<InputNode> inputNodes = new ArrayList<>();
		inputNodes.add(x1);
		inputNodes.add(x2);
		ArrayList<Neuron> outputNeurons = new ArrayList<>();
		outputNeurons.add(n2);

		NeuralNetwork network = new NeuralNetwork(inputNodes, outputNeurons, trainingSet, targetOutput);

		// setup connections
		x1.connect(n1);
		x1.connect(n2);
		x2.connect(n1);
		x2.connect(n2);
		n1.connect(n2);

		network.setExportingLoss(true);
		network.setPrinting(true);
		network.setPrettyPrint(true);
		network.setShouldPrintWhileTraining(false);
		network.setShouldPrintWeights(true);

		network.train(iterations, learningRate);
		// network.train(maxError, learningRate);

		network.iterativePropagation();

		return network;
	}

	private static void learningRateSamples()
	{
		// for (int j = 20; j < 40; j++)
		// {
			int samples = 1000;
			long sum = 0;
			for (int i = 0; i < samples; i++)
				sum += trainingNetworkXOR(1.0).iterationsDone();
				// sum += trainingNetworkXOR(j).iterationsDone();
			sum /= samples;
			System.out.println(sum);
			// System.out.println(j + ": " + sum);
		// }
	}

	private static void runXOR()
	{
		Matrix trainingSet = new Matrix(new double[][] {
			{ 0, 0, 1, 1 },
			{ 0, 1, 0, 1 },
		});
		Matrix targetOutput = new Matrix(new double[][] {
			{ 0, 1, 1, 0 },
		});
		// setup neurons
		InputNode x1 = new InputNode(trainingSet.getRow(0));
		InputNode x2 = new InputNode(trainingSet.getRow(1));

		Neuron n1 = new Neuron(300.0); // w0
		Neuron n2 = new Neuron(-500.0); // w3

		ArrayList<InputNode> inputNodes = new ArrayList<>();
		inputNodes.add(x1);
		inputNodes.add(x2);
		ArrayList<Neuron> outputNeurons = new ArrayList<>();
		outputNeurons.add(n2);

		NeuralNetwork network = new NeuralNetwork(inputNodes, outputNeurons, trainingSet, targetOutput);

		x1.connect(n1, -200.0); // w1
		x1.connect(n2, 200.0); // w4
		x2.connect(n1, -200.0); // w2
		x2.connect(n2, 200.0); // w6
		n1.connect(n2, 400.0); // w5

		network.setPrettyPrint(true);

		network.propagate();

		network.printWeights();
		network.printOutputs();

		network.iterativePropagation();
	}

	private static void runAND()
	{
		Matrix trainingSet = new Matrix(new double[][] {
			{ 0, 0, 1, 1 },
			{ 0, 1, 0, 1 },
		});
		Matrix targetOutput = new Matrix(new double[][] {
			{ 0, 0, 0, 1 },
		});

		InputNode x1 = new InputNode(trainingSet.getRow(0));
		InputNode x2 = new InputNode(trainingSet.getRow(1));
		Neuron n1 = new Neuron(-1.5);

		ArrayList<InputNode> inputNodes = new ArrayList<>();
		inputNodes.add(x1);
		inputNodes.add(x2);
		ArrayList<Neuron> outputNeurons = new ArrayList<>();
		outputNeurons.add(n1);

		NeuralNetwork network = new NeuralNetwork(inputNodes, outputNeurons, trainingSet, targetOutput);

		x1.connect(n1, 1);
		x2.connect(n1, 1);

		network.setPrettyPrint(true);

		network.propagate();

		network.printWeights();
		network.printOutputs();

		network.iterativePropagation();
	}
}
