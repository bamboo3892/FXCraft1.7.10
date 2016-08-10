package com.okina.fxcraft.client.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.okina.fxcraft.main.FXCraft;
import com.okina.fxcraft.utils.RenderingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

public class GuiTable<T extends GuiTableRow> extends GuiButton implements ITipComponent {

	private final static ResourceLocation TEXTURE = new ResourceLocation(FXCraft.MODID + ":textures/gui/container/numbers.png");

	public List<T> rowList = Lists.newArrayList();
	protected T titleRow;
	protected int row;

	public GuiTable(int buttonID, int startX, int startY, T titleRow, int row) {
		super(buttonID, startX, startY, titleRow.sizeX, titleRow.sizeY * row, "");
		this.titleRow = titleRow;
		this.row = row;
		rowList.add(titleRow);
	}

	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		if(visible){
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			FontRenderer fontrenderer = minecraft.fontRenderer;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			field_146123_n = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			int k = getHoverState(field_146123_n);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);

			for (int i = 0; i < row; i++){
				drawRect(xPosition, yPosition + titleRow.sizeY * i, xPosition + width, yPosition + titleRow.sizeY * (i + 1), i % 2 == 0 ? 0x60000000 : 0x30000000);
			}
			if(k == 2){
				int row = (int) ((mouseY - yPosition - 1) / (float) titleRow.sizeY);
				int column = 0;
				int x = mouseX - xPosition;
				for (int index = 0; index < titleRow.rowPosition.length; index++){
					if(titleRow.rowPosition[index] > x){
						column = index - 1;
						break;
					}
				}
				column = column < 0 ? 0 : column;
				drawRect(xPosition, yPosition + row * titleRow.sizeY, xPosition + width, yPosition + (row + 1) * titleRow.sizeY, 0x60000000);
				drawRect(xPosition + titleRow.rowPosition[column], yPosition + row * titleRow.sizeY, xPosition + titleRow.rowPosition[column + 1], yPosition + (row + 1) * titleRow.sizeY, 0x607fff00);
			}
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			mouseDragged(minecraft, mouseX, mouseY);

			for (int row = 0; row < rowList.size(); row++){
				T t = rowList.get(row);
				for (int i = 0; i < titleRow.fieldCount; i++){
					RenderingHelper.drawMiniString(t.getContent(i), xPosition + titleRow.rowPosition[i] + 1, yPosition + row * titleRow.sizeY + 1, 0xFFFFFFFF);
				}
			}

			GL11.glPopAttrib();
		}
	}

	@Override
	public List<String> getTipList(int mouseX, int mouseY, boolean shift, boolean ctrl) {
		int row = (int) ((mouseY - yPosition - 1) / (float) titleRow.sizeY);
		int column = 0;
		int x = mouseX - xPosition;
		for (int index = 0; index < titleRow.fieldCount; index++){
			if(titleRow.rowPosition[index] > x){
				column = index - 1;
				break;
			}
		}
		column = column < 0 ? 0 : column;
		if(row < rowList.size()){
			return rowList.get(row).getTips(column);
		}else{
			return Lists.newArrayList();
		}
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
	public void func_146113_a(SoundHandler soundHandler) {}

}
