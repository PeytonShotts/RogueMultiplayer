package MapGen;

import java.util.LinkedList;
import java.util.Random;

import MapCode.*;

public class MapGen2 {
	
	public static LinkedList<Room> roomList = new LinkedList<Room>();
	public static int roomCount = 0;

	public static Map create(int mapWidth, int mapHeight, int mapLayers)
	{
		
		Map newMap = new Map(mapWidth, mapHeight, mapLayers);
		
		Random rand = new Random();
		
		for (int mapY=0; mapY<mapHeight; mapY++)
		{
			int start = 0;
			int max = mapWidth-1;
			int add = 1;
			if (mapY % 2 == 0) 
			{
				start = mapWidth-1;
				max = 0;
				add = -1;
			}
			for (int mapX=start; mapX!=max; mapX += add)
			{
				if (newMap.layers[0].data[mapX][mapY] == 0)
				{
					
					int  r = rand.nextInt(100) + 0;
					if (r == 50 && mapX < mapWidth-10 && mapY < mapHeight-10 && mapX > 2 && mapY > 2)
					{
						//generate room parameters
						int roomWidth = rand.nextInt(8) + 3;
						int roomHeight = rand.nextInt(8) + 3;
						Room newRoom = new Room();
						newRoom.x = mapX;
						newRoom.y = mapY;
						newRoom.width = roomWidth;
						newRoom.height = roomHeight;
						roomList.add(newRoom);
						roomCount++;
						
						//create room
						for (int roomY=0; roomY<roomHeight; roomY++)
						{
							for (int roomX=0; roomX<roomWidth; roomX++)
							{
								newMap.layers[0].data[mapX+roomX][mapY+roomY] = 1;
							}
						}
						
						if (roomList.size() >= 2)
						{
							Room roomOne = roomList.get(roomCount-2);
							Room roomTwo = roomList.get(roomCount-1);
							
							
							Point point1 = new Point(roomOne.x + (roomOne.width/2), roomOne.y + (roomOne.height/2));
							Point point2 = new Point(roomTwo.x + (roomTwo.width/2), roomTwo.y + (roomTwo.height/2));
							
							int xdistance = point2.x - point1.x;
							int ydistance = point2.y - point1.y;
							
							while (point1.y != point2.y)
							{
								point1.y += Math.signum(ydistance);
								newMap.layers[0].data[point1.x][point1.y] = 1;
							}
							while (point1.x != point2.x)
							{
								point1.x += Math.signum(xdistance);
								newMap.layers[0].data[point1.x][point1.y] = 1;
							}
							
						}
					}
				}		
			}
		}
		
		return newMap;
	}
}
