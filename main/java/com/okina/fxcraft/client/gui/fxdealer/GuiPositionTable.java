package com.okina.fxcraft.client.gui.fxdealer;

import org.lwjgl.opengl.GL11;

import com.okina.fxcraft.account.FXPosition;
import com.okina.fxcraft.client.gui.GuiTable;
import com.okina.fxcraft.utils.RenderingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;

public class GuiPositionTable extends GuiTable<GuiPositionTableRow> {

	private int selectedRow = 0;
	private boolean click = false;
	private boolean focused = false;

	public GuiPositionTable(int buttonID, int startX, int startY, GuiPositionTableRow titleRow, int row) {
		super(buttonID, startX, startY, titleRow, row);
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
				if(click){
					drawRect(xPosition + titleRow.rowPosition[column] + 1, yPosition + row * titleRow.sizeY + 1, xPosition + titleRow.rowPosition[column + 1] - 1, yPosition + (row + 1) * titleRow.sizeY - 1, 0x607fff00);
				}else{
					drawRect(xPosition + titleRow.rowPosition[column], yPosition + row * titleRow.sizeY, xPosition + titleRow.rowPosition[column + 1], yPosition + (row + 1) * titleRow.sizeY, 0x607fff00);
				}
			}
			if(focused){
				drawRect(xPosition, yPosition + selectedRow * titleRow.sizeY, xPosition + width, yPosition + (selectedRow + 1) * titleRow.sizeY, 0x807fff00);
			}

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			mouseDragged(minecraft, mouseX, mouseY);

			for (int row = 0; row < rowList.size(); row++){
				GuiPositionTableRow t = rowList.get(row);
				for (int i = 0; i < titleRow.fieldCount; i++){
					RenderingHelper.drawMiniString(t.getContent(i), xPosition + titleRow.rowPosition[i] + 1, yPosition + row * titleRow.sizeY + 1, 0xFFFFFFFF);
				}
			}

			GL11.glPopAttrib();
		}
	}

	public FXPosition getSelectedPosition() {
		if(selectedRow < 0 || selectedRow >= rowList.size()){
			return rowList.get(0).position;
		}else{
			return rowList.get(selectedRow).position;
		}
	}

	public void setForcused(boolean focused) {
		this.focused = focused;
	}

	@Override
	public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
		if(enabled && visible && mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height){
			click = true;
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
			selectedRow = row;
			return true;
		}
		return false;
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		click = false;
	}

}
