package Projectile;

import Vector.*;

public class Projectile {
	
	public int id;
	
	public Vector position = new Vector();
	public Vector direction = new Vector();

	public int size = 10;
	public int time;
	
	public int speed;
	public int knockback = 8;
	public short damage = 5;
	
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
