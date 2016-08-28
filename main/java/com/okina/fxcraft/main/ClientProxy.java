package com.okina.fxcraft.main;

import static com.okina.fxcraft.main.FXCraft.*;

import java.io.File;
import java.util.Comparator;

import com.okina.fxcraft.client.model.ModelJentleArmor;
import com.okina.fxcraft.client.renderer.BlockAccountManagerRenderer;
import com.okina.fxcraft.network.SimpleTilePacket;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.world.World;

public class ClientProxy extends CommonProxy {

	public static ModelJentleArmor modelJentlemensCap;
	public static ModelJentleArmor modelJentlemensPanz;

	@Override
	protected void loadConfiguration(File pfile) {
		super.loadConfiguration(pfile);
	}

	@Override
	protected void registerRenderer() {
		RenderingRegistry.registerBlockHandler(new BlockAccountManagerRenderer());
		modelJentlemensCap = new ModelJentleArmor();
		modelJentlemensPanz = new ModelJentleArmor();
		modelJentlemensPanz.leg = true;
	}

	@Override
	public void sendPacketToServer(SimpleTilePacket packet) {
		packetDispatcher.sendToServer(packet);
	}

	//file io//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected void updatePropertyFile() {
		//		new Thread(new Runnable() {
		//			@Override
		//			public void run() {
		//				PrintWriter writer = null;
		//				try{
		//					Gson gson = new Gson();
		//					File file = new File(ConfigFile.getAbsolutePath() + File.separator + MODID + ".properties");
		//					writer = new PrintWriter(new FileWriter(file));
		//					String json = gson.toJson(effectProperties);
		//					//					json.replaceAll("~", "");
		//					//					json.replaceAll("{", "~\n");
		//					//					json.replaceAll("~", "{");
		//					//					json.replaceAll("}", "~\n");
		//					//					json.replaceAll("~", "}");
		//					//					json.replaceAll(",", "~\n");
		//					//					json.replaceAll("~", ",");
		//					writer.print(json);
		//				}catch (Exception e){
		//					e.printStackTrace();
		//				}finally{
		//					if(writer != null) writer.close();
		//				}
		//			}
		//		}, "Update Property Thread").start();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected void spawnParticle(World world, int id, Object... objects) {
		//		try{
		//			switch (id) {
		//			case TestCore.PARTICLE_GROWER:
		//				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleGrower(world, objects));
		//				break;
		//			case TestCore.PARTICLE_ENERGY:
		//				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleEnergyProvide(world, objects).set1());
		//				break;
		//			case TestCore.PARTICLE_BEZIER:
		//				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleBezierCurve(world, objects));
		//				break;
		//			case TestCore.PARTICLE_BEZIER_DOTS:
		//				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleBezierDots(world, objects));
		//				break;
		//			case TestCore.PARTICLE_DOT:
		//				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleDot(world, objects));
		//				break;
		//			case TestCore.PARTICLE_CRUCK:
		//				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleCruck(world, objects));
		//				break;
		//			case TestCore.PARTICLE_BEZIER_DOT:
		//				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleBezierDot(world, objects));
		//				break;
		//			case TestCore.PARTICLE_FLAME:
		//				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleDirectionalFlame(world, objects));
		//				break;
		//			case TestCore.PARTICLE_CUSTOM_ICON:
		//				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleCustomIcon(world, objects));
		//				break;
		//			case TestCore.PARTICLE_ALTER:
		//				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleAlter(world, objects));
		//				break;
		//			case TestCore.PARTICLE_ALTER_DOT:
		//				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleAlterDot(world, objects));
		//				break;
		//			}
		//		}catch (Exception e){
		//			System.err.println("Illegal parameter");
		//			e.printStackTrace();
		//		}
	}

	private static final Comparator comparator = new Comparator() {
		@Override
		public int compare(Object o1, Object o2) {
			PopUpMessage msg1 = (PopUpMessage) o1;
			PopUpMessage msg2 = (PopUpMessage) o2;
			return msg1.index - msg2.index;
		}
	};

	@Override
	public void appendPopUp(String message) {
		messageList.sort(comparator);
		if(messageList.isEmpty()){
			messageList.add(new PopUpMessage(message, 100, 0));
		}else{
			int checkNum = 0;
			boolean flag = false;
			for (int i = 0; i < messageList.size(); i++){
				if(checkNum < messageList.get(i).index){
					flag = true;
					break;
				}else{
					checkNum = messageList.get(i).index + 1;
				}
			}
			if(!flag){
				checkNum = messageList.get(messageList.size() - 1).index + 1;
			}
			messageList.add(new PopUpMessage(message, 100, checkNum));
		}
		//		System.out.println("aaaaaaaaaaaaaaaa");
	}

}




