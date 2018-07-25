package PathFinding;

import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;


public class AStar {

    private static final int MAX_PATH_LENGTH = 25;

    
    public static void main(String[] args) {
    	
    	TileMap map = new TileMap();
        
        getPath(map, 1, 1, 1, 6);



    }
    
    
    public static Path getPath(TileMap map, int startX, int startY, int goalX, int goalY)
    {
        System.out.println(startX + " " + startY + " " + goalX + " " + goalY);
        
        for (int y=0; y<10; y++)
        {
            for (int x=0; x<10; x++)
            {
            	System.out.print(map.MAP[x][y] + " ");
            }
            
            System.out.print("\n");
        }
        
        
        AStarPathFinder pathFinder = new AStarPathFinder(map, MAX_PATH_LENGTH, false);
        Path path = pathFinder.findPath(null, startX, startY, goalX, goalY);

        if (path != null)
        {
            int length = path.getLength();
            
            System.out.println("Found path of length: " + length + ".");

            for(int i = 0; i < length; i++) {
                System.out.println("Move to: " + path.getX(i) + "," + path.getY(i) + ".");
            }
        }
        
		return path;
    }


}
