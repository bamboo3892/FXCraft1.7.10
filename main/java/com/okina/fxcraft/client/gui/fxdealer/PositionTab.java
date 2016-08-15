package com.okina.fxcraft.client.gui.fxdealer;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.okina.fxcraft.account.AccountInfo;
import com.okina.fxcraft.account.FXPosition;
import com.okina.fxcraft.client.gui.GuiFXRateBox;
import com.okina.fxcraft.client.gui.GuiFlatConfirmButton;
import com.okina.fxcraft.client.gui.GuiFlatToggleButton;
import com.okina.fxcraft.client.gui.GuiSlider;
import com.okina.fxcraft.client.gui.GuiTab;
import com.okina.fxcraft.main.FXCraft;
import com.okina.fxcraft.rate.FXRateGetHelper;
import com.okina.fxcraft.rate.NoValidRateException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.OpenGlHelper;

public class PositionTab extends GuiTab<FXDealerGui> {

	private List<GuiButton> list = Lists.newArrayList();

	private GuiFXRateBox rateBoxUSDJPY;
	private GuiFXRateBox rateBoxEURJPY;
	private GuiFXRateBox rateBoxEURUSD;

	private GuiPositionTable positionTable;
	private GuiOrderTable orderTable;

	/**0: ratebox 1: position 2: oeder*/
	private int focusState = 0;
	private String focusPair = "USDJPY";

	private GuiButton[] removeButtons = new GuiButton[8];
	private GuiSlider dealLotSlider;
	private GuiSlider depositLotSlider;

	private GuiSlider settleLotSlider;

	private GuiFlatToggleButton limitsTradeButton;
	private GuiTextField limitsTradeField;

	private GuiFlatConfirmButton bidButton;
	private GuiFlatConfirmButton askButton;
	private GuiFlatConfirmButton settleButton;
	private GuiFlatConfirmButton orderDeleteButton;

	public PositionTab(FXDealerGui gui, int startX, int startY) {
		super(gui, startX, startY, 1, 1, Lists.newArrayList("Position"));

		int left = (gui.width - gui.getSizeX()) / 2;
		int right = (gui.width + gui.getSizeX()) / 2;
		int up = (gui.height - gui.getSizeY()) / 2;
		int down = (gui.height + gui.getSizeY()) / 2;
		list.add(rateBoxUSDJPY = new GuiFXRateBox(50, left + 2, up + 24, 80, 70, "USDJPY"));
		rateBoxUSDJPY.selected = true;
		list.add(rateBoxEURJPY = new GuiFXRateBox(51, left + 2, up + 96, 80, 70, "EURJPY"));
		list.add(rateBoxEURUSD = new GuiFXRateBox(52, left + 2, up + 168, 80, 70, "EURUSD"));

		list.add(positionTable = new GuiPositionTable(gui.tile, 10, left + 90, up + 39, new GuiPositionTableRow(8, new int[] { 28, 20, 39, 39 }, new String[] { "PAIR", "B/A", "RATE", "POINT" }, new int[] { FXPosition.FIELD_PAIR, FXPosition.FIELD_ASK_BID, FXPosition.FIELD_RATE, FXPosition.FIELD_LOT }), 12));
		list.add(orderTable = new GuiOrderTable(gui.tile, 11, left + 90, up + 157, new GuiOrderTableRow(8, new int[] { 28, 20, 39, 39 }, new String[] { "PAIR", "B/A", "RATE", "POINT" }, new int[] { FXPosition.FIELD_PAIR, FXPosition.FIELD_ASK_BID, FXPosition.FIELD_RATE, FXPosition.FIELD_LOT }, new int[] { FXPosition.FIELD_PAIR, FXPosition.FIELD_ASK_BID, FXPosition.FIELD_RATE, FXPosition.FIELD_LOT }), 10));

		list.add(removeButtons[0] = dealLotSlider = new GuiSlider(3, left + 272, down - 62, 80, 1000, 100000, 1000, 1000));
		list.add(removeButtons[1] = depositLotSlider = new GuiSlider(3, left + 272, down - 48, 80, 1000, 100000, 1000, 1000));
		depositLotSlider.enabled = false;

		removeButtons[2] = settleLotSlider = new GuiSlider(3, left + 272, down - 48, 80, 1000, 100000, 1000, 1000);

		list.add(removeButtons[3] = limitsTradeButton = new GuiFlatToggleButton(20, left + 226, down - 33, 44, 12, "Limits", new float[] { 0.5f, 1f, 0f }));
		limitsTradeButton.enabled = false;
		limitsTradeField = new GuiTextField(gui.getFontRenderer(), left + 273, down - 33, 78, 12);
		limitsTradeField.setTextColor(-1);
		limitsTradeField.setDisabledTextColour(-1);
		limitsTradeField.setEnableBackgroundDrawing(true);
		limitsTradeField.setMaxStringLength(10);
		//		limitsTradeField.setCanLoseFocus(false);

		list.add(removeButtons[4] = bidButton = new GuiFlatConfirmButton(5, left + 226, down - 18, 62, 14, "BID", new float[] { 0.5f, 1f, 0f }, Lists.newArrayList("Press this button", "when you think rate decreasing."), Lists.newArrayList("Click Again To Confirm")));
		list.add(removeButtons[5] = askButton = new GuiFlatConfirmButton(6, left + 290, down - 18, 62, 14, "ASK", new float[] { 0.5f, 1f, 0f }, Lists.newArrayList("Press this button", "when you think rate increasing."), Lists.newArrayList("Click Again To Confirm")));
		removeButtons[6] = settleButton = new GuiFlatConfirmButton(7, left + 226, down - 18, 126, 14, "SETTLE", new float[] { 0.5f, 1f, 0f }, Lists.newArrayList("Settle Your Position"), Lists.newArrayList("Click Again To Confirm"));
		removeButtons[7] = orderDeleteButton = new GuiFlatConfirmButton(8, left + 226, down - 18, 126, 14, "DELETE", new float[] { 0.5f, 1f, 0f }, Lists.newArrayList("Delete Your Order"), Lists.newArrayList("Click Again To Confirm"));
	}

