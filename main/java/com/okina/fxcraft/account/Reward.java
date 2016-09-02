package com.okina.fxcraft.account;

import java.util.Objects;

import com.okina.fxcraft.main.FXCraft;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public abstract class Reward {

	private String name;
	private String displayName;
	private ItemStack item;
	private String conditionMessage;

	public Reward(String rewardName, String displayName, ItemStack item, String conditionMessage) {
		name = rewardName;
		this.displayName = displayName;
		this.item = Objects.requireNonNull(item);
		this.conditionMessage = conditionMessage;
	}

	public final String getName() {
		return name;
	}

	public final String getDisplayName() {
		return displayName;
	}

	public final ItemStack getItem() {
		return item.copy();
	}

	public final String getConditionMessage() {
		return conditionMessage;
	}

	/**Called with account info update<br>
	 * Returns param account can get this reward<br>
	 * Ignore whether param account has already recept this rewrd.
	 * @param account
	 * @return
	 */
	public abstract boolean canGetReward(AccountInfo account);

	public Reward getNextStepReward() {
		return null;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Reward && ((Reward) o).getName().equals(name);
	}

	public static class Rewards {

		private static final int[] DEAL = { 10000, 50000, 100000, 200000, 300000, 400000, 500000, 600000, 700000, 800000 };
		private static final String[] DEAL_STR = { "駆け出し", "駆け出し Mk.2", "そこそこ", "そこそこ Mk.2", "まぁまぁ", "まぁまぁ Mk.2", "中堅", "中堅 Mk.2", "ベテラン", "ベテラン Mk.2" };
		public static final Reward[] TOTAL_DEAL = new Reward[DEAL.length];
		static{
			for (int i = 0; i < DEAL.length; i++){
				final int index = i;
				TOTAL_DEAL[index] = new Reward("total_deal_" + DEAL[index], DEAL_STR[index], new ItemStack(i % 2 == 0 ? FXCraft.limit_dealLot[(int) (i / 2.0)] : FXCraft.limit_leverage[(int) (i / 2.0)], 1), "Deal Total " + DEAL[index] + " Lots") {
					@Override
					public boolean canGetReward(AccountInfo account) {
						return account.totalDeal >= DEAL[index];
					}
					@Override
					public Reward getNextStepReward() {
						if(index != DEAL.length - 1){
							return TOTAL_DEAL[index + 1];
						}else{
							return MAX_LOT;
						}
					}
				};
			}
		}

		private static final int[] GAIN = { 10, 30, 50, 100, 300, 500 };
		private static final String[] GAIN_STR = { "儲けるマン１", "儲けるマン２", "儲けるマン３", "儲けるマン４", "儲けるマン５", "儲けるマン６" };
		public static final Reward[] TOTAL_GAIN = new Reward[GAIN.length];
		static{
			for (int i = 0; i < GAIN.length; i++){
				final int index = i;
				TOTAL_GAIN[i] = new Reward("total_gain_" + GAIN[i], GAIN_STR[i], i == 0 ? new ItemStack(FXCraft.limit_limits_trade) : new ItemStack(FXCraft.limit_position[i - 1]), "Gain Total " + GAIN[i] + " Lots") {
					@Override
					public boolean canGetReward(AccountInfo account) {
						return account.totalGain >= GAIN[index];
					}
					@Override
					public Reward getNextStepReward() {
						if(index != GAIN.length - 1){
							return TOTAL_GAIN[index + 1];
						}else{
							return MAX_LEVERAGE;
						}
					}
				};
			}
		}

		private static final int[] LOSS = { 10, 30, 50, 100, 300 };
		private static final String[] LOSS_STR = { "慰めの報酬１", "慰めの報酬２", "慰めの報酬３", "慰めの報酬４", "慰めの報酬５" };
		private static final ItemStack[] LOSS_ITEM = { new ItemStack(Items.emerald, 1), new ItemStack(Items.emerald, 3), new ItemStack(Items.emerald, 5), new ItemStack(Items.emerald, 7), new ItemStack(Items.emerald, 10) };
		public static final Reward[] TOTAL_LOSS = new Reward[LOSS.length];
		static{
			for (int i = 0; i < LOSS.length; i++){
				final int index = i;
				TOTAL_LOSS[index] = new Reward("total_loss_" + LOSS[index], LOSS_STR[index], LOSS_ITEM[index], "Total Loss " + LOSS[index] + " Lots") {
					@Override
					public boolean canGetReward(AccountInfo account) {
						return account.totalLoss >= LOSS[index];
					}
					@Override
					public Reward getNextStepReward() {
						if(index != LOSS.length - 1){
							return TOTAL_LOSS[index + 1];
						}else{
							return FIRST_LOSSCUT;
						}
					}
				};
			}
		}

		public static final Reward MAX_LOT = new Reward("max_lot", "Big Dealer", new ItemStack(FXCraft.jentlemens_cap), "Deal " + AccountInfo.DEAL_LIMIT[AccountInfo.DEAL_LIMIT.length - 1] + " Lot Position") {
			@Override
			public boolean canGetReward(AccountInfo account) {
				for (FXDealHistory history : account.history){
					if(history.lot >= AccountInfo.DEAL_LIMIT[AccountInfo.DEAL_LIMIT.length - 1]) return true;
				}
				return false;
			}
			@Override
			public Reward getNextStepReward() {
				return MAX_LOT_LEVERAGE;
			}
		};

		public static final Reward MAX_LEVERAGE = new Reward("max_leverage", "Big Dealer2", new ItemStack(FXCraft.jentlemens_panz), "Deal by leverage " + AccountInfo.LEVERAGE_LIMIT[AccountInfo.LEVERAGE_LIMIT.length - 1]) {
			@Override
			public boolean canGetReward(AccountInfo account) {
				for (FXDealHistory history : account.history){
					if(history.lot / history.deposit == AccountInfo.LEVERAGE_LIMIT[AccountInfo.LEVERAGE_LIMIT.length - 1]) return true;
				}
				return false;
			}
			@Override
			public Reward getNextStepReward() {
				return MAX_LOT_LEVERAGE;
			}
		};

		public static final Reward MAX_LOT_LEVERAGE = new Reward("max_lot_leverage", "Big Dealer3", new ItemStack(FXCraft.capitalist_guard), "Deal " + AccountInfo.DEAL_LIMIT[AccountInfo.DEAL_LIMIT.length - 1] + " by leverage " + AccountInfo.LEVERAGE_LIMIT[AccountInfo.LEVERAGE_LIMIT.length - 1]) {
			@Override
			public boolean canGetReward(AccountInfo account) {
				for (FXDealHistory history : account.history){
					if(history.lot >= AccountInfo.DEAL_LIMIT[AccountInfo.DEAL_LIMIT.length - 1] && history.lot / history.deposit == AccountInfo.LEVERAGE_LIMIT[AccountInfo.LEVERAGE_LIMIT.length - 1]) return true;
				}
				return false;
			}
		};

		public static final Reward FIRST_DEAL = new Reward("first_deal", "Hello World", new ItemStack(Items.emerald, 1), "First Deal") {
			@Override
			public boolean canGetReward(AccountInfo account) {
				for (FXDealHistory history : account.history){
					if("Settle".equals(history.dealType)) return true;
				}
				return false;
			}
			@Override
			public Reward getNextStepReward() {
				return TOTAL_DEAL[0];
			}
		};

		public static final Reward FIRST_LEVERAGE_DEAL = new Reward("first_leverage_deal", "High Risk High Return", new ItemStack(FXCraft.iPhone), "First Leverage Trade") {
			@Override
			public boolean canGetReward(AccountInfo account) {
				for (FXDealHistory history : account.history){
					if(history.lot > history.deposit) return true;
				}
				return false;
			}
		};

		public static final Reward FIRST_LIMITS_DEAL = new Reward("first_limits_deal", "Tricky Trade", new ItemStack(FXCraft.capitalist_gun), "First Limits Trade") {
			@Override
			public boolean canGetReward(AccountInfo account) {
				for (FXDealHistory history : account.history){
					if(history.isLimits) return true;
				}
				return false;
			}
		};

		public static final Reward FIRST_LOSSCUT = new Reward("first_losscut", "Oh shit!", new ItemStack(FXCraft.fx_mask, 1), "First Loss-Cut") {
			@Override
			public boolean canGetReward(AccountInfo account) {
				for (FXDealHistory history : account.history){
					if(history.gain == -history.deposit) return true;
				}
				return false;
			}
		};

	}

}





