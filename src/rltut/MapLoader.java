package rltut;
import java.awt.Color;
import java.io.File;
import java.util.LinkedList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MapLoader {
	
	private int mapWidth = 0;
	private int mapHeight = 0;
	private String mapReference;
	private LinkedList<TileData> tileSet;
	
	private World world;
	private TileData[][][] tiles;
	
	public World preBuild(String mapName)
	{
		tileSet = new LinkedList<TileData>();
		
		if( checkFile(mapName) ){
			String[] lines = mapReference.split("\n");
			mapHeight = lines.length;
			mapWidth = lines[0].length();
			
			tiles = new TileData[mapWidth][mapHeight][1];
			
			for(int y = 0; y < lines.length; y++){
				for(int x = 0; x < lines[y].length(); x++){
					char stringIndex = lines[y].replace("\n", "").replace("\r", "").replace("\t", "").charAt(x);
					
					if(!(stringIndex >= '0' && stringIndex <= '9')){
						//En caso de que arranquemos con letras
						stringIndex -= 87;	// -87 para que a = 10
						tiles[x][y][0] = tileSet.get((int)stringIndex);
					}else{
						//Los primeros n�meros
						tiles[x][y][0] = tileSet.get(Integer.parseInt(stringIndex+""));
					}
				}
			}
			
			world = new World(tiles, mapName);
		}
		
		return world;
	}
	
	private boolean checkFile(String mapName){
		 try {
			  File file = new File("maps/"+mapName+".xml");
			  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			  DocumentBuilder db = dbf.newDocumentBuilder();
			  Document doc = db.parse(file);
			  doc.getDocumentElement().normalize();

			  NodeList nodeLst = doc.getElementsByTagName("version");

			  for (int s = 0; s < nodeLst.getLength(); s++) {
			    Node fstNode = nodeLst.item(s);
			    
			    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
			    	
			    	Element firstPersonElement = (Element)fstNode;
			    	
	                //---------------MAP-----------------
			    	NodeList mapElementList = firstPersonElement.getElementsByTagName("map");
			    	Element mapElement;
			    	
			    	if(mapElementList.getLength() > 1){
			    		Random rand = new Random();
			    		int mapNro = rand.nextInt((mapElementList.getLength() - 1) + 1);
			    		
			    		mapElement = (Element)mapElementList.item(mapNro);
			    	}else{
				    	mapElement = (Element)mapElementList.item(0);
			    	}
	                
	                NodeList textMNList = mapElement.getChildNodes();
	                
	                mapReference = ((Node)textMNList.item(0)).getNodeValue().trim();
	                mapReference = mapReference.replace("\r", "").replace("\t", "");
			    }
			}
			  
		    nodeLst = doc.getElementsByTagName("tiles");

			for (int s1 = 0; s1 < nodeLst.getLength(); s1++) {
			    Node scndNode = nodeLst.item(s1);
			    
			    if (scndNode.getNodeType() == Node.ELEMENT_NODE) {
			    	
			    	Element firstPersonElement = (Element)scndNode;
			    	
			    	//---------------TILE----------------
			    	NodeList tileElementList = firstPersonElement.getElementsByTagName("tile");
			    				    	
			    	for(int t = 0; t < tileElementList.getLength(); t++){
			    		Element tileElement = (Element)tileElementList.item(t);
			    		
			    		TileData tile = analyzeTile(tileElement);
			            
			    		tileSet.add(t, tile);
			    	}
			    }
			}
		 } catch (Exception e) {
		    System.out.println("MAP LOADER ERROR: " + e.getLocalizedMessage());
		    e.printStackTrace();
		    return false;
		 }
		 
		 return true;
	}
	
	private TileData analyzeTile(Element tileElement){
		NodeList textWNList = tileElement.getChildNodes();
		String tileName = ((Node)textWNList.item(0)).getNodeValue().trim();
		TileData tile = null;
		
		if(TileData.VALUES_BY_NAME.containsKey(tileName)){
			tile = TileData.VALUES_BY_NAME.get(tileName);
		}else{
			tile = new TileData();
		}
				
		String portal_to = tileElement.getAttribute("viaja");
		String details = tileElement.getAttribute("details");
		int glyphN = Integer.parseInt((tileElement.getAttribute("glyphN").isEmpty() ? "0" : tileElement.getAttribute("glyphN")));
		char glyphC = (char) (tileElement.getAttribute("glyphC").isEmpty() ? ' ' : tileElement.getAttribute("glyphC"));
		String interactable = tileElement.getAttribute("interactable");
		String action = tileElement.getAttribute("action");
		String cloneOf = tileElement.getAttribute("clone");
		
		if(cloneOf != null && !cloneOf.isEmpty()){
			TileData patient_zero = TileData.VALUES_BY_NAME.get(cloneOf);
			
			tile.setGlyph(patient_zero.glyph());
			tile.setColor(patient_zero.color());
			tile.setDetails(patient_zero.details());
			tile.makeGround(patient_zero.isGround());
			tile.makeSeeTrough(patient_zero.seeTrough());
			tile.setInteraction(patient_zero.interaction());
		}
		
		if(interactable.indexOf("change") != -1){
			NodeList textToList = tileElement.getElementsByTagName("tile_to");
			Element tileToName = (Element) textToList.item(0);
				  			  	
			TileData tile_to = analyzeTile(tileToName);
			
			tile_to.setChangeTo(tile);
			tile.setChangeTo(tile_to);
		}
		
		if(portal_to != null && !portal_to.isEmpty()){
		  	int x = Integer.parseInt(tileElement.getAttribute("x"));
		  	int y = Integer.parseInt(tileElement.getAttribute("y"));
		  	int z = Integer.parseInt(tileElement.getAttribute("z"));
		  	
		  	Point p = new Point(x,y,z);
		  	
		  	tile.setPortalTo(portal_to);
		  	tile.setPortalPos(p);
		}
		
		if(!tileElement.getAttribute("isGround").isEmpty()){
			tile.makeGround(Boolean.parseBoolean(tileElement.getAttribute("isGround")));
		}
		 
		if(!tileElement.getAttribute("seeThrough").isEmpty()){
			tile.makeSeeTrough(Boolean.parseBoolean(tileElement.getAttribute("isGround")));
		}
		 
		if(!tileElement.getAttribute("color").isEmpty()){		
			String color = tileElement.getAttribute("color");
			String[] rgb = color.split("-");
						
			int r = Integer.parseInt(rgb[0]);
			int g = Integer.parseInt(rgb[1]);
			int b = Integer.parseInt(rgb[2]);
			
			Color change_color = new Color(r,g,b);
		    tile.setColor(change_color);
		}
		 
		if(!action.isEmpty()){
			tile.setAction(action);
		}

		if(!details.isEmpty()){
		 	tile.setDetails(details);
		}
		
		if(glyphC != ' ' || glyphN > 0){
			tile.setGlyph((char) (glyphN > 0 ? glyphN : glyphC));
		}
		 
		return tile;
	}
}