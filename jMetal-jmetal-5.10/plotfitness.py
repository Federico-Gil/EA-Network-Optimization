#plot the fitness of the best individual in each generation
#read the csv file containing the fitness of the best individual in each generation
#and plot it

import matplotlib.pyplot as plt
import numpy as np
import csv

#read the csv file
with open('C:/Users/Fede/Desktop/AE/EA-Network-Optimization/data/bestFitnessPerGeneration.csv', 'r') as f:
    reader = csv.reader(f)
    data = list(reader)

#convert the data to a numpy array
data = np.array(data)

#convert the data to float
data = data.astype(np.float)

#plot the data
plt.plot(data[1:,0], data[1:,1], 'o')
plt.xlabel('Generation')
plt.ylabel('Fitness')
plt.show()

#save the plot