import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class TrainingLabs
{
	public static void train1() throws IOException
	{
		String separator = ",";
		String networkFile = "src/main/testing1.ser";
		String inSetFile = "dataset/dataset.csv";
		String targetOutFile = "dataset/labels.csv";
		// RandomNumberGenerator.setSeed(2479559307156667474L);
		RandomNumberGenerator.setSeed(137137L);


		File nFile = new File(networkFile);
		if (nFile.exists())
		{
			double[][] mnistOutputs = DataPreprocessor.readMatrix("dataset/mnist_labels.csv", separator);
			double[][] mnistInputs = DataPreprocessor.normalize(DataPreprocessor.readMatrix("dataset/mnist.csv", separator));
			NeuralNetwork mnistNetwork = NeuralNetwork.loadFromFile(networkFile);
			Matrix mnistIn = new Matrix(mnistInputs).transpose();
			Matrix mnistOut = new Matrix(mnistOutputs).transpose();
			mnistNetwork.setTestingSet(mnistIn, mnistOut);
			System.out.println("--- mnist ---");
			System.out.println("testing error: " + mnistNetwork.getTestingError());
			System.out.println("testing precision: " + mnistNetwork.getPrecision(true));
			System.out.println("testing accuracy: " + mnistNetwork.getAccuracy(true));
			System.out.println("testing kappa: " + mnistNetwork.getKappa(true));
			System.out.println("--- mnist ---");
		}


		// tuning parameters
		int iterations = 10;
		double learningRate = 0.50;
		double trainingToTestingRatio = 0.80;

		// Read from file
		double[][] outputs = DataPreprocessor.readMatrix(targetOutFile, separator);
		double[][] inputs = DataPreprocessor.normalize(DataPreprocessor.readMatrix(inSetFile, separator));
		DataPreprocessor.shuffleRowsPreserve(inputs, outputs);

		// split by training to test ratio
		Matrix[] allSets = DataPreprocessor.getSplitSetsFromDataset(inputs, outputs, trainingToTestingRatio);
		Matrix trainingSet = allSets[0];
		Matrix trainingTargetOutput = allSets[1];
		Matrix testingSet = allSets[2];
		Matrix testingTargetOutput = allSets[3];

		System.out.println("training size: " + trainingSet.columns());
		System.out.println("testing size: " + testingSet.columns());

		// build network
		ArrayList<Integer> layerSizes = new ArrayList<>();
		NeuralNetwork network = NeuralNetwork.layeredBuilder(400, 1, trainingSet, trainingTargetOutput, layerSizes);

		// read if exists
		File readFile = new File(networkFile);
		if (readFile.exists())
			network = NeuralNetwork.loadFromFile(networkFile);
		network.setTrainingData(trainingSet, trainingTargetOutput);
		network.setTestingSet(testingSet, testingTargetOutput);

		// set flags
		network.setEarlyStopping(true);
		network.setPrintingTestingError(true);
		network.setApplyGaussianNoise(true);

		network.setExportingLoss(false);
		network.setPrettyPrint(true);
		network.setPrintOutputs(false);
		network.setShouldPrintWhileTraining(false);
		network.setShouldPrintWeights(false);

		// train
		Scanner sc = new Scanner(System.in);
		int i = Integer.MAX_VALUE;
		sc.nextLine();
		while (i-- > 0)
		{
			System.out.println("--- Iteration start ---");
			network.train(iterations, learningRate);
			network.saveToFile(networkFile);
			System.out.println("--- Iteration over ---");
			// try { Thread.sleep(1000); } catch (Exception e) {};
			sc.nextLine();
		}
		sc.close();
	}
}
