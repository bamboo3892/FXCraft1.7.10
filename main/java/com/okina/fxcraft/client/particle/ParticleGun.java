package com.okina.fxcraft.client.particle;

import net.minecraft.world.World;

public class ParticleGun extends ParticleLiner {

	public ParticleGun(World world, double startX, double startY, double startZ, double endX, double endY, double endZ) {
		super(world, startX, startY, startZ, endX, endY, endZ);
		float f = rand.nextFloat() * 0.6F + 0.4F;
		particleRed = particleGreen = particleBlue = 1.0F * f;
		particleRed *= 0.5F;
		particleBlue *= 0.5F;
		particleMaxAge = (int) (Math.random() * 2.0D) + 8;
	}

	@Override
	protected void updateScale(float ageScaled) {
		ageScaled = 1 - ageScaled;
		particleScale = baseScale * ageScaled;
	}

}
