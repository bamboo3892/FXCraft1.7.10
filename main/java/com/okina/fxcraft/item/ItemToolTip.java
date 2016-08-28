package com.okina.fxcraft.item;

import java.util.List;

import com.okina.fxcraft.client.IToolTipUser;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemToolTip extends Item implements IToolTipUser {

	private List<String> toolTip;

	public ItemToolTip(List<String> toolTip) {
		this.toolTip = toolTip;
	}

	@Override
	public void addToolTip(List<String> toolTip, ItemStack itemStack, EntityPlayer player, boolean shiftPressed, boolean advancedToolTip) {
		toolTip.addAll(this.toolTip);
	}

	@Override
	public int getNeutralLines() {
		return 0;
	}

	@Override
	public int getShiftLines() {
		return 0;
	}

}
