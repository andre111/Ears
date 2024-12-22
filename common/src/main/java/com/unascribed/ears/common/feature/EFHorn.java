package com.unascribed.ears.common.feature;

import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsRenderer.Pass;
import com.unascribed.ears.common.render.EarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.render.EarsRenderDelegate.QuadGrow;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexFlip;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexRotation;

public class EFHorn extends AbstractEarsFeature {
	public EFHorn() {
		super(Pass.BASE, false, EarsFeatureType.HORN);
	}

	@Override
	public void render(EarsFeatures features, EarsRenderDelegate delegate, Pass pass, boolean drawingEmissive) {
		if(!features.horn) return;

		delegate.push();
			delegate.anchorTo(BodyPart.HEAD);
			delegate.translate(0, -8, 0);
			delegate.rotate(25, 1, 0, 0);
			delegate.translate(0, -8, 0);
			delegate.renderDoubleSided(56, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
		delegate.pop();
	}
}