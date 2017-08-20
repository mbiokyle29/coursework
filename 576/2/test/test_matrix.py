#!/usr/bin/env python
"""
test_matrix.py
author: kyle mcchesney
"""

import unittest
from lib.models import Matrix
import logging
logger = logging.getLogger(__name__)


class MatrixUnitTest(unittest.TestCase):
    
    def setUp(self):
        self.x = 10
        self.y = 11
        self.m = 1
        self.u = 1
        self.g = 1
        self.s = 1

    def testCreate(self):
        matrix = Matrix(self.x, self.y)
        self.assertTrue(isinstance(matrix, Matrix))

    def testOps(self):
        
        matrix = Matrix(self.x, self.y)
        none = None

        self.assertTrue(isinstance(matrix, Matrix))
        self.assertEqual(matrix.get(0,0), none)

        # try setting a value
        matrix.set(0,0,"Hello")
        self.assertEqual(matrix.get(0,0), "Hello")