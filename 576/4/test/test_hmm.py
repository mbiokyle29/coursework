#!/usr/bin/env python
"""
test_hmm.py
author: Kyle McChesney
"""

import unittest
from lib.models import HMM
import os
import logging
log = logging.getLogger(__name__)

def read_data(file):

    # ensure it exists
    if file_exists(file):

        seqs = []
        with open(file, 'r') as fh:
            for line in fh:
                seqs.append(line.rstrip())

        return seqs

    else:
        log.warn("%s is not a valid file", file)
        raise SystemExit

def file_exists(read_file):

    fullpath = os.path.abspath(read_file)
    log.info("checking if: %s is a real file", fullpath)
    return os.path.exists(fullpath)

class HMMUnitTest(unittest.TestCase):

    def setUp(self):
        self.small_train = read_data("./test/small_training.fa")
        self.small_test = read_data("./test/small_test.fa")
        self.small_output = read_data("./test/small_output.fa")

    def testTestCase(self):

        hmm = HMM()
        hmm.train(self.small_train)
        log.info(hmm)
        hmm.log_transform()
        
        # test
        test_output = []
        for seq in self.small_test:
            test_output.append(hmm.viterbi(seq))

        self.assertEqual(self.small_output, test_output)

   