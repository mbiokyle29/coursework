#!/usr/bin/env python
"""
project_2.py
author: Kyle McChesney
"""
import gmpy2 
from gmpy2 import mpz
import logging as log
import itertools

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

    log.info("Starting Discrete Log Calculation")
    log.info("p: %i", p)
    log.info("g: %i", g)
    log.info("y: %i", y)

    m = gmpy2.ceil(gmpy2.sqrt(p))
    log.info("m: %i", m)

    # custom range since builtin has a size limit
    long_range = lambda start, stop: iter(itertools.count(start).next, stop)
    chunk_size = 100000
    
    if m < 100000:
        chunk_size = m
    
    stop_at = chunk_size
    start = 0

    while True:
        chunk = {}
        log.info("Starting chunk from %i to %i", start, stop_at)
        for i in xrange(start, stop_at):
            chunk[gmpy2.powmod(g,i,p)] = i
        for t in long_range(0,m):
            expone = mpz(gmpy2.mul(-m,t))
            g_term = gmpy2.powmod(g, expone, p)
            res = gmpy2.f_mod(gmpy2.mul(y, g_term), p)
            if res in chunk:
                s = chunk[res]
                dc = gmpy2.f_mod(mpz(gmpy2.add(s, gmpy2.mul(m,t))), p)
                log.info("DC LOG FOUND")
                log.info("dc: %i", dc)
                return
        log.info("Completed chunk run: %i to %i  no DC yet :(", start, stop_at)
        start = stop_at
        stop_at += chunk_size

                

if __name__ == "__main__":
    main()