"""
models.py
author: Kyle McChesney 
"""
import logging as log
import math
import itertools

class Cluster():

    def __init__(self, values, annotations):
        self.clusters = [[ Gene(gene, values[gene]) ] for gene in values ]
        self.annotations = annotations

    def __repr__(self):
        return "\n\n".join([str(clust) for clust in self.clusters])

    def cluster(self, mode, k):
        
        while len(self.clusters) > k:
            
            min_distance = float('inf')
            min_pair = None
                    
            # single link
            # for each cluster, the distance is 
            # the min of any genes in each cluster
            if mode == "S":

                # for each cluster
                for clust_a, clust_b in itertools.combinations(self.clusters, 2):

                    # for each gene in cluster
                    for gene_i, gene_k in itertools.product(clust_a, clust_b):
                        dist = self._distance(gene_i, gene_k)
                        if dist < min_distance:
                            min_distance = dist
                            min_pair = [clust_a, clust_b]

            # Complete Link
            # Same as S except take the max
            elif mode == "C":

                # for each cluster
                for clust_a, clust_b in itertools.combinations(self.clusters, 2):
                    
                    max_distance = float("-inf")                
                    for gene_i, gene_k in itertools.product(clust_a, clust_b):
                        
                        dist = self._distance(gene_i, gene_k)
                        if dist > max_distance:
                            max_distance = dist

                    if max_distance < min_distance:
                        min_distance = max_distance
                        min_pair = [clust_a, clust_b]

            # average link
            else:

                # for each cluster
                for clust_a, clust_b in itertools.combinations(self.clusters, 2):
                    
                    average_sum = 0
                    average_elements = 0
                                 
                    for gene_i, gene_k in itertools.product(clust_a, clust_b):
                        
                        dist = self._distance(gene_i, gene_k)
                        average_sum += dist
                        average_elements += 1

                    average_dist = average_sum / float(average_elements)
                    if average_dist < min_distance:
                        min_distance = average_dist
                        min_pair = [clust_a, clust_b]

            # these are set regardless of the mode
            [self.clusters.remove(x) for x in min_pair]
            new_clust = min_pair[0] + min_pair[1]
            self.clusters.append(new_clust)

        return self._str_clusters()


    # assume that this is only for genes
    def _distance(self, i, j):

        if i == j:
            return 0

        distance = math.sqrt(sum(map(lambda x,y: (y-x)**2, i.expression_values(), j.expression_values())))
        return distance
    
    # the genes within a cluster should be ordered by their average expression ratio (from smallest to largest).
    def _str_clusters(self):

        string_rep = ""
        clust_ave = []
    
        for cluster in self.clusters:
            
            # cluster wide average and sorting
            average = round( (sum(x.average for x in cluster) / float(len(cluster))), 3)
            clust_ave.append((cluster, average))
            clust_ave = sorted(clust_ave, key=lambda x: x[1])

        for cluster, average in clust_ave:
            sorted_by_expression = sorted(cluster, key=lambda gene: gene.average)
            
            for gene in sorted_by_expression:
                string_rep += "{} {} {}\n".format(gene.name, self.annotations[gene.name],  "%.3f" % gene.average)

            string_rep +=  "%.3f\n" % average
            string_rep += "\n"

        return string_rep

class Gene():

    def __init__(self, name, exp):
        self.name = name
        self.exp_values = exp
        self.average = sum(self.exp_values) / float(len(self.exp_values))

    def expression_values(self):
        return self.exp_values

    def __repr__(self):
        return "{}".format(self.name)