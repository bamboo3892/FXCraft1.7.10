package com.okina.fxcraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public interface IHUDArmor {

	public void renderHUD(Minecraft mc, double renderTicks, ItemStack itemStack);

	public boolean comparePastRenderObj(ItemStack object);

}
