import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class TrainingLabs
{
	public static void train1() throws IOException
	{
		String separator = ",";
		DataPreprocessor.normalize("dataset/dataset.csv", "dataset/normalized_dataset.csv", separator);
		// DataPreprocessor.cropEdges("dataset/normalized_dataset.csv", "dataset/cropped_dataset.csv", separator, 20, 2);
		// DataPreprocessor.ditheringNoise("dataset/cropped_dataset.csv", "dataset/dithered_dataset.csv", separator);
		// RandomNumberGenerator.setSeed(2479559307156667474L);
		RandomNumberGenerator.setSeed(137137L);

		// tuning parameters
		int iterations = 10;
		double learningRate = 0.50;
		double trainingToTestingRatio = 0.0001;
		// double trainingToTestingRatio = 0.80;

		// Read from file
		String saveNetworkToFile = "src/main/testing1.ser";
		// String saveNetworkToFile = "saved_networks/mooshak_network_v2.ser";
		String loadNetworkFromFile = saveNetworkToFile;
		// String inSetFile = "dataset/normalized_dataset.csv";
		String inSetFile = "dataset/mnist.csv";
		// String targetOutFile = "dataset/labels.csv";
		String targetOutFile = "dataset/mnist_labels.csv";
		double[][] outputs = DataPreprocessor.readMatrix(targetOutFile, separator);
		double[][] inputs = DataPreprocessor.readMatrix(inSetFile, separator);
		DataPreprocessor.shuffleRowsPreserve(inputs, outputs);

		// split by training to test ratio
		Matrix[] allSets = DataPreprocessor.getSplitSetsFromDataset(inputs, outputs, trainingToTestingRatio);
		Matrix trainingSet = allSets[0];
		Matrix trainingTargetOutput = allSets[1];
		Matrix testingSet = allSets[2];
		Matrix testingTargetOutput = allSets[3];

		System.out.println("training size: " + trainingSet.columns());
		System.out.println("testing size: " + testingSet.columns());

		// hidden layers (none)
		ArrayList<Integer> layerSizes = new ArrayList<>();
		// layerSizes.add(5);
		// layerSizes.add(3);

		// build network
		NeuralNetwork network = NeuralNetwork.layeredBuilder(400, 1, trainingSet, trainingTargetOutput, layerSizes);

		// read if exists
		File readFile = new File(loadNetworkFromFile);
		if (readFile.exists())
			network = NeuralNetwork.loadFromFile(loadNetworkFromFile);
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

		System.out.println("testing error: " + network.getTestingError());
		System.out.println("testing precision: " + network.getPrecision(true));
		System.out.println("testing accuracy: " + network.getAccuracy(true));
		System.out.println("testing kappa: " + network.getKappa(true));

		// train
		// Scanner sc = new Scanner(System.in);
		// int i = Integer.MAX_VALUE;
		// while (i-- > 0)
		// {
		// 	System.out.println("--- Iteration start ---");
		// 	network.train(iterations, learningRate);
		// 	network.saveToFile(saveNetworkToFile);
		// 	System.out.println("--- Iteration over ---");
		// 	try { Thread.sleep(1000); } catch (Exception e) {};
		//
		// 	sc.nextLine();
		// }
		// sc.close();
	}
}
