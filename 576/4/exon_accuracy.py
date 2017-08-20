#!/usr/bin/env python
"""
exon_accuracy.py
author: Kyle McChesney
"""

import os, argparse

def main():
    parser = argparse.ArgumentParser(
        description = ("Calculate HMM accuracy stats given output and truth"),
    )

    parser.add_argument("--output", help="path to the output file", default="./test/small_output.fa")
    parser.add_argument("--truth", help="path to the truth file", default="./test/small_truth.fa")
    args = parser.parse_args()

    output_seq = "".join(read_data(args.output))
    truth_seq  = "".join(read_data(args.truth))

    if len(output_seq) != len(truth_seq):
        print "The data sets do not have the same number of sequences"
        raise SystemExit

    stats = calc_stats(truth_seq, output_seq)
    print "\n".join(["%0.3f" % x for x in stats])

def calc_stats(truth, output):
    
    # accuracy
    total_pos = len(truth)
    total_correct = 0

    # recall
    total_true_exon = 0
    total_correct_exon = 0
    
    # precision
    # uses total_correct_exon
    total_exon = 0

    for i in xrange(total_pos):

        # is this a true exonic position?
        if truth[i].isupper():
            total_true_exon += 1

        # is this a predicted exonic position?
        if output[i].isupper():
            total_exon += 1

        # is this position correct?
        if truth[i] == output[i]:
            total_correct += 1

            # is this a correct exon?
            if output[i].isupper():
                total_correct_exon += 1

    accuracy = total_correct / float(total_pos)
    recall = total_correct_exon / float(total_true_exon)
    precision = total_correct_exon / float(total_exon)

    return (accuracy, recall, precision)

def read_data(file):

    # ensure it exists
    if file_exists(file):

        seqs = []
        with open(file, 'r') as fh:
            for line in fh:
                seqs.append(line.rstrip())

        return seqs

    else:
        print "%s is not a valid file" % file
        raise SystemExit

def file_exists(read_file):

    fullpath = os.path.abspath(read_file)
    return os.path.exists(fullpath)

if __name__ == "__main__":
    main()