package com.unascribed.ears.common.feature;

import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsRenderer.Pass;
import com.unascribed.ears.common.config.EarsFeatureConfig;
import com.unascribed.ears.common.render.EarsRenderDelegate;
import com.unascribed.ears.common.render.EarsSkinImages;

public class EFToggles implements EarsFeature {
	private static final EarsFeatureConfig CONFIG = EarsFeatureConfig.of("Settings").bool("capeEnabled", "Cape Visible").bool("emissive", "Emissive Colors").create();

	@Override
	public boolean shouldRender(EarsFeatures features, EarsRenderDelegate delegate, Pass pass, boolean drawingEmissive) {
		return false;
	}

	@Override
	public void render(EarsFeatures features, EarsRenderDelegate delegate, Pass pass, boolean drawingEmissive) {
	}

	@Override
	public EarsFeatureConfig getConfig() {
		return CONFIG;
	}

	@Override
	public void addTemplate(EarsSkinImages<?> images, EarsFeatures features) {
	}
}
