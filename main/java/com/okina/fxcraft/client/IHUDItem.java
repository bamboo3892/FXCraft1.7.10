package com.okina.fxcraft.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public interface IHUDItem {

	@SideOnly(Side.CLIENT)
	public void renderHUD(Minecraft mc, double renderTicks, ItemStack itemStack);

	@SideOnly(Side.CLIENT)
	public boolean comparePastRenderObj(ItemStack object);

}
