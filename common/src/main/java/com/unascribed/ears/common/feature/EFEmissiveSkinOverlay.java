package com.unascribed.ears.common.feature;

import java.util.List;

import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsRenderer;
import com.unascribed.ears.common.EarsRenderer.Pass;
import com.unascribed.ears.common.image.WritableEarsImage;
import com.unascribed.ears.common.render.EarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;

public class EFEmissiveSkinOverlay extends AbstractEarsFeature {
	public EFEmissiveSkinOverlay() {
		super(Pass.OVERLAY, true, null, List.of());
	}

	@Override
	public void render(EarsFeatures features, EarsRenderDelegate delegate, Pass pass, boolean drawingEmissive) {
		delegate.push();
			delegate.anchorTo(BodyPart.HEAD);
			delegate.translate(0, 1f, 0);
			EarsRenderer.drawVanillaCuboid(delegate, 32, 0, 8, 8, 8, 0.5f);
		delegate.pop();
		delegate.push();
			delegate.anchorTo(BodyPart.TORSO);
			delegate.translate(0, 0.5f, 0);
			EarsRenderer.drawVanillaCuboid(delegate, 16, 32, 8, 12, 4, 0.25f);
		delegate.pop();
		delegate.push();
			delegate.anchorTo(BodyPart.LEFT_ARM);
			delegate.translate(0, 0.5f, 0);
			EarsRenderer.drawVanillaCuboid(delegate, 48, 48, delegate.isSlim() ? 3 : 4, 12, 4, 0.25f);
		delegate.pop();
		delegate.push();
			delegate.anchorTo(BodyPart.RIGHT_ARM);
			delegate.translate(0, 0.5f, 0);
			EarsRenderer.drawVanillaCuboid(delegate, 40, 32, delegate.isSlim() ? 3 : 4, 12, 4, 0.25f);
		delegate.pop();
		delegate.push();
			delegate.anchorTo(BodyPart.LEFT_LEG);
			delegate.translate(0, 0.5f, 0);
			EarsRenderer.drawVanillaCuboid(delegate, 0, 48, 4, 12, 4, 0.25f);
		delegate.pop();
		delegate.push();
			delegate.anchorTo(BodyPart.RIGHT_LEG);
			delegate.translate(0, 0.5f, 0);
			EarsRenderer.drawVanillaCuboid(delegate, 0, 32, 4, 12, 4, 0.25f);
		delegate.pop();
	}

	@Override
	public void addTemplate(WritableEarsImage image, EarsFeatures features) {
	}
}
