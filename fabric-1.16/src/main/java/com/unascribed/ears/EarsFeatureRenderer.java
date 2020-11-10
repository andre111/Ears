package com.unascribed.ears;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.EarsRenderDelegate;
import com.unascribed.ears.common.NotRandom;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

public class EarsFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> implements EarsRenderDelegate {
	
	public EarsFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
		super(context);
	}

	private MatrixStack m;
	private VertexConsumer vc;
	private int light;
	private int overlay;
	private int skipRendering;
	private int stackDepth = 0;
	
	@Override
	public void render(MatrixStack m, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		Identifier skin = getTexture(entity);
		AbstractTexture tex = MinecraftClient.getInstance().getTextureManager().getTexture(skin);
		if (tex instanceof EarsFeaturesHolder && !entity.isInvisible()) {
			this.m = m;
			this.vc = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(skin));
			this.light = light;
			this.overlay = LivingEntityRenderer.getOverlay(entity, 0);
			this.skipRendering = 0;
			this.stackDepth = 0;
			EarsCommon.render(((EarsFeaturesHolder)tex).getEarsFeatures(), this, limbDistance);
			this.m = null;
			this.vc = null;
		}
	}

	@Override
	public void push() {
		stackDepth++;
		m.push();
		if (skipRendering > 0) skipRendering++;
	}

	@Override
	public void pop() {
		if (stackDepth <= 0) {
			new Exception("STACK UNDERFLOW").printStackTrace();
			return;
		}
		stackDepth--;
		m.pop();
		if (skipRendering > 0) skipRendering--;
	}

	@Override
	public void anchorTo(BodyPart part) {
		ModelPart model;
		switch (part) {
			case HEAD:
				model = getContextModel().head;
				break;
			case LEFT_ARM:
				model = getContextModel().leftArm;
				break;
			case LEFT_LEG:
				model = getContextModel().leftLeg;
				break;
			case RIGHT_ARM:
				model = getContextModel().rightArm;
				break;
			case RIGHT_LEG:
				model = getContextModel().rightLeg;
				break;
			case TORSO:
				model = getContextModel().torso;
				break;
			default: return;
		}
		if (!model.visible) {
			if (skipRendering == 0) {
				skipRendering = 1;
			}
			return;
		}
		model.rotate(m);
		Cuboid cuboid = model.getRandomCuboid(NotRandom.INSTANCE);
		m.scale(1/16f, 1/16f, 1/16f);
		m.translate(cuboid.minX, cuboid.maxY, cuboid.minZ);
	}

	@Override
	public void translate(float x, float y, float z) {
		if (skipRendering > 0) return;
		m.translate(x, y, z);
	}

	@Override
	public void rotate(float ang, float x, float y, float z) {
		if (skipRendering > 0) return;
		m.multiply(new Vector3f(x, y, z).getDegreesQuaternion(ang));
	}

	@Override
	public void renderFront(int u, int v, int w, int h, TexRotation rot, TexFlip flip) {
		if (skipRendering > 0) return;
		Matrix4f mv = m.peek().getModel();
		Matrix3f mn = m.peek().getNormal();
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip);
		
		vc.vertex(mv, 0, h, 0).color(1f, 1f, 1f, 1f).texture(uv[0][0], uv[0][1]).overlay(overlay).light(light).normal(mn, 0, 0, -1).next();
		vc.vertex(mv, w, h, 0).color(1f, 1f, 1f, 1f).texture(uv[1][0], uv[1][1]).overlay(overlay).light(light).normal(mn, 0, 0, -1).next();
		vc.vertex(mv, w, 0, 0).color(1f, 1f, 1f, 1f).texture(uv[2][0], uv[2][1]).overlay(overlay).light(light).normal(mn, 0, 0, -1).next();
		vc.vertex(mv, 0, 0, 0).color(1f, 1f, 1f, 1f).texture(uv[3][0], uv[3][1]).overlay(overlay).light(light).normal(mn, 0, 0, -1).next();
	}

	@Override
	public void renderBack(int u, int v, int w, int h, TexRotation rot, TexFlip flip) {
		if (skipRendering > 0) return;
		Matrix4f mv = m.peek().getModel();
		Matrix3f mn = m.peek().getNormal();
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip.flipHorizontally());
		
		vc.vertex(mv, 0, 0, 0).color(1f, 1f, 1f, 1f).texture(uv[3][0], uv[3][1]).overlay(overlay).light(light).normal(mn, 0, 0, 1).next();
		vc.vertex(mv, w, 0, 0).color(1f, 1f, 1f, 1f).texture(uv[2][0], uv[2][1]).overlay(overlay).light(light).normal(mn, 0, 0, 1).next();
		vc.vertex(mv, w, h, 0).color(1f, 1f, 1f, 1f).texture(uv[1][0], uv[1][1]).overlay(overlay).light(light).normal(mn, 0, 0, 1).next();
		vc.vertex(mv, 0, h, 0).color(1f, 1f, 1f, 1f).texture(uv[0][0], uv[0][1]).overlay(overlay).light(light).normal(mn, 0, 0, 1).next();
	}

	@Override
	public void renderDebugDot(float r, float g, float b, float a) {
		if (skipRendering > 0) return;
		Matrix4f mv = m.peek().getModel();
		
		GL11.glPointSize(8);
		GlStateManager.disableTexture();
		BufferBuilder bb = Tessellator.getInstance().getBuffer();
		bb.begin(GL11.GL_POINTS, VertexFormats.POSITION_COLOR);
		bb.vertex(mv, 0, 0, 0).color(r, g, b, a).next();
		Tessellator.getInstance().draw();
		GlStateManager.enableTexture();
	}
}