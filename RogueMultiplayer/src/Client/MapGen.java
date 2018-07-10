package Client;

import java.util.Random;

import org.newdawn.slick.Color;

import Client.Mobs.Snake;

public class MapGen {

	public static Map create(int mapWidth, int mapHeight)
	{
		
		Map newMap = new Map(mapWidth, mapHeight);
		
		Random rand = new Random();
		
		for (int mapY=0; mapY<mapHeight; mapY++)
		{
			for (int mapX=0; mapX<mapWidth; mapX++)
			{
				if (newMap.tileArray[mapX][mapY] == null)
				{
					newMap.tileArray[mapX][mapY] = new Tile();
					newMap.tileArray[mapX][mapY].color = new Color(102, 178, 250);
					
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
						
						if (newMap.roomList.size() == 0)
						{
							newMap.spawnPoint.x = newRoom.x*32; newMap.spawnPoint.y = newRoom.y*32;
						}
						newMap.addRoom(newRoom);
						
						for (int roomY=0; roomY<roomHeight; roomY++)
						{
							for (int roomX=0; roomX<roomWidth; roomX++)
							{
								newMap.tileArray[mapX+roomX][mapY+roomY] = new Tile();
								//newMap.tileArray[mapX+roomX][mapY+roomY].color = new Color(250, 250, 250);
								newMap.tileArray[mapX+roomX][mapY+roomY].isRoom = true;
								

								
								
								
								/*
								int randomMobSpawn = rand.nextInt(50);
								if (randomMobSpawn == 25)
								{
									Snake newSnake = new Snake();
									newSnake.x = (mapX+roomX)*32;
									newSnake.y = (mapY+roomY)*32;
									newMap.mobList.add(newSnake);
								}
								*/
							}
						}
					}
				}		
			}
		}
		
		for(int roomI=0; roomI<newMap.roomList.size()-1; roomI++)
		{
			int x1 = newMap.roomList.get(roomI).x;
			int x2 = newMap.roomList.get(roomI+1).x;
			int y1 = newMap.roomList.get(roomI).y;
			int y2 = newMap.roomList.get(roomI+1).y;
			int w1 = newMap.roomList.get(roomI).width;
			int w2 = newMap.roomList.get(roomI+1).width;
			int h1 = newMap.roomList.get(roomI).height;
			int h2 = newMap.roomList.get(roomI+1).height;
			
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
					newMap.tileArray[point1.x+i][point1.y].isRoom = true;
					x3 = point1.x+i;
					
					
				}
				
				//draw from right room to joinX
				for (int i=0; i < (point2.x - joinX) + 1; i++)
				{
					newMap.tileArray[point2.x-i][point2.y].isRoom = true;
					x4 = point2.x+i;
					
				}
				
				//connect vertically from point 1 end to point 2 end
				for (int i = (point2.y - point1.y + 0) ; i != 0; i -= Math.signum((point2.y - point1.y)))
					{
						System.out.println(i);
						newMap.tileArray[x3][point1.y+i].side = -1;
						newMap.tileArray[x3][point1.y+i].isRoom = true;
						
					}	
				
			}
			
			//room 1 above room 2
			if(y1 + h1 < y2)
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
					newMap.tileArray[point1.x][point1.y+i].color = new Color(250, 250, 250);
					newMap.tileArray[point1.x][point1.y+i].isRoom = true;
					x3 = point1.x+i;
				}
				
				//draw from lower room to joinY
				for (int i=0; i < (point2.y - joinY) + 1; i++)
				{
					newMap.tileArray[point2.x][point2.y-i].color = new Color(250, 250, 250);
					newMap.tileArray[point2.x][point2.y-i].isRoom = true;
					x4 = point2.x+i;
				}
				
				
				//connect vertically from point 1 end to point 2 end
				for (int i = (point2.y - point1.y + 0) ; i != 0; i -= Math.signum((point2.y - point1.y)))
					{
						System.out.println(i);
						newMap.tileArray[x3][point1.y+i].color = new Color(250, 250, 250);
						newMap.tileArray[x3][point1.y+i].isRoom = true;
						
					}	
							
			}
			
			
			
		}
		

		return newMap;	
	}
}
