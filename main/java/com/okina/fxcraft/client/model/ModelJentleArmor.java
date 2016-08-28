package com.okina.fxcraft.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelJentleArmor extends ModelBiped {

	public boolean leg = false;
	ModelRenderer legShape;

	public ModelJentleArmor() {
		textureWidth = 64;
		textureHeight = 32;

		legShape = new ModelRenderer(this, 0, 16);
		legShape.addBox(0F, 0F, 0F, 8, 3, 4);
		legShape.setRotationPoint(-4F, 10F, -2F);
		legShape.setTextureSize(64, 32);
		legShape.mirror = true;
		setRotation(legShape, 0F, 0F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		if(leg) legShape.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}

}
