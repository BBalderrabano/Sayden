package rltut.ai;

import java.util.ArrayList;

import rltut.Constants;
import rltut.Creature;
import rltut.screens.Option;

public class SilvioAi extends CreatureAi {

	private ArrayList<String> messages;
	
	public SilvioAi(Creature creature) {
		super(creature);
		messages = new ArrayList<String>();
	}
	public void onTalkTo(Creature talker){
		if(talker.getBooleanData(Constants.IS_SILENCED)){
			talker.notify(creature.color(), "...");
			return;
		}
		
		if(!creature.getBooleanData("IsIntroduced") && messages.isEmpty()){
			messages.clear();
			messages.add("...? Me sorprendiste, no te vi ahi extraño");
			messages.add("Estas vacio mi amigo, como la mayoria de las gentes de por aqui");
			messages.add("No te ofendas, no era mi intencion");
			messages.add("Mi nombre es Silvio y yo tambien estuve vacio");
			messages.add("Mas tiempo del que me permite admitir mi ego");
			messages.add("No es un problema estar vacio pero es ciertamente cansador");
			messages.add("Hay muchas cosas que uno puede hacer para llenarse...yo aprendi a hablar...");
			messages.add("No seas iluso, claro que no me refiero a la conversacion!");
			messages.add("Hay un tipo de dialogo secreto, olvidado, poderoso");
			messages.add("Excluido para la mayoria de nosotros los mortales");
			messages.add("Vacios...");
			messages.add("Deseas llenar tu ser, extraño? Te advierto que este camino es doloroso");
			messages.add("Muchos se pierden en la disciplina de las palabras");
			messages.add("Muchos se llenan de poder");
			messages.add("Pero muy pocos aprenden a hablar");
			messages.add("Seras tu diferente?");
			creature.setData("IsIntroduced", true);
		}
		if(creature.getBooleanData("IsIntroduced") 
				&& messages.isEmpty() && !creature.getBooleanData("IsFinished")){
			talker.addOption(new Option("Si (aprender palabra)", creature){
				public void onSelect(Creature player){
					player.clearOptions();
					creature.setData(Constants.SILVIO_SPELL, true);
					player.setData(Constants.SILVIO_SPELL, true);
					creature.ai().onTalkTo(player);
				}
			});
			talker.addOption(new Option("No (rechazar la palabra)", creature){
				public void onSelect(Creature player){
					player.clearOptions();
					creature.setData("NotMage", true);
					creature.ai().onTalkTo(player);
				}
			});
		}
		if(creature.getBooleanData("NotMage") && messages.isEmpty() && !creature.getBooleanData("IsFinished")){
			messages.clear();
			messages.add("No...claro que no...");
			messages.add("Jeje, debo admitir que trate de engañarte extraño, de intrigarte con mi lengua viperina");
			messages.add("Prometerte una forma de llenar tu vacio, un mundo nuevo de entendimiento...");
			messages.add("Pero la realidad es que estoy solo en este mundo y quisiera un compañero con quien hablar");
			messages.add("...a nadie le deseo esta soledad, tan silenciosa...");
			messages.add("Mi nombre es Silvio y ahora estoy lleno, ahora SOY");
			messages.add("Ve y no desperdicies tu ingenuidad, ve y busca tu camino");
			creature.setData("IsFinished", true);
			
		}
		if(creature.getBooleanData(Constants.SILVIO_SPELL) && messages.isEmpty() && !creature.getBooleanData("IsFinished")){
			messages.clear();
			messages.add("El camino que te espera delante desconocido es arduo y doloroso");
			messages.add("Te suplico cautela mientras deambules por las arenas del entendimiento");
			messages.add("Hoy aprendes la primer palabra secreta del camino del dialogo verdadero");
			messages.add("Usala con cautela y quizas nos podramos encontrar en el futuro");
			messages.add("Y si la providencia esta con nos, entablaremos una conversacion como nunca antes");
			messages.add("Y se terminara mi silenciosa soledad...");
			messages.add("Silvio es mi nombre extraño, recuerdalo siempre y recuerda tambien la palabra del MIEDO");
			talker.learnSpell(Constants.STARTING_SPELL());
			creature.setData("IsFinished", true);
		}		
		if(!messages.isEmpty()){
			talker.notify(creature.color(), messages.get(0));
			messages.remove(0);
		}
	}
}
