package com.okina.fxcraft.client.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.okina.fxcraft.main.FXCraft;
import com.okina.fxcraft.rate.NoValidRateException;
import com.okina.fxcraft.rate.RateData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

public class GuiFXRateBox extends GuiButton {

	private final static ResourceLocation TEXTURE = new ResourceLocation(FXCraft.MODID + ":textures/gui/container/flat_button.png");

	public boolean selected = false;
	private String displayPair;
	private float[] selectedColor;
	private long updateMills = 0L;
	private double lastRenderedRate;
	private int lastRenderedColor;

	public GuiFXRateBox(int buttonID, int startX, int startY, int sizeX, int sizeY, String displayPair) {
		super(buttonID, startX, startY, sizeX, sizeY, displayPair);
		this.displayPair = displayPair.substring(0, 3) + "/" + displayPair.substring(3, 6);
		selectedColor = new float[] { 0.5f, 1f, 0f };
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

			if(selected){
				GL11.glColor4f(selectedColor[0], selectedColor[1], selectedColor[2], 0.5F);
			}else{
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
			}
			int offsetX = 0;
			int offsetY = 0;
			if(k == 0){//disabled
				offsetX = 128;
				offsetY = 128;
				drawTexturedModalRect(xPosition, yPosition, offsetX, offsetY, width - 1, height - 1);
				drawTexturedModalRect(xPosition, yPosition, offsetX + 128 - width, offsetY + 128 - height, width, height);
			}else{
				offsetX = 0;
				offsetY = 0;
				drawTexturedModalRect(xPosition, yPosition, offsetX, offsetY, width - 1, height - 1);
				drawTexturedModalRect(xPosition, yPosition, offsetX + 128 - width, offsetY + 128 - height, width, height);

			}
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			mouseDragged(minecraft, mouseX, mouseY);

			int color;
			if(selected){
				color = new Color(selectedColor[0], selectedColor[1], selectedColor[2]).getRGB();
			}else{
				color = 0xFFFFFF;
			}
			fontrenderer.drawString(displayPair, xPosition + width / 2 - fontrenderer.getStringWidth(displayPair) / 2, yPosition + 3, color, false);

			fontrenderer.drawString("Rate", xPosition + 2, yPosition + 17, 0xFFFFFF, false);
			if(FXCraft.rateGetter.hasUpdate(updateMills)){
				RateData nowRate;
				try{
					nowRate = FXCraft.rateGetter.getEarliestRate(displayString);
				}catch (NoValidRateException e){
					nowRate = null;
				}
				if(nowRate == null){
					lastRenderedColor = 0xFFFFFF;
					lastRenderedRate = 0;
				}else{
					if(nowRate.open == lastRenderedRate){
						lastRenderedColor = 0xFFFFFF;
					}else if(nowRate.open < lastRenderedRate){
						lastRenderedColor = 0xff4500;
					}else{
						lastRenderedColor = 0x00ffff;
					}
					lastRenderedRate = nowRate.open;
				}
				updateMills = System.currentTimeMillis();
			}
			String str = lastRenderedRate == 0 ? "---" : lastRenderedRate + "";
			fontrenderer.drawString(str, xPosition + width - fontrenderer.getStringWidth(str) - 2, yPosition + 17, lastRenderedColor, false);

			RateData today = FXCraft.rateGetter.getTodaysOpen(displayString);
			fontrenderer.drawString("Today's", xPosition + 2, yPosition + 27, 0xFFFFFF, false);

			fontrenderer.drawString("Open", xPosition + 4, yPosition + 37, 0xFFFFFF, false);
			str = today.open + "";
			fontrenderer.drawString(str, xPosition + width - fontrenderer.getStringWidth(str) - 2, yPosition + 37, 0xFFFFFF, false);

			fontrenderer.drawString("High", xPosition + 4, yPosition + 47, 0xFFFFFF, false);
			str = today.high + "";
			fontrenderer.drawString(str, xPosition + width - fontrenderer.getStringWidth(str) - 2, yPosition + 47, 0xFFFFFF, false);

			fontrenderer.drawString("Low", xPosition + 4, yPosition + 57, 0xFFFFFF, false);
			str = today.low + "";
			fontrenderer.drawString(str, xPosition + width - fontrenderer.getStringWidth(str) - 2, yPosition + 57, 0xFFFFFF, false);

			GL11.glPopAttrib();
		}
	}

	@Override
	public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
		if(enabled && visible && mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height){
			selected = true;
			return true;
		}
		return false;
	}

	@Override
	protected void mouseDragged(Minecraft minecraft, int mouseX, int mouseY) {}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {}

	/**sound
	 */
	@Override
	public void func_146113_a(SoundHandler p_146113_1_) {
		//		p_146113_1_.playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
	}

}
