package com.unascribed.ears.common.config;

import com.unascribed.ears.api.features.EarsFeatures;

@SuppressWarnings("deprecation")
public final class EFCInteger extends EFC<Integer> {
	private final int min;
	private final int max;
	
	public EFCInteger(String key, String display, int min, int max) {
		super(key, display);
		
		this.min = min;
		this.max = max;
	}
	
	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}
	
	@Override
	public Integer get(EarsFeatures features) {
		return features.getInteger(getKey());
	}

	@Override
	public EarsFeatures with(EarsFeatures features, Integer value) {
		return EarsFeatures.builder(features).setInteger(getKey(), value).build();
	}
}
