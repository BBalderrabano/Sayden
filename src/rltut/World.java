package rltut;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class World {
	private Tile[][][] tiles;
	private Item[][][] items;
	
	private int width;
	public int width() { return width; }
	
	private int height;
	public int height() { return height; }

	private int depth;
	public int depth() { return depth; }
	
	private List<Creature> creatures;
	
	public Point startPos = null;
	
	public String name;
	
	public World(){
		this.creatures = new ArrayList<Creature>();
		this.items = new Item[width][height][depth];
	}
	
	public World(Tile[][][] tiles, String name){
		this.tiles = tiles;
		this.width = tiles.length;
		this.height = tiles[0].length;
		this.depth = tiles[0][0].length;
		this.creatures = new ArrayList<Creature>();
		this.items = new Item[width][height][depth];
		this.name = name;
	}

	public void modifyActionPoints(int ap){
		for (Creature c : creatures){
			if (!c.isPlayer())
				c.modifyActionPoints(ap);
		}
	}
	
	public Creature creature(int x, int y, int z){
		for (Creature c : creatures){
			if (c.x == x && c.y == y && c.z == z)
				return c;
		}
		return null;
	}
	
	public Tile tile(int x, int y, int z){
		if (x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth)
			return Tile.BOUNDS;
		else
			return tiles[x][y][z] == null ? Tile.BOUNDS : tiles[x][y][z];
	}
	
	public char glyph(int x, int y, int z){
		Creature creature = creature(x, y, z);
		if (creature != null)
			return creature.glyph();
		
		if (item(x,y,z) != null)
			return item(x,y,z).glyph();
		
		return tile(x, y, z).glyph();
	}
	
	public Color color(int x, int y, int z){
		Creature creature = creature(x, y, z);
		if (creature != null)
			return creature.color();
		
		if (item(x,y,z) != null)
			return item(x,y,z).color();
		
		return tile(x, y, z).color();
	}
	
	public Color backgroundColor(int x, int y, int z){
		Creature creature = creature(x, y, z);
		if (creature != null && creature.statusColor() != null)
			return creature.statusColor();
				
		return tile(x, y, z).backgroundColor();
	}

	/*public void dig(int x, int y, int z) {
		if (tile(x, y, z).isDiggable())
			tiles[x][y][z] = Tile.FLOOR;
	}*/
	
	/*public void useDoor(int x, int y, int z) {
		if (tile(x, y, z).isDoor() && tile(x, y, z).isClosedDoor())
			tiles[x][y][z] = Tile.OPEN_DOOR;
	}*/
	
	public void changeTile(int x, int y, int z, Tile newTile){
		tiles[x][y][z] = newTile;
	}
	
	public void addAtEmptyLocation(Creature creature, int z){
		int x;
		int y;
		
		if(z < 0)
			return;
		
		do {
			x = (int)(Math.random() * width);
			y = (int)(Math.random() * height);
		} 
		while (!tile(x,y,z).isGround() || creature(x,y,z) != null);
		
		creature.x = x;
		creature.y = y;
		creature.z = z;
		creatures.add(creature);
	}
	
	public void update(){
		List<Creature> toUpdate = new ArrayList<Creature>(creatures);
		for (Creature creature : toUpdate){
			creature.update();
		}
	}

	public void remove(Creature other) {
		creatures.remove(other);
	}
	
	public void remove(Item item) {
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++){
				for (int z = 0; z < depth; z++){
					if (items[x][y][z] == item) {
						items[x][y][z] = null;
						return;
					}
				}	
			}	
		}
	}
	
	public Item item(int x, int y, int z){
		try{
			return items[x][y][z];
		}catch(IndexOutOfBoundsException e){
			return null;
		}
	}
	
	public void addAtEmptyLocation(Item item, int depth) {
		int x;
		int y;
		
		if(depth <= -1)
			return;
		
		do {
			x = (int)(Math.random() * width);
			y = (int)(Math.random() * height);
		} 
		while (!tile(x,y,depth).isGround() || item(x,y,depth) != null);
		
		items[x][y][depth] = item;
	}
	
	public void addAtEmptyTileLocation(Item item, int depth, Tile tileType) {
		int x;
		int y;

		do {
			x = (int)(Math.random() * width);
			y = (int)(Math.random() * height);
		} 
		while (!tile(x,y,depth).isGround() || item(x,y,depth) != null || tile(x,y,depth) != tileType);
		
		items[x][y][depth] = item;
	}

	public void remove(int x, int y, int z) {
		items[x][y][z] = null;
	}
	
	public boolean addAtEmptySpace(Item item, int x, int y, int z){
		if (item == null)
			return true;
		
		List<Point> points = new ArrayList<Point>();
		List<Point> checked = new ArrayList<Point>();
		
		points.add(new Point(x, y, z));
		
		while (!points.isEmpty()){
			Point p = points.remove(0);
			checked.add(p);
			
			if (!tile(p.x, p.y, p.z).isGround())
				continue;
				
			if (items[p.x][p.y][p.z] == null){
				items[p.x][p.y][p.z] = item;
				Creature c = this.creature(p.x, p.y, p.z);
				
				if (c != null)
					c.notify((item.gender() == 'M' ? "Un" : "Una") + " %s cae entre tus pies.", c.nameOf(item));
				
				return true;
			} else {
				List<Point> neighbors = p.neighbors8();
				neighbors.removeAll(checked);
				points.addAll(neighbors);
			}
		}
		return false;
	}
	
	public boolean addAtEmptySpace(Creature creature, int x, int y, int z){
		if (creature == null)
			return true;
		
		List<Point> points = new ArrayList<Point>();
		List<Point> checked = new ArrayList<Point>();
		
		points.add(new Point(x, y, z));
		
		while (!points.isEmpty()){
			Point p = points.remove(0);
			checked.add(p);
			
			if (!tile(p.x, p.y, p.z).isGround())
				continue;
				
			if (creature(p.x,p.y,p.z) == null){
				creature.x = x;
				creature.y = y;
				creature.z = z;
				
				return true;
			} else {
				List<Point> neighbors = p.neighbors8();
				neighbors.removeAll(checked);
				points.addAll(neighbors);
			}
		}
		return false;
	}
	
	public boolean changeAtEmptySpace(Color effectColor, int x, int y, int z){
		if (effectColor == null)
			return true;
		
		List<Point> points = new ArrayList<Point>();
		List<Point> checked = new ArrayList<Point>();
		
		points.add(new Point(x, y, z));
		
		while (!points.isEmpty()){
			Point p = points.remove(0);
			checked.add(p);
			
			if (!tile(p.x, p.y, p.z).isGround())
				continue;
				
			if (tiles[p.x][p.y][p.z].backgroundColor() != effectColor){
				Creature c = this.creature(p.x, p.y, p.z);
				
				if (c != null){
					c.notify("Sangre salpica sobre tu cuerpo, empapandote.");
				}
				
				Tile bloodyTile = new Tile(tiles[p.x][p.y][p.z]);
				
				bloodyTile.setBackgroundColor(Constants.BLOOD_COLOR);
				bloodyTile.setDetails(bloodyTile.details() + " Hay un charco de sangre.");
				
				changeTile(p.x, p.y, p.z, bloodyTile);

				return true;
			} else {
				List<Point> neighbors = p.neighbors8();
				neighbors.removeAll(checked);
				points.addAll(neighbors);
			}
		}
		return false;
	}

	public void add(List<Creature> pets){
		for(int i = 0; i < pets.size(); i++){
			pets.get(i).setWorld(this);
		}
		creatures.addAll(pets);
	}
	
	public void add(Creature pet) {
		pet.setWorld(this);
		creatures.add(pet);
	}
}
