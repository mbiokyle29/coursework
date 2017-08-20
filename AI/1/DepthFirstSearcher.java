import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Depth-First Search (DFS)
 * 
 * You should fill the search() method of this class.
 */
public class DepthFirstSearcher extends Searcher {
	
	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public DepthFirstSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main depth first search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {
		// CLOSED list is a 2D Boolean array that indicates if a state associated with a given position in the maze has already been expanded.
		boolean[][] closed = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];

		// Stack implementing the Frontier list
		LinkedList<State> stack = new LinkedList<State>();
		
		// Set inital state
		Square playerSquare = maze.getPlayerSquare();
		
		State startState = new State(playerSquare, null, 0,0);
		stack.add(startState);
		
		// Run the search until stack is empty or solution found
		while (!stack.isEmpty()) {
			State current = stack.pop();
			this.noOfNodesExpanded++;
			
			//  Check if the newly poped state is a goal
			if(current.isGoal(this.maze)) {
				drawSolutionPath(current.getParent());
				this.cost = current.getDepth();
				return true;
				
			// It wasnt a goal so we expand it
			} else {
				ArrayList<State> successors = current.getSuccessors(closed, maze);
				if(!successors.isEmpty()) {
					stack.addAll(0,successors);
					closed[current.getX()][current.getY()] = true;
				}
			}
		}
		// return false if no solution was found and frontier is empty
		return false;
	}
}
