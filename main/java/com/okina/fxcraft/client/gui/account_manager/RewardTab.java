package com.okina.fxcraft.client.gui.account_manager;

import java.util.List;

import com.google.common.collect.Lists;
import com.okina.fxcraft.client.gui.GuiTab;

import net.minecraft.client.gui.GuiButton;

public class RewardTab extends GuiTab<AccountManagerGui> {

	public RewardTab(AccountManagerGui gui, int startX, int startY) {
		super(gui, startX, startY, 6, 0, Lists.newArrayList("Reward", "Now Disabled"));
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
