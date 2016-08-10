package com.okina.fxcraft.client.gui.account_manager;

import java.util.List;

import com.google.common.collect.Lists;
import com.okina.fxcraft.client.gui.GuiTab;

import net.minecraft.client.gui.GuiButton;

public class LimitReleaseTab extends GuiTab<AccountManagerGui> {

	public LimitReleaseTab(AccountManagerGui gui, int startX, int startY) {
		super(gui, startX, startY, 4, 0, Lists.newArrayList("Limit Release", "Now Disabled"));
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
