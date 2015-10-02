package rltut;

public class Spell {
	private String name;
	public String name() { return name; }

	private Effect effect;
	public Effect effect() { return effect; }

	private boolean requiresTarget = true;
	public boolean requiresTarget() { return requiresTarget; }
	
	public Spell(String name, Effect effect){
		this.name = name;
		this.effect = effect;
	}
	public Spell(String name, Effect effect, boolean requiresTarget){
		this.name = name;
		this.effect = effect;
		this.requiresTarget = requiresTarget;
	}
}
