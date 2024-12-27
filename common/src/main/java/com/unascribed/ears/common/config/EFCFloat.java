package com.unascribed.ears.common.config;

import com.unascribed.ears.api.features.EarsFeatures;

@SuppressWarnings("deprecation")
public final class EFCFloat extends EFC<Float> {
	private final float min;
	private final float max;
	
	public EFCFloat(String key, String display, float min, float max) {
		super(key, display);
		
		this.min = min;
		this.max = max;
	}
	
	public float getMin() {
		return min;
	}

	public float getMax() {
		return max;
	}
	
	@Override
	public Float get(EarsFeatures features) {
		return features.getFloat(getKey());
	}

	@Override
	public EarsFeatures with(EarsFeatures features, Float value) {
		return EarsFeatures.builder(features).setFloat(getKey(), value).build();
	}
}
