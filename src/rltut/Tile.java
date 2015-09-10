package rltut;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import asciiPanel.AsciiPanel;

public class Tile {
	public static Tile GRASS = new Tile(',', AsciiPanel.green, "Pasto cubierto ligeramente en rocio, se siente bien a los pies desnudos.", true);
	public static Tile OPEN_DOOR = new Tile((char)9, AsciiPanel.brightBlack, "Dura puerta de madera abierta.", true).addInteraction(Interaction.CHANGES);
	public static Tile CLOSE_DOOR = new Tile((char)10, AsciiPanel.brightBlack, "Dura puerta de madera cerrada.", false, false).addInteraction(Interaction.CHANGES);
	public static Tile FLOOR = new Tile((char)250, AsciiPanel.yellow, "Piso de tierra y roca.", true);
	public static Tile WALL = new Tile((char)177, AsciiPanel.yellow, "Muro de tierra y roca.", false, false);
	public static Tile TOWN_WALL = new Tile((char)177, Color.yellow, "Un alto muro de madera, usado como perímetro.", false, false);
	public static Tile BUILD_WALL = new Tile((char)177, Color.gray, "Un robusto muro de madera.", false, false);
	public static Tile SECRET_BUILD_WALL = new Tile((char)177, Color.gray, "Un muro de madera, sientes una ligera brisa colarse entre las planchas", false, false);
	public static Tile TOWN_FLOOR = new Tile((char)250, Color.gray, "Una simple plancha de madera que sirve de piso.", true);
	public static Tile STAIRS_UP = new Tile('<', AsciiPanel.white, "Una escalera de piedra que asciende.", true).addInteraction(Interaction.STAIRS_UP);
	public static Tile STAIRS_DOWN = new Tile('>', AsciiPanel.white, "Una escalera de piedra que desciende.", true).addInteraction(Interaction.STAIRS_DOWN);
	public static Tile PORTAL_LEFT = new Tile((char)27, AsciiPanel.brightWhite, "Camino a otro mapa.", true);
	public static Tile PORTAL_RIGHT = new Tile((char)26, AsciiPanel.brightWhite, "Camino a otro mapa.", true);
	public static Tile PORTAL_DOWN = new Tile((char)25, AsciiPanel.brightWhite, "Camino a otro mapa.", true);
	public static Tile PORTAL_UP = new Tile((char)24, AsciiPanel.brightWhite, "Camino a otro mapa.", true);
	public static Tile FENCE = new Tile((char)26, AsciiPanel.brightWhite, "Una valla de metal, vieja y oxidada.", true, false);

	public static final Tile BOUNDS = new Tile(' ', AsciiPanel.brightBlack, "Limites del mundo.", false, false);
	public static final Tile UNKNOWN = new Tile(' ', AsciiPanel.white, "(desconocido)", true);
	
	static final Map<String, Tile> VALUES_BY_NAME;
	static {
	    final Map<String, Tile> valuesByName = new HashMap<>();
	    valuesByName.put("OPEN_DOOR", OPEN_DOOR);
	    valuesByName.put("CLOSE_DOOR", CLOSE_DOOR);
	    valuesByName.put("FLOOR", FLOOR);
	    valuesByName.put("WALL", WALL);
	    valuesByName.put("TOWN_WALL", TOWN_WALL);
	    valuesByName.put("BUILD_WALL", BUILD_WALL);
	    valuesByName.put("SECRET_BUILD_WALL", SECRET_BUILD_WALL);
	    valuesByName.put("TOWN_FLOOR", TOWN_FLOOR);
	    valuesByName.put("STAIRS_UP", STAIRS_UP);
	    valuesByName.put("STAIRS_DOWN", STAIRS_DOWN);
	    valuesByName.put("PORTAL_LEFT", PORTAL_LEFT);
	    valuesByName.put("PORTAL_RIGHT", PORTAL_RIGHT);
	    valuesByName.put("PORTAL_DOWN", PORTAL_DOWN);
	    valuesByName.put("PORTAL_UP", PORTAL_UP);
	    valuesByName.put("FENCE", FENCE);
	    valuesByName.put("GRASS", GRASS);
	    
	    VALUES_BY_NAME = Collections.unmodifiableMap(valuesByName);
	}
	
	public enum Interaction{
		STAIRS_DOWN,
		STAIRS_UP,
		CHANGES
	}
		
	private char glyph;
	public char glyph() { return this.glyph;}
	public void setGlyph(char glyph) { this.glyph = glyph; }
	
	private Color color;
	public Color color() { return this.color; }
	public void setColor(Color color) { this.color = color; }
	
	private Color backgroundColor;
	public Color backgroundColor() { return this.backgroundColor; }
	public void setBackgroundColor(Color backgroundColor) { this.backgroundColor = backgroundColor; }
	
	private String details;
	public String details() { return details; }
	public void setDetails(String details) { this.details = details; }
	
	private String action;
	public String action() { return action; }
	public void setAction(String action) { this.action = action; }
	
	private boolean seeTrough;
	public boolean seeTrough() { return seeTrough; }
	public void makeSeeTrough(boolean seeTrough) { this.seeTrough = seeTrough; }
	
	private boolean isGround;
	public boolean isGround() {	return isGround; }
	public void makeGround(boolean isGround) { this.isGround = isGround; }
	
	private String portal_to;
	public String getPortalTo() { return portal_to; }
	public Tile setPortalTo(String portal_to) { this.portal_to = portal_to; return this; }
	
	private Point portal_position;
	public Point getPortalPos() { return portal_position; }
	public void setPortalPos(Point point) { this.portal_position = point; }
	
	private Interaction interaction;
	public Interaction interaction() { return interaction; }
	public void setInteraction(Interaction interaction) { this.interaction = interaction; }
	
	private int fluid;
	public int fluid() { return fluid; }
	public void modifyFluid(int amount) { this.fluid += amount; }
	
	private Tile change_to;
	public Tile change_to() { return change_to; }
	public void setChangeTo(Tile tile) { this.change_to = tile; }
	
	private Tile addInteraction(Interaction interaction) { this.interaction = interaction; return this; }
	
	Tile(char glyph, Color color, String details, boolean seeTrough, boolean isGround){
		this.glyph = glyph;
		this.color = color;
		this.backgroundColor = Constants.BACKGROUND_COLOR;
		this.details = details;
		this.seeTrough = seeTrough;
		this.isGround = isGround;
		this.portal_to = null;
		this.change_to = new Tile();
	}
	
	Tile(char glyph, Color color, String details, boolean seeTrough){
		this.glyph = glyph;
		this.color = color;
		this.backgroundColor = Constants.BACKGROUND_COLOR;
		this.details = details;
		this.seeTrough = seeTrough;
		this.portal_to = null;
		this.isGround = true;
		this.change_to = new Tile();
	}
	
	Tile(Tile clone){
		this.glyph = clone.glyph();
		this.color = clone.color();
		this.backgroundColor = clone.backgroundColor();
		this.details = clone.details();
		this.seeTrough = clone.seeTrough();
		this.isGround = clone.isGround();
		this.portal_to = clone.getPortalTo();
		this.change_to = clone.change_to();
		this.fluid = clone.fluid();
	}
	
	Tile(){
		this.glyph = '?';
		this.color = Color.PINK;
		this.backgroundColor = Constants.BACKGROUND_COLOR;
		this.details = "INITIALIZE_ERROR";
		this.seeTrough = false;
		this.isGround = true;
		this.portal_to = null;
	}
}
