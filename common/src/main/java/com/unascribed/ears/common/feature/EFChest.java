package com.unascribed.ears.common.feature;

import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.EarsStateType;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsRenderer;
import com.unascribed.ears.common.EarsRenderer.Pass;
import com.unascribed.ears.common.render.EarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.render.EarsRenderDelegate.QuadGrow;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexFlip;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexRotation;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexSource;

public class EFChest implements EarsFeature {

	@Override
	public boolean shouldRender(EarsFeatures features, EarsRenderDelegate delegate, Pass pass, boolean drawingEmissive) {
		// restrictions
		if(features.chestSize <= 0) return false;
		if(EarsRenderer.isInhibited(delegate, EarsFeatureType.CHEST)) return false;
		boolean wearingArmor = EarsRenderer.isActive(delegate, EarsStateType.WEARING_CHESTPLATE);
		if(wearingArmor && !delegate.canBind(TexSource.CHESTPLATE)) return false;
		
		// conditions
		if(pass == Pass.BASE) return true;
		if(pass == Pass.OVERLAY && delegate.isJacketEnabled()) return true;
		if(pass == Pass.ARMOR && wearingArmor) return true;
		if(pass == Pass.ARMOR_GLINT && wearingArmor && delegate.canBind(TexSource.GLINT_CHESTPLATE)) return true;
		
		return false;
	}

	@Override
	public void render(EarsFeatures features, EarsRenderDelegate delegate, Pass pass, boolean drawingEmissive) {
		
		delegate.push();
			delegate.anchorTo(BodyPart.TORSO);
			delegate.translate(0, -10, 0);
			delegate.rotate(-features.chestSize*45, 1, 0, 0);
			
			if (pass == Pass.ARMOR) {
				delegate.bind(TexSource.CHESTPLATE);
			} else if (pass == Pass.ARMOR_GLINT) {
				delegate.bind(TexSource.GLINT_CHESTPLATE);
			}
			
			if (pass == Pass.BASE) {
				delegate.renderDoubleSided(20, 22, 8, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
			} else if (pass == Pass.OVERLAY) {
				delegate.push();
					delegate.translate(4, 2, 0);
					// can't use QuadGrow as we have two quads side-by-side
					delegate.scale(8.5f/8, 4.5f/4, 1);
					delegate.translate(-4, -2, 0);
					delegate.translate(0, 0, -0.25f);
					delegate.renderDoubleSided(0, 48, 4, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
					delegate.translate(4, 0, 0);
					delegate.renderDoubleSided(12, 48, 4, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				delegate.pop();
			} else if (pass == Pass.ARMOR || pass == Pass.ARMOR_GLINT) {
				delegate.push();
					delegate.translate(0, 1, -1f);
					delegate.renderFront(20, 24, 8, 3, TexRotation.NONE, TexFlip.NONE, QuadGrow.FULLPIXEL);
				delegate.pop();
			}
			delegate.push();
				delegate.translate(0, 4, 0);
				delegate.rotate(90, 1, 0, 0);
				if (pass == Pass.BASE) {
					delegate.renderDoubleSided(56, 44, 8, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				} else if (pass == Pass.OVERLAY) {
					delegate.push();
						delegate.translate(0, 0, -0.25f);
						delegate.renderDoubleSided(28, 48, 8, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.QUARTERPIXEL);
					delegate.pop();
				} else if (pass == Pass.ARMOR || pass == Pass.ARMOR_GLINT) {
					delegate.push();
						delegate.translate(0, 0, -1f);
						delegate.renderFront(20, 25, 8, 3, TexRotation.NONE, TexFlip.NONE, QuadGrow.FULLPIXEL);
					delegate.pop();
				}
			delegate.pop();
			delegate.push();
				delegate.rotate(90, 0, 1, 0);
				delegate.translate(-4f, 0, 0.01f);
				if (pass == Pass.BASE) {
					delegate.renderDoubleSided(60, 48, 4, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.NONE);
				} else if (pass == Pass.OVERLAY) {
					delegate.push();
						delegate.translate(0, 0, -0.25f);
						delegate.renderDoubleSided(48, 48, 4, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.QUARTERPIXEL);
					delegate.pop();
				} else if (pass == Pass.ARMOR || pass == Pass.ARMOR_GLINT) {
					delegate.push();
						delegate.translate(0, 0, -1f);
						delegate.renderFront(16, 20, 4, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.FULLPIXEL);
					delegate.pop();
				}
				delegate.translate(0, 0, 7.98f);
				delegate.rotate(180, 0, 1, 0);
				delegate.translate(-4, 0, 0);
				if (pass == Pass.BASE) {
					delegate.renderDoubleSided(60, 48, 4, 4, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.NONE);
				} else if (pass == Pass.OVERLAY) {
					delegate.push();
						delegate.translate(0, 0, -0.25f);
						delegate.renderDoubleSided(48, 48, 4, 4, TexRotation.NONE, TexFlip.HORIZONTAL, QuadGrow.QUARTERPIXEL);
					delegate.pop();
				} else if (pass == Pass.ARMOR || pass == Pass.ARMOR_GLINT) {
					delegate.push();
						delegate.translate(0, 0, -1f);
						delegate.renderFront(16, 20, 4, 4, TexRotation.NONE, TexFlip.NONE, QuadGrow.FULLPIXEL);
					delegate.pop();
				}
			delegate.pop();
		delegate.pop();
	}
}
