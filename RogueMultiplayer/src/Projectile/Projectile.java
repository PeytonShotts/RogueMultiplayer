package Projectile;

import Vector.*;

public class Projectile {
	
	public int id;
	
	public Vector position = new Vector();
	public Vector direction = new Vector();

	public int time;
	public int speed;
	
	public int size = 8;
	
	public void update()
	{
		if (time > 0)
		{
			position.x += direction.x*speed;
			position.y += direction.y*speed;
			
			time += -1;
		}
		
	}

}
