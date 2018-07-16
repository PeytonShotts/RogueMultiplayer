package Server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.*;

import MapCode.Map;



public class JsonConverter {
	
	public static Map convert(String filePath)
	{
		
		Gson g = new Gson(); 
		
		String jsonText = null;
		try {
			jsonText = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TiledMap.Map newTiledMap = g.fromJson(jsonText, TiledMap.Map.class);	
		
		Map newConvertedMap = new Map(newTiledMap.width, newTiledMap.height, newTiledMap.layers.length);
		
		System.out.println(newTiledMap.layers.length);
		
		for (int layerNum=0; layerNum<newConvertedMap.numLayers; layerNum++)
		{
			for (int y=0; y<newTiledMap.height; y++)
			{
				for (int x=0; x<newTiledMap.width; x++)
				{
					newConvertedMap.layers[layerNum].data[x][y] = newTiledMap.layers[layerNum].data[y*newTiledMap.width + x] - 1;
				}
			}
		}

		
		return newConvertedMap;

	}

}
