package com.okina.fxcraft.client.gui.fxdealer;

import java.util.List;

import com.google.common.collect.Lists;
import com.okina.fxcraft.client.gui.GuiTab;
import com.okina.fxcraft.client.gui.GuiTabbedPane;

import net.minecraft.client.gui.GuiButton;

public class HistoryTab extends GuiTab {

	public HistoryTab(GuiTabbedPane gui, int startX, int startY) {
		super(gui, startX, startY, 3, 1, Lists.newArrayList("History", "Now Disabled"));
		enabled = false;
	}

	@Override
	public void actionPerformed(GuiButton guiButton) {

	}

	@Override
	public List<GuiButton> getButtonList() {
		return Lists.newArrayList();
	}

}
