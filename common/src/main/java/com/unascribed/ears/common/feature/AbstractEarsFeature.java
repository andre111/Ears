package com.unascribed.ears.common.feature;

import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsRenderer;
import com.unascribed.ears.common.EarsRenderer.Pass;
import com.unascribed.ears.common.config.EarsFeatureConfig;
import com.unascribed.ears.common.image.WritableEarsImage;
import com.unascribed.ears.common.render.EarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexFlip;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexRotation;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexSource;

public abstract class AbstractEarsFeature implements EarsFeature {
	private final Pass pass;
	private final boolean emissiveOnly;
	private final EarsFeatureType type;
	private final EarsFeatureConfig config;
	
	public AbstractEarsFeature(Pass pass, boolean emissiveOnly) {
		this.pass = pass;
		this.emissiveOnly = emissiveOnly;
		this.type = null;
		this.config = null;
	}
	public AbstractEarsFeature(Pass pass, boolean emissiveOnly, EarsFeatureType type, EarsFeatureConfig.Builder config) {
		this(pass, emissiveOnly, type, config.create());
	}
	public AbstractEarsFeature(Pass pass, boolean emissiveOnly, EarsFeatureType type, EarsFeatureConfig config) {
		this.pass = pass;
		this.emissiveOnly = emissiveOnly;
		this.type = type;
		this.config = config;
	}

	@Override
	public boolean shouldRender(EarsFeatures features, EarsRenderDelegate delegate, Pass pass, boolean drawingEmissive) {
		if(pass != this.pass) return false;
		if(this.emissiveOnly && !drawingEmissive) return false;
		if(this.type != null && EarsRenderer.isInhibited(delegate, this.type)) return false;
		return true;
	}

	@Override
	public EarsFeatureConfig getConfig() {
		return config;
	}
	
	protected static void addTemplateRect(WritableEarsImage image, int u, int v, int w, int h, int r, int g, int b) {
		int color = 0xFF000000 | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
		int darkColor = 0xFF000000 | (((r>>1) & 0xFF) << 16) | (((g>>1) & 0xFF) << 8) | ((b>>1) & 0xFF);
		for(int x=u; x<u+w; x++) {
			for(int y=v; y<v+h; y++) {
				if((image.getARGB(x, y) & 0xFF000000) == 0) {
					boolean edge = x == u || x == u+w-1 || y == v || y == v+h-1;
					image.setARGB(x, y, edge ? color : darkColor);
				}
			}
		}
	}
	
	protected static void addTemplateRect(WritableEarsImage image, int u, int v, int w, int h, int r, int g, int b, TexRotation rot, TexFlip flip, TexSource source) {
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip, source);
		
		int x1 = Integer.MAX_VALUE;
		int y1 = Integer.MAX_VALUE;
		int x2 = 0;
		int y2 = 0;
		for(float[] uvEntry : uv) {
			int x = (int) (uvEntry[0] * source.getWidth());
			int y = (int) (uvEntry[1] * source.getWidth());
			x1 = Math.min(x1, x);
			y1 = Math.min(y1, y);
			x2 = Math.max(x2, x);
			y2 = Math.max(y2, y);
		}
		
		addTemplateRect(image, x1, y1, x2-x1, y2-y1, r, g, b);
	}
}
