package com.okina.fxcraft.client.gui;

import java.util.List;

public interface ITipComponent {

	List<String> getTipList(int mouseX, int mouseY, boolean shift, boolean ctrl);

}
