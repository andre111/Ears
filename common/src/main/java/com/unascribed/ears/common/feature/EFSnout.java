package com.unascribed.ears.common.feature;

import java.util.List;

import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsRenderer.Pass;
import com.unascribed.ears.common.config.EFCInteger;
import com.unascribed.ears.common.render.EarsRenderDelegate;
import com.unascribed.ears.common.render.EarsSkinImages;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.render.EarsRenderDelegate.QuadGrow;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexFlip;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexRotation;

public class EFSnout extends AbstractEarsFeature {
	public EFSnout() {
		//TODO: actual snoutOffset should be limited to 8-snoutHeight
		super(Pass.BASE, false, EarsFeatureType.SNOUT, List.of(new EFCInteger("snoutOffset", 0, 8), new EFCInteger("snoutWidth", 0, 7), new EFCInteger("snoutHeight", 0, 4), new EFCInteger("snoutDepth", 0, 8)));
	}

	@Override
	public void render(EarsFeatures features, EarsRenderDelegate delegate, Pass pass, boolean drawingEmissive) {
		int snoutOffset = features.snoutOffset;
		int snoutWidth = features.snoutWidth;
		int snoutHeight = features.snoutHeight;
		int snoutDepth = features.snoutDepth;
		if(snoutWidth <= 0 || snoutHeight <= 0 || snoutDepth <= 0) return;
		
		delegate.push();
			delegate.anchorTo(BodyPart.HEAD);
			delegate.translate((8-snoutWidth)/2f, -(snoutOffset+snoutHeight), -snoutDepth);
			delegate.renderDoubleSided(0, 2, snoutWidth, snoutHeight, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
			delegate.push();
				// top
				delegate.rotate(-90, 1, 0, 0);
				delegate.translate(0, -1, 0);
				delegate.renderDoubleSided(0, 1, snoutWidth, 1, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				for (int i = 0; i < snoutDepth-1; i++) {
					delegate.translate(0, -1, 0);
					delegate.renderDoubleSided(0, 0, snoutWidth, 1, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				}
			delegate.pop();
			delegate.push();
				// bottom
				delegate.translate(0, snoutHeight, 0);
				delegate.rotate(90, 1, 0, 0);
				delegate.renderDoubleSided(0, 2+snoutHeight, snoutWidth, 1, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				for (int i = 0; i < snoutDepth-1; i++) {
					delegate.translate(0, 1, 0);
					delegate.renderDoubleSided(0, 2+snoutHeight+1, snoutWidth, 1, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				}
			delegate.pop();
			delegate.push();
				delegate.rotate(90, 0, 1, 0);
				// right
				delegate.push();
					delegate.translate(-1, 0, 0);
					delegate.renderDoubleSided(7, 0, 1, snoutHeight, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
					for (int i = 0; i < snoutDepth-1; i++) {
						delegate.translate(-1, 0, 0);
						delegate.renderDoubleSided(7, 4, 1, snoutHeight, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
					}
				delegate.pop();
				// left
				delegate.push();
					delegate.translate(-1, 0, snoutWidth);
					delegate.renderDoubleSided(7, 0, 1, snoutHeight, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
					for (int i = 0; i < snoutDepth-1; i++) {
						delegate.translate(-1, 0, 0);
						delegate.renderDoubleSided(7, 4, 1, snoutHeight, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
					}
				delegate.pop();
			delegate.pop();
		delegate.pop();
	}

	@Override
	public void addTemplate(EarsSkinImages images, EarsFeatures features) {
		int snoutWidth = features.snoutWidth;
		int snoutHeight = features.snoutHeight;
		int snoutDepth = features.snoutDepth;
		if(snoutWidth <= 0 || snoutHeight <= 0 || snoutDepth <= 0) return;

		addTemplateRect(images.skin(), 0, 2, snoutWidth, snoutHeight, 255, 255, 0);
		// top
		addTemplateRect(images.skin(), 0, 1, snoutWidth, 1, 255, 225, 0);
		addTemplateRect(images.skin(), 0, 0, snoutWidth, 1, 255, 195, 0);
		// bottom
		addTemplateRect(images.skin(), 0, 2+snoutHeight, snoutWidth, 1, 255, 165, 0);
		addTemplateRect(images.skin(), 0, 2+snoutHeight+1, snoutWidth, 1, 255, 135, 0);
		// sides
		addTemplateRect(images.skin(), 7, 0, 1, snoutHeight, 255, 105, 0);
		addTemplateRect(images.skin(), 7, 4, 1, snoutHeight, 255, 75, 0);
	}
}
