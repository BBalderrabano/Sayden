package rltut.ai;

import java.awt.Color;
import java.util.HashMap;

import asciiPanel.AsciiPanel;
import rltut.Point;
import rltut.BodyPart;
import rltut.Constants;
import rltut.Creature;
import rltut.DamageType;
import rltut.Item;
import rltut.StringUtils;
import rltut.StuffFactory;
import rltut.Wound;
import rltut.Item.ItemType;

public class WolfAi extends CreatureAi {
	private Creature player;
	private StuffFactory factory;
	
	private int hunger = 0;
	private int growTime = 400;
	
	public WolfAi(Creature creature, Creature player, StuffFactory factory) {
		super(creature);
		this.player = player;
		this.factory = factory;
		this.modifyDesire(0, StringUtils.randInt(0, 100));
		this.modifyDesire(1, StringUtils.randInt(0, 80));
		
		if(Math.random() < 1){
			creature.setData("IsHungry", true);
			creature.modifyColor(Color.RED);
			creature.setName(creature.name() + " hambriento");
			hunger = 10;
			return;
		}
		
		creature.modifyAttackSpeed(100);
		
		if(Math.random() < 0.15){
			creature.setData("IsCub", true);
			creature.modifyColor(AsciiPanel.brightBlue);
			creature.setName("lobezno");
			modifyDesire(1, 50);
			return;
		}
				
		if(Math.random() < 0.15){
			creature.setData("IsPregnant", true);
			creature.modifyColor(AsciiPanel.cyan);
			creature.setName("loba");
			creature.modifyMovementSpeed(100);
			modifyDesire(1, 50);
			return;
		}
	}
	
	public Wound getWound(DamageType type, BodyPart bodyPart, Creature target) {
		if(bodyPart.position() == BodyPart.ARMS.position()
				&& type.wondType() == DamageType.SLICE.wondType()){
			return new Wound("Pierna amputada", "pierna amputada", Constants.WOUND_PERMANENT, type, bodyPart, 2){
				public void onApply(Creature creature, Creature applier){
					creature.notifyArround(Constants.WOUND_COLOR, "El ataque es certero y separa %s!", 
							creature.isPlayer() ? "tu pierna":"la pierna "+ StringUtils.genderizeCreature(creature.gender(), creature.name(),false));
					creature.notify(Constants.WOUND_COLOR,"[amputas la pierna]");
					creature.force_drop(new Item(ItemType.STATIC,Math.random() < 0.5 ? (char)192 : (char)217, 'F', creature.color(), "pierna de lobo separada", null));
					creature.removeBodyPart(creature.getBodyPart(BodyPart.LEGS.position()));
				}
			};
		}
		return null;
	}
	
	/*public Wound getWoundAttack(DamageType type, BodyPart bodyPart, Creature target) { 
		if(bodyPart.position() == BodyPart.HEAD.position() && !target.hasWound("Mordida a la yugular"))
			return new Wound("Mordida a la yugular", "sangrado al moverse", 10, type, bodyPart, 2){
				public void onApply(Creature creature, Creature applier){
					creature.notifyArround(Constants.WOUND_COLOR, "El lobo logra morder tu yugular, inflingiendo un horrible corte!");
					creature.notifyArround(Constants.WOUND_COLOR, "Logras detener el sangrado aplicando presion...");
					creature.notify(Constants.WOUND_COLOR,"[sangrado al moverse]");
				}
				public void onMove(Creature creature){
					creature.bleed(1);
					creature.notify(Constants.WOUND_COLOR, "Al moverte no puedes atender tu herida en el cuello!");
					creature.modifyHp(-1, "Mueres desangrado");
				}
			};
		else
			return null;
	}*/
		
	public void onAttack(Creature target){
		if(player.hp() <= creature.maxHp()){
			propagate(0, 10);
		}else{
			propagate(1, 10);
		}
	}
	
	public void onGetAttacked(Creature attacker){
		if(creature.getBooleanData("IsPregnant")){
			propagate(0, 80);
			creature.doAction("aulla de dolor y panico, con terror en su mirada y un bebe en el vientre...");
			return;
		}
		if(creature.getBooleanData("IsCub")){
			propagate(0, 50);
			creature.doAction("gime desamparado, con terror en su mirada");
			return;
		}
		if(player.hp() > creature.hp()){
			propagate(1, 10);
		}else{
			propagate(0, 10);
		}
	}
	
	public void onUpdate(){
		if(creature.getBooleanData("IsCub")){
			if(creature.canSee(player.x, player.y, player.z)){
				flee(player);
				return;
			}else{
				growTime--;
				
				if(growTime < 1){
					creature.unsetData("IsCub");
					creature.modifyColor(creature.originalColor());
					creature.setName(creature.originalName());
				}
				wander();
				return;
			}
		}
		if(creature.getBooleanData("IsPregnant")){
			if(creature.canSee(player.x, player.y, player.z)){
				flee(player);
				return;
			}else{
				growTime--;
				
				if(growTime < 1){
					factory.newMaleWolf(creature.z, player);
				}
				
				wander();
				return;
			}
		}
		if(creature.getBooleanData("IsHungry")){
			Item check = creature.item(creature.x, creature.y, creature.z);

			if(check != null
					&& check.getBooleanData("IsEdible")){
								
				if(Point.distance(creature.x, creature.y, player.x, player.y) < 2){
					hunt(player);
					return;
				}else if(Point.distance(creature.x, creature.y, player.x, player.y) <= 3){
					creature.doAction("eriza su pelo y te observa de reojo...");
				}
				
				creature.doAction("consume avidamente %s", StringUtils.genderizeCreature(check.gender(), check.name(), false));
				
				hunger--;
				
				if(hunger < 1){
					creature.unsetData("IsHungry");
					creature.modifyColor(creature.originalColor());
					creature.getWorld().remove(creature.x, creature.y, creature.z);
					creature.setName(creature.originalName());
				}
			}

			HashMap<Item, Point> items = creature.getItemsArroundMe();
			
			if(!items.isEmpty()){
				for(Item i : items.keySet()){
					if(i.getBooleanData("IsEdible"))
						moveTo(items.get(i));
				}
			}

			if(creature.canSee(player.x, player.y, player.z)){
				hunt(player);
				return;
			}else{
				wander();
				return;
			}
		}
		if(canSee(player.x, player.y, player.z) &&
				(Point.distance(creature.x, creature.y, player.x, player.y) <= 3 
					|| getDesire(0) > getDesire(1))){
			hunt(player);
			return;
		}else if(canSee(player.x, player.y, player.z) && 
				getDesire(1) > getDesire(0)){
			flee(player);
			return;
		}else{
			wander();
			return;
		}
	}
}
