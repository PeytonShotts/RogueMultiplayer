package Client.Mobs;

import Client.Mob;

public class Snake extends Mob {

	public Snake()
	{
		spriteX = 20;
		spriteY = 4;
		
		width = 28;
		height = 28;
		
		maxHealth = 5;
		health = maxHealth;
		
		speed = (float) 0.15;
	}
}
