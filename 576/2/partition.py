#!/usr/bin/env python
"""
partition.py
author: Kyle McChesney
"""

import os, argparse, logging, itertools, copy
from collections import Counter
import pandas as pd
import numpy as np

def main():


    parser = argparse.ArgumentParser(
        description = (" Find the best partition of a seq"),
    )

    parser.add_argument("--string", default="CGCCATTAT")
    parser.add_argument("-a", type=int, default=-2)
    parser.add_argument("-b", type=int, default=2)
    parser.add_argument("-c", type=int, default=-1)

    opts = parser.parse_args()

    print compute_overlap_prob("GCG","CCG",6)
    #partition(opts.string, opts.a, opts.b, opts.c)
    ##dynamic_part(opts.string, opts.a, opts.b, opts.c)
    #while True:
    #    part = raw_input("Enter a partition string: ").split("|")
    #    print "{} scored: {}".format(part, str(force_score(part, "T", opts.a, opts.b, opts.c)))

def partition(string, a, b, c):

    print "a: {}, b: {}, c: {}".format(str(a),str(b),str(c))

    max_score = float("-inf")
    max_partition = None
    partition_positions = [x for x in range(0, len(string)-1)]
    partition_count = 0

    for i in range(0, len(string)):
        
        # generate all possible partion combos
        # build them from the string and score them
        for part in itertools.combinations(partition_positions, i):

            # part is a partition choice
            # lets get a new copy of the string as a list
            seq_list = list(string)
            partition_count += 1

            count = 1
            for part_location in part:
                # we want to insert a |
                # at each partition location, but need to make sure
                # we update indicies
                # if we have a partition at 0, we want to insert(1, "|")
                # but then a partition at 1 would need to be at 2+1
                seq_list.insert(part_location+count, "|")
                count += 1
            done = "".join(seq_list).split("|")
            score_val = score(done, a, b, c)

            if score_val > max_score:
                max_score = score_val
                max_partition = done

    print "The max partition had score {}:".format(str(max_score))
    print "|".join(max_partition)


def dynamic_part(string, a, b, c):

    # possible labels
    labels = np.array(["A","T","C","G"])

    # possible prefixes
    prefixes = np.array([prefix for prefix in [string[:n] for n in range(1,len(string)+1)]])
    df = pd.DataFrame(index=labels, columns=prefixes).fillna(0)
    
def matches(str, label):
    return str.count(label)

def mismatches(str, label):
    count = str.count(label)
    return len(str) - count

def score(partitions, a, b, c):

    n_p = len(partitions)
    matches = 0
    unmatched = 0

    for chunk in partitions:

        label = label_partition(chunk)
        chunk_matches = chunk.count(label)
        chunk_unmatch = len(chunk) - chunk_matches

        matches += chunk_matches
        unmatched += chunk_unmatch
    return a*n_p + b*matches + c*unmatched

def force_score(partitions, label, a, b, c):

    n_p = len(partitions)
    matches = 0
    unmatched = 0

    for chunk in partitions:

        chunk_matches = chunk.count(label)
        chunk_unmatch = len(chunk) - chunk_matches

        matches += chunk_matches
        unmatched += chunk_unmatch
    return a*n_p + b*matches + c*unmatched

# label a partition based on the most prevelant char
def label_partition(partition):
    return Counter(partition).most_common()[0][0]

def compute_overlap_prob(x,y,give_o):
    n = len(x)

    first_min = min((give_o - 1), n)
    third_max = max((n - give_o + 2), 1)

    first_product = reduce(lambda x,y: x*y, [0.25 for _ in range(0, first_min)], 1)
    print first_product
    second_product = reduce(lambda x,y: x*y, [p(x[i], y[i-give_o+1]) for i in range((give_o - 1), n)], 1)
    print second_product
    third_product = reduce(lambda x,y: x*y, [0.25 for _ in range(third_max, n+1)], 1)
    print third_product
    return first_product * second_product * third_product


def p(a,b):
    return float(3.0/16.0) if a == b else float(1.0/48.0)
if __name__ == "__main__":
    main()