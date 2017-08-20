#!/usr/bin/env python
"""
greedy_assemble.py
author: Kyle McChesney
"""

import os, argparse, logging
from itertools import islice
from lib.models import Graph, Node, Edge

# Kenny loggins
log = logging.getLogger(__name__)
log.setLevel(logging.WARN)
log_formatter = logging.Formatter('%(asctime)s {%(levelname)s}: %(message)s')

# console log
stream_handler = logging.StreamHandler()
stream_handler.setLevel(logging.WARN)
stream_handler.setFormatter(log_formatter)

# set it all up
log.addHandler(stream_handler)

def main():
    parser = argparse.ArgumentParser(
        description = (" An implemention of a greedy sequence assembler using Graphs / Ham Paths"),
    )

    parser.add_argument("--reads")
    parser.add_argument("--fastq", help="Option to process reads as a fastq file")
    opts = parser.parse_args()
    
    log.info("Starting Greedy Assembly")
    log.info("Read file: %s", opts.reads)

    if opts.reads:
        nodes = read_file_to_node_set(opts.reads)
        graph = Graph(nodes)
        print(graph.assemble())

    elif opts.fastq:
        nodes = fastq_file_to_node_set(opts.fastq)
        graph = Graph.Graph(nodes)
        print(graph.assemble())

def file_exists(read_file):
	fullpath = os.path.abspath(read_file)
	return os.path.exists(fullpath)

def read_file_to_node_set(read_file):

	if file_exists(read_file):
		
		# set up
		log.info("Building graph from read file: %s", read_file)
		strings_in = 0
		nodes = []

		# read in
		with open(read_file) as fh:
			for line in fh:
				nodes.append(Node(line.rstrip()))
				strings_in += 1

		log.info("Read in %i strings into graph", strings_in)
		return nodes

	else:
		log.warn("%s read file does exist", read_file)
		raise SystemExit("Exiting - Invalid Read File")


def fastq_file_to_node_set(fastq_file):

    if file_exists(fastq_file):
        log.info("Building graph from fastq file: %s", fastq_file)
        strings_in = 0

        # we use a set cause fastq will almost have dupes
        reads = set()
        nodes = []
        index = 2

        with open(fastq_file) as fh:
            for line in islice(fh,1,None,4):
                read = line.rstrip()

                if not read in reads:
                    reads.add(read)
                    nodes.append(Node(read))
                    index += 1
                    strings_in += 1

        log.info("Read in %i strings into graph", strings_in)
        return nodes

if __name__ == "__main__":
    main()