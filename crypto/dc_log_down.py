#!/usr/bin/env python
"""
project_2.py
author: Kyle McChesney
"""
import gmpy2 
from gmpy2 import mpz
import logging as log

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

    # custom range since builtin has a size limit
    log.info("Starting brute force in reverse")
    try:
        i = p
        while p > 2181019112:
            if check_dc_brute(i,g,p) == y:
                log.info("Discrete Log found, x = {}".format(i))
                return
            p -= 1
            log.info(p)    
    except KeyboardInterrupt:
        log.info("Stopped at: {}".format(i))

def check_dc_brute(i,g,N):
    val = gmpy2.powmod(i,g,N)
    return val

if __name__ == "__main__":
    main()