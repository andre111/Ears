package com.unascribed.ears.common.config;

import com.unascribed.ears.api.features.EarsFeatures;

@SuppressWarnings("deprecation")
public final class EFCBoolean extends EarsFeatureConfig<Boolean> {
	public EFCBoolean(String name) {
		super(name);
	}

	@Override
	public Boolean get(EarsFeatures features) {
		return features.getBoolean(getName());
	}

	@Override
	public EarsFeatures with(EarsFeatures features, Boolean value) {
		return EarsFeatures.builder(features).setBoolean(getName(), value).build();
	}
}
