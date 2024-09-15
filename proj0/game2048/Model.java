package game2048;

import java.util.Formatter;
import java.util.Iterator;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author CYZ
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public boolean tilt(Side side) {
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.
        board.setViewingPerspective(side);

        boolean changed;
        changed = false;
        boolean prevStatus = false;//检测上一次移动的状态(是否合并)
        /** NOTE:
         * 1.Tile@(0,0)在左下角
         * 2.Tile.next()当Tile.next值为null时返回的是this,而非null.
         * 3.board.move()调用后,原tile被解引用,新Tile的next指针为null.因此不应该使用Tile.next().
         * 4.无需调用Tile.move()以及Tile.merge()，已经封装在board.move()中.
         * 5.更新changed状态应在每次board.move()后
         */
        for(int col = 0; col < board.size(); col++) {
            for(int row = board.size()-1; row >= 0; row--) {
                Tile me = board.tile(col, row);
                if(me == null){
                    //跳过空Tile
                    continue;
                }

                //寻找最近的非空Tile
                Tile adjacent = null;
                int rowAdj = row+1;
                for(; rowAdj < board.size() ; rowAdj++) {
                    if(board.tile(col, rowAdj) != null) {
                        adjacent = board.tile(col, rowAdj);
                        //不能直接调用adjacent.row()&col()，返回的是原视角的坐标;
                        break;
                    }
                }

                //若找不到，则移动至顶格，并跳过后续判断
                if(adjacent == null) {
                    prevStatus = board.move(col, board.size()-1,me);
                    if(me.row() != board.size()-1){
                        //若本身为顶格，则未改变棋盘
                        changed = true;
                    }
                    continue;
                }

                /**
                 * 移动逻辑:
                 * 若上次遍历已进行合并操作,或Tile值不相同,则移动至最近Tile下方
                 * 若上次遍历未进行合并操作,且Tile值相同,则合并
                 * 若都不满足则pass
                 */
                if(prevStatus || (adjacent.value() != me.value())) {
                    prevStatus = board.move(col, rowAdj - 1, me);
                    changed = true;
                }
                else if(adjacent.value() == me.value()){
                    prevStatus = board.move(col, rowAdj, me);
                    score += me.value()*2;
                    changed = true;
                }
            }
        }
        board.setViewingPerspective(Side.NORTH);
        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        boolean hasEmptyTile = false;
        Iterator<Tile> iter = b.iterator();
        while(iter.hasNext()){
            Tile tile = iter.next();
            if(tile == null){
                hasEmptyTile = true;
                break;
            }
        }
        return hasEmptyTile;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        boolean hasMaxTile = false;
        Iterator<Tile> iter = b.iterator();
        while(iter.hasNext()){
            Tile tile = iter.next();
            if(tile != null && tile.value() == MAX_PIECE){
                hasMaxTile = true;
                break;
            }
        }
        return hasMaxTile;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        boolean hasEmptyTile = emptySpaceExists(b);
        boolean hasSameAdjacentTiles = false;
        if(!hasEmptyTile){
            Iterator<Tile> iter = b.iterator();
            while(iter.hasNext() && !hasSameAdjacentTiles){
                Tile tile = iter.next();
                int col = tile.col(),row = tile.row();
                Tile[] adjacent = new Tile[4];
                adjacent[0] = (col == 0)?null:b.tile(col-1,row); //left
                adjacent[1] = (col == b.size()-1)?null:b.tile(col+1,row); //right
                adjacent[2] = (row == 0)?null:b.tile(col,row-1); //up
                adjacent[3] = (row == b.size()-1)?null:b.tile(col,row+1);//down
                for(Tile t:adjacent){
                    if(t != null && t.value() == tile.value()){
                        hasSameAdjacentTiles = true;
                        break;
                    }
                }
            }
        }
        return hasEmptyTile || hasSameAdjacentTiles;
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
