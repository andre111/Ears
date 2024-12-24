package com.unascribed.ears.common.feature;

import java.util.List;

import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsRenderer.Pass;
import com.unascribed.ears.common.config.EFCBoolean;
import com.unascribed.ears.common.config.EarsFeatureConfig;
import com.unascribed.ears.common.image.WritableEarsImage;
import com.unascribed.ears.common.render.EarsRenderDelegate;

public class EFToggles implements EarsFeature {
	private static final List<EarsFeatureConfig<?>> CONFIG = List.of(new EFCBoolean("capeEnabled"), new EFCBoolean("emissive"));

	@Override
	public boolean shouldRender(EarsFeatures features, EarsRenderDelegate delegate, Pass pass, boolean drawingEmissive) {
		return false;
	}

	@Override
	public void render(EarsFeatures features, EarsRenderDelegate delegate, Pass pass, boolean drawingEmissive) {
	}

	@Override
	public List<EarsFeatureConfig<?>> getConfig() {
		return CONFIG;
	}

	@Override
	public void addTemplate(WritableEarsImage image, EarsFeatures features) {
	}
}
