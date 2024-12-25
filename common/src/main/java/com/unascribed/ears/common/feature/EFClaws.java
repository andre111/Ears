package com.unascribed.ears.common.feature;

import java.util.List;

import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.EarsStateType;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsRenderer;
import com.unascribed.ears.common.EarsRenderer.Pass;
import com.unascribed.ears.common.config.EFCBoolean;
import com.unascribed.ears.common.render.EarsRenderDelegate;
import com.unascribed.ears.common.render.EarsSkinImages;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.render.EarsRenderDelegate.QuadGrow;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexFlip;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexRotation;

public class EFClaws extends AbstractEarsFeature {
	public EFClaws() {
		super(Pass.BASE, false, null, List.of(new EFCBoolean("claws")));
	}

	@Override
	public void render(EarsFeatures features, EarsRenderDelegate delegate, Pass pass, boolean drawingEmissive) {
		if(!features.claws) return;
		
		if (!EarsRenderer.isActive(delegate, EarsStateType.WEARING_BOOTS)) {
			if (!EarsRenderer.isInhibited(delegate, EarsFeatureType.CLAW_LEFT_LEG)) {
				delegate.push();
					delegate.anchorTo(BodyPart.LEFT_LEG);
					delegate.translate(0, 0, -4);
					delegate.rotate(90, 1, 0, 0);
					delegate.renderDoubleSided(16, 48, 4, 4, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.NONE);
				delegate.pop();
			}
			
			if (!EarsRenderer.isInhibited(delegate, EarsFeatureType.CLAW_RIGHT_LEG)) {
				delegate.push();
					delegate.anchorTo(BodyPart.RIGHT_LEG);
					delegate.translate(0, 0, -4);
					delegate.rotate(90, 1, 0, 0);
					delegate.renderDoubleSided(0, 16, 4, 4, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.NONE);
				delegate.pop();
			}
		}
		
		if (!EarsRenderer.isInhibited(delegate, EarsFeatureType.CLAW_LEFT_ARM)) {
			delegate.push();
				delegate.anchorTo(BodyPart.LEFT_ARM);
				delegate.rotate(90, 0, 1, 0);
				delegate.translate(-4, 0, delegate.isSlim() ? 3 : 4);
				delegate.renderDoubleSided(44, 48, 4, 4, TexRotation.UPSIDE_DOWN, TexFlip.HORIZONTAL, QuadGrow.NONE);
			delegate.pop();
		}
		
		if (!EarsRenderer.isInhibited(delegate, EarsFeatureType.CLAW_RIGHT_ARM)) {
			delegate.push();
				delegate.anchorTo(BodyPart.RIGHT_ARM);
				delegate.rotate(90, 0, 1, 0);
				delegate.translate(-4, 0, 0);
				delegate.renderDoubleSided(52, 16, 4, 4, TexRotation.UPSIDE_DOWN, TexFlip.NONE, QuadGrow.NONE);
			delegate.pop();
		}
	}

	@Override
	public void addTemplate(EarsSkinImages images, EarsFeatures features) {
		if(!features.claws) return;
		
		addTemplateRect(images.skin(), 16, 48, 4, 4, 0, 0, 255);
		addTemplateRect(images.skin(), 0, 16, 4, 4, 0, 0, 255);
		addTemplateRect(images.skin(), 44, 48, 4, 4, 0, 0, 255);
		addTemplateRect(images.skin(), 52, 16, 4, 4, 0, 0, 255);
	}
}
