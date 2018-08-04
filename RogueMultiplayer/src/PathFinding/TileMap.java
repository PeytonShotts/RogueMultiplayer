package PathFinding;

import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

public class TileMap implements TileBasedMap, java.io.Serializable {
    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;

    public int[][] MAP = new int[WIDTH][HEIGHT];

    @Override
    public boolean blocked(PathFindingContext ctx, int x, int y) {
        return MAP[x][y] != 0;
    }

    @Override
    public float getCost(PathFindingContext ctx, int x, int y) {
        return 1.0f;
    }

    @Override
    public int getHeightInTiles() {
        return HEIGHT;
    }

    @Override
    public int getWidthInTiles() {
        return WIDTH;
    }

    @Override
    public void pathFinderVisited(int x, int y) {}
		
	}