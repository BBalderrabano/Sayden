package rltut;

import java.awt.Color;

import asciiPanel.AsciiPanel;
import rltut.Item.ItemType;

public class Constants {
	public static final Color BACKGROUND_COLOR = AsciiPanel.black;
	
	public static final int SCREEN_WIDTH = 80;
	public static final int SCREEN_HEIGHT = 32;
	public static final int WOUND_MENU_WIDTH = 20;
	public static final int REAL_SCREEN_WIDTH = SCREEN_WIDTH + WOUND_MENU_WIDTH;
	public static final int WOUND_MENU_OFFSET_HEIGHT = 3;
	
	public static final int WORLD_WIDTH = 80;
	public static final int WORLD_HEIGHT = 24;
	
	public static final int MENU_OFFSET = (int)(WORLD_HEIGHT);
	
	public static final String STARTING_MAP = "Pueblo";
	
	public static final int INVENTORY_SIZE = 10;
	public static final int STARTING_ATTACK_SPEED = 100;
	public static final int STARTING_MOVE_SPEED = 100;
	
	public static final Color MESSAGE_STATUS_EFFECT_COLOR = AsciiPanel.yellow;
	public static final String MESSAGE_DODGE = "falla el ataque!";				//doAction(MESSAGE_DODGE)
	public static final Color MESSAGE_CRIT_COLOR = Color.RED;
	public static final String MESSAGE_CRIT = "impacta con gran presicion!";		//doAction(MESSAGE_CRIT)
	public static final Color MESSAGE_DODGE_COLOR = AsciiPanel.brightBlue;
	public static final String MESSAGE_DODGE_WARNING = "prepara para atacar";	//doAction(MESSAGE_DODGE_WARNING)
	public static final String MESSAGE_COUNTER = "aprovecha la oportunidad para atacar";
	public static final Color WOUND_COLOR = new Color(255,144,0);
	protected static final Color BLOOD_COLOR = AsciiPanel.red;

	public static final int STARTING_VISION_RADIUS = 9;
	
	public static final int WOUND_DURATION_LOW = 10;
	public static final int WOUND_DURATION_MID = 20;
	public static final int WOUND_DURATION_HIGH = 30;
	public static final int WOUND_PERMANENT = -1;
		
	public static final int SEV_LEG_PENALTY = 100;
	public static final float DISMEMBER_CHANCE_LIMB_OVER_KILL = 0.70f;			//brazo < chance
	
	public static final String TOMAS_WEAPON = "IsWarrior";						//Le pidio un arma a Tomas?
	public static final String SILVIO_SPELL = "IsMage";							//Le pidio un hechizo a Silvio?
	public static final String IS_SILENCED = "IsSilenced";						//Esta silenciado
	
	public static final Item SLICE_STARTING_WEAPON(){
		Item slice_weapon = new Item(ItemType.WEAPON, ')', 'F', AsciiPanel.brightWhite, "espada", null);
		slice_weapon.addDamageType(DamageType.SLICE, 2);
		slice_weapon.modifyAttackSpeed(50);
		return slice_weapon;
	}
	public static final Item PIERCING_STARTING_WEAPON(){
		Item piercing_weapon = new Item(ItemType.WEAPON, ')', 'F', AsciiPanel.brightWhite, "daga", null);
		piercing_weapon.addDamageType(DamageType.PIERCING, 2);
		return piercing_weapon;
	}
	public static final Item BLUNT_STARTING_WEAPON(){
		Item blunt_weapon = new Item(ItemType.WEAPON, ')', 'F', AsciiPanel.brightWhite, "maza", null);
		blunt_weapon.addDamageType(DamageType.BLUNT, 2);
		blunt_weapon.modifyAttackSpeed(50);
		return blunt_weapon;
	}
	public static final Spell STARTING_SPELL(){
		Spell starting_spell = new Spell("provocar miedo", new Effect(5){
			public void start(Creature creature){
				creature.ai().modifyDesire(1, 200);
				creature.doAction("siente un terror abasallante!");
				
			}
			public void update(Creature creature){
				creature.ai().modifyDesire(1, -40);
			}
			public void end(Creature creature){
				creature.doAction("disipa su terror!");
			}
		}, true);
		
		return starting_spell;
	}
}
