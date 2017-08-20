#!/usr/bin/env python
"""
finite_automata.py
author: Kyle McChesney
"""

import os, argparse, logging, random, itertools

def main():
    parser = argparse.ArgumentParser(
        description = (" Solve the FA problem of matrix coloring with cross patterns"),
    )

    parser.add_argument("-N", help="The size of the matrix to solve", default=4, type=int)
    parser.add_argument("--mode", help="Exection mode, manual or automatic", choices=['auto','manual'], default='manual')
    opts = parser.parse_args()

    N = opts.N
    mode = opts.mode

    # model the matrix as a boolean matrix, false = black, true = white
    # solve the N = 4 version
    matrix = [["B" for _ in range(N)] for _ in range(N)]
    

    if mode == "manual":
        moves = []
        running = True
        while(running):
            
            print "\n\n"
            pretty_print_mtx(matrix)
            status = is_complete(matrix)
            print "Completion status: {}".format(str(is_complete(matrix)))
            
            if status:
                print "Solved!"
                print moves
                running = False
                continue
            
            user_input = raw_input("Enter one of the following: i,j | N | q: ")
            user_input = user_input.split(",")

            if len(user_input) == 1:
                command = user_input[0]

                if command == "q":
                    running = False
                    print "Quitting"
                    print "Matrix is of size {}".format(len(matrix[0]))
                    print "Completion status: {}".format(str(is_complete(matrix)))
                    pretty_print_mtx(matrix)
                    print "Moves:"
                    print moves
                    continue

                else:
                    try:
                        new_n = int(command)
                        sure = raw_input("Are you sure you want to create a new matrix of size {}  (y/n): ".format(new_n))
                        if sure == "y":
                            print "OLD Matrix is of size {}".format(len(matrix[0]))
                            print "Completion status: {}".format(str(is_complete(matrix)))
                            pretty_print_mtx(matrix)
                            print "Moves:"
                            print moves
                            matrix = [["B" for _ in range(new_n)] for _ in range(new_n)]
                            moves = []
                        continue
                            
                    except ValueError:
                        print "Must enter an integer value for the new matrix size"
                        continue
            else:
                try:
                    i = int(user_input[0])
                    j = int(user_input[1])
                    place_cross(matrix, i, j)
                    moves.append([i,j])
                except ValueError:
                    print "i,j must be ints seperated by a comma no spaces"
                    continue

    else:
        print "Auto Mode starting"
        done = is_complete(matrix)
        rating = score(matrix)
        available_moves = [ x for x in itertools.product(range(N), range(N))]
        moves = []

        # smart stuff here
        while not done and len(available_moves) > 0:

            move = random.choice(available_moves)

            place_cross(matrix, move[0], move[1])
            new_score = score(matrix)

            if new_score > rating*.7:
                rating = new_score
                done = is_complete(matrix)
                available_moves.remove(move)
                #moves.append(move)

            else:
                place_cross(matrix, move[0], move[1])

            #available_moves.remove(move)

        pretty_print_mtx(matrix)
        print moves

def score(matrix):
    count = 0
    N = len(matrix[0])
    for i in range(N):
        for j in range(N):
            if matrix[i][j] == "W":
                count += 1

    return count

def is_complete(matrix):

    all_white = True
    N = len(matrix[0])
    for i in range(N):
        for j in range(N):
            all_white = all_white and (matrix[i][j] == "W")

    return all_white

def place_cross(matrix, i,j):

    # set N
    N = len(matrix[0])

    # change the center
    if i in range(N) and j in range(N):
        matrix[i][j] = "W" if matrix[i][j] == "B" else "B"

    # other spots
    up = i-1
    down = i+1
    left = j-1
    right = j+1

    if up in range(N) and j in range(N):
        matrix[up][j] = "W" if matrix[up][j] == "B" else "B"

    if i in range(N) and left in range(N):
        matrix[i][left] = "W" if matrix[i][left] == "B" else "B"

    if down in range(N) and j in range(N):
        matrix[down][j] = "W" if matrix[down][j] == "B" else "B"

    if i in range(N) and right in range(N):
        matrix[i][right] = "W" if matrix[i][right] == "B" else "B"

def pretty_print_mtx(matrix):
    N = len(matrix[0]) # its square
    print "Printing Colored Matrix of size {} x {}".format(N,N)
    print "["
    for n in range(N):
        print "\t"+str(matrix[n])
    print "]"

if __name__ == "__main__":
    main()