package rltut.screens;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import rltut.Constants;
import rltut.Creature;
import rltut.Item;
import rltut.Spell;
import asciiPanel.AsciiPanel;

public class ReadSpellScreen implements Screen {

	protected Creature player;
	private String letters;
	private int sx;
	private int sy;
	
	public ReadSpellScreen(Creature player, int sx, int sy){
		this.player = player;
		this.letters = "abcdefghijklmnopqrstuvwxyz";
		this.sx = sx;
		this.sy = sy;
	}
	
	public void displayOutput(AsciiPanel terminal) {
		ArrayList<String> lines = getList();
		
		int y = 23 - lines.size();
		int x = 4;

		if (lines.size() > 0)
			terminal.clear(' ', x, y, 20, lines.size());
		
		for (String line : lines){
			terminal.write(line, x, y++);
		}
		
		terminal.clear(' ', 0, 23, 80, 1);
		terminal.write("Que quieres conjurar?", 2, 23);
		
		terminal.repaint();
	}
	
	private ArrayList<Spell> getSpellList(){
		ArrayList<Spell> availableSpells = new ArrayList<Spell>();
		
		for(Item item : player.inventory().getItems()){
			if(item == null)
				continue;
			if(item.writtenSpells() == null)
				continue;
						
			for (int i = 0; i < item.writtenSpells().size(); i++){
				Spell spell = item.writtenSpells().get(i);
								
				availableSpells.add(spell);
			}
		}
		
		for (int i = 0; i < player.learnedSpells().size(); i++){
			Spell spell = player.learnedSpells().get(i);
									
			availableSpells.add(spell);
		}
		
		return availableSpells;
	}
	
	private ArrayList<String> getList() {
		ArrayList<String> lines = new ArrayList<String>();
		int n = 0;
		
		for(Item item : player.inventory().getItems()){
			if(item == null)
				continue;
			if(item.writtenSpells() == null)
				continue;
			
			for (int i = 0; i < item.writtenSpells().size(); i++){
				Spell spell = item.writtenSpells().get(i);
				
				String line = letters.charAt(i) + " - " + spell.name() + " " + "(" + item.name() + ")";
				n++;
				
				lines.add(line);
			}
		}
		
		for (int i = n; i < n + player.learnedSpells().size(); i++){
			Spell spell = player.learnedSpells().get(i - n);
						
			String line = letters.charAt(i) + " - " + spell.name() + " ";
			
			lines.add(line);
		}
		return lines;
	}

	public Screen respondToUserInput(KeyEvent key) {
		char c = key.getKeyChar();

		ArrayList<Spell> spells = getSpellList();
		
		if (letters.indexOf(c) > -1 
				&& spells.size() > letters.indexOf(c)
				&& spells.get(letters.indexOf(c)) != null) {
			return use(spells.get(letters.indexOf(c)));
		} else if (key.getKeyCode() == KeyEvent.VK_ESCAPE) {
			return null;
		} else {
			return this;
		}
	}

	protected Screen use(Spell spell){
		if(player.getBooleanData(Constants.IS_SILENCED) == true){
			player.doAction("esta silenciado! No puedes pronunciar palabra alguna!");
			return null;
		}
		if (spell.requiresTarget())
			return new CastSpellScreen(player, "", sx, sy, spell);
		else
			player.castSpell(spell, player.x, player.y);
		
		return null;
	}

	@Override
	public String getScreenName() {
		return "APRENDER";
	}
}
