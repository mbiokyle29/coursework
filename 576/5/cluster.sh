#!/bin/sh

# Uncomment the line below to run a compiled C or C++ program
#./cluster $1 $2 $3

# Uncomment the line below if you write a Java program
#java cluster $1 $2 $3

# Uncomment the line below for a Python program
python cluster.py --exp-file $1 --clust-type $2 -k $3

# Uncomment the line below for a Perl program
#perl cluster.pl $1 $2 $3

# Uncomment the line below for an R program
#Rscript cluster.R $1 $2 $3
