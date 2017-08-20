#!/usr/bin/env python
"""
test_graph.py
author: 
"""

import unittest
from lib.models import Graph, Node, Edge
import logging
logger = logging.getLogger(__name__)


class GraphUnitTest(unittest.TestCase):

    def testCreateGraph(self):
        
        reads = ["AAA", "AGG", "GTT"]
        nodes = [Node(str) for str in reads]
        graph = Graph(nodes)

        self.assertEqual(len(graph.potential_edges), 6)

    def testTieBreaker(self):

        reads = ["ATCGGA", "GGAT", "GGAA", "GGA"]

        nodes = [Node(str) for str in reads]
        graph = Graph(nodes)

        self.assertEqual(graph.potential_edges[0].sink.val, "GGA")

    def testEasyAssembly(self):

        reads = ["Hello", "o_my_friend", "d_its_me"]
        nodes = [Node(str) for str in reads]
        graph = Graph(nodes)
        assembled = graph.assemble()

        self.assertEqual(assembled, "Hello_my_friend_its_me")

    def testSampleAssembly(self):

        read_file = "test/test.reads"

        nodes = []
        with open(read_file) as fh:
            for line in fh:
                nodes.append(Node(line.rstrip()))
        
        graph = Graph(nodes)
        assembled = graph.assemble()
        answer = "the_quick_brown_fox_jumps_over_the_lazy_dog"
        
        self.assertEqual(assembled, answer)

    def testWillCycle(self):
        reads = ["AAA", "AGG", "GTT"]
        
        first_node = Node("AAA")
        second_node = Node("AGG")
        third_node = Node("GGT")

        graph = Graph([first_node, second_node, third_node])

        # add edges
        fs_edge = Edge(0, first_node, second_node)
        first_node.out_edges.append(fs_edge)
        second_node.in_edges.append(fs_edge)

        st_edge = Edge(0, second_node, third_node)
        second_node.out_edges.append(st_edge)
        third_node.in_edges.append(st_edge)

        # cycle edge
        tf_edge = Edge(0, third_node, first_node)
        self.assertTrue(graph.will_create_cycle(tf_edge))

    def testPaths(self):

        nodes = [ Node(read) for read in ["AAA", "AGG", "GTT", "TCA"]]
        graph = Graph(nodes)

        edge = Edge(1, nodes[0], nodes[1])
        
        self.assertFalse(graph.will_create_cycle(edge))
        nodes[0].out_edges.append(edge)
        nodes[1].in_edges.append(edge)

        other_edge = Edge(0, nodes[1], nodes[0])

        self.assertTrue(graph.will_create_cycle(other_edge))

