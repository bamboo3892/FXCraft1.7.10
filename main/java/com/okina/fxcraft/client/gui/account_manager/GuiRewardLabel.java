package com.okina.fxcraft.client.gui.account_manager;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.okina.fxcraft.account.Reward;
import com.okina.fxcraft.client.gui.GuiIconLabel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;

public class GuiRewardLabel extends GuiIconLabel {

	public Reward reward;
	public boolean dropShadow;

	public GuiRewardLabel(int id, int x, int y, Reward reward, boolean dropShadow, RenderItem renderItem) {
		super(x, y, reward.getItem(), renderItem);
		this.id = id;
		this.reward = reward;
		this.dropShadow = dropShadow;
		this.enabled = true;
	}

	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		if(visible && dropShadow){
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			FontRenderer fontrenderer = minecraft.fontRenderer;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			field_146123_n = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			if(field_146123_n){
				drawRect(xPosition, yPosition, xPosition + width, yPosition + height, 0x33000000);
			}

			mouseDragged(minecraft, mouseX, mouseY);

			GL11.glPopAttrib();
		}
		super.drawButton(minecraft, mouseX, mouseY);
	}

	@Override
	public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
		if(enabled && visible && mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height){
			return true;
		}
		return false;
	}

	@Override
	public List<String> getTipList(int mouseX, int mouseY, boolean shift, boolean ctrl) {
		List<String> toolTip = Lists.newArrayList("§b§o" + reward.getDisplayName(), "-" + reward.getConditionMessage() + "-");
		toolTip.addAll(super.getTipList(mouseX, mouseY, shift, ctrl));
		return toolTip;
	}

}
