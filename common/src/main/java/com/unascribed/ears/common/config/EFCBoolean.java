package com.unascribed.ears.common.config;

import com.unascribed.ears.api.features.EarsFeatures;

@SuppressWarnings("deprecation")
public final class EFCBoolean extends EFC<Boolean> {
	public EFCBoolean(String key, String display) {
		super(key, display);
	}

	@Override
	public Boolean get(EarsFeatures features) {
		return features.getBoolean(getKey());
	}

	@Override
	public EarsFeatures with(EarsFeatures features, Boolean value) {
		return EarsFeatures.builder(features).setBoolean(getKey(), value).build();
	}
}
