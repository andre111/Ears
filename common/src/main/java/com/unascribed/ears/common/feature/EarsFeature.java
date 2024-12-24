package com.unascribed.ears.common.feature;

import java.util.List;

import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsRenderer;
import com.unascribed.ears.common.config.EarsFeatureConfig;
import com.unascribed.ears.common.image.WritableEarsImage;
import com.unascribed.ears.common.render.EarsRenderDelegate;

public interface EarsFeature {
	public boolean shouldRender(EarsFeatures features, EarsRenderDelegate delegate, EarsRenderer.Pass pass, boolean drawingEmissive);
	public void render(EarsFeatures features, EarsRenderDelegate delegate, EarsRenderer.Pass pass, boolean drawingEmissive);
	public void addTemplate(WritableEarsImage image, EarsFeatures features);
	public List<EarsFeatureConfig<?>> getConfig();
}
