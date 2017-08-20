#!/usr/bin/env python
"""
graphs.py
author: Kyle McChesney
"""

import os, argparse, logging
import matplotlib as plt
import networkx as nx
import itertools
from nxpd import draw
import pygraphviz as pgv
import string

def main():

    strings = ["TACCGGACTTAGG","TATCGGATCGTTA"]

    for sequence in strings:
        three_mers = set(find_kmers(sequence, 3))

        edges = []
        for three_mer in three_mers:
            start = three_mer[:-1]
            end = three_mer[1:]
            edges.append((start, end))

        nodes = set(find_kmers(sequence, 2))
        graph(sequence, nodes, edges)



def find_kmers(string, k):
    
      kmers = []
      n = len(string)

      for i in range(0, n-k+1):
           kmers.append(string[i:i+k])

      return kmers

def overlap(source_str, sink_str):

    min_score = 0
    for n in range(1, len(source_str)):
        suffix = source_str[n:]
        if string.find(sink_str, suffix, 0, len(suffix)) != -1:
            if -len(suffix) < min_score:
                min_score = -len(suffix) 

    return min_score

def graph(name, nodes, edges):

    G = nx.DiGraph()
    G.graph['dpi'] = 200
    G.graph['label'] = "SBH Graph: {}".format(name)
    G.add_nodes_from(nodes)
    G.add_edges_from(edges)
    draw(G)

if __name__ == "__main__":
    main()