package MapCode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.*;



public class JsonConverter {
	
	public static Map convert(String filePath) throws IOException
	{
		
		Gson g = new Gson(); 
		
		String jsonText = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);

		TiledMap.Map newTiledMap = g.fromJson(jsonText, TiledMap.Map.class);
		
		Map newConvertedMap = new Map(newTiledMap.width, newTiledMap.height, newTiledMap.nextlayerid-1);
		
		for (int layerNum=0; layerNum<newConvertedMap.numLayers; layerNum++)
		{
			for (int y=0; y<newTiledMap.height; y++)
			{
				for (int x=0; x<newTiledMap.width; x++)
				{
					newConvertedMap.layers[layerNum].data[x][y] = newTiledMap.layers[layerNum].data[y*newTiledMap.width + x];
				}
			}
		}

		
		return newConvertedMap;

	}

}
