package com.okina.fxcraft.client.gui.account_manager;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.okina.fxcraft.client.gui.GuiTab;
import com.okina.fxcraft.client.gui.GuiTabbedPane;
import com.okina.fxcraft.client.gui.ITipComponent;
import com.okina.fxcraft.main.FXCraft;
import com.okina.fxcraft.tileentity.AccountManegerTileEntity;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class AccountManagerGui extends GuiTabbedPane {

	private final static ResourceLocation TEXTURE_INVENTORY = new ResourceLocation(FXCraft.MODID + ":textures/gui/container/account_manager2.png");
	private static final int TEXTURE_X_SIZE1 = 176;
	private static final int TEXTURE_Y_SIZE1 = 90;
	private final static ResourceLocation TEXTURE_MAIN = new ResourceLocation(FXCraft.MODID + ":textures/gui/container/account_manager3.png");
	private static final int TEXTURE_X_SIZE2 = 200;
	private static final int TEXTURE_Y_SIZE2 = 90;
	private List<GuiTab> tabList = Lists.newArrayList();
	protected AccountManegerTileEntity tile;

	public AccountManagerGui(EntityPlayer player, AccountManegerTileEntity tile) {
		super(new AccountManagerContainer(player));
		this.tile = tile;
		xSize = 176;
		ySize = 200;
	}

	@Override
	public void initGui() {
		tabList = Lists.newArrayList();
		tabList.add(new DisposingTab(this, (width - TEXTURE_X_SIZE2) / 2, (height - TEXTURE_Y_SIZE2) / 2 - 40));
		tabList.add(new RealizationTab(this, (width - TEXTURE_X_SIZE2) / 2 + 24, (height - TEXTURE_Y_SIZE2) / 2 - 40));
		tabList.add(new LimitReleaseTab(this, (width - TEXTURE_X_SIZE2) / 2 + 48, (height - TEXTURE_Y_SIZE2) / 2 - 40));
		tabList.add(new RewardTab(this, (width - TEXTURE_X_SIZE2) / 2 + 72, (height - TEXTURE_Y_SIZE2) / 2 - 40));
		tabList.add(new StaticsticsTab(this, (width - TEXTURE_X_SIZE2) / 2 + 96, (height - TEXTURE_Y_SIZE2) / 2 - 40));
		tabList.add(new LoginTab(this, (width + TEXTURE_X_SIZE2) / 2 - 48, (height - TEXTURE_Y_SIZE2) / 2 - 40));
		tabList.add(new MakeAccountTab(this, (width + TEXTURE_X_SIZE2) / 2 - 24, (height - TEXTURE_Y_SIZE2) / 2 - 40));
		super.initGui();
		int tab = tile.lastOpenedTab;
		if(tab < 0 || tab >= tabList.size()){
			tab = 0;
		}
		changeTab(tab);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {}

	@Override
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
		super.drawScreen(mouseX, mouseY, p_73863_3_);
		List<String> list = Lists.newArrayList();
		for (Object object : buttonList){
			if(object instanceof ITipComponent && object instanceof GuiButton){
				GuiButton button = (GuiButton) object;
				if(button.visible && mouseX >= button.xPosition && mouseY >= button.yPosition && mouseX < button.xPosition + button.width && mouseY < button.yPosition + button.height){
					list.addAll(((ITipComponent) object).getTipList(mouseX, mouseY, false, false));
				}
			}
		}
		if(!list.isEmpty()){
			drawHoveringText(list, mouseX, mouseY, fontRendererObj);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialRenderTick, int mouseX, int mouseY) {
		mc.getTextureManager().bindTexture(TEXTURE_INVENTORY);
		int x1 = (width - TEXTURE_X_SIZE1) / 2;
		int y1 = (height - TEXTURE_Y_SIZE1) / 2;
		drawTexturedModalRect(x1, y1 + 58, 0, 0, TEXTURE_X_SIZE1, TEXTURE_Y_SIZE1);

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(true);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		mc.getTextureManager().bindTexture(TEXTURE_MAIN);
		int x2 = (width - TEXTURE_X_SIZE2) / 2;
		int y2 = (height - TEXTURE_Y_SIZE2) / 2;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
		drawTexturedModalRect(x2, y2 - 18, 0, 0, TEXTURE_X_SIZE2, TEXTURE_Y_SIZE2);
		GL11.glPopAttrib();
	}

	@Override
	public void drawWorldBackground(int hoge) {
		if(mc.theWorld != null){
			drawGradientRect(0, 0, width, height, 0x60101010, 0xb0101010);
		}else{
			drawBackground(hoge);
		}
	}

	@Override
	protected void actionPerformed(GuiButton guiButton) {
		super.actionPerformed(guiButton);
		tile.lastOpenedTab = tabList.indexOf(selectedTab);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		tile.lastOpenedTab = tabList.indexOf(selectedTab);
	}

	@Override
	protected List<GuiTab> getTabInstanceList() {
		return tabList;
	}

}
