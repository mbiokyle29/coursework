#!/usr/bin/env python
"""
predict_exons.py
author: Kyle McChesney (kgmcchesney@wisc.edu)
"""

import os, argparse, logging
from lib.models import HMM

# Kenny loggins
log = logging.getLogger(__name__)
log.setLevel(logging.WARN)
log_formatter = logging.Formatter('%(asctime)s {%(levelname)s}: %(message)s')

# console log
stream_handler = logging.StreamHandler()
stream_handler.setLevel(logging.INFO)
stream_handler.setFormatter(log_formatter)

# set it all up
log.addHandler(stream_handler)

def main():
    parser = argparse.ArgumentParser(
        description = (" Train a HMM to detect exon/intron gaps "),
    )

    parser.add_argument("--train", help="path to training data file", default="./test/small_training.fa")
    parser.add_argument("--test", help="path to testing data file", default="./test/small_test.fa")
    opts = parser.parse_args()

    hmm = HMM()

    # train
    training_wheels = read_data(opts.train)
    hmm.train(training_wheels)
    print hmm
    hmm.log_transform()

    # test
    testing_seqs = read_data(opts.test)
    for seq in testing_seqs:
        print hmm.log_viterbi(seq)

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

if __name__ == "__main__":
    main()