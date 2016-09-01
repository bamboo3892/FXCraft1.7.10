package com.okina.fxcraft.utils;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import com.okina.fxcraft.main.FXCraft;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

public final class RenderingHelper {

	public final static ResourceLocation CHAR_TEXTURE = new ResourceLocation(FXCraft.MODID + ":textures/gui/container/numbers.png");

	public static void drawMiniString(String str, int x, int y, int color) {
		str = str.toUpperCase();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(true);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(CHAR_TEXTURE);
		for (int i = 0; i < str.length(); i++){
			char c = str.charAt(i);
			switch (c) {
			case '0':
				drawTexturedModalRect(x + i * 4, y, 0, 0, 3, 6);
				break;
			case '1':
				drawTexturedModalRect(x + i * 4, y, 4, 0, 3, 6);
				break;
			case '2':
				drawTexturedModalRect(x + i * 4, y, 8, 0, 3, 6);
				break;
			case '3':
				drawTexturedModalRect(x + i * 4, y, 12, 0, 3, 6);
				break;
			case '4':
				drawTexturedModalRect(x + i * 4, y, 16, 0, 3, 6);
				break;
			case '5':
				drawTexturedModalRect(x + i * 4, y, 20, 0, 3, 6);
				break;
			case '6':
				drawTexturedModalRect(x + i * 4, y, 24, 0, 3, 6);
				break;
			case '7':
				drawTexturedModalRect(x + i * 4, y, 28, 0, 3, 6);
				break;
			case '8':
				drawTexturedModalRect(x + i * 4, y, 32, 0, 3, 6);
				break;
			case '9':
				drawTexturedModalRect(x + i * 4, y, 36, 0, 3, 6);
				break;
			case '.':
				drawTexturedModalRect(x + i * 4, y, 40, 0, 3, 6);
				break;
			case '/':
				drawTexturedModalRect(x + i * 4, y, 44, 0, 3, 6);
				break;
			case '_':
				drawTexturedModalRect(x + i * 4, y, 48, 0, 3, 6);
				break;
			case ':':
				drawTexturedModalRect(x + i * 4, y, 52, 0, 3, 6);
				break;

			case 'A':
				drawTexturedModalRect(x + i * 4, y, 0, 7, 3, 6);
				break;
			case 'B':
				drawTexturedModalRect(x + i * 4, y, 4, 7, 3, 6);
				break;
			case 'C':
				drawTexturedModalRect(x + i * 4, y, 8, 7, 3, 6);
				break;
			case 'D':
				drawTexturedModalRect(x + i * 4, y, 12, 7, 3, 6);
				break;
			case 'E':
				drawTexturedModalRect(x + i * 4, y, 16, 7, 3, 6);
				break;
			case 'F':
				drawTexturedModalRect(x + i * 4, y, 20, 7, 3, 6);
				break;
			case 'G':
				drawTexturedModalRect(x + i * 4, y, 24, 7, 3, 6);
				break;
			case 'H':
				drawTexturedModalRect(x + i * 4, y, 28, 7, 3, 6);
				break;
			case 'I':
				drawTexturedModalRect(x + i * 4, y, 32, 7, 3, 6);
				break;
			case 'J':
				drawTexturedModalRect(x + i * 4, y, 36, 7, 3, 6);
				break;
			case 'K':
				drawTexturedModalRect(x + i * 4, y, 40, 7, 3, 6);
				break;
			case 'L':
				drawTexturedModalRect(x + i * 4, y, 44, 7, 3, 6);
				break;
			case 'M':
				drawTexturedModalRect(x + i * 4, y, 48, 7, 3, 6);
				break;
			case 'N':
				drawTexturedModalRect(x + i * 4, y, 52, 7, 3, 6);
				break;
			case 'O':
				drawTexturedModalRect(x + i * 4, y, 56, 7, 3, 6);
				break;
			case 'P':
				drawTexturedModalRect(x + i * 4, y, 60, 7, 3, 6);
				break;
			case 'Q':
				drawTexturedModalRect(x + i * 4, y, 64, 7, 3, 6);
				break;
			case 'R':
				drawTexturedModalRect(x + i * 4, y, 68, 7, 3, 6);
				break;
			case 'S':
				drawTexturedModalRect(x + i * 4, y, 72, 7, 3, 6);
				break;
			case 'T':
				drawTexturedModalRect(x + i * 4, y, 76, 7, 3, 6);
				break;
			case 'U':
				drawTexturedModalRect(x + i * 4, y, 80, 7, 3, 6);
				break;
			case 'V':
				drawTexturedModalRect(x + i * 4, y, 84, 7, 3, 6);
				break;
			case 'W':
				drawTexturedModalRect(x + i * 4, y, 88, 7, 3, 6);
				break;
			case 'X':
				drawTexturedModalRect(x + i * 4, y, 92, 7, 3, 6);
				break;
			case 'Y':
				drawTexturedModalRect(x + i * 4, y, 96, 7, 3, 6);
				break;
			case 'Z':
				drawTexturedModalRect(x + i * 4, y, 100, 7, 3, 6);
				break;
			}
		}
		GL11.glPopAttrib();
	}

