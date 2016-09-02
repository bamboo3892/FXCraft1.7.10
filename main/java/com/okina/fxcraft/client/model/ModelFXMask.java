package com.okina.fxcraft.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelFXMask extends ModelBiped {

	private ModelRenderer mask;

	public ModelFXMask() {
		bipedHead.showModel = true;
		bipedHeadwear.showModel = false;
		bipedBody.showModel = false;
		bipedRightArm.showModel = false;
		bipedLeftArm.showModel = false;
		bipedRightLeg.showModel = false;
		bipedLeftLeg.showModel = false;

		textureWidth = 32;
		textureHeight = 16;

		mask = new ModelRenderer(this, 0, 0);
		mask.addBox(0F, 0F, 0F, 8, 8, 8);
		mask.setRotationPoint(-4F, -8F, -4.01F);
		mask.setTextureSize(32, 16);
		mask.mirror = true;
		setRotation(mask, 0F, 0F, 0F);
		bipedHead.addChild(mask);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}

}
