package lab11.graphs;

import java.util.PriorityQueue;

/**
 * @author Josh Hug
 */
public class MazeAStarPath extends MazeExplorer {
    private int s;
    private int t;
    private boolean targetFound = false;
    private Maze maze;

    public MazeAStarPath(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    /**
     * Estimate of the distance from v to the target.
     */
    private int h(int v) {
        return Math.abs(maze.toX(v) - maze.toX(t)) + Math.abs(maze.toY(v) - maze.toY(t));
    }

    /**
     * Performs an A star search from vertex s.
     */
    private void astar(int s) {
        PriorityQueue<aStarNode> q = new PriorityQueue<>();
        distTo[s] = 0;
        edgeTo[s] = s;
        q.offer(new aStarNode(s, distTo[s], h(s)));
        while (!q.isEmpty()) {
            aStarNode current = q.poll();
            int currentDis = distTo[edgeTo[current.v]] + 1;
            distTo[current.v] = currentDis;
            marked[current.v] = true;

            if (current.v == t) {
                announce();
                break;
            }
            for (int adjacent : maze.adj(current.v)) {
                if (!marked[adjacent]) {
                    edgeTo[adjacent] = current.v;
                    q.offer(new aStarNode(adjacent, currentDis + 1, h(adjacent)));
                }
            }
            announce();
        }
    }

    static class aStarNode implements Comparable<aStarNode> {
        int v, est;

        aStarNode(int v, int d, int h) {
            this.v = v;
            est = d + h;
        }

        @Override
        public int compareTo(aStarNode o) {
            return est - o.est;
        }
    }

    @Override
    public void solve() {
        astar(s);
    }

}

