package MapGen;

import java.util.LinkedList;
import java.util.Random;

import org.newdawn.slick.Color;

import MapCode.*;

//copy of old map generator

/*
public class MapGen_old {
	
	public static LinkedList<Room> roomList = new LinkedList<Room>();
	public static int connectedRoomCount = 0;

	public static Map create(int mapWidth, int mapHeight, int mapLayers)
	{
		
		Map newMap = new Map(mapWidth, mapHeight, mapLayers);
		
		Random rand = new Random();
		
		for (int mapY=0; mapY<mapHeight; mapY++)
		{
			for (int mapX=0; mapX<mapWidth; mapX++)
			{
				if (newMap.layers[0].data[mapX][mapY] == 0)
				{
					newMap.layers[0].data[mapX][mapY] = 0;
					
					int  r = rand.nextInt(100) + 0;
					if (r == 50 && mapX < mapWidth-10 && mapY < mapHeight-10 && mapX > 2 && mapY > 2)
					{
						int roomWidth = rand.nextInt(8) + 3;
						int roomHeight = rand.nextInt(8) + 3;
						
						Room newRoom = new Room();
						newRoom.x = mapX;
						newRoom.y = mapY;
						newRoom.width = roomWidth;
						newRoom.height = roomHeight;
						
						if (roomList.size() == 0)
						{
							newMap.spawnPoint.x = newRoom.x*32; newMap.spawnPoint.y = newRoom.y*32;
						}
						roomList.add(newRoom);
						
						for (int roomY=0; roomY<roomHeight; roomY++)
						{
							for (int roomX=0; roomX<roomWidth; roomX++)
							{
								newMap.layers[0].data[mapX+roomX][mapY+roomY] = 1;
							}
						}
					}
				}		
			}
		}
		
		
		while (connectedRoomCount < roomList.size())
		{
			int roomOne = rand.nextInt(roomList.size()-1);
			int roomTwo = rand.nextInt(roomList.size()-1);
			while (roomList.get(roomOne).isConnected != false)
			{
				roomOne = rand.nextInt(roomList.size()-1);
			}
			while (roomList.get(roomTwo).isConnected != false)
			{
				roomTwo = rand.nextInt(roomList.size()-1);
			}
			int x1 = roomList.get(roomOne).x;
			int x2 = roomList.get(roomTwo).x;
			int y1 = roomList.get(roomOne).y;
			int y2 = roomList.get(roomTwo).y;
			int w1 = roomList.get(roomOne).width;
			int w2 = roomList.get(roomTwo).width;
			int h1 = roomList.get(roomOne).height;
			int h2 = roomList.get(roomTwo).height;
			
			System.out.println(roomOne + " " + roomTwo);
			
			int x3 = 0;
			int x4 = 0;
			
			//room 1 left of room 2
			if(x1 + w1 < x2)
			{
				//choose point on room 1
				Point point1 = new Point();
				point1.x = x1 + w1;
				point1.y = y1 + rand.nextInt(h1);
				
				//choose point on room 2
				Point point2 = new Point();
				point2.x = x2;
				point2.y = y2 + rand.nextInt(h2);
				
				//choose random x between the two rooms
				int joinX = point1.x + rand.nextInt(x2 - (x1+w1));
				
				
				//draw from left room to joinX
				for (int i=0; i < (joinX - point1.x) + 1; i++)
				{
					newMap.layers[0].data[point1.x+i][point1.y] = 1;
					x3 = point1.x+i;
					
					
				}
				
				//draw from right room to joinX
				for (int i=0; i < (point2.x - joinX) + 1; i++)
				{
					newMap.layers[0].data[point2.x-i][point2.y] = 1;
					x4 = point2.x+i;
					
				}
				
				//connect vertically from point 1 end to point 2 end
				for (int i = (point2.y - point1.y + 0) ; i != 0; i -= Math.signum((point2.y - point1.y)))
				{
					newMap.layers[0].data[x3][point1.y+i] = 1;
						
				}	
				
			}
			
			//room 1 above room 2
			else if(y1 + h1 < y2 && false)
			{
				//choose point on room 1
				Point point1 = new Point();
				point1.x = x1 + rand.nextInt(w1);
				point1.y = y1 + h1;
				
				//choose point on room 2
				Point point2 = new Point();
				point2.x = x2 + rand.nextInt(w2);
				point2.y = y2;
				
				//choose random y between the two rooms
				int joinY = point1.y + rand.nextInt(y2 - (y1+h1));
				
				//draw from upper room to joinY
				for (int i=0; i < (joinY - point1.y) + 1; i++)
				{
					newMap.layers[0].data[point1.x][point1.y+i] = 1;
					x3 = point1.x+i;
				}
				
				//draw from lower room to joinY
				for (int i=0; i < (point2.y - joinY) + 1; i++)
				{
					newMap.layers[0].data[point2.x][point2.y-i] = 1;
					x4 = point2.x+i;
				}
				
				
				//connect vertically from point 1 end to point 2 end
				for (int i = (point2.y - point1.y + 0) ; i != 0; i -= Math.signum((point2.y - point1.y)))
				{
					newMap.layers[0].data[x3][point1.y+i] = 1;	
				}	
							
			}
			//room 1 right of room 2
			if(x2 + w2 < x1)
			{
				//choose point on room 1
				Point point1 = new Point();
				point1.x = x1 + w1;
				point1.y = y1 + rand.nextInt(h1);
				
				//choose point on room 2
				Point point2 = new Point();
				point2.x = x2;
				point2.y = y2 + rand.nextInt(h2);
				
				//choose random x between the two rooms
				int joinX = point2.x + rand.nextInt(x1 - (x2+w2));
				
				
				//draw from left room to joinX
				for (int i=0; i < (joinX - point2.x) + 1; i++)
				{
					newMap.layers[0].data[point2.x+i][point2.y] = 1;
					x3 = point2.x+i;
					
					
				}
				
				//draw from right room to joinX
				for (int i=0; i < (point1.x - joinX) + 1; i++)
				{
					newMap.layers[0].data[point1.x-i][point1.y] = 1;
					x4 = point1.x+i;
					
				}
				
				//connect vertically from point 1 end to point 2 end
				for (int i = (point1.y - point2.y + 0) ; i != 0; i -= Math.signum((point1.y - point2.y)))
				{
					newMap.layers[0].data[x3][point2.y+i] = 1;
				}	
				
			}
			
			connectedRoomCount++;
			
			
		}
		

		return newMap;	
	}
}
*/
