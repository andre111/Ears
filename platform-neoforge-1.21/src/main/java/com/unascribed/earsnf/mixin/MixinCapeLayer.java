package com.unascribed.earsnf.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.api.registry.EarsInhibitorRegistry;
import com.unascribed.earsnf.EarsLayerRenderer;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CapeLayer;

@Mixin(CapeLayer.class)
public class MixinCapeLayer {
	@Inject(at=@At("HEAD"), method="render", cancellable=true)
	public void render(PoseStack p_116615_, MultiBufferSource p_116616_, int p_116617_, AbstractClientPlayer player, float p_116619_, float p_116620_, float p_116621_, float p_116622_, float p_116623_, float p_116624_, CallbackInfo ci) {
		EarsFeatures features = EarsLayerRenderer.getEarsFeatures(player);
		if (features != null && (!features.capeEnabled || EarsInhibitorRegistry.isInhibited(EarsFeatureType.CAPE, player) != null)) {
			ci.cancel();
		}
	}
}
