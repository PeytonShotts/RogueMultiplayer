package Projectile;

import Vector.*;

public class Projectile {
	
	public int id;
	
	public Vector position = new Vector();
	public Vector direction = new Vector();

	public int size = 2;
	public int time;
	
	public int speed;
	public int knockback = 2;
	public short damage = 1;
	
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
