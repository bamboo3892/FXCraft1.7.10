package com.okina.fxcraft.client.gui.account_manager;

import java.util.List;

import com.google.common.collect.Lists;
import com.okina.fxcraft.account.AccountInfo;
import com.okina.fxcraft.client.gui.GuiFlatButton;
import com.okina.fxcraft.client.gui.GuiIconLabel;
import com.okina.fxcraft.client.gui.GuiSlider;
import com.okina.fxcraft.client.gui.GuiTab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RealizationTab extends GuiTab<AccountManagerGui> {

	private List<GuiButton> list;
	private GuiSlider slider;

	public RealizationTab(AccountManagerGui gui, int startX, int startY) {
		super(gui, startX, startY, 3, 0, Lists.newArrayList("Realization"));
		int i = (gui.width - gui.getSizeX()) / 2;
		int j = (gui.height - gui.getSizeY()) / 2;
		list = Lists.newArrayList();
		list.add(new GuiFlatButton(1, i + 50, j + 88, 80, 14, "Realize"));
		list.add(slider = new GuiSlider(3, i + 107, j + 73, 50, 1, 64, 1, 1));
		list.add(new GuiIconLabel(i + 72, j + 62, 32, 16, 16, 0, Lists.newArrayList("1 Emerald = 10000")));
	}

	@Override
	public void actionPerformed(GuiButton guiButton) {

	}

	@Override
	public void drawComponent(Minecraft minecraft, int mouseX, int mouseY) {
		AccountInfo account = gui.tile.getLogInAccount();
		int i = (gui.width - gui.getSizeX()) / 2;
		int j = (gui.height - gui.getSizeY()) / 2;
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		if(account != null){
			fontRenderer.drawString("Your Balance : " + account.balance, i, j + 43, 0xffffff, false);
		}else{
			fontRenderer.drawString("Not Logged In", i, j + 43, 0xffffff, false);
		}
		fontRenderer.drawString(Integer.toString(slider.getValue() * 10000), i + 28, j + 67, 0xffffff, false);
		gui.getItemRenderer().renderItemAndEffectIntoGUI(fontRenderer, minecraft.getTextureManager(), new ItemStack(Items.emerald, 1), i + 123, j + 55);
	}

	@Override
	public List<GuiButton> getButtonList() {
		return list;
	}

}
