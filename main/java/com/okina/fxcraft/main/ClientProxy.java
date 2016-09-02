package com.okina.fxcraft.main;

import static com.okina.fxcraft.main.FXCraft.*;

import java.io.File;
import java.util.Comparator;

import com.okina.fxcraft.client.model.ModelFXMask;
import com.okina.fxcraft.client.model.ModelJentleArmor;
import com.okina.fxcraft.client.particle.ParticleGun;
import com.okina.fxcraft.client.renderer.BlockAccountManagerRenderer;
import com.okina.fxcraft.network.SimpleTilePacket;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class ClientProxy extends CommonProxy {

	public static ModelJentleArmor modelJentlemensCap;
	public static ModelJentleArmor modelJentlemensPanz;
	public static ModelFXMask modelFXMask;

	@Override
	protected void loadConfiguration(File pfile) {
		super.loadConfiguration(pfile);
	}

	@Override
	protected void registerRenderer() {
		RenderingRegistry.registerBlockHandler(new BlockAccountManagerRenderer());
		modelJentlemensCap = new ModelJentleArmor(false);
		modelJentlemensPanz = new ModelJentleArmor(true);
		modelFXMask = new ModelFXMask();

		//		String libname;
		//		String[] library_names;
		//		switch (LWJGLUtil.getPlatform()) {
		//		case LWJGLUtil.PLATFORM_WINDOWS:
		//			libname = "OpenAL32";
		//			library_names = new String[] { "OpenAL64.dll", "OpenAL32.dll" };
		//			break;
		//		case LWJGLUtil.PLATFORM_LINUX:
		//			libname = "openal";
		//			library_names = new String[] { "libopenal64.so", "libopenal.so", "libopenal.so.0" };
		//			break;
		//		case LWJGLUtil.PLATFORM_MACOSX:
		//			libname = "openal";
		//			library_names = new String[] { "openal.dylib" };
		//			break;
		//		default:
		//			return;
		//		}
		//		String[] oalPaths = LWJGLUtil.getLibraryPaths(libname, library_names, AL.class.getClassLoader());
		//		LWJGLUtil.log("Found " + oalPaths.length + " OpenAL paths");
		//		for (String oalPath : oalPaths){
		//			System.out.println(oalPath);
		//		}
	}

	@Override
	public void sendPacketToServer(SimpleTilePacket packet) {
		packetDispatcher.sendToServer(packet);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void spawnParticle(World world, int id, double x, double y, double z, double vecX, double vecY, double vecZ) {
		try{
			switch (id) {
			case PARTICLE_GUN:
				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleGun(world, x, y, z, vecX, vecY, vecZ));
				break;
			}
		}catch (Exception e){
			System.err.println("Illegal parameter");
			e.printStackTrace();
		}
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




