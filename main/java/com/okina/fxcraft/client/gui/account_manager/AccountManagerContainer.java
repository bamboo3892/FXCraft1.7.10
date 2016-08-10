package com.okina.fxcraft.client.gui.account_manager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class AccountManagerContainer extends Container {

	EntityPlayer player;

	public AccountManagerContainer(EntityPlayer player) {
		this.player = player;

		for (int col = 0; col < 3; ++col){
			for (int row = 0; row < 9; ++row){
				addSlotToContainer(new Slot(player.inventory, row + col * 9 + 9, 8 + row * 18, 121 + col * 18));
			}
		}
		for (int row = 0; row < 9; ++row){
			addSlotToContainer(new Slot(player.inventory, row, 8 + row * 18, 179));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

}
