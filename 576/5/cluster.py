#!/usr/bin/env python
"""
cluster.py
author: Kyle McChesney
"""

# imports
import os, argparse
import logging as log
from lib.models import Cluster

# configure the logger
log.basicConfig(format='%(asctime)s {%(levelname)s}| %(message)s', 
                level=log.WARN, datefmt='%m/%d/%Y-%I:%M:%S')

def main():
    parser = argparse.ArgumentParser(
        description = (" Agglomerative Hierarchical Cluster Implementation "),
    )
    
    # args
    parser.add_argument("--exp-file", required=True, help="Expression Matrix / TSV file")
    parser.add_argument("--clust-type", default="S", choices=["S", "C", "A"], help="Type of clustering")
    parser.add_argument("-k", type=int, help="Number of clusters", default=2)
    opts = parser.parse_args()

    (annotations, expression) = read_exp_data(opts.exp_file)
    
    # report some stuff
    log.info("Loaded expression/annotations for %i genes", len(annotations))
    clusterObj = Cluster(expression, annotations)
    print clusterObj.cluster(opts.clust_type, opts.k)

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

if __name__ == "__main__":
    main()