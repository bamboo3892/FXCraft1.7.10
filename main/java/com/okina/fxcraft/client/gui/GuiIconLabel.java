package com.okina.fxcraft.client.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.okina.fxcraft.main.FXCraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

public class GuiIconLabel extends GuiButton implements ITipComponent {

	private final static ResourceLocation TEXTURE = new ResourceLocation(FXCraft.MODID + ":textures/gui/container/icon_labels.png");

	private int textureX = 0;
	private int textureY = 0;
	private List<String> tips;

	public GuiIconLabel(int startX, int startY, int sizeX, int sizeY, int textureX, int textureY, List<String> tips) {
		super(998, startX, startY, sizeX, sizeY, "");
		this.textureX = textureX;
		this.textureY = textureY;
		this.tips = tips;
	}

	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		if(visible){
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			FontRenderer fontrenderer = minecraft.fontRenderer;
			minecraft.getTextureManager().bindTexture(TEXTURE);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			field_146123_n = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			int k = getHoverState(field_146123_n);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);

			drawTexturedModalRect(xPosition, yPosition, textureX, textureY, width, height);

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
