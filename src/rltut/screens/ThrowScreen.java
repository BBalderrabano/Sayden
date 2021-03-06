package rltut.screens;

import rltut.Creature;
import rltut.Item;

public class ThrowScreen extends InventoryBasedScreen {
	private int sx;
	private int sy;
	
	@Override
	protected Screen leftScreen(){
		return new EatScreen(player);
	}
	
	@Override
	protected Screen rightScreen(){
		return new DropScreen(player);
	}
	
	public ThrowScreen(Creature player, int sx, int sy) {
		super(player);
		this.sx = sx;
		this.sy = sy;
	}

	@Override
	protected String getVerb() {
		return "arrojar";
	}

	@Override
	protected boolean isAcceptable(Item item) {
		return true;
	}

	@Override
	protected Screen use(Item item) {
		return new ThrowAtScreen(player, sx, sy, item);
	}

	@Override
	public String getScreenName() {
		return "ARROJAR";
	}
}
