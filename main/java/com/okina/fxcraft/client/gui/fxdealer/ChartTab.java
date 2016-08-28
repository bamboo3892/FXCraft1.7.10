package com.okina.fxcraft.client.gui.fxdealer;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.okina.fxcraft.account.AccountInfo;
import com.okina.fxcraft.account.FXPosition;
import com.okina.fxcraft.client.gui.GuiFXChart;
import com.okina.fxcraft.client.gui.GuiFlatConfirmButton;
import com.okina.fxcraft.client.gui.GuiFlatSelectButton;
import com.okina.fxcraft.client.gui.GuiFlatToggleButton;
import com.okina.fxcraft.client.gui.GuiSlider;
import com.okina.fxcraft.client.gui.GuiTab;
import com.okina.fxcraft.main.FXCraft;
import com.okina.fxcraft.rate.FXRateGetHelper;
import com.okina.fxcraft.rate.NoValidRateException;
import com.okina.fxcraft.rate.RateData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.OpenGlHelper;

public class ChartTab extends GuiTab<FXDealerGui> {

	private List<GuiButton> list;
	private long lastAccountUpdate;

	private GuiFXChart guiChart;
	private GuiPositionTable positionTable;
	private GuiFlatSelectButton[] pairButtons = new GuiFlatSelectButton[3];
	private GuiFlatSelectButton[] termButtons = new GuiFlatSelectButton[5];

	protected boolean positionFocus = false;

	private GuiButton[] getPositionButtons = new GuiButton[2];
	private GuiSlider dealLotSlider;
	private GuiSlider depositLotSlider;

	private GuiSlider settleLotSlider;

	private GuiFlatToggleButton limitsTradeButton;
	private GuiTextField limitsTradeField;

	private GuiFlatConfirmButton bidButton;
	private GuiFlatConfirmButton askButton;
	private GuiFlatConfirmButton settleButton;

	public ChartTab(FXDealerGui gui, int startX, int startY) {
		super(gui, startX, startY, 0, 1, Lists.newArrayList("Chart"));
		int left = (gui.width - gui.getSizeX()) / 2;
		int right = (gui.width + gui.getSizeX()) / 2;
		int up = (gui.height - gui.getSizeY()) / 2;
		int down = (gui.height + gui.getSizeY()) / 2;

		list = Lists.newArrayList();
		list.add(pairButtons[0] = new GuiFlatSelectButton(50, left + 4, up + 26, 68, 14, "USD/JPY", new float[] { 0.5f, 1f, 0f }));
		list.add(pairButtons[1] = new GuiFlatSelectButton(51, left + 76, up + 26, 68, 14, "EUR/JPY", new float[] { 0.5f, 1f, 0f }));
		list.add(pairButtons[2] = new GuiFlatSelectButton(52, left + 148, up + 26, 68, 14, "AUD/JPY", new float[] { 0.5f, 1f, 0f }));
		pairButtons[0].selected = true;
		list.add(termButtons[0] = new GuiFlatSelectButton(FXRateGetHelper.TERM_REALTIME + 100, left + 148, down - 18, 68, 14, "REALTIME", new float[] { 0.5f, 1f, 0f }));
		list.add(termButtons[1] = new GuiFlatSelectButton(FXRateGetHelper.TERM_1m + 100, left + 112, down - 18, 32, 14, "1MIN", new float[] { 0.5f, 1f, 0f }));
		list.add(termButtons[2] = new GuiFlatSelectButton(FXRateGetHelper.TERM_15m + 100, left + 76, down - 18, 32, 14, "15MIN", new float[] { 0.5f, 1f, 0f }));
		list.add(termButtons[3] = new GuiFlatSelectButton(FXRateGetHelper.TERM_1d + 100, left + 40, down - 18, 32, 14, "DAY", new float[] { 0.5f, 1f, 0f }));
		list.add(termButtons[4] = new GuiFlatSelectButton(FXRateGetHelper.TERM_1M + 100, left + 4, down - 18, 32, 14, "MON", new float[] { 0.5f, 1f, 0f }));
		termButtons[0].selected = true;

		list.add(positionTable = new GuiPositionTable(gui.tile, 10, left + 226, up + 37, new GuiPositionTableRow(8, new int[] { 28, 20, 39, 39 }, new String[] { "PAIR", "B/A", "RATE", "POINT" }, new int[] { FXPosition.FIELD_PAIR, FXPosition.FIELD_ASK_BID, FXPosition.FIELD_RATE, FXPosition.FIELD_LOT }), 12));

		list.add(getPositionButtons[0] = dealLotSlider = new GuiSlider(3, left + 272, down - 62, 80, 1000, 100000, 1000, 1000));
		list.add(getPositionButtons[1] = depositLotSlider = new GuiSlider(3, left + 272, down - 48, 80, 1000, 100000, 1000, 1000));

		settleLotSlider = new GuiSlider(3, left + 272, down - 48, 80, 1000, 100000, 1000, 1000);

		list.add(limitsTradeButton = new GuiFlatToggleButton(20, left + 226, down - 33, 44, 12, "Limits", new float[] { 0.5f, 1f, 0f }));
		//		limitsTradeButton.enabled = false;
		limitsTradeField = new GuiTextField(gui.getFontRenderer(), left + 273, down - 33, 78, 12);
		limitsTradeField.setTextColor(-1);
		limitsTradeField.setDisabledTextColour(-1);
		limitsTradeField.setEnableBackgroundDrawing(true);
		limitsTradeField.setMaxStringLength(10);
		//		limitsTradeField.setCanLoseFocus(false);

		list.add(bidButton = new GuiFlatConfirmButton(5, left + 226, down - 18, 62, 14, "BID", new float[] { 0.5f, 1f, 0f }, Lists.newArrayList("Press this button", "when you think rate decreasing."), Lists.newArrayList("Click Again To Confirm")));
		list.add(askButton = new GuiFlatConfirmButton(6, left + 290, down - 18, 62, 14, "ASK", new float[] { 0.5f, 1f, 0f }, Lists.newArrayList("Press this button", "when you think rate increasing."), Lists.newArrayList("Click Again To Confirm")));
		settleButton = new GuiFlatConfirmButton(7, left + 226, down - 18, 126, 14, "SETTLE", new float[] { 0.5f, 1f, 0f }, Lists.newArrayList("Settle Your Position"), Lists.newArrayList("Click Again To Confirm"));

		list.add(guiChart = new GuiFXChart(left + 4, up + 44, 212, 174));

		updateAccount(gui.tile.getAccountInfo());
		lastAccountUpdate = System.currentTimeMillis();
	}

