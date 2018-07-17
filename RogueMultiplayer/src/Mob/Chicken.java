package Mob;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Chicken extends Mob{
	
	public int spriteCount = 3;
	public int spriteX = 0;
	public int spriteY = 0;
	
	
	public Chicken()
	{
		try {
			spriteSheet = new Image("res/owlishmedia_pixel_tiles.png");
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
