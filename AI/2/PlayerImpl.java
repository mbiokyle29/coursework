/***************************************************************************************
  PlayerImpl.java
  Implement five functions in this file.
  ---------
  Licensing Information:  You are free to use or extend these projects for
  educational purposes provided that (1) you do not distribute or publish
  solutions, (2) you retain this notice, and (3) you provide clear
  attribution to UW-Madison.
 
  Attribution Information: The Take Stone Games was developed at UW-Madison.
  The initial project was developed by Jerry(jerryzhu@cs.wisc.edu) and his TAs.
  Current version with depthLimit and SBE was developed by Fengan Li(fengan@cs.wisc.edu)
  and Chuck Dyer(dyer@cs.wisc.edu)
  
*****************************************************************************************/

import java.util.*;

public class PlayerImpl implements Player {
	// Identifies the player
	private int name = 0;
	int n = 0;
	

	// Constructor
	public PlayerImpl(int name, int n) {
		this.name = 0;
		this.n = n;
	}

	/**
	 * generateSuccessors
	 * 	find all of the valid next moves from a given move
	 * @param lastMove - the last stone taken
	 * @param takenList - the list of all stones taken
	 * @return successors - the list of all allowed moves
	 */
	@Override
	public ArrayList<Integer> generateSuccessors(int lastMove, int[] takenList) 
	{
		ArrayList<Integer> successors = new ArrayList<Integer>();
		// If first move choose odd < n-2
		if(lastMove == -1) {
			for(int i = (this.n/2)-1; i > 0; i--) {
				// No need to check taken list
				if(!(i%2 == 0)) {
					successors.add(i);
				}
			}
		} else {
			// Generate factors and multiples of lastMove < n
			for(int i = n; i > 0; i--) {
				if(i % lastMove == 0 | lastMove % i == 0) {
					// Make sure it has not been taken
					if(takenList[i] == 0) {
						successors.add(i);
					}
				}
			}
		}
		return successors;
	}

	@Override
	/**
	 * Calculate the max value for a given state
	 * @param s - the gameState to analyze
	 * @param depthLimit - the depthLimit to search through (-1 --> go forever)
	 * @return alpha - the utility function value of the best move for MAX player
	 */
	public double max_value(GameState s, int depthLimit) 
	{
		ArrayList<Integer> possibleMoves = this.generateSuccessors(s.lastMove, s.takenList);
		s.bestMove = -1;
		
		// Do the leaf initialization
		s.leaf = (possibleMoves.size() == 0) ? true : false;
		
		if(depthLimit == 0) {
			if(s.leaf) {
				return -1;
			} else {
				return this.stateEvaluator(s);
			} 
		} else {
			double alpha = -1000;
			for(int successor : possibleMoves) {
				int[] copyList = s.takenList.clone();
				copyList[successor] = 1;
				GameState nextState = new GameState(copyList,successor);
				
				// Generate it's min
				int newDepth = depthLimit;
				if(depthLimit != -1) { newDepth--; }
				double nextValue = this.min_value(nextState, newDepth);
				
				// Do the comparison
				// If there is a tie pick the max
				if(nextValue == alpha) {
					s.bestMove = (s.bestMove > successor) ? s.bestMove : successor;
				}
				
				// Update if it is bigger
				else if(nextValue > alpha) {
					s.bestMove = successor;
					alpha = nextValue;
				}
			}
			return alpha;
		}
	}
	