	public static void drawTexturedModalRect(int x, int y, int textureU, int textureV, int sizeX, int sizeY) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + sizeY, 0, (textureU + 0) * f, (textureV + sizeY) * f1);
		tessellator.addVertexWithUV(x + sizeX, y + sizeY, 0, (textureU + sizeX) * f, (textureV + sizeY) * f1);
		tessellator.addVertexWithUV(x + sizeX, y + 0, 0, (textureU + sizeX) * f, (textureV + 0) * f1);
		tessellator.addVertexWithUV(x + 0, y + 0, 0, (textureU + 0) * f, (textureV + 0) * f1);
		tessellator.draw();
	}

	public static void renderLaser(TileEntity host, double x, double y, double z, int length, ForgeDirection orientation, int red, int green, int blue, int alpha, float partialTicks) {
		Tessellator tessellator = Tessellator.instance;
		GL11.glPushMatrix();

		double xStart = x;
		double yStart = y;
		double zStart = z;

		if(orientation == ForgeDirection.DOWN){
			yStart -= length;
		}
		if(orientation == ForgeDirection.NORTH){
			GL11.glRotatef(270, 1, 0, 0);
			yStart = -z - 1;
			zStart = y;
		}else if(orientation == ForgeDirection.SOUTH){
			GL11.glRotatef(90, 1, 0, 0);
			yStart = z;
			zStart = -y - 1;
		}else if(orientation == ForgeDirection.EAST){
			GL11.glRotatef(270, 0, 0, 1);
			yStart = x;
			xStart = -y - 1;
		}else if(orientation == ForgeDirection.WEST){
			GL11.glRotatef(90, 0, 0, 1);
			yStart = -x - 1;
			xStart = y;
		}
		yStart = yStart + 7.0 / 16.0D;

		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		RenderHelper.disableStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		double height = length + 2.0 / 16.0D;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthMask(false);
		tessellator.startDrawingQuads();
		if(alpha < 26) alpha = 26;
		tessellator.setColorRGBA(red, green, blue, alpha);
		double d18 = 7.0 / 16.0D;
		double d19 = 7.0 / 16.0D;
		double d20 = 9.0 / 16.0D;
		double d21 = 7.0 / 16.0D;
		double d22 = 7.0 / 16.0D;
		double d23 = 9.0 / 16.0D;
		double d24 = 9.0 / 16.0D;
		double d25 = 9.0 / 16.0D;

		tessellator.addVertex(xStart + d18, yStart + height, zStart + d19);
		tessellator.addVertex(xStart + d18, yStart, zStart + d19);
		tessellator.addVertex(xStart + d20, yStart, zStart + d21);
		tessellator.addVertex(xStart + d20, yStart + height, zStart + d21);
		tessellator.addVertex(xStart + d24, yStart + height, zStart + d25);
		tessellator.addVertex(xStart + d24, yStart, zStart + d25);
		tessellator.addVertex(xStart + d22, yStart, zStart + d23);
		tessellator.addVertex(xStart + d22, yStart + height, zStart + d23);
		tessellator.addVertex(xStart + d20, yStart + height, zStart + d21);
		tessellator.addVertex(xStart + d20, yStart, zStart + d21);
		tessellator.addVertex(xStart + d24, yStart, zStart + d25);
		tessellator.addVertex(xStart + d24, yStart + height, zStart + d25);
		tessellator.addVertex(xStart + d22, yStart + height, zStart + d23);
		tessellator.addVertex(xStart + d22, yStart, zStart + d23);
		tessellator.addVertex(xStart + d18, yStart, zStart + d19);
		tessellator.addVertex(xStart + d18, yStart + height, zStart + d19);

		tessellator.draw();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(true);
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	public static void renderCubeFrame(int x, int y, int z, Block block, double startX, double startY, double startZ, double sizeX, double sizeY, double sizeZ, double thickness, RenderBlocks renderer) {
		renderer.setRenderBounds(startX, startY, startZ, startX + thickness, startY + sizeY, startZ + thickness);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(startX + sizeX - thickness, startY, startZ, startX + sizeX, startY + sizeY, startZ + thickness);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(startX, startY, startZ + sizeZ - thickness, startX + thickness, startY + sizeY, startZ + sizeZ);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(startX + sizeX - thickness, startY, startZ + sizeZ - thickness, startX + sizeX, startY + sizeY, startZ + sizeZ);
		renderer.renderStandardBlock(block, x, y, z);

		renderer.setRenderBounds(startX + thickness, startY, startZ, startX + sizeX - thickness, startY + thickness, startZ + thickness);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(startX, startY, startZ + thickness, startX + thickness, startY + thickness, startZ + sizeZ - thickness);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(startX + sizeX - thickness, startY, startZ + thickness, startX + sizeX, startY + thickness, startZ + sizeZ - thickness);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(startX + thickness, startY, startZ + sizeZ - thickness, startX + sizeX - thickness, startY + thickness, startZ + sizeZ);
		renderer.renderStandardBlock(block, x, y, z);

		renderer.setRenderBounds(startX + thickness, startY + sizeY - thickness, startZ, startX + sizeX - thickness, startY + sizeY, startZ + thickness);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(startX, startY + sizeY - thickness, startZ + thickness, startX + thickness, startY + sizeY, startZ + sizeZ - thickness);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(startX + sizeX - thickness, startY + sizeY - thickness, startZ + thickness, startX + sizeX, startY + sizeY, startZ + sizeZ - thickness);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(startX + thickness, startY + sizeY - thickness, startZ + sizeZ - thickness, startX + sizeX - thickness, startY + sizeY, startZ + sizeZ);
		renderer.renderStandardBlock(block, x, y, z);
	}

	public static void renderConnectedCubeFrame(boolean[] connection, int x, int y, int z, Block block, double thickness, RenderBlocks renderer) {
		if(connection == null || connection.length != 6) return;

		for (int j1 = 0; j1 < 2; j1++){
			for (int j2 = 0; j2 < 2; j2++){
				for (int j3 = 0; j3 < 2; j3++){
					double x1 = j1 == 0 ? 0 : 1 - thickness;
					double x2 = j1 == 0 ? thickness : 1;
					double y1 = j2 == 0 ? 0 : 1 - thickness;
					double y2 = j2 == 0 ? thickness : 1;
					double z1 = j3 == 0 ? 0 : 1 - thickness;
					double z2 = j3 == 0 ? thickness : 1;

					renderer.setRenderBounds(x1, y1, z1, x2, y2, z2);
					renderer.renderStandardBlock(block, x, y, z);
				}
			}
		}

		double[] c1 = new double[3];
		double[] c2 = new double[3];
		int[] i = new int[3];

		for (i[0] = -1; i[0] < 2; i[0]++){
			for (i[1] = -1; i[1] < 2; i[1]++){
				if(i[0] == 0 && i[1] == 0) continue;
				for (i[2] = -1; i[2] < 2; i[2]++){
					if(i[2] == 0 && (i[0] == 0 || i[1] == 0)) continue;
					for (int j = 0; j < 3; j++){
						if(i[j] == -1){
							c1[j] = 0;
							c2[j] = thickness;
						}else if(i[j] == 0){
							c1[j] = thickness;
							c2[j] = 1 - thickness;
						}else{
							c1[j] = 1 - thickness;
							c2[j] = 1;
						}
					}
					int side1 = -1;
					int side2 = -1;
					if(i[0] != 0){
						side1 = getDirection(i[0], 0, 0);
					}
					if(i[1] != 0){
						if(side1 == -1){
							side1 = getDirection(0, i[1], 0);
						}else{
							side2 = getDirection(0, i[1], 0);
						}
					}
					if(i[2] != 0){
						side2 = getDirection(0, 0, i[2]);
					}
					if(!(connection[side1] || connection[side2])){
						renderer.setRenderBounds(c1[0], c1[1], c1[2], c2[0], c2[1], c2[2]);
						renderer.renderStandardBlock(block, x, y, z);
					}
				}
			}
		}
	}

	private static int getDirection(int offsetX, int offsetY, int offsetZ) {
		for (int i = 0; i < 6; i++){
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if(dir.offsetX == offsetX && dir.offsetY == offsetY && dir.offsetZ == offsetZ){
				return dir.ordinal();
			}
		}
		return 6;
	}

	public static void renderInvCubeFrame(RenderBlocks renderer, Block block, float startX, float startY, float startZ, float sizeX, float sizeY, float sizeZ, float thickness) {
		renderInvCuboid(renderer, block, startX, startY, startZ, startX + thickness, startY + sizeY, startZ + thickness, 0);
		renderInvCuboid(renderer, block, startX + sizeX - thickness, startY, startZ, startX + sizeX, startY + sizeY, startZ + thickness, 0);
		renderInvCuboid(renderer, block, startX, startY, startZ + sizeZ - thickness, startX + thickness, startY + sizeY, startZ + sizeZ, 0);
		renderInvCuboid(renderer, block, startX + sizeX - thickness, startY, startZ + sizeZ - thickness, startX + sizeX, startY + sizeY, startZ + sizeZ, 0);
		renderInvCuboid(renderer, block, startX + thickness, startY, startZ, startX + sizeX - thickness, startY + thickness, startZ + thickness, 0);
		renderInvCuboid(renderer, block, startX, startY, startZ + thickness, startX + thickness, startY + thickness, startZ + sizeZ - thickness, 0);
		renderInvCuboid(renderer, block, startX + sizeX - thickness, startY, startZ + thickness, startX + sizeX, startY + thickness, startZ + sizeZ - thickness, 0);
		renderInvCuboid(renderer, block, startX + thickness, startY, startZ + sizeZ - thickness, startX + sizeX - thickness, startY + thickness, startZ + sizeZ, 0);
		renderInvCuboid(renderer, block, startX + thickness, startY + sizeY - thickness, startZ, startX + sizeX - thickness, startY + sizeY, startZ + thickness, 0);
		renderInvCuboid(renderer, block, startX, startY + sizeY - thickness, startZ + thickness, startX + thickness, startY + sizeY, startZ + sizeZ - thickness, 0);
		renderInvCuboid(renderer, block, startX + sizeX - thickness, startY + sizeY - thickness, startZ + thickness, startX + sizeX, startY + sizeY, startZ + sizeZ - thickness, 0);
		renderInvCuboid(renderer, block, startX + thickness, startY + sizeY - thickness, startZ + sizeZ - thickness, startX + sizeX - thickness, startY + sizeY, startZ + sizeZ, 0);
	}

	public static void renderInvCuboid(RenderBlocks renderer, Block block, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		renderInvCuboid(renderer, block, minX, minY, minZ, maxX, maxY, maxZ, 0);
	}

	public static void renderInvCuboid(RenderBlocks renderer, Block block, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, IIcon icon) {
		Tessellator tessellator = Tessellator.instance;
		renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		block.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void renderInvCuboid(RenderBlocks renderer, Block block, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int meta) {
		Tessellator tessellator = Tessellator.instance;
		renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, meta));
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void renderWorldTileCube(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		for (int i = 0; i < 6; i++){
			renderWorldTileCube(minX, minY, minZ, maxX, maxY, maxZ, i);
		}
	}

	public static void renderWorldTileCube(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int side) {
		if(side >= 0 && side < 6){
			Tessellator tessellator = Tessellator.instance;
			if(side == 0){
				//y neg
				tessellator.addVertexWithUV(minX, minY, minZ, minX, minZ);
				tessellator.addVertexWithUV(maxX, minY, minZ, minX, maxZ);
				tessellator.addVertexWithUV(maxX, minY, maxZ, maxX, maxZ);
				tessellator.addVertexWithUV(minX, minY, maxZ, maxX, minZ);
			}else if(side == 1){
				//y pos
				tessellator.addVertexWithUV(minX, maxY, minZ, minX, minZ);
				tessellator.addVertexWithUV(minX, maxY, maxZ, minX, maxZ);
				tessellator.addVertexWithUV(maxX, maxY, maxZ, maxX, maxZ);
				tessellator.addVertexWithUV(maxX, maxY, minZ, maxX, minZ);
			}else if(side == 4){
				//z neg
				tessellator.addVertexWithUV(minX, minY, minZ, minX, minX);
				tessellator.addVertexWithUV(minX, maxY, minZ, maxX, minX);
				tessellator.addVertexWithUV(maxX, maxY, minZ, maxX, maxY);
				tessellator.addVertexWithUV(maxX, minY, minZ, minX, maxY);
			}else if(side == 5){
				//z pos
				tessellator.addVertexWithUV(minX, minY, maxZ, minX, minY);
				tessellator.addVertexWithUV(maxX, minY, maxZ, maxX, minY);
				tessellator.addVertexWithUV(maxX, maxY, maxZ, maxX, maxY);
				tessellator.addVertexWithUV(minX, maxY, maxZ, minX, maxY);
			}else if(side == 2){
				//x neg
				tessellator.addVertexWithUV(minX, minY, minZ, minY, minZ);
				tessellator.addVertexWithUV(minX, minY, maxZ, maxY, minZ);
				tessellator.addVertexWithUV(minX, maxY, maxZ, maxY, maxZ);
				tessellator.addVertexWithUV(minX, maxY, minZ, minY, maxZ);
			}else if(side == 3){
				///x pos
				tessellator.addVertexWithUV(maxX, minY, minZ, minY, minZ);
				tessellator.addVertexWithUV(maxX, maxY, minZ, maxY, minZ);
				tessellator.addVertexWithUV(maxX, maxY, maxZ, maxY, maxZ);
				tessellator.addVertexWithUV(maxX, minY, maxZ, minY, maxZ);
			}
		}
	}

	public static void renderHUDCenter(Minecraft mc, List<ColoredString> list) {
		if(list != null && !list.isEmpty()){
			ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
			Point center = new Point(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2);
			int size = list.size();
			for (int i = 0; i < list.size(); i++){
				ColoredString str = list.get(i);
				if(str != null && !str.isEmpty()){
					int length = mc.fontRenderer.getStringWidth(str.str);
					GL11.glPushMatrix();
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glTranslatef(center.getX(), center.getY(), 0);
					GL11.glTranslatef(-length / 2, 20 + i * 10, 0);
					mc.fontRenderer.drawString(str.str, 0, 0, str.color, true);
					GL11.glPopMatrix();
				}
			}
		}
	}

	public static void renderHUDRight(Minecraft mc, List<ColoredString> list) {
		if(list != null && !list.isEmpty()){
			ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
			Point right = new Point(sr.getScaledWidth(), sr.getScaledHeight() / 2);
			int size = list.size();
			for (int i = 0; i < list.size(); i++){
				ColoredString str = list.get(i);
				if(str != null && !str.isEmpty()){
					int length = mc.fontRenderer.getStringWidth(str.str);
					GL11.glPushMatrix();
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glTranslatef(right.getX(), right.getY(), 0);
					GL11.glTranslatef(-length - 5, -size * 10 / 2 + i * 10, 0);
					mc.fontRenderer.drawString(str.str, 0, 0, str.color, true);
					GL11.glPopMatrix();
				}
			}
		}
	}

}




