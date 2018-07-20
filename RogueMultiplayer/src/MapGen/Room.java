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
}
