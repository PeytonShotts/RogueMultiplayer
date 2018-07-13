package MapCode;

import java.io.IOException;

public class Main {
	
	public static void main(String[] args) throws IOException {
		
		
		Map newMap = JsonConverter.convert("C:/Users/p05119/Desktop/newfolder2/jsonmap.json");
		System.out.println(newMap.width);
		System.out.println(newMap.height);
		
		int x = 40; int y = 42;
		
		System.out.println(newMap.layers[0].data[40][42]);
		
		
	}

}
