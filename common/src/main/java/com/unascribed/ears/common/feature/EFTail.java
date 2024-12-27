package com.unascribed.ears.common.feature;

import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.api.features.EarsFeatures.TailMode;
import com.unascribed.ears.common.EarsRenderer.Pass;
import com.unascribed.ears.common.config.EarsFeatureConfig;
import com.unascribed.ears.common.render.EarsRenderDelegate;
import com.unascribed.ears.common.render.EarsSkinImages;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.render.EarsRenderDelegate.QuadGrow;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexFlip;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexRotation;

public class EFTail extends AbstractEarsFeature {
	public EFTail() {
		super(Pass.BASE, false, EarsFeatureType.TAIL, EarsFeatureConfig.of("Tail").enm("", TailMode.values()).integer("tailSegments", "Segments", 0, 4).flt("tailBend0", "First Bend", -90, 90).flt("tailBend1", "Second Bend", -90, 90).flt("tailBend2", "Third Bend", -90, 90).flt("tailBend3", "Fourth Bend", -90, 90));
	}

	@Override
	public void render(EarsFeatures features, EarsRenderDelegate delegate, Pass pass, boolean drawingEmissive) {
		TailMode tailMode = features.tailMode;
		if(tailMode == TailMode.NONE) return;

		float swingAmount = delegate.getLimbSwing();
		
		float ang = 0;
		float swing = 0;
		if (tailMode == TailMode.DOWN) {
			ang = 30;
			swing = 40;
		} else if (tailMode == TailMode.BACK) {
			if (features.tailBend0 != 0) {
				ang = 90;
			} else {
				ang = 80;
			}
			swing = 20;
		} else if (tailMode == TailMode.UP) {
			ang = 130;
			swing = -20;
		}
		float baseAngle = features.tailBend0;
		//if (EarsRenderer.isActive(delegate, EarsStateType.GLIDING)) {
		//	baseAngle = -30;
		//	ang = 0;
		//}
		delegate.push();
			delegate.anchorTo(BodyPart.TORSO);
			delegate.translate(0, -2, 4);
			delegate.rotate(ang+(swingAmount*swing)+(float)(Math.sin(delegate.getTime()/12)*4), 1, 0, 0);
			boolean vert = tailMode == TailMode.VERTICAL;
			if (vert) {
				delegate.translate(4, 0, 0);
				delegate.rotate(90, 0, 0, 1);
				if (baseAngle < 0) {
					delegate.translate(4, 0, 0);
					delegate.rotate(baseAngle, 0, 1, 0);
					delegate.translate(-4, 0, 0);
				}
				delegate.translate(-4, 0, 0);
				if (baseAngle > 0) {
					delegate.rotate(baseAngle, 0, 1, 0);
				}
				delegate.rotate(90, 1, 0, 0);
			}
			int segments = features.tailSegments;
			if (segments <= 0) segments = 1;
			float[] angles = {vert ? 0 : baseAngle, features.tailBend1, features.tailBend2, features.tailBend3};
			int segHeight = 12/segments;
			for (int i = 0; i < segments; i++) {
				delegate.rotate(angles[i]*(1-(swingAmount/2)), 1, 0, 0);
				delegate.renderDoubleSided(56, 16+(i*segHeight), 8, segHeight, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.NONE);
				delegate.translate(0, segHeight, 0);
			}
		delegate.pop();
	}

	@Override
	public void addTemplate(EarsSkinImages<?> images, EarsFeatures features) {
		TailMode tailMode = features.tailMode;
		if(tailMode == TailMode.NONE) return;
		
		addTemplateRect(images.skin(), 56, 16, 8, 12, 255, 0, 0);
	}
}