	@Override
	public void actionPerformed(GuiButton guiButton) {
		int id = guiButton.id;
		if(id == 5){//bid
			if(!bidButton.selected){
				if(limitsTradeButton.selected){
					try{
						double limits = Double.valueOf(limitsTradeField.getText());
						gui.tile.tryGetPositionOrder(guiChart.getDisplayPair(), dealLotSlider.getValue(), depositLotSlider.getValue(), false, limits);
					}catch (NumberFormatException e){
						FXCraft.proxy.appendPopUp("Illegal Limits");
					}
				}else{
					gui.tile.tryGetPosition(guiChart.getDisplayPair(), dealLotSlider.getValue(), depositLotSlider.getValue(), false);
				}
			}
		}else if(id == 6){//ask
			if(!askButton.selected){
				if(limitsTradeButton.selected){
					try{
						double limits = Double.valueOf(limitsTradeField.getText());
						gui.tile.tryGetPositionOrder(guiChart.getDisplayPair(), dealLotSlider.getValue(), depositLotSlider.getValue(), true, limits);
					}catch (NumberFormatException e){
						FXCraft.proxy.appendPopUp("Illegal Limits");
					}
				}else{
					gui.tile.tryGetPosition(guiChart.getDisplayPair(), dealLotSlider.getValue(), depositLotSlider.getValue(), true);
				}
			}
		}else if(id == 7){//settle
			if(!settleButton.selected){
				if(limitsTradeButton.selected){
					try{
						double limits = Double.valueOf(limitsTradeField.getText());
						gui.tile.trySettlePositionOrder(positionTable.getSelectedPosition(), settleLotSlider.getValue(), limits);
					}catch (NumberFormatException e){
						FXCraft.proxy.appendPopUp("Illegal Limits");
					}
				}else{
					gui.tile.trySettlePosition(positionTable.getSelectedPosition(), settleLotSlider.getValue());
				}
			}
		}else if(id == 50){//usdjpy
			guiChart.setDisplayPair("USDJPY");
			pairButtons[1].selected = false;
			pairButtons[2].selected = false;
			changeFocus(false);
		}else if(id == 51){//eurjpy
			guiChart.setDisplayPair("EURJPY");
			pairButtons[0].selected = false;
			pairButtons[2].selected = false;
			changeFocus(false);
		}else if(id == 52){//eurusd
			guiChart.setDisplayPair("AUDJPY");
			pairButtons[0].selected = false;
			pairButtons[1].selected = false;
			changeFocus(false);
		}else if(100 <= guiButton.id && guiButton.id <= 111){//term
			guiChart.setDisplayTerm(guiButton.id - 100);
			for (GuiFlatToggleButton termButton : termButtons){
				if(termButton.id != guiButton.id) termButton.selected = false;
			}
			changeFocus(false);
		}else if(id == 10){//table
			changeFocus(true);
		}else if(id == 997){//chart
			changeFocus(false);
		}
	}

	private void changeFocus(boolean positionFocus) {
		if(this.positionFocus != positionFocus){
			this.positionFocus = positionFocus;
			positionTable.setForcused(positionFocus);
			if(positionFocus){
				for (GuiButton button : getPositionButtons){
					list.remove(button);
				}
				list.remove(bidButton);
				list.remove(askButton);
				list.add(settleLotSlider);
				list.add(settleButton);
			}else{
				list.remove(settleLotSlider);
				list.remove(settleButton);
				for (GuiButton button : getPositionButtons){
					list.add(button);
				}
				list.add(bidButton);
				list.add(askButton);
			}
			gui.updateButton();
		}
		if(positionFocus){
			FXPosition position = positionTable.getSelectedPosition();
			settleLotSlider.setMaxValue((int) position.lot);
			settleLotSlider.setValue((int) position.lot);
		}
	}

