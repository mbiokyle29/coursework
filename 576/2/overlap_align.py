#!/usr/bin/env python
"""
overlap_align.py
author: kgmcchesney
CS576 HW2 Overlap Alignment
"""

import os, argparse, logging
from lib.models import OverlapAligner

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
        description = (" A script to find the optimal alignment of a suffix/prefix pair"),
    )

    # required args
    parser.add_argument("--read-file", help="path to read file with 2 reads", required=True, type=str)
    parser.add_argument("--match-score", help="score for an alignment match", default=1, type=int)
    parser.add_argument("--mismatch-score", help="score for an alignment mis-match", default=-1, type=int)
    parser.add_argument("--gap-score", help="score for an alignment gap", default=-2, type=int)
    parser.add_argument("--space-score", help="score for an space-score", default=-1, type=int)

    opts = parser.parse_args()

    (x_read, y_read) = get_reads(opts.read_file)
    
    rows = len(x_read) + 1
    cols = len(y_read) + 1

    alignment = OverlapAligner(x_read, y_read, rows, cols, opts.match_score, opts.mismatch_score, opts.gap_score, opts.space_score)

    log.info("Done initilizing matricies")
    log.info("Starting alignment on %s and %s", x_read, y_read)

    # run this bad boy
    # start at (1,1) --> (1,2) to (1,cols) then (2,1)
    alignment.align()
    

def file_exists(read_file):

    fullpath = os.path.abspath(read_file)
    log.info("checking if: %s is a real file", fullpath)
    return os.path.exists(fullpath)

def get_reads(file):

    # ensure it exists
    if file_exists(file):
        reads = []
        with open(file, 'r') as fh:
            reads.extend([line.rstrip() for line in fh])

        if len(reads) != 2:
            log.warn("%s did not have two reads in it!", file)
            raise SystemExit

        return reads

    else:
        log.warn("%s is not a valid file", file)
        raise SystemExit

if __name__ == "__main__":
    main()