	@Override
	/**
	 * Calculate the min value for a given state
	 * @param s - the gameState to analyze
	 * @param depthLimit - the depthLimit to search through (-1 --> go forever)
	 * @return beta - the utility function value of the best move for MIN player
	 */
	public double min_value(GameState s, int depthLimit)
	{
		ArrayList<Integer> possibleMoves = this.generateSuccessors(s.lastMove, s.takenList);
		s.bestMove = -1;
		
		// Do the leaf initialization
		s.leaf = (possibleMoves.size() == 0) ? true : false;
		
		if(depthLimit == 0) {
			if(s.leaf) {
				return 1;
			} else {
				return (-1)*this.stateEvaluator(s);
			} 
		} else {
			double beta = 1000;
			for(int successor : possibleMoves) {
				int[] copyList = s.takenList.clone();
				copyList[successor] = 1;
				GameState nextState = new GameState(copyList,successor);
				
				// Generate it's max
				int newDepth = depthLimit;
				if(depthLimit != -1) { newDepth--; }
				double nextValue = this.max_value(nextState, newDepth);
				
				// Do the comparison
				// If there is a tie pick the max
				if(nextValue == beta) {
					s.bestMove = (s.bestMove > successor) ? s.bestMove : successor;
				}
				
				// Update if it is smaller
				else if(nextValue < beta) {
					s.bestMove = successor;
					beta = nextValue;
				}
			}
			return beta;
		}
	}
	
	@Override
	/**
	 *  The toplevel move function - it generates the best move based on MINMAX
	 * @param lastMove - the last move that was made
	 * @param takenList - the list of stones that have been taken 
	 * @param depthLimit - the depth to search (-1 == exhaustive)
	 * @return
	 */
	public int move(int lastMove, int[] takenList, int depthLimit) {
		// Generate game state
		GameState currState = new GameState(takenList, lastMove);
		
		// Get the best move
		this.max_value(currState, depthLimit);
		
		// return the best move
		return currState.bestMove;
	}
	

	@Override
	/**
	 * stateEvaluator - the utility function to score states with
	 * @param s - the state to be scored
	 * @return boardValue - the calculated value for the given state
	 */
	public double stateEvaluator(GameState s)
	{
		double boardValue;
		ArrayList<Integer> succ = this.generateSuccessors(s.lastMove, s.takenList);
		
		// If one is still there it is even
		if(s.takenList[1] == 0) {
			boardValue = 0;
		
		// If it is 1
		} else if(s.lastMove == 1) {
			// get list of valid moves 
			boardValue = ((succ.size() % 2) == 0) ? -0.5 : 0.5;
		
		// If it is prime
		} else if(isPrime(s.lastMove)) {
			int multipleCount = 0;
			for(int move : succ) {
				if(s.lastMove % move == 0) {
					multipleCount++;
				}
			}
			boardValue = ((multipleCount % 2) == 0) ? -0.7 : 0.7;
		
		// If it is composite
		// It has to be now
		} else  {
			// find the largest prime that can divide lastMove
			int largestPrime = findLargestPrime(s.lastMove, succ);
			
			// Count its multiples
			int count = countMultiples(largestPrime, succ);
			
			// If the count is odd, return 0.6. Return -0.6 otherwise.
			boardValue = ((count % 2) == 0) ? -0.6 : 0.6;
		}
		return boardValue;
	}
	
	/**
	 * isPrime - check if a number is prime
	 * @param value - number to be checked
	 * @return true = prime, false = not prime
	 */
	private static boolean isPrime(int value) {
		// A number will be not prime if is even
		// or have an odd divisor (less then value)
		
		// Check if it is even
		if(value % 2 == 0) {
			return false;
		} else {
			// Check the odds 
			for(int i = 3; i < value; i+=2) {
				if(value % i == 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * find the largest prime number that is a valid move in a list of moves
	 * @param lastMove - the last move used to check if a move is valid
	 * @param list
	 * @return the largest prime
	 */
	private static int findLargestPrime(int lastMove, ArrayList<Integer> list) {
		int largest = 0;
		for(int val : list) {
			if(isPrime(val) && (lastMove % val == 0)) {
				largest = (val > largest) ? val : largest;
			}
		}
		return largest;
	}
	
	/**
	 * countMultiples - determine the number of multiples of a number that are in a list
	 * @param val - the number to find multiples of
	 * @param list - the list of potential values to check
	 * @return the number of multiples
	 */
	private static int countMultiples(int val, ArrayList<Integer> list) {
		int count = 0;
		for(int item : list) {
			if(val % item == 0) {
				count++;
			}
		}
		return count;
	}
}
