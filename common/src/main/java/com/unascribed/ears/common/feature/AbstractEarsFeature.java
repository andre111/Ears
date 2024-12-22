package com.unascribed.ears.common.feature;

import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsRenderer;
import com.unascribed.ears.common.EarsRenderer.Pass;
import com.unascribed.ears.common.render.EarsRenderDelegate;

public abstract class AbstractEarsFeature implements EarsFeature {
	private final Pass pass;
	private final boolean emissiveOnly;
	private final EarsFeatureType type;
	
	public AbstractEarsFeature(Pass pass, boolean emissiveOnly, EarsFeatureType type) {
		this.pass = pass;
		this.emissiveOnly = emissiveOnly;
		this.type = type;
	}

	@Override
	public boolean shouldRender(EarsFeatures features, EarsRenderDelegate delegate, Pass pass, boolean drawingEmissive) {
		if(pass != this.pass) return false;
		if(this.emissiveOnly && !drawingEmissive) return false;
		if(this.type != null && EarsRenderer.isInhibited(delegate, this.type)) return false;
		return true;
	}
}
