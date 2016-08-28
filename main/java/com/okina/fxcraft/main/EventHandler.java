package com.okina.fxcraft.main;

import static com.okina.fxcraft.main.FXCraft.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.okina.fxcraft.client.IHUDUser;
import com.okina.fxcraft.client.IToolTipUser;
import com.okina.fxcraft.main.CommonProxy.PopUpMessage;
import com.okina.fxcraft.utils.UtilMethods;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

public class EventHandler {

	@SubscribeEvent
	public void toolTip(ItemTooltipEvent event) {
		Item item = event.itemStack.getItem();
		if(item instanceof IToolTipUser){
			((IToolTipUser) item).addToolTip(event.toolTip, event.itemStack, event.entityPlayer, Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT), event.showAdvancedItemTooltips);
		}else if(item != null){
			Block block = Block.getBlockFromItem(item);
			if(block instanceof IToolTipUser){
				((IToolTipUser) block).addToolTip(event.toolTip, event.itemStack, event.entityPlayer, Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT), event.showAdvancedItemTooltips);
			}
		}

		if(item != null){
			if(item instanceof IToolTipUser || (item instanceof ItemBlock && Block.getBlockFromItem(item) instanceof IToolTipUser)){
				IToolTipUser tooltip;
				if(item instanceof IToolTipUser){
					tooltip = (IToolTipUser) item;
				}else{
					tooltip = (IToolTipUser) Block.getBlockFromItem(item);
				}
				for (int i = 0; i < tooltip.getNeutralLines(); i++){
					event.toolTip.add(StatCollector.translateToLocal("tooltip." + item.getUnlocalizedName() + "." + i));
				}

				if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)){
					for (int i = 0; i < tooltip.getShiftLines(); i++){
						event.toolTip.add(StatCollector.translateToLocal("tooltipshift." + item.getUnlocalizedName() + "." + i));
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void registerOreEvent(final OreRegisterEvent event) {}

	@SubscribeEvent
	public void interactTest(EntityInteractEvent event) {}

	@SubscribeEvent
	public void onCraftedHook(PlayerEvent.ItemCraftedEvent event) {}

	@SubscribeEvent
	public void onSmeltedHook(PlayerEvent.ItemSmeltedEvent event) {}

	@SubscribeEvent
	public void onPickedUpHook(PlayerEvent.ItemPickupEvent event) {}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onServerTick(TickEvent.ServerTickEvent event) {
		for (int i = 0; i < proxy.serverPacketList.size(); i++){
			packetDispatcher.sendToAll(proxy.serverPacketList.get(i));
		}
		proxy.serverPacketList.clear();
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		for (int i = 0; i < FXCraft.proxy.messageList.size(); i++){
			PopUpMessage msg = FXCraft.proxy.messageList.get(i);
			if(msg == null || msg.liveTime <= 0){
				FXCraft.proxy.messageList.remove(i);
				i--;
			}else{
				msg.liveTime--;
			}
		}
	}

	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {}

	private IHUDUser pastRenderedObject = null;
	private MovingObjectPosition pastMOP = null;
	private double renderStartTicks = 0;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		Minecraft mc = Minecraft.getMinecraft();

		if(!mc.isGamePaused() && mc.thePlayer != null){

			//hud
			if(mc.currentScreen == null){
				IHUDUser renderObj = null;
				MovingObjectPosition mop = UtilMethods.getMovingObjectPositionFromPlayer(mc.theWorld, mc.thePlayer, true);
				if(mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof IHUDUser){
					renderObj = (IHUDUser) mc.thePlayer.getCurrentEquippedItem().getItem();
				}else{
					if(mop != null && mop.sideHit != -1 && mop.typeOfHit == MovingObjectType.BLOCK){
						if(mc.theWorld.getTileEntity(mop.blockX, mop.blockY, mop.blockZ) instanceof IHUDUser){
							renderObj = (IHUDUser) mc.theWorld.getTileEntity(mop.blockX, mop.blockY, mop.blockZ);
						}else if(mc.theWorld.getBlock(mop.blockX, mop.blockY, mop.blockZ) instanceof IHUDUser){
							renderObj = (IHUDUser) mc.theWorld.getBlock(mop.blockX, mop.blockY, mop.blockZ);
						}
					}
				}
				if(renderObj != null){
					if(renderObj.comparePastRenderObj(pastRenderedObject, pastMOP, mop)){
						double tick = mc.theWorld.getTotalWorldTime() % 72000 + event.renderTickTime;
						renderObj.renderHUD(mc, tick - renderStartTicks, mop);
						return;
					}else{
						renderObj.renderHUD(mc, 0.0D, mop);
						pastRenderedObject = renderObj;
						pastMOP = mop;
						renderStartTicks = mc.theWorld.getTotalWorldTime() % 72000 + event.renderTickTime;
						return;
					}
				}
			}

			//popup
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDepthMask(true);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			for (PopUpMessage msg : FXCraft.proxy.messageList){
				if(msg.liveTime >= 0){
					int offsetX = msg.liveTime <= 10 ? 10 - msg.liveTime : 0;
					float alpha = msg.liveTime <= 10 ? (msg.liveTime / 10f) + 0.1f : 1.0F;
					if(alpha > 1f){
						alpha = 1f;
					}
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					Tessellator tessellator = Tessellator.instance;
					FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
					int size = fontRenderer.getStringWidth(msg.message);
					GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.5F);
					tessellator.startDrawingQuads();
					tessellator.addVertex(29 + offsetX, 72 + msg.index * 10, 0);
					tessellator.addVertex(31 + size + offsetX, 72 + msg.index * 10, 0);
					tessellator.addVertex(31 + size + offsetX, 72 + (msg.index + 1) * 10, 0);
					tessellator.addVertex(29 + offsetX, 72 + (msg.index + 1) * 10, 0);
					tessellator.draw();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					fontRenderer.drawString(msg.message, 30 + offsetX, 73 + msg.index * 10, 0x7cfc00, false);
				}
			}
			GL11.glPopAttrib();
		}
		pastRenderedObject = null;
		pastMOP = null;

	}

}
