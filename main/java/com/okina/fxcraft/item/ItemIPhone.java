package com.okina.fxcraft.item;

import com.okina.fxcraft.main.FXCraft;

import net.minecraft.item.Item;

public class ItemIPhone extends Item {

	public ItemIPhone() {
		setTextureName(FXCraft.MODID + ":iphone");
		setUnlocalizedName("fxcraft_iphone");
		setCreativeTab(FXCraft.FXCraftCreativeTab);
		setMaxStackSize(1);
	}

}
