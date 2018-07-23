package MapGen;

public class Room {
	
	int x;
	int y;
	
	int width;
	int height;
	
	boolean isConnected;
	
	public Room()
	{
		isConnected = false;
	}
	
	public int getDistance(Room roomTwo)
	{
		return (int) Math.hypot(this.x-roomTwo.x, this.y-roomTwo.y);
	}
	
	public boolean connects(Room b) {
		  return (Math.abs(this.x - b.x) * 2 < (this.width + b.width)) &&
		         (Math.abs(this.y - b.y) * 2 < (this.height + b.height));
		}
}
