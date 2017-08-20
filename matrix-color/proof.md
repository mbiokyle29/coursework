# Proof of coloring matrix with crosses

We can model this problem as a deterministic finite automata state machine. Each state represents a 'board' or instances of the matrix with a different configuration of white and black colored tiles. The start state qₒ is the state in which all tiles are black. There is only one acceptence state, the one which has all tiles colored white.

As example
```
BBB   (1,2)    BBW
BBB   --->     BWW
BBB            BBW  
```

The transitions represent placements of a cross onto a given tile. We could place a cross and any tile in the state, so there will be at most N*N transitions from each state.

The language that the FA accepts as input consists of integer tuples of size two. Represetning the location to place the next cross.

If a given *message* of tuples ends in the accept state, the message is accepted. Else it is not.

The goal is to show that there exists some *message* for every N sized FA machine of this time which will be accepted.

I have developed an interactive program for solving these problems. Below are the outputs for varying Ns


### N = 1 Solution
This is trivial
```
./finite_automata.py -N 1
Printing Colored Matrix of size 1 x 1
[
    ['B']
]
Completion status: False
Enter one of the following: i,j | N | q: 0,0

Printing Colored Matrix of size 1 x 1
[
    ['W']
]
Completion status: True
Solved!
[[0, 0]]

```


### N = 2 Solution
```
./finite_automata.py -N 2
Printing Colored Matrix of size 2 x 2
[
    ['B', 'B']
    ['B', 'B']
]
Completion status: False
Enter one of the following: i,j | N | q: 0,0

Printing Colored Matrix of size 2 x 2
[
    ['W', 'W']
    ['W', 'B']
]
Completion status: False
Enter one of the following: i,j | N | q: 1,0

Printing Colored Matrix of size 2 x 2
[
    ['B', 'W']
    ['B', 'W']
]
Completion status: False
Enter one of the following: i,j | N | q: 1,1

Printing Colored Matrix of size 2 x 2
[
    ['B', 'B']
    ['W', 'B']
]
Completion status: False
Enter one of the following: i,j | N | q: 0,1

Printing Colored Matrix of size 2 x 2
[
    ['W', 'W']
    ['W', 'W']
]
Completion status: True
Solved!
[[0, 0], [1, 0], [1, 1], [0, 1]]
```

Even this small sized examples shows us some useful stuff.
 1. There are solutions which involve flipping already white tiles back to black
    - There is not always a clear solution (non overlaping moves)
 2. There exists some known structure to the (n-1) solution.
    - namley that it will contain at most 4 black tiles, all of which lay within one cross placement (move)