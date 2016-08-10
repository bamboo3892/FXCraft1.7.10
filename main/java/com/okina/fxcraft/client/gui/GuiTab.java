package com.okina.fxcraft.client.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.okina.fxcraft.main.FXCraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

public abstract class GuiTab<T extends GuiTabbedPane> extends GuiButton implements ITipComponent {

	private final static ResourceLocation TEXTURE = new ResourceLocation(FXCraft.MODID + ":textures/gui/container/tabs.png");

	public boolean selected = false;
	protected T gui;
	private List<String> tips;
	private int textureIndexX = 0;
	private int textureIndexY = 0;

	public GuiTab(T gui, int startX, int startY, int textureIndexX, int textureIndexY, List<String> tips) {
		super(999, startX, startY, 24, 22, "");
		this.gui = gui;
		this.tips = tips;
		this.textureIndexX = textureIndexX;
		this.textureIndexY = textureIndexY;
	}

	@Override
	public final void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		if(visible){
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			minecraft.getTextureManager().bindTexture(TEXTURE);
			field_146123_n = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDepthMask(true);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
			drawTexturedModalRect(xPosition, yPosition, textureIndexX * 24, textureIndexY * 22, width, height);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

			if(!selected){
				drawRect(xPosition, yPosition, xPosition + width, yPosition + height, 0x33000000);
				if(getHoverState(field_146123_n) == 2){
					drawRect(xPosition, yPosition, xPosition + width, yPosition + height, 0x33000000);
				}
			}

			if(selected){
				drawComponent(minecraft, mouseX, mouseY);
			}

			mouseDragged(minecraft, mouseX, mouseY);

			GL11.glPopAttrib();
		}
	}

	/**Called if this tab is selected and any button (except GuiTab) is pressed.
	 * @param guiButton
	 */
	public abstract void actionPerformed(GuiButton guiButton);

	/**Use this to detect click for not button component(example GuiTextField ).
	 * @param mouseX
	 * @param mouseY
	 * @param mouse
	 */
	public void mouseClicked(int mouseX, int mouseY, int mouse) {}

	public void handleMouseInput() {}

	public boolean keyTyped(char keyChar, int key) {
		return false;
	}

	/**BUtton ID 999 is not available.<br>
	 * If you want to use button ID, you should avoid 999.
	 * @return
	 */
	public abstract List<GuiButton> getButtonList();

	/**Called if this tab is visible and selected */
	public void drawComponent(Minecraft minecraft, int mouseX, int mouseY) {

	}

	/**sound
	 */
	@Override
	public void func_146113_a(SoundHandler p_146113_1_) {
		//		p_146113_1_.playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
	}

	@Override
	public List<String> getTipList(int mouseX, int mouseY, boolean shift, boolean ctrl) {
		return tips;
	}

}
