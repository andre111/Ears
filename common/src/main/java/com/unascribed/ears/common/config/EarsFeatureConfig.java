package com.unascribed.ears.common.config;

import com.unascribed.ears.api.features.EarsFeatures;

public sealed abstract class EarsFeatureConfig<T> permits EFCBoolean, EFCEnum, EFCFloat, EFCInteger {
	private final String name;
	
	public EarsFeatureConfig(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract T get(EarsFeatures features);
	public abstract EarsFeatures with(EarsFeatures features, T value);
}
