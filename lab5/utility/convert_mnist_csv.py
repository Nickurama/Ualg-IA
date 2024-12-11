import idx2numpy
import numpy as numpy
import pandas as pd
from PIL import Image
import cv2

# Paths to the MNIST dataset files
file_images = 'train-images-idx3-ubyte'
file_labels = 'train-labels-idx1-ubyte'

# Read the image and label files
images = idx2numpy.convert_from_file(file_images)
labels = idx2numpy.convert_from_file(file_labels)

# copy
images_resized = numpy.empty((len(images), 20, 20));

# from 28x28 to 20x20
for i in range(len(images)):
    res = cv2.resize(images[i], dsize=(20,20), interpolation=cv2.INTER_LINEAR)
    images_resized[i] = res;
    # images_resized = numpy.concatenate((images_resized, [res]), axis=0)

# turn into image
# img = Image.fromarray(images_resized[0])
# if (img.mode != 'RGB'):
#     img = img.convert('RGB')
# img.save('test_img.png')

# cv2.imwrite('test_img', images_resized[0])

# Flatten the image array and normalize pixel values
# images_flattened = images.reshape(images.shape[0], -1) / 255.0
images_flattened = images_resized.reshape(images_resized.shape[0], -1) / 255.0

# remove anything other than 0s and 1s
data_filtered = numpy.empty((len(images), len(images_flattened[0])), numpy.double)
labels_filtered = numpy.empty((len(images), 1), int)
curr = 0
for i in range(len(images_flattened)):
    if labels[i] == 0 or labels[i] == 1:
        labels_filtered[curr] = labels[i]
        data_filtered[curr] = images_flattened[i]
        curr = curr + 1
        # labels_filtered = numpy.concatenate((labels_filtered, [numpy.atleast_1d(labels[i])]), axis=0)
        # data_filtered = numpy.concatenate((data_filtered, [images_flattened[i]]), axis=0)
data_filtered = data_filtered[0:curr]
labels_filtered = labels_filtered[0:curr]

# Combine labels and images
# data = np.column_stack((labels, images_flattened))
data1 = data_filtered
data2 = labels_filtered

# Convert to DataFrame and save as CSV
df1 = pd.DataFrame(data1)
df1.to_csv('mnist.csv', index=False, header=False)

df2 = pd.DataFrame(data2)
df2.to_csv('mnist_labels.csv', index=False, header=False)
