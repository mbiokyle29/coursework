#!/usr/bin/env python
"""
test_cluster.py
author: Kyle McChesney
"""

import unittest
from lib.models import Cluster
import os
import logging as log

def read_exp_data(file):

    # ensure it exists
    if file_exists(file):
        annot = {}
        exp = {}
        with open(file, 'r') as fh:
            for line in fh:
                arrayed = line.rstrip().split("\t")
                annot[arrayed[0]] = arrayed[1]
                rest = [float(x) for x in arrayed[2:]]
                exp[arrayed[0]] = rest

        return (annot, exp)

    else:
        log.warn("%s is not a valid file", file)
        raise SystemExit

def file_exists(read_file):

    fullpath = os.path.abspath(read_file)
    log.info("checking if: %s is a real file", fullpath)
    return os.path.exists(fullpath)

def read_results(file):

    if file_exists(file):

        output = ""
        with open(file, "r") as fh:
            for line in fh:
                output += line

        return output

    else:
        log.warn("%s is not a valid file", file)
        raise SystemExit 

class ClusterUnitTest(unittest.TestCase):

    def setUp(self):
        (annotations, expression) = read_exp_data("./test/data/tiny-yeast.mtx")
        self.annotations = annotations
        self.expression = expression

    def testSingleTwo(self):
        clusterObj = Cluster(self.expression, self.annotations)
        results = read_results("./test/key/single-2.out")
        clusters = clusterObj.cluster("S",2)
        self.assertEqual(results, clusters)

    def testSingleFour(self):
        clusterObj = Cluster(self.expression, self.annotations)
        results = read_results("./test/key/single-4.out")
        clusters = clusterObj.cluster("S",4)
        self.assertEqual(results, clusters)

    def testSingleSix(self):
        clusterObj = Cluster(self.expression, self.annotations)
        results = read_results("./test/key/single-6.out")
        clusters = clusterObj.cluster("S",6)
        self.assertEqual(results, clusters)
    
    def testCompleteTwo(self):
        clusterObj = Cluster(self.expression, self.annotations)
        results = read_results("./test/key/complete-2.out")
        clusters = clusterObj.cluster("C",2)
        self.assertEqual(results, clusters)

    def testCompleteFour(self):
        clusterObj = Cluster(self.expression, self.annotations)
        results = read_results("./test/key/complete-4.out")
        clusters = clusterObj.cluster("C",4)
        self.assertEqual(results, clusters)
    
    def testCompleteSix(self):
        clusterObj = Cluster(self.expression, self.annotations)
        results = read_results("./test/key/complete-6.out")
        clusters = clusterObj.cluster("C",6)
        self.assertEqual(results, clusters)

    def testAverageTwo(self):
        clusterObj = Cluster(self.expression, self.annotations)
        results = read_results("./test/key/avg-2.out")
        clusters = clusterObj.cluster("A",2)
        self.assertEqual(results, clusters)

    def testAverageFour(self):
        clusterObj = Cluster(self.expression, self.annotations)
        results = read_results("./test/key/avg-4.out")
        clusters = clusterObj.cluster("A",4)
        self.assertEqual(results, clusters)

    def testAverageSix(self):
        clusterObj = Cluster(self.expression, self.annotations)
        results = read_results("./test/key/avg-6.out")
        clusters = clusterObj.cluster("A",6)
        self.assertEqual(results, clusters)

   