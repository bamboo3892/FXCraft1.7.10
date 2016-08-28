package com.okina.fxcraft.client.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.okina.fxcraft.main.FXCraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiIconLabel extends GuiButton implements ITipComponent {

	private final static ResourceLocation TEXTURE = new ResourceLocation(FXCraft.MODID + ":textures/gui/container/icon_labels.png");

	private boolean isItem;
	private int textureX = 0;
	private int textureY = 0;
	private ItemStack item;
	private RenderItem renderItem;
	private List<String> tips;

	public GuiIconLabel(int startX, int startY, int sizeX, int sizeY, int textureX, int textureY, List<String> tips) {
		super(998, startX, startY, sizeX, sizeY, "");
		isItem = false;
		this.textureX = textureX;
		this.textureY = textureY;
		this.tips = tips;
	}

	public GuiIconLabel(int x, int y, ItemStack item, RenderItem renderItem) {
		super(998, x, y, 16, 16, "");
		isItem = true;
		this.item = item;
		this.renderItem = renderItem;
		this.tips = item.getTooltip(Minecraft.getMinecraft().thePlayer, false);
	}

	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		if(visible){
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			FontRenderer fontRenderer = minecraft.fontRenderer;
			minecraft.getTextureManager().bindTexture(TEXTURE);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			if(!isItem){
				drawTexturedModalRect(xPosition, yPosition, textureX, textureY, width, height);
			}else{
				renderItem.renderItemAndEffectIntoGUI(fontRenderer, minecraft.getTextureManager(), item, xPosition, yPosition);
				renderItem.renderItemOverlayIntoGUI(fontRenderer, minecraft.getTextureManager(), item, xPosition, yPosition);
			}

			mouseDragged(minecraft, mouseX, mouseY);

			GL11.glPopAttrib();
		}
	}

	public void setTips(List<String> tips) {
		this.tips = tips;
	}

	@Override
	public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
		return false;
	}

	@Override
	protected void mouseDragged(Minecraft minecraft, int mouseX, int mouseY) {}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {}

	@Override
	public void func_146113_a(SoundHandler p_146113_1_) {}

	@Override
	public List<String> getTipList(int mouseX, int mouseY, boolean shift, boolean ctrl) {
		return tips;
	}

}
