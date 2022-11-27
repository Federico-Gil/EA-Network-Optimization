#this program will take a csv file that contains edges in a graph and will show it
#the data in the csv file must be in the following format: node1,node2,failure_rate,distance

import networkx as nx
import matplotlib.pyplot as plt
import sys

from networkx.algorithms.bipartite import color

def main():
    #read the file
    file = "C:/Users/Fede/Desktop/AE/EA-Network-Optimization/data/54-nodesWc.csv"
    file = open(file, "r")
    #create a graph
    G = nx.Graph()
    #add the edges to the graph
    for line in file:
        line = line.split(", ")

        G.add_edge(line[0], line[1], color='black', weight=float(line[2])*5.0, distance=float(line[3]))

    
    colors = nx.get_edge_attributes(G,'color').values()
    weights = nx.get_edge_attributes(G,'weight').values()
    
    pos = nx.spring_layout(G)

    nx.draw(G, pos, 
            edge_color=colors, 
            width=list(weights),
            with_labels=True,
            node_color='grey',style='dashed')


    #save the graph as an image in the same folder as the data
    plt.savefig("C:/Users/Fede/Desktop/AE/EA-Network-Optimization/data/OriginalGraph.png")
    plt.show()


    #plt.savefig("C:/Users/Fede/Desktop/AE/EA-Network-Optimization/Network reliability optimization/data/OriginalGraph.png")

    #now read other file and add the edges to the graph with a different color (red)
    file = "C:/Users/Fede/Desktop/AE/EA-Network-Optimization/data/newEdges541.csv"
    file = open(file, "r")
    for line in file:
        line = line.split(", ")
        #the edge added will be a full line.
        G.add_edge(line[0], line[1], color='red', weight=float(line[2])*30.0)
    #draw the graph
    """
    pos = nx.spring_layout(G)
    nx.draw(G, pos, with_labels=True)
    edge_labels = nx.get_edge_attributes(G, 'weight')
    nx.draw_networkx_edge_labels(G, pos, edge_labels=edge_labels)
    plt.show()
    """
    colors = nx.get_edge_attributes(G,'color').values()
    weights = nx.get_edge_attributes(G,'weight').values()

    pos = nx.spring_layout(G)
    nx.draw(G, pos, 
            edge_color=colors, 
            width=list(weights),
            with_labels=True,
            node_color='grey',style='dashed')

    #save the graph as an image in the same folder as the data
    plt.savefig("C:/Users/Fede/Desktop/AE/EA-Network-Optimization/data/GraphWithNewEdges.png")
    plt.show()

if __name__ == "__main__":
    main()
