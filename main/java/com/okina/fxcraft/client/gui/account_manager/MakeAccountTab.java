package com.okina.fxcraft.client.gui.account_manager;

import java.util.List;

import com.google.common.collect.Lists;
import com.okina.fxcraft.account.AccountHandler;
import com.okina.fxcraft.account.AccountInfo;
import com.okina.fxcraft.client.gui.GuiFlatButton;
import com.okina.fxcraft.client.gui.GuiIconLabel;
import com.okina.fxcraft.client.gui.GuiTab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;

public class MakeAccountTab extends GuiTab<AccountManagerGui> {

	private List<GuiButton> list = Lists.newArrayList();
	private GuiIconLabel checkLabel;
	private GuiTextField nameField;
	private GuiTextField passwordField;

	public MakeAccountTab(AccountManagerGui gui, int startX, int startY) {
		super(gui, startX, startY, 0, 0, Lists.newArrayList("Make Account"));
		int i = (gui.width - gui.getSizeX()) / 2;
		int j = (gui.height - gui.getSizeY()) / 2;
		list.add(new GuiFlatButton(1, i + 50, j + 88, 80, 14, "Make Account", new float[] { 0.5f, 1f, 0f }));
		list.add(checkLabel = new GuiIconLabel(i + 172, j + 54, 16, 16, 0, 0, Lists.newArrayList("Blank Name")));
		list.add(new GuiFlatButton(2, i + 135, j + 40, 50, 14, "LogOut", new float[] { 0.5f, 1f, 0f }));
		checkLabel.visible = false;
		nameField = new GuiTextField(gui.getFontRenderer(), i + 80, j + 57, 91, 12);
		nameField.setTextColor(-1);
		nameField.setDisabledTextColour(-1);
		nameField.setEnableBackgroundDrawing(true);
		nameField.setMaxStringLength(10);
		passwordField = new GuiTextField(gui.getFontRenderer(), i + 80, j + 73, 91, 12);
		passwordField.setTextColor(-1);
		passwordField.setDisabledTextColour(-1);
		passwordField.setEnableBackgroundDrawing(true);
		passwordField.setMaxStringLength(10);
	}

	@Override
	public void drawComponent(Minecraft minecraft, int mouseX, int mouseY) {

		if(!AccountHandler.instance.checkIsValidAccountName(nameField.getText())){
			checkLabel.visible = true;
			checkLabel.setTips(Lists.newArrayList("Invalid Name"));
		}else{
			String checked = gui.tile.checkedAccountName;
			if(checked != null && checked.equals(nameField.getText()) && !gui.tile.accountCheck){
				checkLabel.visible = true;
				checkLabel.setTips(Lists.newArrayList("Not Available Name"));
			}else{
				checkLabel.visible = false;
			}
		}

		int i = (gui.width - gui.getSizeX()) / 2;
		int j = (gui.height - gui.getSizeY()) / 2;
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		AccountInfo login = gui.tile.getAccountInfo();
		fontRenderer.drawString("LogIn : " + (login == null ? "No Account" : login.name), i, j + 43, 0xffffff, false);
		fontRenderer.drawString("Account Name", i, j + 59, 0xffffff, false);
		fontRenderer.drawString("Password", i, j + 75, 0xffffff, false);
		nameField.drawTextBox();
		passwordField.drawTextBox();
	}

	@Override
	public void actionPerformed(GuiButton guiButton) {
		int id = guiButton.id;
		if(id == 1){
			gui.tile.tryMakeAccount(nameField.getText(), passwordField.getText());
			nameField.setText("");
			passwordField.setText("");
		}else if(id == 2){
			gui.tile.logOut();
			nameField.setText("");
			passwordField.setText("");
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
		String checking = gui.tile.checkingAccountName;
		if(checking == null || !checking.equals(nameField.getText())){
			gui.tile.checkAccount(nameField.getText());
		}
		return true;
	}

	@Override
	public List<GuiButton> getButtonList() {
		return list;
	}

}
