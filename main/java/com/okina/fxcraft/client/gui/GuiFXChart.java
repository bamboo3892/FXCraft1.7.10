package com.okina.fxcraft.client.gui;

import java.util.Calendar;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.okina.fxcraft.main.FXCraft;
import com.okina.fxcraft.rate.FXRateGetHelper;
import com.okina.fxcraft.rate.RateData;
import com.okina.fxcraft.utils.RenderingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public class GuiFXChart extends GuiButton {

	private final static ResourceLocation TEXTURE = new ResourceLocation(FXCraft.MODID + ":textures/gui/container/numbers.png");

	private List<RateData> dataList = Lists.newArrayList();
	private long updateMills = 0L;
	private boolean isClicked = false;
	private String displayPair = "USDJPY";
	private int displayTerm = 0;
	private int displayDataMaxSize = 200;

	public GuiFXChart(int startX, int startY, int sizeX, int sizeY) {
		super(997, startX, startY, sizeX, sizeY, "");
	}

	public void setDisplayPair(String pair) {
		if(!displayPair.equals(pair)){
			displayPair = pair;
			updateMills = 0L;
		}
	}

	public String getDisplayPair() {
		return displayPair;
	}

	public void setDisplayTerm(int term) {
		if(displayTerm != term){
			displayTerm = term;
			updateMills = 0L;
		}
	}

	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		if(visible){
			double effWidth = width - 30;
			double effHeight = height - 10;
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			Tessellator tessellator = Tessellator.instance;
			FontRenderer fontrenderer = minecraft.fontRenderer;
			field_146123_n = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			int k = getHoverState(field_146123_n);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);

			//back
			drawRect(xPosition, yPosition, xPosition + width, yPosition + height, 0x60000000);

			//line mouse
			if(mouseX >= xPosition && mouseX < xPosition + width - 30 && mouseY >= yPosition && mouseY < yPosition + height - 10){
				drawRect(xPosition, mouseY, xPosition + width - 30, mouseY + 1, 0x30000000);
				drawRect(mouseX, yPosition, mouseX + 1, yPosition + height - 10, 0x30000000);
			}

			//bordar
			drawRect(xPosition, yPosition + height - 10, xPosition + width, yPosition + height, 0x60000000);
			drawRect(xPosition + width - 30, yPosition, xPosition + width, yPosition + height - 10, 0x60000000);

			//mesh
			double dx = effWidth / 8;
			for (int i = 1; i < 8; i++){
				drawRect((int) (xPosition + dx * i), yPosition, (int) (xPosition + dx * i + 1), (int) (yPosition + effHeight), 0x60000000);
			}

			//chart
			//			drawRect(xPosition, yPosition + 10, xPosition + width, yPosition + height, 0xFF00FFFF);
			//			drawLine(tessellator, xPosition, yPosition + 10, xPosition + width, yPosition + height);

			if(dataList == null || FXCraft.rateGetter.hasUpdate(updateMills)){
				List<RateData> list = FXCraft.rateGetter.getRateForChart(displayPair, displayTerm);
				dataList = list;
				updateMills = System.currentTimeMillis();
			}
			if(dataList != null && !dataList.isEmpty()){
				List<RateData> dataList = Lists.newLinkedList();
				for (int i = 0; i < displayDataMaxSize; i++){
					if(this.dataList.size() > i){
						dataList.add(this.dataList.get(i));
					}else{
						break;
					}
				}
				double maxRate = Integer.MIN_VALUE;
				double minRate = Integer.MAX_VALUE;
				for (RateData data : dataList){
					maxRate = Math.max(data.open, maxRate);
					minRate = Math.min(data.open, minRate);
				}
				double valueMargin = maxRate - minRate;
				Calendar latest = dataList.get(dataList.size() - 1).calendar;
				Calendar earliest = dataList.get(0).calendar;
				//				long earliestMills = earliest.getTimeInMillis();
				//				long termMargin = earliestMills - latest.getTimeInMillis();
				int dataSize = dataList.size();
				List<VertexCoord> coordList = Lists.newArrayList();
				for (int index = 0; index < dataSize; index++){
					RateData data = dataList.get(index);
					//					long margin = earliestMills - data.calendar.getTimeInMillis();
					//					double x = xPosition + effWidth - ((double) margin / termMargin) * effWidth;
					double x = xPosition + effWidth - ((double) index / dataSize) * effWidth;
					double y = yPosition + (maxRate - data.open) / valueMargin * effHeight;
					coordList.add(new VertexCoord(x, y, data));
				}
				drawLines(coordList, 0xFF7FFF00);

				RenderingHelper.drawMiniString(String.valueOf(maxRate), xPosition + width - 29, yPosition + 1, 0x60000000);
				RenderingHelper.drawMiniString(String.valueOf(minRate), xPosition + width - 29, yPosition + height - 18, 0x60000000);
				RenderingHelper.drawMiniString(String.valueOf(coordList.get(0).rate.open), xPosition + width - 29, (int) coordList.get(0).y - 3, 0x60000000);
				String time2 = FXRateGetHelper.getCalendarString(earliest, displayTerm);
				RenderingHelper.drawMiniString(time2, xPosition + width - 30 - time2.length() * 4, yPosition + height - 8, 0x60000000);
				RenderingHelper.drawMiniString(FXRateGetHelper.getCalendarString(latest, displayTerm), xPosition + 1, yPosition + height - 8, 0x60000000);

				//mouse coord
				if(mouseX >= xPosition && mouseX < xPosition + width - 30 && mouseY >= yPosition && mouseY < yPosition + height - 10){
					for (VertexCoord coord : coordList){
						if(mouseX > coord.x){
							drawRect((int) coord.x - 1, (int) coord.y - 1, (int) coord.x + 1, (int) coord.y + 1, 0xffd2691e);
							String str1 = String.valueOf(coord.rate.open);
							drawRect((int) coord.x + 3, (int) coord.y - 1, (int) coord.x + str1.length() * 4 + 5, (int) coord.y + 7, 0x60000000);
							RenderingHelper.drawMiniString(str1, (int) coord.x + 4, (int) coord.y, 0x60000000);
							String str2 = FXRateGetHelper.getCalendarString(coord.rate.calendar, displayTerm);
							drawRect((int) coord.x + 3, yPosition + height - 9, (int) coord.x + str2.length() * 4 + 4, yPosition + height, 0xa0000000);
							RenderingHelper.drawMiniString(str2, (int) coord.x + 4, yPosition + height - 8, 0x60000000);
							String str3 = String.format("%g", maxRate - (mouseY - yPosition) / effHeight * valueMargin);
							drawRect(xPosition + width - 29, mouseY - 1, xPosition + width - 29 + str3.length() * 4, mouseY + 7, 0xa0000000);
							RenderingHelper.drawMiniString(str3, xPosition + width - 29, mouseY, 0x60000000);
							break;
						}
					}
				}
			}else{
				Minecraft.getMinecraft().fontRenderer.drawString("This chart is not loaded", xPosition + (int) (width / 2f) - 76, yPosition + (int) (height / 2f) - 8, 0xFFFFFF, false);
			}

			mouseDragged(minecraft, mouseX, mouseY);

			int color;
			if(isClicked || k == 2){
				color = 0x808080;
			}else{
				color = 0xFFFFFF;
			}
			fontrenderer.drawString(displayString, xPosition + width / 2 - fontrenderer.getStringWidth(displayString) / 2, yPosition + (height - 8) / 2, color, false);

			GL11.glPopAttrib();
		}
	}

	@Override
	public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
		if(enabled && visible && mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height){
			isClicked = true;
			return true;
		}
		return false;
	}

	@Override
	protected void mouseDragged(Minecraft minecraft, int mouseX, int mouseY) {
		if(!(mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height)){
			isClicked = false;
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		isClicked = false;
	}

	public void handleMouseInput() {
		int i = Mouse.getEventDWheel();
		displayDataMaxSize -= i / 10f;
		if(displayDataMaxSize < 30){
			displayDataMaxSize = 30;
		}else if(displayDataMaxSize > 200){
			displayDataMaxSize = 200;
		}
	}

	public static void drawLines(List<VertexCoord> vertexList, int color) {
		Tessellator tessellator = Tessellator.instance;
		float f3 = (color >> 24 & 255) / 255.0F;
		float f = (color >> 16 & 255) / 255.0F;
		float f1 = (color >> 8 & 255) / 255.0F;
		float f2 = (color & 255) / 255.0F;
		Tessellator tessellator2 = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glColor4f(f, f1, f2, f3);
		tessellator.startDrawing(GL11.GL_LINE_STRIP);
		for (VertexCoord coords : vertexList){
			tessellator2.addVertex(coords.x, coords.y, 0.0D);
		}
		tessellator2.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}

	/**params: coords from xPosition, yPosition*/
	public static void drawLine(Tessellator tessellator, double startX, double startY, double endX, double endY) {
		double j1;

		//		if(startX < endX){
		//			j1 = startX;
		//			startX = endX;
		//			endX = j1;
		//		}
		//
		//		if(startY < endY){
		//			j1 = startY;
		//			startY = endY;
		//			endY = j1;
		//		}

		int color = 0xFF7fff00;

		float f3 = (color >> 24 & 255) / 255.0F;
		float f = (color >> 16 & 255) / 255.0F;
		float f1 = (color >> 8 & 255) / 255.0F;
		float f2 = (color & 255) / 255.0F;
		Tessellator tessellator2 = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glColor4f(f, f1, f2, f3);
		tessellator.startDrawing(GL11.GL_LINE_STRIP);
		//		tessellator2.addVertex((double) startX, (double) endY, 0.0D);
		//		tessellator2.addVertex((double) endX, (double) endY, 0.0D);
		//		tessellator2.addVertex((double) endX, (double) startY, 0.0D);
		//		tessellator2.addVertex((double) startX, (double) startY, 0.0D);
		tessellator2.addVertex(startX, startY, 0.0D);
		tessellator2.addVertex(endX, endY, 0.0D);
		tessellator2.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}

	//	private void drawMiniString(String str, int x, int y, int color) {
	//		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
	//		GL11.glEnable(GL11.GL_BLEND);
	//		GL11.glDisable(GL11.GL_LIGHTING);
	//		GL11.glDisable(GL11.GL_CULL_FACE);
	//		GL11.glDepthMask(true);
	//		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	//		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	//		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
	//		for (int i = 0; i < str.length(); i++){
	//			char c = str.charAt(i);
	//			switch (c) {
	//			case '0':
	//				drawTexturedModalRect(x + i * 4, y, 0, 0, 3, 6);
	//				break;
	//			case '1':
	//				drawTexturedModalRect(x + i * 4, y, 4, 0, 3, 6);
	//				break;
	//			case '2':
	//				drawTexturedModalRect(x + i * 4, y, 8, 0, 3, 6);
	//				break;
	//			case '3':
	//				drawTexturedModalRect(x + i * 4, y, 12, 0, 3, 6);
	//				break;
	//			case '4':
	//				drawTexturedModalRect(x + i * 4, y, 16, 0, 3, 6);
	//				break;
	//			case '5':
	//				drawTexturedModalRect(x + i * 4, y, 20, 0, 3, 6);
	//				break;
	//			case '6':
	//				drawTexturedModalRect(x + i * 4, y, 24, 0, 3, 6);
	//				break;
	//			case '7':
	//				drawTexturedModalRect(x + i * 4, y, 28, 0, 3, 6);
	//				break;
	//			case '8':
	//				drawTexturedModalRect(x + i * 4, y, 32, 0, 3, 6);
	//				break;
	//			case '9':
	//				drawTexturedModalRect(x + i * 4, y, 36, 0, 3, 6);
	//				break;
	//			case '.':
	//				drawTexturedModalRect(x + i * 4, y, 40, 0, 3, 6);
	//				break;
	//			case '/':
	//				drawTexturedModalRect(x + i * 4, y, 44, 0, 3, 6);
	//				break;
	//			case '_':
	//				drawTexturedModalRect(x + i * 4, y, 48, 0, 3, 6);
	//				break;
	//			case ':':
	//				drawTexturedModalRect(x + i * 4, y, 52, 0, 3, 6);
	//				break;
	//			}
	//		}
	//		GL11.glPopAttrib();
	//	}

	/**sound
	 */
	@Override
	public void func_146113_a(SoundHandler p_146113_1_) {
		//		p_146113_1_.playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
	}

	public class VertexCoord {
		final double x;
		final double y;
		final RateData rate;

		public VertexCoord(double x, double y, RateData rate) {
			this.x = x;
			this.y = y;
			this.rate = rate;
		}
	}

}