	@Override
	public void actionPerformed(GuiButton guiButton) {
		int id = guiButton.id;
		if(id == 5){//bid
			if(!bidButton.selected){
				gui.tile.tryGetPosition(getSelectedPair(), dealLotSlider.getValue(), depositLotSlider.getValue(), false);
			}
		}else if(id == 6){//ask
			if(!bidButton.selected){
				gui.tile.tryGetPosition(getSelectedPair(), dealLotSlider.getValue(), depositLotSlider.getValue(), true);
			}
		}else if(id == 7){//settle
			if(!settleButton.selected){
				gui.tile.trySettlePosition(positionTable.getSelectedPosition(), settleLotSlider.getValue());
			}
		}else if(id == 50){
			rateBoxEURJPY.selected = false;
			rateBoxEURUSD.selected = false;
			focusPair = "USDJPY";
			changeFocus(0);
		}else if(id == 51){
			rateBoxEURUSD.selected = false;
			rateBoxUSDJPY.selected = false;
			focusPair = "EURJPY";
			changeFocus(0);
		}else if(id == 52){
			rateBoxUSDJPY.selected = false;
			rateBoxEURJPY.selected = false;
			focusPair = "EURUSD";
			changeFocus(0);
		}else{
			if(id == 10){
				changeFocus(1);
			}else if(id == 11){
				changeFocus(2);
			}
		}
	}

	private void changeFocus(int focusState) {
		if(this.focusState != focusState){
			this.focusState = focusState;
			positionTable.setForcused(focusState == 1);
			limitsTradeField.setCanLoseFocus(focusState != 2);
			limitsTradeField.setVisible(focusState != 2);
			orderTable.setForcused(focusState == 2);

			for (GuiButton button : removeButtons){
				list.remove(button);
			}

			if(focusState != 0){
				rateBoxUSDJPY.selected = false;
				rateBoxEURJPY.selected = false;
				rateBoxEURUSD.selected = false;
			}

			if(focusState == 0){
				list.add(dealLotSlider);
				list.add(depositLotSlider);
				list.add(limitsTradeButton);
				list.add(bidButton);
				list.add(askButton);
			}else if(focusState == 1){
				list.add(limitsTradeButton);
				list.add(settleLotSlider);
				list.add(settleButton);
			}else{
				list.add(orderDeleteButton);
			}
			gui.updateButton();
		}
	}

	private String getSelectedPair() {
		if(rateBoxUSDJPY.selected){
			return "USDJPY";
		}else if(rateBoxEURJPY.selected){
			return "EURJPY";
		}else if(rateBoxEURUSD.selected){
			return "EURUSD";
		}
		return "None";
	}

	@Override
	public void drawComponent(Minecraft minecraft, int mouseX, int mouseY) {
		super.drawComponent(minecraft, mouseX, mouseY);
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
		AccountInfo login = gui.tile.getAccountInfo();

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);

		drawRect(left + 85, up + 25, left + 87, down - 3, 0x33000000);

		fontRenderer.drawString("Positions", left + 90, up + 28, 0xFFFFFF, false);

		drawRect(left + 87, up + 140, left + 219, up + 142, 0x33000000);

		fontRenderer.drawString("Orders", left + 90, up + 147, 0xFFFFFF, false);

		drawRect(left + 219, up + 25, left + 221, down - 3, 0x33000000);

