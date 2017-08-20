import java.util.ArrayList;

/**
 * A state in the search represented by the (x,y) coordinates of the square and
 * the parent. In other words a (square,parent) pair where square is a Square,
 * parent is a State.
 * 
 * You should fill the getSuccessors(...) method of this class.
 * 
 */
public class State {

	private Square square;
	private State parent;
	private final char forestChar = '%';

	// Maintain the gValue (the distance from start)
	// You may not need it for the DFS but you will
	// definitely need it for AStar
	private int gValue;

	// States are nodes in the search tree, therefore each has a depth.
	private int depth;

	/**
	 * @param square
	 *            current square
	 * @param parent
	 *            parent state
	 * @param gValue
	 *            total distance from start
	 */
	public State(Square square, State parent, int gValue, int depth) {
		this.square = square;
		this.parent = parent;
		this.gValue = gValue;
		this.depth = depth;
	}

	/**
	 * @param visited
	 *            closed[i][j] is true if (i,j) is already expanded
	 * @param maze
	 *            initial maze to get find the neighbors
	 * @return all the successors of the current state
	 */
	public ArrayList<State> getSuccessors(boolean[][] closed, Maze maze) {
		ArrayList<State> validNeighbors = new ArrayList<State>();
		
		// Store the neighbor squares in an array
		// order matching what should be pushed
		Square left = new Square(this.getX(), this.getY()-1);
		Square down = new Square(this.getX()+1, this.getY());
		Square right = new Square(this.getX(), this.getY()+1);
		Square up = new Square(this.getX()-1, this.getY());
		Square[] neighbors = {up,right,down,left};
		
		// Validate each neighbor and potentially add it to the return list
		for(Square neighbor : neighbors) {
			
			// First make sure the neighbor is in bounds
			if(maze.checkSquareInBounds(neighbor.X, neighbor.Y)) {
				
				// Make sure the neighbor is not a forest
				if(!(maze.getSquareValue(neighbor.X, neighbor.Y) == forestChar)) {
				
					// If its valid make sure it has not been visited
					if(!closed[neighbor.X][neighbor.Y]) {
						
						// This is a valid neighbor add it to the return list
						State neighborState = new State(neighbor, this, this.gValue+1, this.depth+1);
						validNeighbors.add(neighborState);
					}
				}
			}
		}
		return validNeighbors;
	}

	/**
	 * @return x coordinate of the current state
	 */
	public int getX() {
		return square.X;
	}

	/**
	 * @return y coordinate of the current state
	 */
	public int getY() {
		return square.Y;
	}

	/**
	 * @param maze initial maze
	 * @return true is the current state is a goal state
	 */
	public boolean isGoal(Maze maze) {
		if (square.X == maze.getGoalSquare().X
				&& square.Y == maze.getGoalSquare().Y)
			return true;

		return false;
	}

	/**
	 * @return the current state's square representation
	 */
	public Square getSquare() {
		return square;
	}

	/**
	 * @return parent of the current state
	 */
	public State getParent() {
		return parent;
	}

	/**
	 * You may not need g() value in the DFS but you will need it in A-star
	 * search.
	 * 
	 * @return g() value of the current state
	 */
	public int getGValue() {
		return gValue;
	}

	/**
	 * @return depth of the state (node)
	 */
	public int getDepth() {
		return depth;
	}
	
	public String toString() {
		return "("+this.getX()+","+this.getY()+")";
	}
}
