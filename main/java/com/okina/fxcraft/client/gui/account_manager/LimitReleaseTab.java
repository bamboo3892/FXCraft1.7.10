package com.okina.fxcraft.client.gui.account_manager;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.okina.fxcraft.account.AccountInfo;
import com.okina.fxcraft.account.FXDealLimit;
import com.okina.fxcraft.client.gui.GuiFlatButton;
import com.okina.fxcraft.client.gui.GuiIconLabel;
import com.okina.fxcraft.client.gui.GuiTab;
import com.okina.fxcraft.main.FXCraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LimitReleaseTab extends GuiTab<AccountManagerGui> {

	private final static ResourceLocation TEXTURE = new ResourceLocation(FXCraft.MODID + ":textures/gui/container/icon_labels.png");

	private List<GuiButton> list = Lists.newArrayList();
	private GuiIconLabel labelLimitLimits;
	private GuiFlatButton buttonLimitLot;
	private GuiFlatButton buttonLimitLeverage;
	private GuiFlatButton buttonLimitPosition;
	private GuiFlatButton buttonLimitLimits;
	private long lastAccountUpdate;

	public LimitReleaseTab(AccountManagerGui gui, int startX, int startY) {
		super(gui, startX, startY, 4, 0, Lists.newArrayList("Limit Release"));
		int i = (gui.width - gui.getSizeX()) / 2;
		int j = (gui.height - gui.getSizeY()) / 2;
		buttonLimitLot = new GuiFlatButton(1, i - 5, j + 84, 40, 14, "Done", new float[] { 0.5f, 1f, 0f });
		buttonLimitLeverage = new GuiFlatButton(2, i + 44, j + 84, 40, 14, "Done", new float[] { 0.5f, 1f, 0f });
		buttonLimitPosition = new GuiFlatButton(3, i + 93, j + 84, 40, 14, "Done", new float[] { 0.5f, 1f, 0f });
		buttonLimitLimits = new GuiFlatButton(4, i + 141, j + 84, 40, 14, "Done", new float[] { 0.5f, 1f, 0f });
		labelLimitLimits = new GuiIconLabel(i + 153, j + 59, new ItemStack(FXCraft.limit_limits_trade), gui.getItemRenderer());
		updateAccount(gui.tile.getAccountInfo());
		lastAccountUpdate = System.currentTimeMillis();
	}

	@Override
	public void drawComponent(Minecraft minecraft, int mouseX, int mouseY) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		FontRenderer fontRenderer = minecraft.fontRenderer;
		minecraft.getTextureManager().bindTexture(TEXTURE);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		AccountInfo info = gui.tile.getAccountInfo();
		if(gui.tile.hasAccountUpdate(lastAccountUpdate)){
			updateAccount(info);
			lastAccountUpdate = System.currentTimeMillis();
		}
		int i = (gui.width - gui.getSizeX()) / 2;
		int j = (gui.height - gui.getSizeY()) / 2;
		String str = "Now " + (info == null ? "" : "Lv." + info.dealLotLimit);
		fontRenderer.drawString(str, i - 4, j + 42, 0xffffff, false);
		str = "Now " + (info == null ? "" : "Lv." + info.leverageLimit);
		fontRenderer.drawString(str, i + 45, j + 42, 0xffffff, false);
		str = "Now " + (info == null ? "" : "Lv." + info.positionLimit);
		fontRenderer.drawString(str, i + 94, j + 42, 0xffffff, false);
		str = "Now " + (info == null ? "" : (info.limitsTradePermission ? "o" : "x"));
		fontRenderer.drawString(str, i + 146, j + 42, 0xffffff, false);

		GL11.glPopAttrib();
	}

	@Override
	public void actionPerformed(GuiButton guiButton) {
		int id = guiButton.id;
		if(id == 1){
			gui.tile.tryLimitRelease(gui.player, FXDealLimit.LOT);
		}else if(id == 2){
			gui.tile.tryLimitRelease(gui.player, FXDealLimit.LEVERAGE);
		}else if(id == 3){
			gui.tile.tryLimitRelease(gui.player, FXDealLimit.POSITION);
		}else if(id == 4){
			gui.tile.tryLimitRelease(gui.player, FXDealLimit.LIMITS_TRADE);
		}
	}

	private void updateAccount(AccountInfo login) {
		int i = (gui.width - gui.getSizeX()) / 2;
		int j = (gui.height - gui.getSizeY()) / 2;
		list.clear();
		list.add(buttonLimitLot);
		list.add(buttonLimitLeverage);
		list.add(buttonLimitPosition);
		list.add(buttonLimitLimits);
		if(login != null){
			if(login.dealLotLimit >= 0 && login.dealLotLimit < 5){
				list.add(new GuiIconLabel(i + 7, j + 59, new ItemStack(FXCraft.limit_dealLot[login.dealLotLimit]), gui.getItemRenderer()));
				buttonLimitLot.enabled = true;
			}else{
				buttonLimitLot.enabled = false;
			}
			if(login.leverageLimit >= 0 && login.leverageLimit < 5){
				list.add(new GuiIconLabel(i + 56, j + 59, new ItemStack(FXCraft.limit_leverage[login.leverageLimit]), gui.getItemRenderer()));
				buttonLimitLeverage.enabled = true;
			}else{
				buttonLimitLeverage.enabled = false;
			}
			if(login.positionLimit >= 0 && login.positionLimit < 5){
				list.add(new GuiIconLabel(i + 105, j + 59, new ItemStack(FXCraft.limit_position[login.positionLimit]), gui.getItemRenderer()));
				buttonLimitPosition.enabled = true;
			}else{
				buttonLimitPosition.enabled = false;
			}
			if(!login.limitsTradePermission){
				list.add(labelLimitLimits);
				buttonLimitLimits.enabled = true;
			}else{
				buttonLimitLimits.enabled = false;
			}
		}else{
			list.add(new GuiIconLabel(i + 7, j + 59, new ItemStack(FXCraft.limit_dealLot[0]), gui.getItemRenderer()));
			list.add(new GuiIconLabel(i + 56, j + 59, new ItemStack(FXCraft.limit_leverage[0]), gui.getItemRenderer()));
			list.add(new GuiIconLabel(i + 105, j + 59, new ItemStack(FXCraft.limit_position[0]), gui.getItemRenderer()));
			list.add(labelLimitLimits);
			buttonLimitLot.enabled = false;
			buttonLimitLeverage.enabled = false;
			buttonLimitPosition.enabled = false;
			buttonLimitLimits.enabled = false;
		}
		gui.updateButton();
	}

	@Override
	public List<GuiButton> getButtonList() {
		return list;
	}

}
