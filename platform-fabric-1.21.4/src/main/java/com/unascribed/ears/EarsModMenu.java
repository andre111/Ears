package com.unascribed.ears;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.config.EarsConfigScreen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.session.Session;
import net.minecraft.util.Util;

public class EarsModMenu implements ModMenuApi {

	@SuppressWarnings("resource")
	@Override
	public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
		return screen -> {
			if(MinecraftClient.getInstance().player != null) {
				return new EarsConfigScreen(screen);
			} else {
				Session s = MinecraftClient.getInstance().getSession();
				return new ConfirmLinkScreen(
					clicked -> {
						if (clicked) {
							Util.getOperatingSystem().open(EarsCommon.getConfigUrl(s.getUsername(), s.getUuidOrNull().toString()));
						}
						MinecraftClient.getInstance().setScreen(screen);
					},
					EarsCommon.getConfigPreviewUrl(), true) {
						@Override
						public void copyToClipboard() {
							client.keyboard.setClipboard(EarsCommon.getConfigUrl(s.getUsername(), s.getUuidOrNull().toString()));
						}
					};
			}
		};
	}

}
