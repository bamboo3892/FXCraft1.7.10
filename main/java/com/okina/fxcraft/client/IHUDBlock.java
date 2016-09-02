package com.okina.fxcraft.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;

/**implements this interface on TileEntity, Block*/
public interface IHUDBlock {

	/**Max renderTicks is 1 hour*/
	@SideOnly(Side.CLIENT)
	public void renderHUD(Minecraft mc, double renderTicks, MovingObjectPosition mop);

	/**
	 * @param object Past rendered object(tile or block or item)
	 * @param past
	 * @param current
	 * @return whether continue to count renderTicks that is passed to {@link IHUDBlock#renderHUD} method
	 */
	@SideOnly(Side.CLIENT)
	public boolean comparePastRenderObj(Object object, MovingObjectPosition past, MovingObjectPosition current);

}
