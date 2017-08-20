import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * A* algorithm search
 * 
 * You should fill the search() method of this class.
 */
public class AStarSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public AStarSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main a-star search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {
		// CLOSED list is a Boolean array that indicates if a state associated with a given position in the maze has already been expanded. 
		boolean[][] closed = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];

		// OPEN list (aka Frontier list)
		PriorityQueue<StateFValuePair> open = new PriorityQueue<StateFValuePair>();

		// initialize the root state and add
		// to OPEN list
		// Set initial state
		Square playerSquare = maze.getPlayerSquare();
		
		// Note start state has null parent!
		State startState = new State(playerSquare, null, 0,0);
		int hValue = distanceToGoal(playerSquare);
		StateFValuePair startFVP = new StateFValuePair(startState, startState.getGValue()+hValue);
		
		// Add first start state to frontier
		open.add(startFVP);
		
		while(!open.isEmpty()) {
			// Pop the top element
			StateFValuePair current = open.remove();
			this.noOfNodesExpanded++;
			closed[current.getState().getX()][current.getState().getY()] = true;
			
			// Check if it is a goal state
			if(current.getState().isGoal(this.maze)) {
				drawSolutionPath(current.getState().getParent());
				this.cost = current.getState().getDepth();
				return true;
				
			// Expand
			} else {
				List<State> successors = current.getState().getSuccessors(closed, maze);
				for(State successor : successors) {
					// Check if the successor is already in the frontier
					StateFValuePair successorFVP = new StateFValuePair(successor, successor.getGValue()+distanceToGoal(successor.getSquare()));
					
					// If it is in open, compare g values, keep the best
					if(open.contains(successorFVP)) {
						
						// Must iterate to find the matching one
						Iterator<StateFValuePair> iter = open.iterator();
						StateFValuePair oldToRemove = null;
						boolean removeOld = false;
						
						// Check each
						while(iter.hasNext()) {
							StateFValuePair next = iter.next();
							
							// This is the one that contain() found
							if(next.getState().equals(successorFVP.getState())) { 
								if(successorFVP.getState().getGValue() < next.getState().getGValue()) {
									System.out.println("Removing old");
									removeOld = true;
									oldToRemove = next;
									break;
								}
							}
						}
						
						// If the existing identical state had a worse g(n) value
						if(removeOld && oldToRemove != null) {
							open.remove(oldToRemove);
							open.add(successorFVP);
						}
					// Otherwise just add it
					} else {
						open.add(successorFVP);
					}
				}
			}
		}

		// No solution
		return false;
	}

	/**
	 * Heuristic Function - City Block Distance
	 * @param  square - the sqaure to calculate distance for
	 *
	 * @return int - the value of h(n)
	 */
	private int distanceToGoal(Square square) {
		Square goal = this.maze.getGoalSquare();
		int distance = Math.abs(square.X - goal.X) + Math.abs(square.Y - goal.Y);
		return distance;
	}

}
