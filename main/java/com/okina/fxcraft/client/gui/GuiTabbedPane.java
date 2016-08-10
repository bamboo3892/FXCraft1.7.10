package com.okina.fxcraft.client.gui;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.inventory.Container;

public abstract class GuiTabbedPane extends GuiContainer {

	protected GuiTab selectedTab;

	public GuiTabbedPane(Container container) {
		super(container);
	}

	/**Every GuiTab's button ID must be 999.
	 * @return
	 */
	protected abstract List<GuiTab> getTabInstanceList();

	@Override
	public void initGui() {
		super.initGui();
		changeTab(0);
	}

	@Override
	protected void actionPerformed(GuiButton guiButton) {
		super.actionPerformed(guiButton);
		if(guiButton instanceof GuiTab && getTabInstanceList().contains(guiButton)){
			GuiTab tab = (GuiTab) guiButton;
			if(tab != selectedTab){
				changeTab(getTabInstanceList().indexOf(guiButton));
			}
		}else{
			if(selectedTab != null){
				selectedTab.actionPerformed(guiButton);
			}
		}
	}

	protected void changeTab(int index) {
		buttonList.clear();
		if(!getTabInstanceList().isEmpty()){
			for (GuiTab tab : getTabInstanceList()){
				buttonList.add(tab);
				tab.selected = false;
			}
			if(index >= 0 && index < getTabInstanceList().size()){
				GuiTab tab = getTabInstanceList().get(index);
				tab.selected = true;
				selectedTab = tab;
				buttonList.addAll(tab.getButtonList());
			}else{
				selectedTab = null;
			}
		}else{
			selectedTab = null;
		}
	}

	public void updateButton() {
		buttonList.clear();
		if(!getTabInstanceList().isEmpty()){
			for (GuiTab tab : getTabInstanceList()){
				buttonList.add(tab);
				tab.selected = false;
			}
			if(selectedTab != null){
				selectedTab.selected = true;
				buttonList.addAll(selectedTab.getButtonList());
			}else{
				selectedTab = null;
			}
		}else{
			selectedTab = null;
		}
	}

	public int getSizeX() {
		return xSize;
	}
	public int getSizeY() {
		return ySize;
	}
	public FontRenderer getFontRenderer() {
		return fontRendererObj;
	}
	public RenderItem getItemRenderer() {
		return itemRender;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouse) {
		super.mouseClicked(mouseX, mouseY, mouse);
		if(selectedTab != null){
			selectedTab.mouseClicked(mouseX, mouseY, mouse);
		}
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		if(selectedTab != null){
			selectedTab.handleMouseInput();
		}
	}

	@Override
	protected void keyTyped(char keyChar, int key) {
		if(selectedTab == null || !selectedTab.keyTyped(keyChar, key)){
			super.keyTyped(keyChar, key);
		}
	}

}
