package PathFinding;

import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

public class TileMap implements TileBasedMap, java.io.Serializable {
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;

    public int[][] MAP = {
        {1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,1,1,1,1},
        {1,0,1,1,1,0,1,1,1,1},
        {1,0,1,1,1,0,0,0,1,1},
        {1,0,0,0,1,1,1,0,1,1},
        {1,1,1,0,1,1,1,0,0,0},
        {1,0,1,0,0,0,0,0,1,0},
        {1,0,1,1,1,1,1,1,1,0},
        {1,0,0,0,0,0,0,0,0,0},
        {1,1,1,1,1,1,1,1,1,0}
    };

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