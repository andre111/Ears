package com.unascribed.ears;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.EarsFeaturesStorage;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexSource;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public class EarsMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		if (EarsLog.DEBUG) {
			String ver;
			try {
				ver = SharedConstants.getGameVersion().getName();
			} catch (NoSuchMethodError e) {
				try {
					ver = (String)SharedConstants.class.getDeclaredFields()[3].get(null);
				} catch (Throwable t) {
					ver = "1.21.4?";
				}
			}
			EarsLog.debugva(EarsLog.Tag.PLATFORM, "Initialized - Minecraft {} / Fabric {}; Env={}",
					ver,
					FabricLoader.getInstance().getModContainer("fabricloader").get().getMetadata().getVersion().getFriendlyString(),
					FabricLoader.getInstance().getEnvironmentType());
		}
	}
	
	public static EarsFeatures overrideFeatures = null;
	public static Map<TexSource, Identifier> overrides = new HashMap<>();

	public static EarsFeatures getEarsFeatures(PlayerEntityRenderState peer) {
		if(overrideFeatures != null) return overrideFeatures;
		
		Identifier skin = peer.skinTextures.texture();
		AbstractTexture tex = MinecraftClient.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "getEarsFeatures(): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder holder) {
			@SuppressWarnings("resource")
			UUID uuid = MinecraftClient.getInstance().world.getEntityById(peer.id).getUuid();
			EarsFeatures feat = holder.getEarsFeatures();
			EarsFeaturesStorage.INSTANCE.put(peer.name, uuid, feat);
			
			if (!peer.invisible) {
				return feat;
			}
		}
		return EarsFeatures.DISABLED;
	}
	
	@SuppressWarnings("resource")
	public static NativeImage getSkinImage() {
		Identifier skin = MinecraftClient.getInstance().player.getSkinTextures().texture();
		AbstractTexture tex = MinecraftClient.getInstance().getTextureManager().getTexture(skin);
		if(tex instanceof NativeImageBackedTexture imgTex) {
			return imgTex.getImage();
		}
		return null;
	}
	
	@SuppressWarnings("resource")
	public static NativeImage getWingImage() {
		Identifier skin = MinecraftClient.getInstance().player.getSkinTextures().texture();
		Identifier id = Identifier.tryParse(skin.getNamespace(), TexSource.WING.addSuffix(skin.getPath()));
		AbstractTexture tex = MinecraftClient.getInstance().getTextureManager().getTexture(id);
		if(tex instanceof NativeImageBackedTexture imgTex) {
			return imgTex.getImage();
		}
		return null;
	}
	
	@SuppressWarnings("resource")
	public static File getBaseDir() {
		return MinecraftClient.getInstance().runDirectory;
	}
}
