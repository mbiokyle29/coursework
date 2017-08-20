#!/usr/bin/env python
"""
models.py
author: 
"""

import logging
import random
import string
from multiprocessing import Pool

logger = logging.getLogger(__name__)

class Graph(object):

    def __init__(self, nodes):
        self.nodes = []
        self.potential_edges = []

        for node in nodes:
            #logger.info("Adding %s to graph", node)
            self.add_node(node)

        # sort the edges, to make a queue
        self.potential_edges = sorted(self.potential_edges, cmp=lambda x,y: self.cmp_edge(x,y)) 

        logger.info("Graph construction completed")
        logger.info("%i nodes added", len(self.nodes))
        
        for edge in self.potential_edges:
            logger.info("EDGE: %s", edge)
    
    def cmp_edge(self, edge_a, edge_b):

        if edge_a.score < edge_b.score:
            return -1
        
        elif edge_a.score == edge_b.score:

            # lex tie break with string in source first
            if edge_a.source.val < edge_b.source.val:
                return -1

            elif edge_a.source.val > edge_b.source.val:
                return 1
            
            else:
                if edge_a.sink.val < edge_b.sink.val:
                    return -1
                elif edge_a.sink.val > edge_b.sink.val:
                    return 1
                else:
                    raise SystemError("Identical nodes")
        else:
            return 1

    def add_node(self, node):

        # create the original fully connected
        # directed graph
        for other_node in self.nodes:
        
            # from curr to nth node
            out_val = self.overlap(node, other_node)
            out_edge = Edge(out_val, node, other_node)

            # from nth node to curr
            in_val = self.overlap(other_node, node)
            in_edge = Edge(in_val, other_node, node)

            # add them to potentials
            for edge in [out_edge, in_edge]:
                self.potential_edges.append(edge)

        # add it now
        self.nodes.append(node)
   
    def assemble(self):

        next_edge = None
        while not self.is_connected():

            next_edge = self.potential_edges.pop(0)

            if self.addable_edge(next_edge):
                next_edge.source.out_edges.append(next_edge)
                next_edge.sink.in_edges.append(next_edge)

        # find the path
        path = []
        start = filter(lambda node: len(node.in_edges) == 0, self.nodes)
        
        if(len(start) != 1):
            raise SystemExit("Multiple start nodes")

        start = start[0]
        path.append(start.val)

        while len(start.out_edges) != 0:
            edge = start.out_edges.pop()
            next_node = edge.sink
            path.append(next_node.val[-edge.score:])
            start = next_node

        return "".join(path)

    def addable_edge(self, edge):
        
        addable = True

        if len(edge.source.out_edges) != 0:
            addable = False
        if len(edge.sink.in_edges) != 0:
            addable = False
        if self.will_create_cycle(edge):
            addable = False

        return addable

    def will_create_cycle(self, edge):
        
        # call the directed dfs from the edge source
        will_cycle = self.path_from(edge.sink, edge.source)

        # be sure to reset
        self.clear()

        return will_cycle

    def overlap(self, source_node, sink_node):
    
        # I think the idea here is:
        # if the first letter of string a is
        # the first letter of string we have a sub
        # recurse some how
        sink_str = sink_node.val
        source_str = source_node.val

        overlap = 0
        for n in range(1, len(source_str)):
            suffix = source_str[n:]
            if sink_str.startswith(suffix):
                return -len(suffix)

        return 0

    def is_connected(self):

        # get nodes with in_deg == 0
        zero_in = []
        zero_out = []
        
        for node in self.nodes:

            if len(node.in_edges) == 0:
                zero_in.append(node)
            if len(node.out_edges) == 0:
                zero_out.append(node)

        return len(zero_in) == 1 and len(zero_out) == 1 and zero_out[0] != zero_in[0]

    # undirected dfs
    def path_from(self, start, stop):

        stack = [start]
        while len(stack) != 0:
            
            node = stack.pop(0)

            if node is stop:
                logger.info("Path found in DFS, bailing")
                return True

            if not node.marked:
                node.marked = True
                nodes = [edge.sink for edge in node.out_edges]
                stack.extend(nodes)

        return False

    def all_marked(self):
        return reduce(lambda a,b: a and b, map(lambda node: node.marked == True, self.nodes))

    def clear(self):
        for node in self.nodes:
            node.marked = False

class Edge(object):

    def __init__(self, score, source, sink):
        self.score = score
        self.source = source
        self.sink = sink

    def __str__(self):
        return "{} --( {} )--> {}".format(self.source, self.score, self.sink)

class Node(object):

    def __init__(self, val):
        self.val = val
        self.marked = False
        self.out_edges = []
        self.in_edges = []

    def __str__(self):
        return self.val

