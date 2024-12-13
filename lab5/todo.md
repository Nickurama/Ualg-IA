# TODO competition nns
- Oneshot network (5-3-1)
- 1 neuron network normal
- 1 neuron network normal + gaussian noise
- 1 neuron network w/ thresholding + gaussian noise
- 1 neuron network w/ convolution + gaussian noise
- 1 neuron network w/ cropping + convolution + gaussian noise
- 1 neuron network w/ cropping + convolution + thresholding + gaussian noise

# TODO competition
- gaussian noise for data augmentation
- apply convolution to the input (decrease weights)
- prepare several neural networks as a project zip
- normalize mooshak input for v2+
- make powerpoint


# TODO bugs
- printing the trainingSet has a row at full 0s? (black pixel at bottom right)
- k-folds should initialize random weights to the network each time (no need to load it from file either)
- NeuralNetwork.getTestingError() is the only way to update lastTestingStats


# competition observations
- comp_full_kappa -> normalize / extremely good
- comp_full_statistical -> normalize / extremely good
- comp_full_alt -> normalize / ok-ish (passes mooshak?)
- comp_full_threshold -> threshold(normalize) / very good
- comp_256_crop -> cropEdges(normalize, 20, 2) / good
- comp_256_crop_threshold -> binaryThreshold(cropEdges(normalize, 20, 2))
- comp_100_downscale_averages -> downscaleAverages(normalize, 20) / ok-ish

- nuke -> threshold(rotate90(normalize)) / kinda bad
- **CALL MOOSHAK FROM MAIN**
- **DONT PRINT EVALUATION**
- **PUT MOOSHAK FOLDER**
- **MAKE SURE RIGHT PARAMS**


# DONE
- generate javadoc
- document all code
- k-fold cross validation
- normalize mooshak input
- make mooshak tests but locally


# DONE report
- report done
- make sure the accuracy at the start in introduction is "real" (>99%?)
- explain there's a lack of unit tests
- explain the code isn't refined
- explain momenturm term could be added for faster conversion
- plot MSE
- training metrics
- k-fold cross validation
- make an analysis of the network using random samples as training/testing and check testing values
- generate UML
- methodology
- results and discussion
- conclusion
- introduction
