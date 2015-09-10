package rltut;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Item extends DataStructure{
	public static enum ItemType{
		SHIELD,
		WEAPON,
		ARMOR,
		HELMENT,
		STATIC,
		INTRINSIC,
		UNEQUIPPABLE
	}
	
	private ItemType itemType;
	public ItemType itemType() { return itemType; }
	
	private char gender;
	public char gender() { return gender; }
	
	private char glyph;
	public char glyph() { return glyph; }
	
	private Color color;
	public Color color() { return color; }

	private String name;
	public String name() { return getBooleanData("IsBroken") ? name+ " " + (gender == 'M' ? "roto" : "rota") : name; }
	
	public String nameAElALa() { return (gender == 'M' ? "al " : "a la ") + name(); }
	public String nameElLa() { return (gender == 'M' ? (name().endsWith("s") ? "los " :"el ") : (name().endsWith("s") ? "las " : "la ")) + name(); }
	public String nameElLaTu(Creature reference) { return reference.isPlayer() ? (name().endsWith("s") ? "tus " : "tu ") + name() :	nameElLa(); } 
	public String nameTuSu(Creature reference) { return (reference.isPlayer() ? (name().endsWith("s") ? "tus " : "tu ") : (name().endsWith("s") ? "sus " : "su ")) + name(); }
	public String nameUnUna() { return (gender == 'M' ? (name().endsWith("s") ? "unos " :"un ") : (name.endsWith("s") ? "unas " : "una ")) + name(); }
	public String nameDelDeLa() { return (gender == 'M' ? (name.endsWith("s") ? "de los " : "del ") : (name.endsWith("s") ? "de las " : "de la ")) + name(); }
	
	private String appearance;
	public String appearance() { return appearance; }
	
	private Effect quaffEffect;
	public Effect quaffEffect() { return quaffEffect; }
	public void setQuaffEffect(Effect effect) { this.quaffEffect = effect; }
	
	private List<Spell> writtenSpells;
	public List<Spell> writtenSpells() { return writtenSpells; }
	
	public Wound getWound(DamageType type, BodyPart bodyPart, Creature target) { return null; }
	
	private List<DamageType> damageTypes;
	public List<DamageType> damageTypes() { return damageTypes; }
	public DamageType getDamageType(DamageType value) {
		for(DamageType t : damageTypes){
			if(t.wondType() == value.wondType())
				return t;
		}
		return null;
	}
	public Item addDamageType(DamageType newDamage, int power) { 
		DamageType newType = new DamageType(newDamage.name(), newDamage.wondType(), newDamage.causeOfDeath());
		this.damageTypes.add(newType.addPower(power)); 
		return this; 
	}
	
	private int playerBonusDamage;
	public int playerBonusDamage() { return playerBonusDamage; }
	public void modifyPlayerBonusDamage(int amount) { playerBonusDamage += amount; }
	
	private int attackSpeedModifier;
	public int attackSpeedModifier() { return attackSpeedModifier; }
	public void modifyAttackSpeed(int amount) { this.attackSpeedModifier += amount; }
	
	private int movementSpeedModifier;
	public int movementSpeedModifier() { return movementSpeedModifier; }
	public void modifyMovementSpeed(int amount) { this.movementSpeedModifier += amount; }
	
	public void addWrittenSpell(String name, int manaCost, Effect effect){
		writtenSpells.add(new Spell(name, manaCost, effect));
	}
	
	/**Constructor para los items intrinsecos (arma / armadura de la criatura, pelaje, puños, etc)*/
	public Item(ItemType type, char gender, String name){
		super();
		this.itemType = type;
		this.gender = gender;
		this.name = name;
		this.damageTypes = new ArrayList<DamageType>();
		this.playerBonusDamage = 0;
	}
	
	public Item(ItemType type, char glyph, char gender, Color color, String name, String appearance){
		super();
		this.glyph = glyph;
		this.color = color;
		this.name = name;
		this.gender = gender;
		this.appearance = appearance == null ? name : appearance;
		this.writtenSpells = new ArrayList<Spell>();
		this.damageTypes = new ArrayList<DamageType>();
		this.itemType = type;
		this.playerBonusDamage = 0;
	}
	
	public String details() {
		String details = "";
		
		for(int i = 0; i < damageTypes.size(); i++){
			details += "  "+damageTypes.get(i).name() + ": " + damageTypes.get(i).power();
		}
		
		/*if (attackValue != 0)
			details += "  ataque:" + attackValue;

		if (thrownAttackValue != 1)
			details += "  arrojadizo:" + thrownAttackValue;
		
		if (rangedAttackValue > 0)
			details += "  rango:" + rangedAttackValue;
		
		if (defenseValue != 0)
			details += "  defensa:" + defenseValue;

		if (foodValue != 0)
			details += "  comida:" + foodValue;*/
		
		return details;
	}
}
