#!/bin/sh

# Uncomment the line below to run a compiled C or C++ program
#./overlap_align $1 $2 $3 $4 $5

# Uncomment the line below if you write a Java program
#java overlap_align $1 $2 $3 $4 $5

# Uncomment the line below for a Python program
python overlap_align.py --read-file $1 --match-score $2 --mismatch-score $3 --gap-score $4 --space-score $5

# Uncomment the line below for a Perl program
#perl overlap_align.pl $1 $2 $3 $4 $5

# Uncomment the line below for an R program
#Rscript overlap_align.R $1 $2 $3 $4 $5