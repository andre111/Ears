package com.unascribed.ears.common;

import java.util.ArrayList;
import java.util.List;

import com.unascribed.ears.Identified;
import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.EarsStateType;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.api.registry.EarsInhibitorRegistry;
import com.unascribed.ears.api.registry.EarsStateOverriderRegistry;
import com.unascribed.ears.common.debug.DebuggingDelegate;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.feature.EFChest;
import com.unascribed.ears.common.feature.EFClaws;
import com.unascribed.ears.common.feature.EFEars;
import com.unascribed.ears.common.feature.EFEmissiveSkin;
import com.unascribed.ears.common.feature.EFEmissiveSkinOverlay;
import com.unascribed.ears.common.feature.EFHorn;
import com.unascribed.ears.common.feature.EFSnout;
import com.unascribed.ears.common.feature.EFTail;
import com.unascribed.ears.common.feature.EFWings;
import com.unascribed.ears.common.feature.EarsFeature;
import com.unascribed.ears.common.render.EarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.render.EarsRenderDelegate.QuadGrow;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexFlip;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexRotation;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexSource;

import com.unascribed.ears.common.render.IndirectEarsRenderDelegate;

public class EarsRenderer {
	private static final List<EarsFeature> FEATURES = new ArrayList<>();
	static {
		FEATURES.add(new EFEmissiveSkin());
		FEATURES.add(new EFEmissiveSkinOverlay());
		FEATURES.add(new EFEars());
		FEATURES.add(new EFTail());
		FEATURES.add(new EFClaws());
		FEATURES.add(new EFHorn());
		FEATURES.add(new EFSnout());
		FEATURES.add(new EFChest());
		FEATURES.add(new EFWings());
	}
	
	/**
	 * Render all the features described in {@code features} using {@code delegate}.
	 */
	public static void render(EarsFeatures features, EarsRenderDelegate delegate) {
		EarsLog.debug(EarsLog.Tag.COMMON_RENDERER, "render({}, {})", features, delegate);
		boolean slim = delegate.isSlim();
		
		if (EarsLog.DEBUG && EarsLog.shouldLog(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE)) {
			delegate = new DebuggingDelegate(delegate);
		}
	
		if (EarsLog.DEBUG && EarsLog.shouldLog(EarsLog.Tag.COMMON_RENDERER_DOTS)) {
			for (BodyPart part : BodyPart.values()) {
				delegate.push();
					delegate.anchorTo(part);
					delegate.renderDebugDot(1, 1, 1, 1);
					delegate.push();
						delegate.translate(part.getXSize(slim), 0, 0);
						delegate.renderDebugDot(1, 0, 0, 1);
					delegate.pop();
					delegate.push();
						delegate.translate(0, -part.getYSize(slim), 0);
						delegate.renderDebugDot(0, 1, 0, 1);
					delegate.pop();
					delegate.push();
						delegate.translate(0, 0, part.getZSize(slim));
						delegate.renderDebugDot(0, 0, 1, 1);
					delegate.pop();
				delegate.pop();
			}
		}
		
		if (features != null && features.enabled) {
			// the 1.15+ rendering pipeline introduces nasty transparency sort bugs due to the buffering it does
			// render in multiple passes to avoid it (first is base skin, second is overlay layer, third is for armor, fourth is for armor glint)
			delegate.setUp();
			for (int p = 0; p < 4; p++) {
				renderInner(features, delegate, p, false);
				if (features != null && features.emissive && p < 2) {
					delegate.setEmissive(true);
					renderInner(features, delegate, p, true);
					delegate.setEmissive(false);
				}
			}
			delegate.bind(TexSource.SKIN);
			delegate.tearDown();
		}
	}
		
	private static void renderInner(EarsFeatures features, EarsRenderDelegate delegate, int p, boolean drawingEmissive) {
		if (p == 1 && delegate instanceof IndirectEarsRenderDelegate indirect) {
			indirect.beginTranslucent();
		}
		delegate.bind(drawingEmissive ? TexSource.EMISSIVE_SKIN : TexSource.SKIN);
		
		for(EarsFeature feature : FEATURES) {
			if(feature.shouldRender(features, delegate, Pass.values()[p], drawingEmissive)) {
				feature.render(features, delegate, Pass.values()[p], drawingEmissive);
			}
		}
	}

	public static void drawVanillaCuboid(EarsRenderDelegate delegate, int u, int v, int w, int h, int d, float g) {
		float g2 = g*2;
		delegate.translate(w/2f, h/2f, d/2f);
		delegate.scale((w+g2)/w, (h+g2)/h, (d+g2)/d);
		delegate.translate(-w/2f, -h*1.5f, -d/2f);
		// front
		delegate.renderDoubleSided(u+d, v+d, w, h, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
		delegate.push();
			// left
			delegate.translate(w, 0, d);
			delegate.rotate(90, 0, 1, 0);
			delegate.renderDoubleSided(u+d+w, v+d, d, h, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.NONE);
			// back
			delegate.translate(0, 0, 0);
			delegate.rotate(90, 0, 1, 0);
			delegate.renderDoubleSided(u+d+w+d, v+d, w, h, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
			// right
			delegate.translate(w, 0, d);
			delegate.rotate(90, 0, 1, 0);
			delegate.renderDoubleSided(u, v+d, d, h, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.NONE);
		delegate.pop();
		
		// top
		delegate.rotate(90, 1, 0, 0);
		delegate.renderDoubleSided(u+d, v, w, d, TexRotation.NONE, TexFlip.VERTICAL, QuadGrow.NONE);
		
		// bottom
		delegate.translate(0, 0, -h);
		delegate.renderDoubleSided(u+d+w, v, w, d, TexRotation.NONE, TexFlip.VERTICAL, QuadGrow.NONE);
	}

	public static boolean isInhibited(EarsRenderDelegate delegate, EarsFeatureType feature) {
		String namespace = EarsInhibitorRegistry.isInhibited(feature, delegate.getPeer());
		if (namespace != null) {
			EarsLog.debug(EarsLog.Tag.COMMON_API, "Rendering of feature {} is being inhibited by {}", feature, namespace);
			return true;
		}
		return false;
	}

	public static boolean isActive(EarsRenderDelegate delegate, EarsStateType state) {
		boolean def = false;
		switch (state) {
			case CREATIVE_FLYING:
				def = delegate.isFlying();
				break;
			case GLIDING:
				def = delegate.isGliding();
				break;
			case WEARING_BOOTS:
				def = delegate.isWearingBoots();
				break;
			case WEARING_CHESTPLATE:
				def = delegate.isWearingChestplate();
				break;
			case WEARING_ELYTRA:
				def = delegate.isWearingElytra();
				break;
			case WEARING_HELMET:
				def = false;
				break;
			case WEARING_LEGGINGS:
				def = false;
				break;
			default:
				break;
		}
		Identified<Boolean> id = EarsStateOverriderRegistry.isActive(state, delegate.getPeer(), def);
		if (id.getNamespace() != null) {
			EarsLog.debug(EarsLog.Tag.COMMON_API, "State of {} is being overridden to {} from {} by {}", state, id.getValue(), def, id.getNamespace());
		}
		return id.getValue();
	}

	public static enum Pass {
		BASE,
		OVERLAY,
		ARMOR,
		ARMOR_GLINT;
	}
}
