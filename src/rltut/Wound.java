package rltut;

import java.util.ArrayList;
import java.util.Collections;

public class Wound extends DataStructure{
	protected int duration;
	public int duration() { return duration; }
	public void endDuration() { this.duration = 0; }
	
	protected int originalDuration;
	public void resetDuration() { this.duration = this.originalDuration; }
	
	public boolean isHealed() { return duration < 0 ? false : duration < 1; }
	
	private DamageType type;
	public DamageType type() { return type; }
	
	private BodyPart bodyPart;
	public BodyPart bodyPart() { return bodyPart; }
	public Wound setBodyPart(BodyPart position) { this.bodyPart = position; return this; }
	
	private Wound reference;
	
	private String name;
	public String name() { return name; }
	public void modifyName(String newName) { this.name = newName; }
	
	private String description;
	public String description() { return description; }
	
	private double weight;
	public double weight() { return weight; }
	
	public static Wound pickWeightedWound(ArrayList<Wound> wounds){
		Collections.shuffle(wounds);
		
		double totalWeight = 0.0d;
		
		for (Wound i : wounds)
		{
		    totalWeight += i.weight();
		}

		int randomIndex = -1;
		double random = Math.random() * totalWeight;
		for (int i = 0; i < wounds.size(); ++i)
		{
		    random -= wounds.get(i).weight();
		    if (random <= 0.0d)
		    {
		        randomIndex = i;
		        break;
		    }
		}
		
		return wounds.get(randomIndex);
	}

