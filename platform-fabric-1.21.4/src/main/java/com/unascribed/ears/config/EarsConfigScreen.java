package com.unascribed.ears.config;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;
import com.unascribed.ears.EarsMod;
import com.unascribed.ears.NativeImageAdapter;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeaturesStorage;
import com.unascribed.ears.common.EarsFeaturesWriterV1;
import com.unascribed.ears.common.EarsRenderer;
import com.unascribed.ears.common.config.EFCBoolean;
import com.unascribed.ears.common.config.EFCEnum;
import com.unascribed.ears.common.config.EFCFloat;
import com.unascribed.ears.common.config.EFCInteger;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.feature.EarsFeature;
import com.unascribed.ears.common.image.WritableEarsImage;
import com.unascribed.ears.common.util.QDPNG;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EarsConfigScreen extends Screen {
	private static final Identifier modifiedSkinTextureID = Identifier.of("ears", "skinoverride");
	
	private final Screen parent;
	
	private EarsFeatures features;
	private boolean showTemplates;

	@SuppressWarnings({ "resource", "deprecation" })
	public EarsConfigScreen(Screen parent) {
		super(Text.literal("Ears Configurator"));
		this.parent = parent;
		
		EarsFeatures sourceFeatures = EarsFeaturesStorage.INSTANCE.getById(MinecraftClient.getInstance().player.getUuid());
		features = EarsFeatures.builder(sourceFeatures).build();
		showTemplates = false;
	}
	
	@Override
	protected void init() {
		TextWidget titleWidget = new TextWidget(title, getTextRenderer());
		titleWidget.setDimensionsAndPosition(width, 20, 0, 0);
		addDrawable(titleWidget);

		TextWidget noteWidget = new TextWidget(Text.literal("Note: This is only a preview. Upload the modified skin to Mojang to apply changes."), getTextRenderer());
		noteWidget.setDimensionsAndPosition(width, 20, 0, 10);
		noteWidget.setTextColor(0xFFAAAAAA);
		addDrawable(noteWidget);
		
		addDrawableChild(ButtonWidget.builder(Text.literal("Export Skin"), (button) -> exportSkin(features)).dimensions(2, height-18, width/4-10, 16).build());
		addDrawableChild(CyclingButtonWidget.onOffBuilder(false).build(2 + width/4 + 2, height-18, width/4-10, 16, Text.literal("Show Template"), (button, value) -> {
			showTemplates = value;
			update();
		}));
		
		//TODO: actual layout
		//TODO: function to export (+ import?) modified skin texture
		//TODO: function to export + import wing texture
		//TODO: function to "show/add template" for enabled features (only where the current texture is transparent)
		int x = width/2;
		int y = 32;
		int w = width/4 - 2;
		int h = 16;
		for(EarsFeature feature : EarsCommon.FEATURES) {
			for(var config : feature.getConfig()) {
				switch(config) {
				case EFCBoolean bool: {
					addDrawableChild(CyclingButtonWidget.onOffBuilder(bool.get(features)).build(x, y, w, h, Text.literal(bool.getName()), (button, value) -> {
						features = bool.with(features, value);
						update();
					}));
					break;
				}
				case EFCEnum<?> enm: {
					addDrawableChild(createEnumButton(enm, x, y, w, h));
					break;
				}
				case EFCFloat flt: {
					addDrawableChild(new SliderWidget(x, y, w, h, Text.literal(flt.getName()), (flt.get(features) - flt.getMin()) / (flt.getMax() - flt.getMin())) {
						{
							updateMessage();
						}
						
						@Override
						protected void updateMessage() {
							setMessage(Text.literal(flt.getName()+": "+getScaledValue()));
						}

						@Override
						protected void applyValue() {
							features = flt.with(features, getScaledValue());
							update();
						}
						
						protected float getScaledValue() {
							return (float) (value * (flt.getMax() - flt.getMin())) + flt.getMin();
						}
					});
					break;
				}
				case EFCInteger integer: {
					addDrawableChild(new SliderWidget(x, y, w, h, Text.literal(integer.getName()), (integer.get(features) - integer.getMin()) / (double) (integer.getMax() - integer.getMin())) {
						{
							updateMessage();
						}
						
						@Override
						protected void updateMessage() {
							setMessage(Text.literal(integer.getName()+": "+getIntValue()));
						}

						@Override
						protected void applyValue() {
							features = integer.with(features, getIntValue());
							update();
						}
						
						protected int getIntValue() {
							return (int) (value * (integer.getMax() - integer.getMin())) + integer.getMin();
						}
					});
					break;
				}
				default: {
					EarsLog.debug(EarsLog.Tag.PLATFORM, "Unimplemented config type {}", config);
					break;
				}
				}
				if(x == width/2) {
					x = width/2 + w + 2;
				} else {
					x = width/2;
					y = y + 16 + 2;
				}
			}
		}
	}
	
	private <E extends Enum<E>> CyclingButtonWidget<E> createEnumButton(EFCEnum<E> enm, int x, int y, int width, int height) {
		return CyclingButtonWidget.<E>builder(t -> Text.literal(t+""))
				.values(enm.getValues())
				.initially(enm.get(features))
				.build(x, y, width, height, Text.literal(enm.getName()), (button, value) -> {
					features = enm.with(features, value);
					update();
				});
	}
	
	private void update() {
		if(showTemplates) {
			// copy skin
			NativeImage image = new NativeImage(NativeImage.Format.RGBA, 64, 64, true);
			image.copyFrom(EarsMod.getSkinImage());
			
			// add templates
			NativeImageAdapter modifiedSkinImage = new NativeImageAdapter(image);
			EarsRenderer.addTemplates(modifiedSkinImage, features);
			
			// register
			MinecraftClient.getInstance().getTextureManager().registerTexture(modifiedSkinTextureID, new NativeImageBackedTexture(image));
		}
	}
	
	@SuppressWarnings("resource")
	private void exportSkin(EarsFeatures features) {
		WritableEarsImage img = EarsMod.getCopyOfSkin();
		if(img != null) {
			try {
				if(showTemplates) EarsRenderer.addTemplates(img, features);
				EarsFeaturesWriterV1.write(features, img);
				Files.write(QDPNG.write(img), new File(MinecraftClient.getInstance().runDirectory, showTemplates ? "ears-skin-template.png" : "ears-skin.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		super.renderBackground(context, mouseX, mouseY, delta);
		
		context.getMatrices().push();
		context.getMatrices().translate(0, 0, 120);
		EarsMod.overrideFeatures = features;
		if(showTemplates) EarsMod.overrideSkin = modifiedSkinTextureID;
		InventoryScreen.drawEntity(context, 0, 0, width/2, height, 100, 0.0625F, mouseX, mouseY, client.player);
		EarsMod.overrideSkin = null;
		EarsMod.overrideFeatures = null;
		context.getMatrices().pop();
	}
	
	public void close() {
		client.setScreen(parent);
	}
}
