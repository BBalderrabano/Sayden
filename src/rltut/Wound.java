package rltut;

import java.util.HashMap;
import java.util.Map;

public class Wound {
	protected int duration;
	
	public boolean isHealed() { return duration < 1; }
	
	private int severity;
	public int severity() { return severity; }
	
	private String name;
	public String name() { return name; }
	
	private String position;
	public String position() { return position; }
	public Wound setPosition(String position) { this.position = position; return this; }
	
	public static Map<String, Wound> TYPES;
	
	private Wound reference;
	
	public static void instantiateWounds(){
		TYPES = new HashMap<String, Wound>();
		TYPES.put("BLUNT1-ARM",new Wound(12,1,"moreton"){
			public void onApply(Creature creature, Creature applier){
				applier.doAction("golpea inflingiendo un moreton en el brazo");
			}
			public void update(Creature creature){}
			public void onFinish(Creature creature){}
		});
		TYPES.put("BLUNT1-LEG",new Wound(12,1,"moreton"){
			public void onApply(Creature creature, Creature applier){
				applier.doAction("golpea inflingiendo un moreton en la pierna");
			}
			public void update(Creature creature){}
			public void onFinish(Creature creature){}
		});
		TYPES.put("BLUNT1-HEAD",new Wound(12,1,"moreton"){
			public void onApply(Creature creature, Creature applier){
				applier.doAction("golpea inflingiendo un moreton en la cabeza");
			}
			public void update(Creature creature){}
			public void onFinish(Creature creature){}
		});
		TYPES.put("BLUNT1-BACK",new Wound(12,1,"moreton"){
			public void onApply(Creature creature, Creature applier){
				applier.doAction("golpea inflingiendo un moreton en la espalda");
			}
			public void update(Creature creature){}
			public void onFinish(Creature creature){}
		});
		TYPES.put("BLUNT1-CHEST",new Wound(12,1,"moreton"){
			public void onApply(Creature creature, Creature applier){
				applier.doAction("golpea inflingiendo un moreton en el pecho");
			}
			public void update(Creature creature){}
			public void onFinish(Creature creature){}
		});
	}
	
	public Wound(int duration, int severity, String name){
		this.duration = duration;
		this.severity = severity;
		this.name = name;
	}
	
	public Wound(Wound other){
		this.duration = other.duration; 
		this.name = other.name;
		this.severity = other.severity;
		this.position = other.position;
		this.reference = other;
	}
	
	public void update(Creature creature){
		duration--;
		if(reference != null)
			reference.update(creature);
	}
	
	public void onApply(Creature creature, Creature applier){
		if(reference != null)
			reference.onApply(creature, applier);
	}
	
	public void onFinish(Creature creature){ }
}
