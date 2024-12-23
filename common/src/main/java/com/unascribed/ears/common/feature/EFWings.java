package com.unascribed.ears.common.feature;

import java.util.List;

import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.EarsStateType;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.api.features.EarsFeatures.WingMode;
import com.unascribed.ears.common.EarsRenderer;
import com.unascribed.ears.common.EarsRenderer.Pass;
import com.unascribed.ears.common.config.EFCBoolean;
import com.unascribed.ears.common.config.EFCEnum;
import com.unascribed.ears.common.render.EarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.render.EarsRenderDelegate.QuadGrow;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexFlip;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexRotation;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexSource;

public class EFWings extends AbstractEarsFeature {
	public EFWings() {
		super(Pass.BASE, false, EarsFeatureType.WINGS, List.of(new EFCEnum<>("Wings", WingMode.values()), new EFCBoolean("animateWings")));
	}

	@Override
	public void render(EarsFeatures features, EarsRenderDelegate delegate, Pass pass, boolean drawingEmissive) {
		WingMode wingMode = features.wingMode;
		if(wingMode == WingMode.NONE) return;

		boolean g = EarsRenderer.isActive(delegate, EarsStateType.GLIDING);
		boolean f = EarsRenderer.isActive(delegate, EarsStateType.CREATIVE_FLYING);
		delegate.push();
			float wiggle;
			if (features.animateWings) {
				wiggle = g ? -40 : ((float)(Math.sin((delegate.getTime()+8)/(f ? 2 : 12))*(f ? 20 : 2)))+(delegate.getLimbSwing()*10);
			} else {
				wiggle = 0;
			}
			delegate.anchorTo(BodyPart.TORSO);
			delegate.bind(drawingEmissive ? TexSource.EMISSIVE_WING : TexSource.WING);
			delegate.translate(2, -14, 4);
			if (wingMode == WingMode.SYMMETRIC_DUAL || wingMode == WingMode.ASYMMETRIC_R) {
				delegate.push();
					delegate.rotate(-120+wiggle, 0, 1, 0);
					delegate.renderDoubleSided(0, 0, 20, 16, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.pop();
			}
			if (wingMode == WingMode.SYMMETRIC_DUAL || wingMode == WingMode.ASYMMETRIC_L) {
				delegate.translate(4, 0, 0);
				delegate.push();
					delegate.rotate(-60-wiggle, 0, 1, 0);
					delegate.renderDoubleSided(0, 0, 20, 16, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.pop();
			}
			if (wingMode == WingMode.SYMMETRIC_SINGLE) {
				delegate.translate(2, 0, 0);
				delegate.push();
					delegate.rotate(-90+wiggle, 0, 1, 0);
					delegate.renderDoubleSided(0, 0, 20, 16, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.pop();
			}
		delegate.pop();
	}
}
