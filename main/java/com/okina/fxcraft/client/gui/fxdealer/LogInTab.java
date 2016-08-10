package com.okina.fxcraft.client.gui.fxdealer;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.okina.fxcraft.account.AccountInfo;
import com.okina.fxcraft.client.gui.GuiFlatButton;
import com.okina.fxcraft.client.gui.GuiTab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.OpenGlHelper;

public class LogInTab extends GuiTab<FXDealerGui> {

	private List<GuiButton> list;
	private GuiTextField nameField;
	private GuiTextField passwordField;

	public LogInTab(FXDealerGui gui, int startX, int startY) {
		super(gui, startX, startY, 1, 0, Lists.newArrayList("LogIn"));
		int i = (gui.width - gui.getSizeX()) / 2;
		int j = (gui.height - gui.getSizeY()) / 2;
		list = Lists.newArrayList();
		list.add(new GuiFlatButton(1, i + 300, j + 42, 50, 14, "LogIn"));
		list.add(new GuiFlatButton(2, i + 25, j + 40, 50, 14, "LogOut"));
		nameField = new GuiTextField(gui.getFontRenderer(), i + 203, j + 27, 91, 12);
		nameField.setTextColor(-1);
		nameField.setDisabledTextColour(-1);
		nameField.setEnableBackgroundDrawing(true);
		nameField.setMaxStringLength(10);
		passwordField = new GuiTextField(gui.getFontRenderer(), i + 203, j + 43, 91, 12);
		passwordField.setTextColor(-1);
		passwordField.setDisabledTextColour(-1);
		passwordField.setEnableBackgroundDrawing(true);
		passwordField.setMaxStringLength(10);
	}

	@Override
	public void drawComponent(Minecraft minecraft, int mouseX, int mouseY) {
		FontRenderer fontRenderer = minecraft.fontRenderer;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(true);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
		int left = (gui.width - gui.getSizeX()) / 2;
		int right = (gui.width + gui.getSizeX()) / 2;
		int up = (gui.height - gui.getSizeY()) / 2;
		int down = (gui.height + gui.getSizeY()) / 2;
		AccountInfo login = gui.tile.getLogInAccount();
		fontRenderer.drawString("LogIn : " + (login == null ? "No Account" : login.name), left + 4, up + 27, 0xffffff, false);
		fontRenderer.drawString("Account Name", left + 126, up + 27, 0xffffff, false);
		fontRenderer.drawString("Password", left + 126, up + 43, 0xffffff, false);
		nameField.drawTextBox();
		passwordField.drawTextBox();

		drawRect(left + 4, up + 59, right - 4, up + 61, 0x33000000);

		//account
		fontRenderer.drawString("Total Deal", left + 6, up + 65, 0xffd700, false);
		fontRenderer.drawString("Total Limits Trade", left + 6, up + 82, 0xffd700, false);
		fontRenderer.drawString("Total Gain", left + 6, up + 99, 0xffd700, false);
		fontRenderer.drawString("Total Loss", left + 6, up + 116, 0xffd700, false);
		fontRenderer.drawString("Account Balance", left + 6, up + 133, 0x00ffff, false);
		fontRenderer.drawString("Positions Total Value", left + 6, up + 150, 0x00ffff, false);
		fontRenderer.drawString("Your Total Assets", left + 6, up + 167, 0x00ffff, false);
		//		fontRenderer.drawString("Balance", left + 6, up + 184, 0x00ffff, false);
		fontRenderer.drawString("Account Total Achievement", left + 6, up + 201, 0x7fff00, false);
		fontRenderer.drawString("Account Rating", left + 6, up + 218, 0x7fff00, false);
		if(login != null){
			String str;
			str = String.valueOf(login.totalDeal);
			fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), up + 65, 0xffd700, false);

			str = String.valueOf(login.totalLimitsDeal);
			fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), up + 82, 0xffd700, false);

			str = String.valueOf(login.totalGain);
			fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), up + 99, 0xffd700, false);

			str = String.valueOf(login.totalLoss);
			fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), up + 116, 0xffd700, false);

			str = String.valueOf(login.balance);
			fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), up + 133, 0x00ffff, false);

			str = String.valueOf(0);
			fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), up + 150, 0x00ffff, false);

			str = String.valueOf(0);
			fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), up + 167, 0x00ffff, false);

			//			str = String.valueOf(0);
			//			fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), up + 184, 0x00ffff, false);

			str = "10.2";
			fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), up + 201, 0x7fff00, false);

			str = "F";
			fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), up + 218, 0xffff00, false);

		}

		drawRect(left + 4, up + 76, right - 4, up + 78, 0x33000000);
		drawRect(left + 4, up + 93, right - 4, up + 95, 0x33000000);
		drawRect(left + 4, up + 110, right - 4, up + 112, 0x33000000);
		drawRect(left + 4, up + 127, right - 4, up + 129, 0x33000000);
		drawRect(left + 4, up + 144, right - 4, up + 146, 0x33000000);
		drawRect(left + 4, up + 161, right - 4, up + 163, 0x33000000);
		drawRect(left + 4, up + 178, right - 4, up + 180, 0x33000000);
		drawRect(left + 4, up + 195, right - 4, up + 197, 0x33000000);
		drawRect(left + 4, up + 212, right - 4, up + 214, 0x33000000);
		drawRect(left + 4, up + 229, right - 4, up + 231, 0x33000000);

		GL11.glPopAttrib();
	}

	@Override
	public void actionPerformed(GuiButton guiButton) {
		int id = guiButton.id;
		if(id == 1){
			gui.tile.tryLogIn(nameField.getText() == null ? "" : nameField.getText(), passwordField.getText() == null ? "" : passwordField.getText());
		}else if(id == 2){
			gui.tile.logOut();
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouse) {
		super.mouseClicked(mouseX, mouseY, mouse);
		nameField.mouseClicked(mouseX, mouseY, mouse);
		passwordField.mouseClicked(mouseX, mouseY, mouse);
	}

	@Override
	public boolean keyTyped(char keyChar, int key) {
		if(!nameField.textboxKeyTyped(keyChar, key)){
			if(!passwordField.textboxKeyTyped(keyChar, key)){
				return super.keyTyped(keyChar, key);
			}
		}
		return true;
	}

	@Override
	public List<GuiButton> getButtonList() {
		return list;
	}

}