		fontRenderer.drawString("Login :", left + 226, up + 26, 0x7fff00, false);
		String str = login == null ? "No Account" : login.name;
		fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), up + 26, 0xFFFFFF, false);

		fontRenderer.drawString("Balance", left + 228, up + 36, 0xFFFFFF, false);
		str = login == null ? "0" : String.format("%.3f", login.balance);
		fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), up + 36, 0xFFFFFF, false);

		Map<String, Double> rateMap = FXCraft.rateGetter.getEarliestRate();

		fontRenderer.drawString("Pos Value", left + 228, up + 46, 0xFFFFFF, false);
		str = login == null ? "0" : String.format("%.3f", login.getPosiitionsValue(rateMap));
		fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), up + 46, 0xFFFFFF, false);

		fontRenderer.drawString("Orders Value", left + 228, up + 56, 0xFFFFFF, false);
		str = login == null ? "0" : String.format("%.3f", login.getOrdersValue(rateMap));
		fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), up + 56, 0xFFFFFF, false);

		fontRenderer.drawString("Total", left + 228, up + 66, 0xFFFFFF, false);
		str = login == null ? "0" : String.format("%.3f", login.getTotalBalence(rateMap));
		fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), up + 66, 0xFFFFFF, false);

		if(focusState == 0){
			fontRenderer.drawString("Get New Position", left + 226, down - 104, 0x7fff00, false);

			fontRenderer.drawString("Pair", left + 228, down - 93, 0xFFFFFF, false);
			str = focusPair;
			fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), down - 93, 0xFFFFFF, false);

			fontRenderer.drawString("Rate", left + 228, down - 83, 0xFFFFFF, false);
			try{
				str = String.valueOf(FXCraft.rateGetter.getEarliestRate(str));
			}catch (NoValidRateException e){
				str = "---";
			}
			fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), down - 83, 0xFFFFFF, false);

			fontRenderer.drawString("Leverage", left + 228, down - 73, 0xFFFFFF, false);
			str = String.valueOf(dealLotSlider.getValue() / (float) depositLotSlider.getValue());
			fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), down - 73, 0xFFFFFF, false);

			fontRenderer.drawString("Deal", left + 228, down - 59, 0xFFFFFF, false);
			fontRenderer.drawString("Deposit", left + 228, down - 45, 0xFFFFFF, false);
		}else if(focusState == 1){
			fontRenderer.drawString("Settle Your Position", left + 226, down - 154, 0x7fff00, false);

			FXPosition position = positionTable.getSelectedPosition();

			fontRenderer.drawString("Pair", left + 228, down - 143, 0xFFFFFF, false);
			fontRenderer.drawString("Date", left + 228, down - 133, 0xFFFFFF, false);
			fontRenderer.drawString("BID/ASK", left + 228, down - 123, 0xFFFFFF, false);
			fontRenderer.drawString("Construct Rate", left + 228, down - 113, 0xFFFFFF, false);
			fontRenderer.drawString("Now Rate", left + 228, down - 103, 0xFFFFFF, false);
			fontRenderer.drawString("Construct Lot", left + 228, down - 93, 0xFFFFFF, false);
			fontRenderer.drawString("Deposit Lot", left + 228, down - 83, 0xFFFFFF, false);
			fontRenderer.drawString("Pos Value", left + 228, down - 73, 0xFFFFFF, false);
			fontRenderer.drawString("Leverage", left + 228, down - 63, 0xFFFFFF, false);

			if(position != FXPosition.NO_INFO){
				str = position.currencyPair;
				fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), down - 143, 0xFFFFFF, false);

				str = FXRateGetHelper.getCalendarString(position.contractDate, -1);
				fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), down - 133, 0xFFFFFF, false);

				str = position.askOrBid ? "ASK" : "BID";
				fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), down - 123, 0xFFFFFF, false);

				str = String.format("%.3f", position.contractRate);
				fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), down - 113, 0xFFFFFF, false);

				double now;
				try{
					now = FXCraft.rateGetter.getEarliestRate(position.currencyPair);
				}catch (NoValidRateException e){
					now = 0;
				}
				str = String.format("%.3f", now);
				fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), down - 103, 0xFFFFFF, false);

				str = String.format("%.1f", position.lot);
				fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), down - 93, 0xFFFFFF, false);

				str = String.format("%.1f", position.depositLot);
				fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), down - 83, 0xFFFFFF, false);

				double gain = position.getGain(now);
				str = String.format("%.3f", gain + position.lot);
				fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), down - 73, gain < 0 ? 0xff4500 : 0x00ffff, false);

				str = String.format("%.1f", position.getLeverage());
				fontRenderer.drawString(str, left + 351 - fontRenderer.getStringWidth(str), down - 63, 0xFFFFFF, false);
			}

			fontRenderer.drawString("Deal", left + 228, down - 45, 0xFFFFFF, false);
		}else{
			fontRenderer.drawString("Delete Your Order", left + 226, down - 30, 0x7fff00, false);
			//TODO
		}

		limitsTradeField.drawTextBox();
		GL11.glPopAttrib();
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouse) {
		super.mouseClicked(mouseX, mouseY, mouse);
		limitsTradeField.mouseClicked(mouseX, mouseY, mouse);
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
