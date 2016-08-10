package com.okina.fxcraft.client.gui;

import net.minecraft.client.Minecraft;

public class GuiFlatSelectButton extends GuiFlatToggleButton {

	public GuiFlatSelectButton(int buttonID, int startX, int startY, int sizeX, int sizeY, String display, float[] selectedColor) {
		super(buttonID, startX, startY, sizeX, sizeY, display, selectedColor);
	}

	@Override
	public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
		if(enabled && visible && mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height){
			isClicked = true;
			selected = true;
			return true;
		}
		return false;
	}

}
