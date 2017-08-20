#!/usr/bin/env python
"""
rev_comp.py
author: Kyle McChesney
"""

import os, argparse, logging, string

def main():
    parser = argparse.ArgumentParser(
        description = ("A simple DNA reverse complementer"),
    )
    parser.add_argument("--dna", required=True)
    opts = parser.parse_args()

    rev_comp(opts.dna)

def rev_comp(dna):
	trans_table = string.maketrans("ATCG","TAGC")
	print dna.translate(trans_table)[::-1]


if __name__ == "__main__":
    main()