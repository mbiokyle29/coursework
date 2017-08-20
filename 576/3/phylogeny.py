#!/usr/bin/env python
"""
phylogeny.py
author: 
"""
import collections

def Tree():
    return collections.defaultdict(Tree)

def main():
    infin = float("inf")
    mtx = [ 
        [infin, 4,     3,     4,     6    ], 
        [infin, infin, 5,     4,     8    ], 
        [infin, infin, infin, 5,     5    ], 
        [infin, infin, infin, infin, 8    ], 
        [infin, infin, infin, infin, infin]
    ]
    clusters = ["1", "2", "3", "4", "5"]
    for i in range(len(mtx)):
        print mtx[i]

    (merge_i, merge_j) = smallest(mtx)
    
    i_val = clusters.pop(merge_i)
    j_val = clusters.pop(merge_j)

    i_dists = mtx[merge_i]
    j_dists = mtx[merge_j]

    clusters.append([i_val, j_val])

    new_mtx = []
    for row in mtx:
        
    print clusters

def smallest(mtx):
    smallest = float("inf")
    smallest_i = None
    smallest_j = None

    for i in range(len(mtx)):
        for j in range(len(mtx[i])):
            if mtx[i][j] < smallest:
                smallest = mtx[i][j]
                smallest_i = i
                smallest_j = j

    if smallest_i is None:
        raise SystemExit("No elements less than infinity found")

    return (smallest_i, smallest_j)

#def update_dist()

if __name__ == "__main__":
    main()