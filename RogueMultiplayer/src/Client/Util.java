package Client;

import Mob.Mob;
import Vector.Vector;

public class Util {
	
	static int closestInteger(int a, int b) {
	    int c1 = a - (a % b);
	    int c2 = (a + b) - (a % b);
	    if (a - c1 > c2 - a) {
	        return c2;
	    } else {
	        return c1;
	    }
	}
	
	public static int getDistance(Player p, Mob m)
	{
		float xDifference = p.x - m.position.x;
		float yDifference = p.y - m.position.y;
		return (int) Math.sqrt( (xDifference*xDifference) + (yDifference*yDifference));
	}
	
	public static Vector getDirectionVector(Player p, Mob m)
	{
		int magnitude = getDistance(p, m);
		Vector directionVector = new Vector();
		
		if (Math.abs(magnitude) > 0)
		{
			directionVector.x = (m.position.x - p.x) / magnitude;
			directionVector.y = (m.position.y - p.y) / magnitude;
		}
		else
		{
			directionVector.x = 0;
			directionVector.y = 0;
		}

		
		return directionVector;
	}
	
	public static Vector getDirectionVector(Mob m, Player p)
	{
		int magnitude = getDistance(p, m);
		Vector directionVector = new Vector();
		directionVector.x = (p.x - m.position.x) / magnitude;
		directionVector.y = (p.y - m.position.y) / magnitude;
		
		return directionVector;
	}
}
