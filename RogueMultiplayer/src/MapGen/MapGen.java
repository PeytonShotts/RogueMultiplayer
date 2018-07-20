package MapGen;

import java.util.LinkedList;
import java.util.Random;

import MapCode.*;
import Vector.Vector;

public class MapGen {
	
	public static LinkedList<Room> roomList = new LinkedList<Room>();
	public static int roomCount = 0;

	public static Map create(int mapWidth, int mapHeight, int mapLayers)
	{
		
		Map newMap = new Map(mapWidth, mapHeight, mapLayers);
		
		Random rand = new Random();
		
		for (int roomI=0; roomI<mapHeight*mapWidth; roomI++)
		{
				int mapX = rand.nextInt(mapWidth);
				int mapY = rand.nextInt(mapHeight);
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
									newMap.layers[0].data[mapX+roomX][mapY+roomY] = 18;
									if (roomCount == 1)
									{
										newMap.spawnPoint = new Vector((mapX*32)+((roomX/2)*32), (mapY*32)+((roomY/2)*32));
									}
							}
						}
						
						//connect rooms
						Room roomOne = newRoom;
							int distance = 1000000;
							Room roomTwo = new Room();
							for (Room searchRoom : roomList)
							{
								if (roomOne.getDistance(searchRoom) < distance && roomOne != searchRoom)
								{
									roomTwo = searchRoom;
									distance = roomOne.getDistance(searchRoom);
									System.out.println(distance);
								}
							}
							
							
							Point point1 = new Point(roomOne.x + (roomOne.width/2), roomOne.y + (roomOne.height/2));
							Point point2 = new Point(roomTwo.x + (roomTwo.width/2), roomTwo.y + (roomTwo.height/2));
							
							int xdistance = point2.x - point1.x;
							int ydistance = point2.y - point1.y;
							
							while (point1.y != point2.y)
							{
								point1.y += Math.signum(ydistance);
								newMap.layers[0].data[point1.x][point1.y] = 18;
							}
							while (point1.x != point2.x)
							{
								point1.x += Math.signum(xdistance);
								newMap.layers[0].data[point1.x][point1.y] = 18;
							}
						
					}
					

						
				}		
		}
		
		for (int x=0; x<mapWidth; x++)
		{
			for (int y=0; y<mapWidth; y++)
			{
				if (newMap.layers[0].data[x][y] == 0)
				{
					newMap.layers[0].data[x][y] = 16;
				}
			}
		}
		
		
		return newMap;
	}
}
