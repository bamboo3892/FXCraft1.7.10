package com.okina.fxcraft.client.gui.account_manager;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.okina.fxcraft.account.AccountInfo;
import com.okina.fxcraft.account.Reward;
import com.okina.fxcraft.account.RewardRegister;
import com.okina.fxcraft.client.gui.GuiTab;
import com.okina.fxcraft.utils.RenderingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;

public class RewardTab extends GuiTab<AccountManagerGui> {

	private List<GuiButton> list = Lists.newArrayList();
	private GuiRewardLabel selectedReward = null;
	private long lastAccountUpdate;

	public RewardTab(AccountManagerGui gui, int startX, int startY) {
		super(gui, startX, startY, 6, 0, Lists.newArrayList("Reward"));
		updateAccount(gui.tile.getAccountInfo());
		lastAccountUpdate = System.currentTimeMillis();
	}

	@Override
	public void actionPerformed(GuiButton guiButton) {
		int id = guiButton.id;
		if(id == 1){
			if(selectedReward == guiButton){
				gui.tile.tryGetReward(gui.player, selectedReward.reward.getName());
				selectedReward = null;
			}else if(guiButton instanceof GuiRewardLabel){
				selectedReward = (GuiRewardLabel) guiButton;
			}
		}
	}

	@Override
	public void drawComponent(Minecraft minecraft, int mouseX, int mouseY) {
		AccountInfo info = gui.tile.getAccountInfo();
		int i = (gui.width - gui.getSizeX()) / 2 - 12;
		int j = (gui.height - gui.getSizeY()) / 2 + 37;
		if(gui.tile.hasAccountUpdate(lastAccountUpdate)){
			updateAccount(info);
			lastAccountUpdate = System.currentTimeMillis();
		}

		FontRenderer fontRenderer = minecraft.fontRenderer;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(true);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);

		drawRect(i, j, i + 200, j + 8, 0x557fff00);
		drawRect(i, j + 44, i + 200, j + 52, 0x557fff00);

		for (int k = 0; k < 6; k++){
			drawRect(i + k * 36, j + 8, i + k * 36 + 18, j + 26, 0x33000000);
			if(k != 5){
				drawRect(i + k * 36 + 18, j + 26, i + k * 36 + 36, j + 44, 0x33000000);
			}else{
				drawRect(i + k * 36 + 18, j + 26, i + k * 36 + 20, j + 44, 0x33000000);
			}
		}

		RenderingHelper.drawMiniString("YOU CAN GET", i + 1, j + 1, 0x7fff00);
		RenderingHelper.drawMiniString("NEXT", i + 1, j + 45, 0x7fff00);

		if(selectedReward != null){
			drawRect(selectedReward.xPosition - 1, selectedReward.yPosition - 1, selectedReward.xPosition + 17, selectedReward.yPosition + 17, 0x337fff00);
		}

		GL11.glPopAttrib();
	}

	private void updateAccount(AccountInfo account) {
		list.clear();
		selectedReward = null;
		if(account != null){
			int i = (gui.width - gui.getSizeX()) / 2 - 12;
			int j = (gui.height - gui.getSizeY()) / 2 + 37;
			List<Reward> available = RewardRegister.instance.getAvailableRewards(account);
			for (int index = 0; index < available.size(); index++){
				if(index >= 22){
					break;
				}else if(index >= 11){
					list.add(new GuiRewardLabel(1, i + 18 * index - 197, j + 27, available.get(index), true, gui.getItemRenderer()));
				}else{
					list.add(new GuiRewardLabel(1, i + 18 * index + 1, j + 9, available.get(index), true, gui.getItemRenderer()));
				}
			}
			List<Reward> aimable = RewardRegister.instance.getNextStepRewards(account);
			for (int index = 0; index < aimable.size(); index++){
				if(index >= 10){
					break;
				}else{
					list.add(new GuiRewardLabel(2, i + 18 * index + 1, j + 52, aimable.get(index), false, gui.getItemRenderer()));
				}
			}
		}
		gui.updateButton();
	}

	@Override
	public List<GuiButton> getButtonList() {
		return list;
	}

}
