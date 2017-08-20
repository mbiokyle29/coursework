#!/usr/bin/env python
"""
project_2.py
author: Kyle McChesney
"""
import gmpy2 
import logging as log
from gmpy2 import mpz
from multiprocessing import Pool
import itertools
import redis
import argparse

# configure the logger
log.basicConfig(format='%(asctime)s {%(levelname)s}| %(message)s', 
                level=log.INFO, datefmt='%m/%d/%Y-%I:%M:%S')

def main():

    # predefine these, we will stop overwriting them when ready to run the big numbers
    p=mpz('13407807929942597099574024998205846127'
         '47936582059239337772356144372176403007'
         '35469768018742981669034276900318581848'
         '6050853753882811946569946433649006084171'
         )
    g=mpz('117178298803662070095161175963353670885'
         '580849999989522055999794590639294997365'
         '837466705721764714603129285948296754282'
         '79466566527115212748467589894601965568'
        )
    y=mpz('323947510405045044356526437872806578864'
         '909752095244952783479245297198197614329'
         '255807385693795855318053287892800149470'
         '6097394108577585732452307673444020333'
         )
    
    parser = argparse.ArgumentParser(
        description = (" Calculate the discrete log of some number mod p"),
    )

    # tuning params
    # computational args
    # parser.add_argument("-p", "--prime", help="The large prime number to use as the base", type=int, default=9048610007)
    # parser.add_argument("-g", "--generator", help="The generator for the group based on the prime", type=int, default=5)
    # parser.add_argument("-y", "--value", help="The value whos discrete log we wish to compute", type=int, default=3668993056)

    args = parser.parse_args()

    # p = mpz(args.prime)
    # g = mpz(args.generator)
    # y = mpz(args.value)

    log.info("Starting Discrete Log Calculation")
    log.info("p: %i", p)
    log.info("g: %i", g)
    log.info("y: %i", y)

    results = redis.StrictRedis(host='localhost', port=6379, db=0)

    # custom range since builtin has a size limit
    long_range = lambda start, stop: iter(itertools.count(start).next, stop)

    # compute m
    (m, rem) = gmpy2.isqrt_rem(p)
    if rem > 0:
        m += 1

    log.info("m: %i", m)
    
    # # compute generator ^ j for  0 <= j < m
    # for value,key in pool.map(alpha_to_the_j, zip(itertools.repeat(g), itertools.repeat(p), long_range(0,m)), 10000):
    #     results.set(key, mpz(value))    
    for i in long_range(0,m):
        (key, value) = alpha_to_the_j(g, p, i, m)
        results.set(key,mpz(value))

    for i in long_range(1, m):
        g_neg = gmpy2.powmod(g, -i , p)
        looking_for = gmpy2.f_mod(gmpy2.mul(y, g_neg), p)

        # if looking for is a g^j
        if results.exists(looking_for):

            # then looking for is a a^j
            j = mpz(int(results.get(looking_for)))
            x = gmpy2.mul(m,j) + i

            log.info("Discrete log is: {}".format(x))
            return

def alpha_to_the_j(generator, N, j, m):
    product = gmpy2.mul(j,m)
    return (gmpy2.powmod(generator,product,N), j)

if __name__ == "__main__":
    main()