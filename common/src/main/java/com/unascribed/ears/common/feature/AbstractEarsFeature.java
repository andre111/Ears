package com.unascribed.ears.common.feature;

import java.util.List;

import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsRenderer;
import com.unascribed.ears.common.EarsRenderer.Pass;
import com.unascribed.ears.common.config.EarsFeatureConfig;
import com.unascribed.ears.common.render.EarsRenderDelegate;

public abstract class AbstractEarsFeature implements EarsFeature {
	private final Pass pass;
	private final boolean emissiveOnly;
	private final EarsFeatureType type;
	private final List<EarsFeatureConfig<?>> config;
	
	public AbstractEarsFeature(Pass pass, boolean emissiveOnly, EarsFeatureType type, List<EarsFeatureConfig<?>> config) {
		this.pass = pass;
		this.emissiveOnly = emissiveOnly;
		this.type = type;
		this.config = config == null ? List.of() : config;
	}

	@Override
	public boolean shouldRender(EarsFeatures features, EarsRenderDelegate delegate, Pass pass, boolean drawingEmissive) {
		if(pass != this.pass) return false;
		if(this.emissiveOnly && !drawingEmissive) return false;
		if(this.type != null && EarsRenderer.isInhibited(delegate, this.type)) return false;
		return true;
	}

	@Override
	public List<EarsFeatureConfig<?>> getConfig() {
		return config;
	}
}
