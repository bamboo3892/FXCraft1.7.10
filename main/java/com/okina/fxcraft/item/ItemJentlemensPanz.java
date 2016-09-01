package com.okina.fxcraft.item;

import java.util.List;

import com.okina.fxcraft.client.IToolTipUser;
import com.okina.fxcraft.client.model.ModelJentleArmor;
import com.okina.fxcraft.main.ClientProxy;
import com.okina.fxcraft.main.FXCraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;

public class ItemJentlemensPanz extends ItemArmor implements IToolTipUser {

	public ItemJentlemensPanz(ArmorMaterial material, int renderId) {
		super(material, renderId, 2);
		setMaxStackSize(1);
		setCreativeTab(FXCraft.FXCraftCreativeTab);
		setTextureName(FXCraft.MODID + ":jentlemens_cap");
		setUnlocalizedName("fxcraft_jentlemens_panz");
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		if(!world.isRemote){
			if(player.isPotionActive(Potion.wither.id)){
				player.removePotionEffect(Potion.wither.id);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemstack, int armorSlot) {
		ModelJentleArmor armorModel = ClientProxy.modelJentlemensCap;
		if(armorModel != null){
			armorModel.bipedHead.showModel = false;
			armorModel.bipedHeadwear.showModel = false;
			armorModel.bipedBody.showModel = false;
			armorModel.bipedRightArm.showModel = false;
			armorModel.bipedLeftArm.showModel = false;
			armorModel.bipedRightLeg.showModel = true;
			armorModel.bipedLeftLeg.showModel = true;

			armorModel.isSneak = entityLiving.isSneaking();
			armorModel.isRiding = entityLiving.isRiding();
			armorModel.isChild = entityLiving.isChild();

			armorModel.heldItemRight = 0;
			armorModel.aimedBow = false;

			EntityPlayer player = (EntityPlayer) entityLiving;
			ItemStack held_item = player.getEquipmentInSlot(0);
			if(held_item != null){
				armorModel.heldItemRight = 1;
				if(player.getItemInUseCount() > 0){
					EnumAction enumaction = held_item.getItemUseAction();
					if(enumaction == EnumAction.bow){
						armorModel.aimedBow = true;
					}else if(enumaction == EnumAction.block){
						armorModel.heldItemRight = 3;
					}
				}
			}
		}
		return armorModel;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String layer) {
		return FXCraft.MODID + ":textures/models/armor/jentlemens_armor.png";
	}

	@Override
	public void addToolTip(List<String> toolTip, ItemStack itemStack, EntityPlayer player, boolean shiftPressed, boolean advancedToolTip) {
		toolTip.add("Cure Wither");
	}

	@Override
	public int getNeutralLines() {
		return 0;
	}

	@Override
	public int getShiftLines() {
		return 0;
	}

}
