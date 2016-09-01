package com.okina.fxcraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public interface IHUDItem {

	public void renderHUD(Minecraft mc, double renderTicks, ItemStack itemStack);

	public boolean comparePastRenderObj(ItemStack object);

}
