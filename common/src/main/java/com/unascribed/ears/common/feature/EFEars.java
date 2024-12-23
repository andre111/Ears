package com.unascribed.ears.common.feature;

import java.util.List;

import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.api.features.EarsFeatures.EarAnchor;
import com.unascribed.ears.api.features.EarsFeatures.EarMode;
import com.unascribed.ears.common.EarsRenderer.Pass;
import com.unascribed.ears.common.config.EFCEnum;
import com.unascribed.ears.common.render.EarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.render.EarsRenderDelegate.QuadGrow;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexFlip;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexRotation;

public class EFEars extends AbstractEarsFeature {
	public EFEars() {
		super(Pass.BASE, false, EarsFeatureType.EARS, List.of(new EFCEnum<>("Ears", EarMode.values()), new EFCEnum<>("Ear Anchor", EarAnchor.values())));
	}

	@Override
	public void render(EarsFeatures features, EarsRenderDelegate delegate, Pass pass, boolean drawingEmissive) {
		EarMode earMode = features.earMode;
		EarAnchor earAnchor = features.earAnchor;
		
		if (earMode == EarMode.ABOVE || earMode == EarMode.AROUND) {
			delegate.push();
				delegate.anchorTo(BodyPart.HEAD);
				if (earAnchor == EarAnchor.CENTER) {
					delegate.translate(0, 0, 4);
				} else if (earAnchor == EarAnchor.BACK) {
					delegate.translate(0, 0, 8);
				}
				delegate.push();
					delegate.translate(-4, -16, 0);
					delegate.renderFront(24, 0, 16, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
					delegate.renderBack(56, 28, 16, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				delegate.pop();
				if (earMode == EarMode.AROUND) {
					delegate.translate(-4, -8, 0);
					delegate.renderFront(36, 16, 4, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
					delegate.renderBack(12, 16, 4, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
					
					delegate.translate(12, 0, 0);
					delegate.renderFront(36, 32, 4, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
					delegate.renderBack(12, 32, 4, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				}
			delegate.pop();
		} else if (earMode == EarMode.SIDES) {
			delegate.push();
				delegate.anchorTo(BodyPart.HEAD);
				if (earAnchor == EarAnchor.CENTER) {
					delegate.translate(0, 0, 4);
				} else if (earAnchor == EarAnchor.BACK) {
					delegate.translate(0, 0, 8);
				}
				delegate.translate(-8, -8, 0);
				delegate.renderFront(24, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 28, 8, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				delegate.translate(16, 0, 0);
				delegate.renderFront(32, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 36, 8, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
			delegate.pop();
		} else if (earMode == EarMode.BEHIND) {
			delegate.push();
				delegate.anchorTo(BodyPart.HEAD);
				delegate.rotate(90, 0, 1, 0);
				delegate.translate(-16, -8, 0);
				delegate.renderFront(24, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 28, 8, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				delegate.rotate(180, 0, 1, 0);
				delegate.translate(-8, 0, -8);
				delegate.renderFront(32, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 36, 8, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
			delegate.pop();
		} else if (earMode == EarMode.FLOPPY) {
			delegate.push();
				delegate.anchorTo(BodyPart.HEAD);
				delegate.rotate(90, 0, 1, 0);
				delegate.translate(-8, -7, 0);
				delegate.rotate(-30, 1, 0, 0);
				delegate.translate(0, 0, 0);
				delegate.renderFront(24, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 28, 8, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
			delegate.pop();
			delegate.push();
				delegate.anchorTo(BodyPart.HEAD);
				delegate.rotate(-90, 0, 1, 0);
				delegate.translate(0, -7, -8);
				delegate.rotate(-30, 1, 0, 0);
				delegate.translate(0, 0, 0);
				delegate.renderFront(32, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 36, 8, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
			delegate.pop();
		} else if (earMode == EarMode.CROSS) {
			delegate.push();
				delegate.anchorTo(BodyPart.HEAD);
				if (earAnchor == EarAnchor.CENTER) {
					delegate.translate(0, 0, 4);
				} else if (earAnchor == EarAnchor.BACK) {
					delegate.translate(0, 0, 8);
				}
				delegate.translate(4, -16, 0);
				delegate.push();
					delegate.rotate(45, 0, 1, 0);
					delegate.translate(-4, 0, 0);
					delegate.renderFront(24, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
					delegate.renderBack(56, 28, 8, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				delegate.pop();
				delegate.push();
					delegate.rotate(-45, 0, 1, 0);
					delegate.translate(-4, 0, 0);
					delegate.renderFront(32, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
					delegate.renderBack(56, 36, 8, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				delegate.pop();
			delegate.pop();
		} else if (earMode == EarMode.OUT) {
			delegate.push();
				delegate.anchorTo(BodyPart.HEAD);
				delegate.rotate(90, 0, 1, 0);
				if (earAnchor == EarAnchor.BACK) {
					delegate.translate(-16, -8, 0);
				} else if (earAnchor == EarAnchor.CENTER) {
					delegate.translate(-8, -16, 0);
				} else if (earAnchor == EarAnchor.FRONT) {
					delegate.translate(0, -8, 0);
				}
				delegate.renderFront(24, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 28, 8, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				delegate.rotate(180, 0, 1, 0);
				delegate.translate(-8, 0, -8);
				delegate.renderFront(32, 0, 8, 8, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 36, 8, 8, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
			delegate.pop();
		} else if (earMode == EarMode.TALL) {
			delegate.push();
				delegate.anchorTo(BodyPart.HEAD);
				delegate.translate(0, -8, 0);
				if (earAnchor == EarAnchor.CENTER) {
					delegate.translate(0, 0, 4);
				} else if (earAnchor == EarAnchor.BACK) {
					delegate.translate(0, 0, 8);
				}
				
				float ang = -6;
				
				double dX = delegate.getCapeX()-delegate.getX();
				double dZ = delegate.getCapeZ()-delegate.getZ();
				
				float yaw = delegate.getBodyYaw();
				double yawX = Math.sin(Math.toRadians(yaw));
				double yawZ = -Math.cos(Math.toRadians(yaw));
				float dForward = (float)(dX * yawX + dZ * yawZ) * 25.0F;
				if (dForward > 80) dForward = 80;
				if (dForward < -80) dForward = -80;
				ang -= dForward;
				
				delegate.rotate(ang/3, 1, 0, 0);
				delegate.translate(0, -4, 0);
				delegate.renderFront(24, 0, 8, 4, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 40, 8, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				
				delegate.rotate(ang, 1, 0, 0);
				delegate.translate(0, -4, 0);
				delegate.renderFront(28, 0, 8, 4, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 36, 8, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				
				delegate.rotate(ang/2, 1, 0, 0);
				delegate.translate(0, -4, 0);
				delegate.renderFront(32, 0, 8, 4, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 32, 8, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				
				delegate.rotate(ang, 1, 0, 0);
				delegate.translate(0, -4, 0);
				delegate.renderFront(36, 0, 8, 4, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
				delegate.renderBack(56, 28, 8, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
			delegate.pop();
		} else if (earMode == EarMode.TALL_CROSS) {
			delegate.push();
				delegate.anchorTo(BodyPart.HEAD);
				if (earAnchor == EarAnchor.CENTER) {
					delegate.translate(0, 0, 4);
				} else if (earAnchor == EarAnchor.BACK) {
					delegate.translate(0, 0, 8);
				}
				delegate.translate(4, -24, 0);
				delegate.push();
					delegate.rotate(45, 0, 1, 0);
					delegate.translate(-4, 0, 0);
					delegate.renderFront(24, 0, 8, 16, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
					delegate.renderBack(56, 28, 8, 16, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.pop();
				delegate.push();
					delegate.rotate(-45, 0, 1, 0);
					delegate.translate(-4, 0, 0);
					delegate.renderFront(24, 0, 8, 16, TexRotation.CW, TexFlip.NONE, QuadGrow.NONE);
					delegate.renderBack(56, 28, 8, 16, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.pop();
			delegate.pop();
		}
	}
}
