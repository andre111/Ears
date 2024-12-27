package com.unascribed.ears.common.config;

import java.util.ArrayList;
import java.util.List;

public record EarsFeatureConfig(String name, List<EFC<?>> efcs) {
	public static Builder of(String name) {
		return new Builder(name);
	}
	
	public static final class Builder {
		private String name;
		private List<EFC<?>> efcs;
		
		private Builder(String name) {
			this.name = name;
			this.efcs = new ArrayList<>();
		}
		
		public Builder bool(String key, String display) {
			efcs.add(new EFCBoolean(key, display));
			return this;
		}
		
		public <E extends Enum<E>> Builder enm(String display, E[] values) {
			efcs.add(new EFCEnum<>(display, values));
			return this;
		}
		
		public Builder flt(String key, String display, float min, float max) {
			efcs.add(new EFCFloat(key, display, min, max));
			return this;
		}
		
		public Builder integer(String key, String display, int min, int max) {
			efcs.add(new EFCInteger(key, display, min, max));
			return this;
		}
		
		public EarsFeatureConfig create() {
			return new EarsFeatureConfig(name, List.copyOf(efcs));
		}
	}
}
