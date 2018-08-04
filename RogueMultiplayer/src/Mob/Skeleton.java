package Mob;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Skeleton extends Mob{
	
	
	public Skeleton()
	{
		spriteSheetPath = "res/skeleton.png";
		maxHealth = 50;
		health = 50;
		spriteCount = 3;
		spriteX = 0;
		spriteY = 0;
		
		spriteWidth = 24;
		spriteHeight = 32;
	}

}