	public static Wound getDefaultWound(final DamageType type, BodyPart bodyPart, Creature target) {
		ArrayList<Wound> possibleWounds = new ArrayList<Wound>();
		
		//********************************************************************************************************************
		if(type.wondType() == DamageType.BLUNT.wondType()){
			if(bodyPart.position() == BodyPart.HEAD.position()){
				if(!target.hasWound("Mandibula rota"))
					possibleWounds.add(new Wound("Mandibula rota", "imposibilita dialogo", Constants.WOUND_DURATION_MID, type, bodyPart, 0.8){
						public void onApply(Creature creature, Creature applier){
							//Crack!! Al golpear con tu martillo destrozas la mandibula del jefe ogro
							//Crack!! Al golpear con el martillo el jefe ogro destroza tu mandibula
							creature.notifyArround(Constants.WOUND_COLOR, "Crack!! Al golpear con "+ type.itemOrigin().nameElLaTu(applier)+ " " +
										(applier.isPlayer() ? "destrozas la mandibula " + creature.nameDelDeLa() : applier.nameElLa() + " destroza tu mandibula"));
							creature.notify(Constants.WOUND_COLOR, "[-2 vitalidad maxima y no puedes pronunciar palabras]");
							creature.setData(Constants.IS_SILENCED, true);
							creature.modifyMaxHp(-2);
						}
						public void onFinish(Creature creature){
							creature.unsetData(Constants.IS_SILENCED);
							creature.modifyMaxHp(2);
						}
					});
				
				possibleWounds.add(new Wound("Lesion craneal", "reduce vitalidad", Constants.WOUND_DURATION_HIGH, type, bodyPart, 1){
					public void onApply(Creature creature, Creature applier){
						//Crush!! Tu martillo impacta con fuerza y el craneo del jefe ogro cruje horriblemente
						//Crush!! El martillo del jefe ogro impacta con fuerza y tu craneo cruje horriblemente
						creature.notifyArround(Constants.WOUND_COLOR, "Crush!! "+ StringUtils.capitalize(type.itemOrigin().nameElLaTu(applier)) +
								(applier.isPlayer() ? "" : " " + applier.nameDelDeLa()) + " impacta con fuerza y "+ (creature.isPlayer() ? "tu craneo" : "el craneo "+creature.nameDelDeLa()) +" cruje horriblemente");
						creature.notify(Constants.WOUND_COLOR, "[-2 vitalidad maxima y aturdimiento]");
						creature.modifyMaxHp(-2);
						creature.modifyStatusColor(Constants.MESSAGE_STATUS_EFFECT_COLOR);
						creature.modifyActionPoints(-100, "aturdido por el impacto en el craneo");
					}
					public void onFinish(Creature creature){
						creature.modifyMaxHp(2);
					}
				});
			}
			if(bodyPart.position() == BodyPart.ARMS.position()){
				if(target.hasWound("Lesion en brazo"))
					possibleWounds.add(new Wound("Fractura en brazo", "penaliza velocidad ataque", Constants.WOUND_DURATION_HIGH, type, bodyPart, 0.8){
						public void onApply(Creature creature, Creature applier){
							//CRA-CRACK!! El brazo lesionado del jefe ogro cede, exponiendo un ensangrentado hueso fracturado!
							//CRA-CRACK!! Tu brazo lesionado cede, exponiendo un ensangrentado hueso fracturado!
							creature.notifyArround(Constants.WOUND_COLOR, "CRA-CRACK!! "+(creature.isPlayer() ? "Tu brazo lesionado" : "El brazo lesionado" + creature.nameDelDeLa())+ 
									" cede, exponiendo un ensangrentado hueso fracturado!");
							creature.notify("[50 penalizador velocidad ataque y desequipar arma]");
							creature.drop(creature.weapon());
							creature.modifyAttackSpeed(50);
						}
						public void onFinish(Creature creature){
							creature.modifyAttackSpeed(-50);
						}
					});
				
				possibleWounds.add(new Wound("Lesion en brazo", "reduce vitalidad", Constants.WOUND_DURATION_MID, type, bodyPart, 1){
					public void onApply(Creature creature, Creature applier){
						//Impactas el brazo del jefe ogro, lesionandolo [y forzando que suelte su martillo] <- si tiene arma
						//El jefe ogro impacta tu brazo, lesionandolo [y forzandote a que sueltes tu martillo] <- si tiene arma
						creature.notifyArround(Constants.WOUND_COLOR, (applier.isPlayer() ? "Impactas" : StringUtils.capitalize(applier.nameElLa()) + " impacta ") +
								(creature.isPlayer() ? "tu brazo" : "el brazo " + creature.nameDelDeLa())+ ", lesionandolo"+ (creature.weapon() != null ? 
										(creature.isPlayer() ? " y forzandote a que sueltes " : " y forzando que suelte ")+creature.weapon().nameTuSu(creature) : ""));
						creature.notify("[-2 vitalidad maxima y desequipar arma]");
						creature.drop(creature.weapon());
						creature.modifyMaxHp(-2);
					}
					public void onFinish(Creature creature){
						creature.modifyMaxHp(2);
					}
				});
			}
			if(bodyPart.position() == BodyPart.LEGS.position()){
				possibleWounds.add(new Wound("Lesion en pierna", "reduce vitalidad y penaliza movimiento", Constants.WOUND_DURATION_MID, type, bodyPart, 1){
					public void onApply(Creature creature, Creature applier){
						//Crush!! El impacto de el martillo lesiona tu pierna
						//Crush!! El impacto de el martillo lesiona la pierna del jefe ogro
						creature.notifyArround(Constants.WOUND_COLOR, "Crush!! El impacto de "+type.itemOrigin().nameElLaTu(applier)+" lesiona"
								+(creature.isPlayer() ? " tu pierna" : " la pierna " + creature.nameDelDeLa()));
						creature.notify("[-2 vitalidad y 50 penalizador movimiento]");
						creature.modifyMaxHp(-2);
						creature.modifyMovementSpeed(50);
					}
					public void onFinish(Creature creature){
						creature.modifyMaxHp(2);
						creature.modifyMovementSpeed(-50);
					}
				});
			}
			if(bodyPart.position() == BodyPart.BACK.position()){
				possibleWounds.add(new Wound("Costilla lesionada", "reduce vitalidad", Constants.WOUND_DURATION_MID, type, bodyPart, 1){
					public void onApply(Creature creature, Creature applier){
						//Slam!! Escuchas el crujir de una de tus costillas mientras caes de rodillas adolorido
						//Slam!! Escuchas el crujir de una de las costillas del jefe ogro mientras se desploma adolorido
						creature.notifyArround(Constants.WOUND_COLOR, "Slam!! Escuchas el crujir de una de"+(creature.isPlayer() ? " tus costillas" : " las costillas "+creature.nameDelDeLa())+ 
								(creature.isPlayer() ? " mientras caes de rodillas, adolorido" : " mientras se desploma adolorido"));
						creature.modifyActionPoints(-100, "de rodillas");
						creature.notify("[-2 vitalidad y aturdimiento]");
						creature.modifyStatusColor(Constants.MESSAGE_STATUS_EFFECT_COLOR);
						creature.modifyMaxHp(-2);
					}
					public void onFinish(Creature creature){
						creature.modifyMaxHp(2);
					}
				});
			}
		}
		
		//********************************************************************************************************************
		if(type.wondType() == DamageType.SLICE.wondType()){
			if(bodyPart.position() == BodyPart.HEAD.position()){
				possibleWounds.add(new Wound("Corte a la cien", "desangra y enceguece", Constants.WOUND_DURATION_LOW, type, bodyPart, 1){
					public void onApply(Creature creature, Creature applier){
						//Slash!! La espada del jefe ogro produce un severo corte en tu cabeza que comienza a sangrar!
						//Slash!! Tu espada produce un severo corte en la cabeza del jefe ogro que comienza a sangrar!
						creature.notifyArround(Constants.WOUND_COLOR, "Slash!! "+ StringUtils.capitalize(type.itemOrigin().nameElLaTu(applier)) + " " + (creature.isPlayer() ? applier.nameDelDeLa() + " " : "") +
								(type.itemOrigin().nameElLa().endsWith("s") ? "producen" : "produce") +  " un "
								+ "severo corte en " + (creature.isPlayer() ? "tu cabeza" : "la cabeza " + applier.nameDelDeLa()) + " que comienza a sangrar!");
						creature.notify(Constants.WOUND_COLOR, "[sangrado y -2 vision]");
						creature.modifyVisionRadius(-2);
					}
					public void onMove(Creature creature){
						creature.bleed(1);
						creature.modifyHp(-1, "Mueres desangrado");
					}
					public void onFinish(Creature creature){
						creature.modifyVisionRadius(2);
					}
				});
			}
			if(bodyPart.position() == BodyPart.ARMS.position()){
				possibleWounds.add(new Wound("Corte en el brazo", "sangrado y reduce presicion", Constants.WOUND_DURATION_LOW, type, bodyPart, 1){
					public void onApply(Creature creature, Creature applier){
						//Sliiick!! Un horrendo corte se genera en tu brazo
						//Sliiick!! Un horrendo corte se genera en el brazo del jefe ogro
						creature.notifyArround(Constants.WOUND_COLOR, "Slliiick!! " + 
								(creature.isPlayer() ? StringUtils.capitalize(applier.nameElLa()) + " genera un horrible corte en " : "Generas un horrible corte en ") + 
								(creature.isPlayer() ? "tu brazo y cercenando tus tendones" : "el brazo " + creature.nameDelDeLa() + ", cercenando sus tendones"));
						creature.notify(Constants.WOUND_COLOR, "[sangrado y reduce presicion]");
						creature.modifyAccuracy(-20);
					}
					public void onMove(Creature creature){
						creature.bleed(1);
						creature.modifyHp(-1, "Mueres desangrado");
					}
					public void onFinish(Creature creature){
						creature.modifyAccuracy(20);
					}
				});
			}
			if(bodyPart.position() == BodyPart.LEGS.position()){
				possibleWounds.add(new Wound("Corte en la pierna", "sangrado y reduce velocidad", Constants.WOUND_DURATION_LOW, type, bodyPart, 1){
					public void onApply(Creature creature, Creature applier){
						//Cortas la pierna del jefe ogro reventando su rodilla y cirniendolo sobre el piso
				      	//El jefe ogro corta tu pierna, reventando tu rodilla y cirniendote sobre el piso
						creature.notifyArround(Constants.WOUND_COLOR, (creature.isPlayer() ? 
								StringUtils.capitalize(applier.nameElLa())+" corta tu pierna, reventando tu rodilla y cirniendote sobre el piso" : 
									"Cortas la pierna "+ creature.nameDelDeLa() + " reventando su rodilla y cirniendolo sobre el piso"));
						creature.notify(Constants.WOUND_COLOR, "[sangrado y reduce velocidad]");
						creature.modifyMovementSpeed(50);
					}
					public void onMove(Creature creature){
						creature.bleed(1);
						creature.modifyHp(-1, "Mueres desangrado");
					}
					public void onFinish(Creature creature){
						creature.modifyMovementSpeed(-50);
					}
				});
			}
			if(bodyPart.position() == BodyPart.BACK.position()){
				possibleWounds.add(new Wound("Corte a la espalda", "sangrado", Constants.WOUND_DURATION_LOW, type, bodyPart, 1){
					public void onApply(Creature creature, Creature applier){
						creature.notifyArround(Constants.WOUND_COLOR, "A este punto me harte de hacer heridas, te pegaron o pegaste en la espalda con un arma cortante");
						creature.notify("[sangrado]");
					}
					public void onMove(Creature creature){
						creature.bleed(1);
						creature.modifyHp(-1, "Mueres desangrado");
					}
				});
			}
		}
		
		//********************************************************************************************************************
		if(type.wondType() == DamageType.PIERCING.wondType()){
			if(bodyPart.position() == BodyPart.HEAD.position()){
				possibleWounds.add(new Wound("Puñal al ojo", "reduccion vision", Constants.WOUND_DURATION_MID, type, bodyPart, 1){
					public void onApply(Creature creature, Creature applier){
				      	//Aaaargh! La punta de la daga penetra en tu ojo, encegueciendote
				      	//Aaaargh! La punta de la daga penetra en el ojo del jefe ogro, encegueciendolo
						creature.notifyArround(Constants.WOUND_COLOR, "Aaaargh!! " 
								+ StringUtils.capitalize(type.itemOrigin().nameElLa()) + " penetra " + 
								(creature.isPlayer() ? "en tu ojo, encegueciendote" : " en el ojo " + creature.nameDelDeLa() + ", encegueciendolo"));
						creature.notify("[reduccion vision]");
						creature.modifyVisionRadius(-4);
					}
					public void onFinish(Creature creature){
						creature.modifyVisionRadius(4);
					}
				});
			}
			if(bodyPart.position() == BodyPart.BACK.position()){
				possibleWounds.add(new Wound("Backstab!", "masivo daño!", Constants.WOUND_DURATION_MID, type, bodyPart, 1){
					public void onApply(Creature creature, Creature applier){
						//Penetrando tu pulmon la daga impacta generando un dolor inimaginable!
						//Penetrando el pulmon del jefe ogro tu daga impacta generando un dolor inimaginable!
						creature.notifyArround(Constants.WOUND_COLOR, "Penetrando " + (creature.isPlayer() ? "tu pulmon" : "el pulmon " + creature.nameDelDeLa())+ 
								" "+ type.itemOrigin().nameElLaTu(applier) + " impacta generando un dolor inimaginable!");
						creature.notify("[bonus de daño]");
						creature.modifyHp(-4, "Pulmon perforado!");
					}
				});
			}
			if(bodyPart.position() == BodyPart.ARMS.position()){
				possibleWounds.add(new Wound("Puñal al brazo", "reduce velocidad", Constants.WOUND_DURATION_MID, type, bodyPart, 1){
					public void onApply(Creature creature, Creature applier){
						creature.notifyArround(Constants.WOUND_COLOR, StringUtils.capitalize(type.itemOrigin().nameElLaTu(creature)) + " perfora con fuerza "
								+ (creature.isPlayer() ? "tu brazo, cortandote un tendon!" : "el brazo "+ creature.nameDelDeLa() + " cortandole un tendon!"));
						creature.notify("[reduccion velocidad]");
						creature.modifyAttackSpeed(50);
					}
					public void onFinish(Creature creature){
						creature.modifyAttackSpeed(-50);
					}
				});
			}
		}
		/*
		if(type.wondType() == DamageType.PIERCING.wondType()){
			
			possibleWounds.add(new Wound("Estocada", "bonus de herida por estocada", 2, type, bodyPart));
			
			if(bodyPart.position() == BodyPart.HEAD.position())
				possibleWounds.add(new Wound("Filo al ojo", "reduce vision", Constants.WOUND_DURATION_MID, type, bodyPart){
					public void onApply(Creature creature, Creature applier){
						creature.notifyArround(Constants.WOUND_COLOR, "Con un movimiento descendente %s!", 
								applier.isPlayer() ? "hundes tu arma en el ojo " + StringUtils.genderizeCreature(creature.gender(), creature.name(), true) : StringUtils.genderizeCreature(applier.gender(), applier.name(), true) + " recibes un impacto en el ojo!");
						creature.notify(Constants.WOUND_COLOR,"[reduccion vision]");
						creature.modifyVisionRadius(-8);

					}
					public void onFinish(Creature creature){
						creature.modifyVisionRadius(8);
					}
				});
			if(bodyPart.position() == BodyPart.BACK.position())
				possibleWounds.add(new Wound("Estoque a la nuca", "5 puntos de herida", 1, type, bodyPart){
					public void onApply(Creature creature, Creature applier){
						creature.notifyArround(Constants.WOUND_COLOR, "Tomando%s del pelo %s craneo!", 
								creature.isPlayer() ? "te" : StringUtils.formatTextToGender("", creature), applier.isPlayer() ? "atraviezas su" : "atraviesas tu");
						creature.notify(Constants.WOUND_COLOR,"[5 puntos de herida]");
						creature.modifyHp(-5, "Mueres al ser perforado en la nuca, atravesando el craneo");

					}
				});
			if(bodyPart.position() == BodyPart.CHEST.position())
				possibleWounds.add(new Wound("Estoque al corazon", "5 puntos de herida", 1, type, bodyPart){
					public void onApply(Creature creature, Creature applier){
						creature.notifyArround(Constants.WOUND_COLOR, "Con un rapido movimiento"+
								(applier.isPlayer() ? StringUtils.genderizeCreature(creature.gender(), creature.name(), false) : "")
								+" hunde!");
						creature.notify(Constants.WOUND_COLOR,"[5 puntos de herida]");
						creature.modifyHp(-5, "Mueres al ser perforado en el corazon");
					}
				});
		}
		
		if(type.wondType() == DamageType.SLICE.wondType()){
			possibleWounds.add(new Wound("Corte superficial", "sangrado por 1 punto", 1, type, bodyPart){
				public void onApply(Creature creature, Creature applier){
					creature.notifyArround(Constants.WOUND_COLOR, "Con un movimiento impreciso %s!", StringUtils.formatTextToGender("inflije un corte", creature, "s"));
					creature.notify(Constants.WOUND_COLOR,"[sangrado por 1 punto]");
				}
				public void update(Creature creature){
					super.update(creature);
					creature.bleed(1);
					creature.modifyHp(-1, "Mueres desangrado");
				}
			});
			possibleWounds.add(new Wound("Corte peligroso", "sangrado por 2 puntos", 2, type, bodyPart){
				public void onApply(Creature creature, Creature applier){
					creature.notifyArround(Constants.WOUND_COLOR, "Con un movimiento ascendente %s!", StringUtils.formatTextToGender("inflije un corte", creature, "s"));
					creature.notify(Constants.WOUND_COLOR,"[sangrado por 2 puntos]");
				}
				public void update(Creature creature){
					super.update(creature);
					creature.bleed(1);
					creature.modifyHp(-1, "Mueres desangrado");
				}
			});
			possibleWounds.add(new Wound("Corte profundo", "sangrado por 3 puntos", 3, type, bodyPart){
				public void onApply(Creature creature, Creature applier){
					creature.notifyArround(Constants.WOUND_COLOR, "Con un movimiento elegante y preciso %s!", StringUtils.formatTextToGender("inflije un profundo corte", creature, "s"));
					creature.notify(Constants.WOUND_COLOR,"[sangrado por 3 puntos]");
				}
				public void update(Creature creature){
					super.update(creature);
					creature.bleed(1);
					creature.modifyHp(-1, "Mueres desangrado");
				}
			});
			
			if(bodyPart.position() == BodyPart.HEAD.position() && !target.hasWound("Corte en la cien"))
				possibleWounds.add(new Wound("Corte en la cien", "enceguecimiento", Constants.WOUND_DURATION_LOW, type, bodyPart){
					public void onApply(Creature creature, Creature applier){
						creature.notifyArround(Constants.WOUND_COLOR, "El ataque genera un corte en %s y la sangre cae hacia los ojos!", 
								StringUtils.formatTextToGender("cabeza", creature, "td"));
						creature.notify(Constants.WOUND_COLOR,"[enceguecimiento]");
						creature.modifyVisionRadius(-20);
					}
					public void onFinish(Creature creature){
						creature.modifyVisionRadius(20);
					}
				});
			if(bodyPart.position() == BodyPart.HEAD.position() && !target.hasWound("Corte al cuello"))
				possibleWounds.add(new Wound("Corte al cuello", "3 herida y 3 desangre", 3, type, bodyPart){
					public void onApply(Creature creature, Creature applier){
						creature.notifyArround(Constants.WOUND_COLOR, "Con un peligroso movimiento el ataque alcanza %s!", 
								StringUtils.formatTextToGender("cuello", creature, "td"));
						creature.notify(Constants.WOUND_COLOR,"[3 puntos de herida y 3 desangre]");
						creature.modifyHp(-3, "Tu cabeza cae rodando al suelo");
					}
					public void onFinish(Creature creature){
						creature.bleed(1);
						creature.modifyHp(-1, "Mueres desangrado");
					}
				});
			
			if(bodyPart.position() == BodyPart.ARMS.position() && target.hasBodyPart(BodyPart.IZQ_ARM.position()))
				possibleWounds.add(new Wound("Brazo amputado", "brazo habil amputado", Constants.WOUND_PERMANENT, type, bodyPart){
					public void onApply(Creature creature, Creature applier){
						creature.notifyArround(Constants.WOUND_COLOR, "El ataque es certero y separa %s!", 
								creature.isPlayer() ? "tu brazo":"el brazo "+ StringUtils.genderizeCreature(creature.gender(), creature.name(),false));
						creature.notify(Constants.WOUND_COLOR,"[brazo habil amputado]");
						creature.force_drop(new Item(ItemType.STATIC,(char)191, 'M', creature.color(), "brazo humano separado", null));
						creature.removeBodyPart(creature.getBodyPart(BodyPart.IZQ_ARM.position()));
					}
				});
			
			if(bodyPart.position() == BodyPart.ARMS.position() && target.hasBodyPart(BodyPart.DER_ARM.position()))
				possibleWounds.add(new Wound("Brazo amputado", "brazo amputado", Constants.WOUND_PERMANENT, type, bodyPart){
					public void onApply(Creature creature, Creature applier){
						creature.notifyArround(Constants.WOUND_COLOR, "El ataque es certero y separa %s!", 
								creature.isPlayer() ? "tu brazo":"el brazo "+ StringUtils.genderizeCreature(creature.gender(), creature.name(),false));
						creature.notify(Constants.WOUND_COLOR,"[brazo amputado]");
						creature.force_drop(new Item(ItemType.STATIC,(char)218, 'M', creature.color(), "brazo humano separado", null));
						creature.removeBodyPart(creature.getBodyPart(BodyPart.DER_ARM.position()));
					}
				});
			
			if(bodyPart.position() == BodyPart.LEGS.position() && target.hasBodyPart(BodyPart.LEGS.position()))
				possibleWounds.add(new Wound("Pierna amputada", "pierna amputada", Constants.WOUND_PERMANENT, type, bodyPart){
					public void onApply(Creature creature, Creature applier){
						creature.notifyArround(Constants.WOUND_COLOR, "El ataque es certero y separa %s!", 
								creature.isPlayer() ? "tu pierna":"la pierna "+ StringUtils.genderizeCreature(creature.gender(), creature.name(),false));
						creature.notify(Constants.WOUND_COLOR,"[amputas la pierna]");
						creature.force_drop(new Item(ItemType.STATIC,Math.random() < 0.5 ? (char)192 : (char)217, 'F', creature.color(), "pierna humana separada", null));
						creature.removeBodyPart(creature.getBodyPart(BodyPart.LEGS.position()));
					}
				});
		}
		
		if(type.wondType() == DamageType.BLUNT.wondType()){
			//Hueso roto
			possibleWounds.add(new Wound("Hueso roto","-1 vida maxima", Constants.WOUND_DURATION_HIGH, type, bodyPart){
				public void onApply(Creature creature, Creature applier){
					creature.notifyArround(Constants.WOUND_COLOR,"El impacto %s!", StringUtils.formatTextToGender("rompe un hueso", creature));
					creature.notify(Constants.WOUND_COLOR,"[-1 vida maxima]");
					creature.modifyMaxHp(-1);
				}
				public void onFinish(Creature creature){
					creature.modifyMaxHp(1);
				}
			});
			//Contusion
			possibleWounds.add(new Wound("Contusion","impacto produce dolor", Constants.WOUND_DURATION_HIGH, type, bodyPart){
				public void onApply(Creature creature, Creature applier){
					creature.notifyArround(Constants.WOUND_COLOR,"El golpe %s!", StringUtils.formatTextToGender("produce una contusion severa", creature));
					creature.notify(Constants.WOUND_COLOR,"[impacto produce dolor]");
					creature.modifyActionPoints(-100, "adolorido");
				}
			});
			//Mareos
			if(bodyPart.position() == BodyPart.HEAD.position() && !target.hasWound("Mareado"))
				possibleWounds.add(new Wound("Mareado","mareos al moverse", Constants.WOUND_DURATION_LOW, type, bodyPart){
					public void onApply(Creature creature, Creature applier){
						creature.notifyArround(Constants.WOUND_COLOR,"El golpe en la cabeza %s!", StringUtils.formatTextToGender("produce severos mareos", creature));
						creature.notify(Constants.WOUND_COLOR,"[mareos severos al moverse]");
					}
					public void onBeforeMove(Creature creature){
						creature.ai().wander();
						creature.notify("Estas mareado!");
					}
				});
			//Creaneo destrozado
			if(bodyPart.position() == BodyPart.HEAD.position())
				possibleWounds.add(new Wound("Craneo destrozado","3 herida y -2 vida maxima", Constants.WOUND_PERMANENT, type, bodyPart){
					public void onApply(Creature creature, Creature applier){
						creature.notifyArround(Constants.WOUND_COLOR,"Con un gran impacto en la cabeza %s!", StringUtils.formatTextToGender("provoca una fisura", creature));
						creature.notify(Constants.WOUND_COLOR,"[3 puntos de herida y -2 vida maxima]");
						creature.modifyHp(-3, "Craneo destrozado por un impacto contundente");
						creature.modifyMaxHp(-2);
					}
					public void onFinish(Creature creature){
						creature.modifyMaxHp(2);
					}
				});
			//Contusion en brazo
			if(bodyPart.position() == BodyPart.ARMS.position())
				possibleWounds.add(new Wound("Contusion en el brazo", "-50 velocidad ataque", Constants.WOUND_DURATION_MID, type, bodyPart){
					public void onApply(Creature creature, Creature applier){
						creature.notifyArround(Constants.WOUND_COLOR,"Al golpear %s!", StringUtils.formatTextToGender("provoca una contusion en el brazo", creature, "sd"));
						creature.notify(Constants.WOUND_COLOR,"[50 penalizador velocidad ataque]");
						creature.modifyAttackSpeed(50);
					}
					public void onFinish(Creature creature){
						creature.modifyAttackSpeed(-50);
					}
				});
			//Fractura en brazo habil
			if(bodyPart.position() == BodyPart.ARMS.position() && !target.hasWound("Brazo habil fracturado"))
				possibleWounds.add(new Wound("Brazo habil fracturado", "suelta el arma al ser golpeado", Constants.WOUND_DURATION_MID, type, bodyPart){
					public void onApply(Creature creature, Creature applier){
						creature.notifyArround(Constants.WOUND_COLOR,"Al golpear %s!", StringUtils.formatTextToGender("fractura el brazo habil", creature, "sd"));
						creature.notify(Constants.WOUND_COLOR,"[suelta el arma al ser golpeado]");						
					}
					public void onGetAttack(Creature creature, Creature attacker, BodyPart part){
						if(part.position() == BodyPart.ARMS.position())
							creature.drop(creature.weapon(), "");
					}
				});
			//Fractura en brazo secundario
			if(bodyPart.position() == BodyPart.ARMS.position() && !target.hasWound("Brazo tosco fracturado"))
				possibleWounds.add(new Wound("Brazo tosco fracturado", "suelta el escudo al ser golpeado", Constants.WOUND_DURATION_MID, type, bodyPart){
					public void onApply(Creature creature, Creature applier){
						creature.notifyArround(Constants.WOUND_COLOR,"Al golpear %s!", StringUtils.formatTextToGender("fractura el brazo tosco", creature, "sd"));
						creature.notify(Constants.WOUND_COLOR,"[suelta el escudo al ser golpeado]");						
					}
					public void onGetAttack(Creature creature, Creature attacker, BodyPart part){
						if(part.position() == BodyPart.ARMS.position())
							creature.drop(creature.shield(), "");
					}
				});
			//Fractura en piernas
			if(bodyPart.position() == BodyPart.LEGS.position())
				possibleWounds.add(new Wound("Contusion en la pierna", "-50 velocidad movimiento", Constants.WOUND_DURATION_MID, type, bodyPart){
					public void onApply(Creature creature, Creature applier){
						creature.notifyArround(Constants.WOUND_COLOR,"Al golpear %s!", StringUtils.formatTextToGender("provoca una contusion en la pierna", creature, "sd"));
						creature.notify(Constants.WOUND_COLOR,"[50 penalizador velocidad movimiento]");
						creature.modifyMovementSpeed(50);
					}
					public void onFinish(Creature creature){
						creature.modifyMovementSpeed(-50);
					}
				});
			//Rodilla dislocada
			if(bodyPart.position() == BodyPart.LEGS.position())
				possibleWounds.add(new Wound("Rodilla dislocada", "1 herida al moverse", Constants.WOUND_DURATION_MID, type, bodyPart){
					public void onApply(Creature creature, Creature applier){
						creature.notifyArround(Constants.WOUND_COLOR,"Al impactar %s!", StringUtils.formatTextToGender("expone el hueso de la rodilla", creature, "sd"));
						creature.notify(Constants.WOUND_COLOR,"[1 punto de herida al moverse]");
					}
					public void onMove(Creature creature){
						creature.modifyHp(-1, "El hueso expuesto de tu pierna fracturada es demasiado, mueres en agonia");
					}
				});
		}*/
		if(!possibleWounds.isEmpty())
			return pickWeightedWound(possibleWounds);
		else
			return null;
	}
	
	public Wound(String name, String description, int duration, DamageType type, BodyPart bodyPart, double weight){
		this.duration = duration;
		this.originalDuration = duration;
		this.type = type;
		this.bodyPart = bodyPart;
		this.name = name;
		this.description = description;
		this.weight = weight;
	}
	
	public Wound(Wound other){
		this.duration = other.duration();
		this.originalDuration = other.duration();
		this.type = other.type();
		this.bodyPart = other.bodyPart();
		this.weight = other.weight();
		this.reference = other;
		this.setAllData(other.getAllData());
	}
	
	public void update(Creature creature){
		if(duration > 0)
			duration--;
		
		if(reference != null)
			reference.update(creature);
	}
	
	public void onBeforeAttack(Creature creature){}
	
	public void onAttack(Creature creature){}
	
	public void onGetAttack(Creature creature, Creature attacker, BodyPart part){}
	
	public void onBeforeMove(Creature creature){}

	public void onMove(Creature creature) {}
	
	public void onApply(Creature creature, Creature applier){
		if(reference != null)
			reference.onApply(creature, applier);
	}
	
	public void onFinish(Creature creature){
		if(reference != null)
			reference.onFinish(creature);
	}
}