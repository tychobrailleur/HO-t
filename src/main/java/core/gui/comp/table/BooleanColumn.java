package core.gui.comp.table;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.model.player.Player;

public class BooleanColumn extends UserColumn {

	public BooleanColumn(int id, String name, String tooltip, int minWidth){
		super(id,name,tooltip);
		this.minWidth = minWidth;
		preferredWidth = minWidth;
	}

	public Boolean getValue(Player player){

		return player.getCanBeSelectedByAssistant();
	}


}
