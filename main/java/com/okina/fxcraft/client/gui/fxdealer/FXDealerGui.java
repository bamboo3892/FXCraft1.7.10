package com.okina.fxcraft.client.gui.fxdealer;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.okina.fxcraft.client.gui.DummyContainer;
import com.okina.fxcraft.client.gui.GuiTab;
import com.okina.fxcraft.client.gui.GuiTabbedPane;
import com.okina.fxcraft.client.gui.ITipComponent;
import com.okina.fxcraft.main.FXCraft;
import com.okina.fxcraft.tileentity.FXDealerTileEntity;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class FXDealerGui extends GuiTabbedPane {

	private final static ResourceLocation TEXTURE = new ResourceLocation(FXCraft.MODID + ":textures/gui/container/fxdealer.png");

	private List<GuiTab> tabList = Lists.newArrayList();
	protected FXDealerTileEntity tile;

	public FXDealerGui(EntityPlayer player, FXDealerTileEntity tile) {
		super(new DummyContainer());
		this.tile = tile;
		xSize = 356;
		ySize = 240;
	}

	@Override
	public void initGui() {
		tabList = Lists.newArrayList();
		tabList.add(new ChartTab(this, (width - xSize) / 2, (height - ySize) / 2));
		tabList.add(new PositionTab(this, (width - xSize) / 2 + 24, (height - ySize) / 2));
		tabList.add(new HistoryTab(this, (width - xSize) / 2 + 48, (height - ySize) / 2));
		tabList.add(new LogInTab(this, (width + xSize) / 2 - 24, (height - ySize) / 2));
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
	public void drawScreen(int mouseX, int mouseY, float partialRenderTick) {
		super.drawScreen(mouseX, mouseY, partialRenderTick);
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
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(true);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		mc.getTextureManager().bindTexture(TEXTURE);
		int x2 = (width - xSize) / 2;
		int y2 = (height - ySize) / 2;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
		drawTexturedModalRect(x2, y2 + 22, 0, 0, xSize, ySize - 22);
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
