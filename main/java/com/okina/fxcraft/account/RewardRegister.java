package com.okina.fxcraft.account;

import java.util.List;

import com.google.common.collect.Lists;

public class RewardRegister {

	public static final RewardRegister instance = new RewardRegister();

	private List<Reward> rewardList = Lists.newArrayList();
	private List<Reward> firstAimable = Lists.newArrayList();

	private RewardRegister() {}

	public boolean registerReward(Reward reward) {
		if(reward == null || rewardList.contains(reward)){
			return false;
		}
		rewardList.add(reward);
		return true;
	}

	public boolean registerFirstAimableReward(Reward reward) {
		if(reward == null || firstAimable.contains(reward)){
			return false;
		}
		firstAimable.add(reward);
		return true;
	}

	public Reward getReward(String name) {
		for (Reward reward : rewardList){
			if(reward.getName().equals(name)){
				return reward;
			}
		}
		return null;
	}

	public List<Reward> getAvailableRewards(AccountInfo account) {
		List<Reward> list = Lists.newArrayList();
		for (Reward reward : rewardList){
			if(account.receivableReward.contains(reward.getName()) && !account.receivedReward.contains(reward.getName())){
				list.add(reward);
			}
		}
		//		for (int i = 0; i < 17; i++){
		//			list.add(Rewards.TOTAL_DEAL_1000);
		//		}
		return list;
	}

	public List<Reward> getNextStepRewards(AccountInfo account) {
		List<Reward> list = Lists.newArrayList();
		List<Reward> available = getAvailableRewards(account);
		for (Reward reward : firstAimable){
			if(!account.receivedReward.contains(reward.getName()) && !available.contains(reward)){
				list.add(reward);
			}
		}
		for (Reward reward : rewardList){
			Reward next = reward.getNextStepReward();
			if(next != null && !list.contains(next)){
				if(account.receivableReward.contains(reward.getName()) || account.receivedReward.contains(reward.getName())){
					if(!account.receivableReward.contains(next.getName()) && !account.receivedReward.contains(next.getName())){
						list.add(next);
					}
				}
			}
		}
		return list;
	}

	protected void updateAccountReward(Account account) {
		AccountInfo info = account.getInfo();
		for (Reward reward : rewardList){
			if(!account.receivableReward.contains(reward.getName()) && !account.receivedReward.contains(reward.getName())){
				if(reward.canGetReward(info)){
					account.receivableReward.add(reward.getName());
				}
			}
		}
	}

}
