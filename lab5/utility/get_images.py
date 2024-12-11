from numpy import genfromtxt
import numpy
from PIL import Image

# put into range 0-255
data = genfromtxt('mnist.csv', delimiter=',') * 255

# reshape into 20x20
data_formed = numpy.empty((len(data), 20, 20))
for i in range(len(data)):
    data_formed[i] = numpy.reshape(data[i], (-1, 20))

# save imgs
for i in range(len(data_formed)):
    img = Image.fromarray(data_formed[i]).convert('RGB')
    img.save(f"imgs/test_img{i}.png")
