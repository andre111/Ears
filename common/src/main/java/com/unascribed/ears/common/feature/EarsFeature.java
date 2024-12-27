package com.unascribed.ears.common.feature;

import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsRenderer;
import com.unascribed.ears.common.config.EarsFeatureConfig;
import com.unascribed.ears.common.render.EarsRenderDelegate;
import com.unascribed.ears.common.render.EarsSkinImages;

public interface EarsFeature {
	public boolean shouldRender(EarsFeatures features, EarsRenderDelegate delegate, EarsRenderer.Pass pass, boolean drawingEmissive);
	public void render(EarsFeatures features, EarsRenderDelegate delegate, EarsRenderer.Pass pass, boolean drawingEmissive);
	public void addTemplate(EarsSkinImages<?> images, EarsFeatures features);
	public EarsFeatureConfig getConfig();
}
