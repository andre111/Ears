package com.unascribed.ears.common.feature;

import java.util.List;

import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsRenderer;
import com.unascribed.ears.common.EarsRenderer.Pass;
import com.unascribed.ears.common.render.EarsRenderDelegate;
import com.unascribed.ears.common.render.EarsSkinImages;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;

public class EFEmissiveSkin extends AbstractEarsFeature {
	public EFEmissiveSkin() {
		super(Pass.BASE, true, null, List.of());
	}

	@Override
	public void render(EarsFeatures features, EarsRenderDelegate delegate, Pass pass, boolean drawingEmissive) {
		delegate.push();
			delegate.anchorTo(BodyPart.HEAD);
			EarsRenderer.drawVanillaCuboid(delegate, 0, 0, 8, 8, 8, 0);
		delegate.pop();
		delegate.push();
			delegate.anchorTo(BodyPart.TORSO);
			EarsRenderer.drawVanillaCuboid(delegate, 16, 16, 8, 12, 4, 0);
		delegate.pop();
		delegate.push();
			delegate.anchorTo(BodyPart.LEFT_ARM);
			EarsRenderer.drawVanillaCuboid(delegate, 32, 48, delegate.isSlim() ? 3 : 4, 12, 4, 0);
		delegate.pop();
		delegate.push();
			delegate.anchorTo(BodyPart.RIGHT_ARM);
			EarsRenderer.drawVanillaCuboid(delegate, 40, 16, delegate.isSlim() ? 3 : 4, 12, 4, 0);
		delegate.pop();
		delegate.push();
			delegate.anchorTo(BodyPart.LEFT_LEG);
			EarsRenderer.drawVanillaCuboid(delegate, 16, 48, 4, 12, 4, 0);
		delegate.pop();
		delegate.push();
			delegate.anchorTo(BodyPart.RIGHT_LEG);
			EarsRenderer.drawVanillaCuboid(delegate, 0, 16, 4, 12, 4, 0);
		delegate.pop();
	}

	@Override
	public void addTemplate(EarsSkinImages<?> images, EarsFeatures features) {
	}
}
