package com.unascribed.ears.common.config;

import com.unascribed.ears.api.features.EarsFeatures;

public sealed abstract class EFC<T> permits EFCBoolean, EFCEnum, EFCFloat, EFCInteger {
	private final String key;
	private final String display;
	
	public EFC(String key, String display) {
		this.key = key;
		this.display = display;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getDisplay() {
		return display;
	}
	
	public abstract T get(EarsFeatures features);
	public abstract EarsFeatures with(EarsFeatures features, T value);
}
