package lab11.graphs;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 *  @author Josh Hug
 */
public class MazeBreadthFirstPaths extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */
    private final int s;
    private final int t;

    public MazeBreadthFirstPaths(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
    }

    /** Conducts a breadth first search of the maze starting at the source. */
    private void bfs() {
        Queue<Integer> q = new ArrayDeque<>();
        q.offer(s);
        distTo[s] = 0;
        edgeTo[s] = s;
        while (!q.isEmpty()) {
            int current = q.poll();
            distTo[current] = distTo[edgeTo[current]] + 1;
            marked[current] = true;

            if (current == t) {
                announce();
                break;
            }
            for (int adjacent : maze.adj(current)) {
                if (!marked[adjacent]) {
                    edgeTo[adjacent] = current;
                    q.offer(adjacent);
                }
            }
            announce();
        }
    }


    @Override
    public void solve() {
        bfs();
    }
}

