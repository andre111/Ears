package com.unascribed.ears.common.config;

import com.unascribed.ears.api.features.EarsFeatures;

@SuppressWarnings("deprecation")
public final class EFCEnum<E extends Enum<E>> extends EFC<E> {
	private final E[] values;
	
	public EFCEnum(String name, E[] values) {
		super("", name);
		
		this.values = values;
	}
	
	public E[] getValues() {
		return values;
	}

	@Override
	public E get(EarsFeatures features) {
		return features.getEnum(values[0]);
	}

	@Override
	public EarsFeatures with(EarsFeatures features, E value) {
		return EarsFeatures.builder(features).setEnum(value).build();
	}
}
