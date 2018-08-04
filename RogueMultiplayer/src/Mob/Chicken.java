package Mob;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Chicken extends Mob{
	

	public Chicken()
	{
		spriteSheetPath = "res/chicken.png";
		maxHealth = 50;
		health = 50;
		spriteCount = 3;
		spriteX = 0;
		spriteY = 0;
		
		spriteWidth = 32;
		spriteHeight = 32;
	}

}