	@Override
	public void drawComponent(Minecraft minecraft, int mouseX, int mouseY) {
		super.drawComponent(minecraft, mouseX, mouseY);

		AccountInfo account = gui.tile.getAccountInfo();
		if(gui.tile.hasAccountUpdate(lastAccountUpdate)){
			updateAccount(account);
			lastAccountUpdate = System.currentTimeMillis();
		}

		FontRenderer fontRenderer = minecraft.fontRenderer;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(true);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		int left = (gui.width - gui.getSizeX()) / 2;
		int right = (gui.width + gui.getSizeX()) / 2;
		int up = (gui.height - gui.getSizeY()) / 2;
		int down = (gui.height + gui.getSizeY()) / 2;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);

		drawRect(left + 220, up + 25, left + 222, down - 3, 0x33000000);

		fontRenderer.drawString("Positions", left + 226, up + 27, 0xFFFFFF, false);
		if(!positionFocus){
			dealLotSlider.setMinValue(depositLotSlider.getValue());
			dealLotSlider.setMaxValue(account == null ? 1000 : Math.min(depositLotSlider.getValue() * AccountInfo.LEVERAGE_LIMIT[account.leverageLimit], AccountInfo.DEAL_LIMIT[account.dealLotLimit]));

			fontRenderer.drawString("Get New Position", left + 226, down - 104, 0x7fff00, false);

			fontRenderer.drawString("Pair", left + 228, down - 93, 0xFFFFFF, false);
			String str = guiChart.getDisplayPair();
			fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), down - 93, 0xFFFFFF, false);

			fontRenderer.drawString("Rate", left + 228, down - 83, 0xFFFFFF, false);
			try{
				str = String.valueOf(FXCraft.rateGetter.getEarliestRate(str).open);
			}catch (NoValidRateException e){
				str = "---";
			}
			fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), down - 83, 0xFFFFFF, false);

			fontRenderer.drawString("Leverage", left + 228, down - 73, 0xFFFFFF, false);
			str = String.format("%.2f", dealLotSlider.getValue() / (float) depositLotSlider.getValue());
			fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), down - 73, 0xFFFFFF, false);

			fontRenderer.drawString("Deal", left + 228, down - 59, 0xFFFFFF, false);
			fontRenderer.drawString("Deposit", left + 228, down - 45, 0xFFFFFF, false);
		}else{
			fontRenderer.drawString("Settle Your Position", left + 226, down - 104, 0x7fff00, false);
			FXPosition position = positionTable.getSelectedPosition();
			if(position != FXPosition.NO_INFO){
				String str;
				fontRenderer.drawString("Date", left + 228, down - 93, 0xFFFFFF, false);
				str = FXRateGetHelper.getCalendarString(position.contractDate, FXRateGetHelper.TERM_1w);
				fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), down - 93, 0xFFFFFF, false);

				fontRenderer.drawString("Leverage", left + 228, down - 83, 0xFFFFFF, false);
				str = String.valueOf(position.getLeverage());
				fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), down - 83, 0xFFFFFF, false);

				fontRenderer.drawString("Now Rate", left + 228, down - 73, 0xFFFFFF, false);
				RateData now;
				try{
					now = FXCraft.rateGetter.getEarliestRate(position.currencyPair);
				}catch (NoValidRateException e){
					now = RateData.NO_DATA;
				}
				str = String.valueOf(now.open);
				fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), down - 73, 0xFFFFFF, false);

				fontRenderer.drawString("Pos Value", left + 228, down - 63, 0xFFFFFF, false);
				double gain = position.getGain(now.open);
				double value = position.getValue(now.open);
				str = String.format("%.3f", value);
				fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), down - 63, gain < 0 ? 0xff4500 : 0x00ffff, false);
			}

			fontRenderer.drawString("Settle", left + 228, down - 45, 0xFFFFFF, false);
		}
		limitsTradeField.drawTextBox();
		GL11.glPopAttrib();
	}

	public void updateAccount(AccountInfo account) {
		depositLotSlider.setMaxValue(account == null ? 1000 : AccountInfo.DEAL_LIMIT[account.dealLotLimit]);
		if(account != null && account.limitsTradePermission){
			limitsTradeButton.enabled = true;
			limitsTradeField.setCanLoseFocus(true);
		}else{
			limitsTradeButton.enabled = false;
			limitsTradeButton.selected = false;
			limitsTradeField.setCanLoseFocus(false);
			limitsTradeField.setText("");
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouse) {
		super.mouseClicked(mouseX, mouseY, mouse);
		limitsTradeField.mouseClicked(mouseX, mouseY, mouse);
	}

	@Override
	public void handleMouseInput() {
		guiChart.handleMouseInput();
	}

	@Override
	public boolean keyTyped(char keyChar, int key) {
		return limitsTradeField.textboxKeyTyped(keyChar, key);
	}

	@Override
	public List<GuiButton> getButtonList() {
		return list;
	}

}
