package com.unascribed.ears.common.config;

import com.unascribed.ears.api.features.EarsFeatures;

@SuppressWarnings("deprecation")
public final class EFCInteger extends EarsFeatureConfig<Integer> {
	private final int min;
	private final int max;
	
	public EFCInteger(String name, int min, int max) {
		super(name);
		
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
		return features.getInteger(getName());
	}

	@Override
	public EarsFeatures with(EarsFeatures features, Integer value) {
		return EarsFeatures.builder(features).setInteger(getName(), value).build();
	}
}
