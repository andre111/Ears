package com.unascribed.ears.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.common.io.Files;
import com.unascribed.ears.EarsMod;
import com.unascribed.ears.NativeImageAdapter;
import com.unascribed.ears.api.Slice;
import com.unascribed.ears.api.features.AlfalfaData;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeaturesParser;
import com.unascribed.ears.common.EarsFeaturesStorage;
import com.unascribed.ears.common.EarsFeaturesWriterV1;
import com.unascribed.ears.common.EarsRenderer;
import com.unascribed.ears.common.config.EFCBoolean;
import com.unascribed.ears.common.config.EFCEnum;
import com.unascribed.ears.common.config.EFCFloat;
import com.unascribed.ears.common.config.EFCInteger;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.feature.EarsFeature;
import com.unascribed.ears.common.image.EarsImage;
import com.unascribed.ears.common.render.EarsSkinImages;
import com.unascribed.ears.common.render.AbstractEarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexSource;
import com.unascribed.ears.common.util.Alfalfa;
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
	private static final Identifier SKIN_OVERRIDE_ID = Identifier.of("ears", "skinoverride");
	private static final Identifier WING_OVERRIDE_ID = Identifier.of("ears", "wingoverride");
	
	private final Screen parent;
	
	private EarsFeatures features;
	private boolean showTemplates;
	
	private NativeImage importedSkin;
	private NativeImage importedWing;

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
		
		int bw = (width-5*2)/5;
		addDrawableChild(ButtonWidget.builder(Text.literal("Export Skin"), (button) -> exportSkin(features)).dimensions(2, height-18, bw, 16).build());
		addDrawableChild(ButtonWidget.builder(Text.literal("Export Wings"), (button) -> exportWings(features)).dimensions(2+bw+2, height-18, bw, 16).build());
		addDrawableChild(CyclingButtonWidget.onOffBuilder(false).build(2+bw+2+bw+2, height-18, bw, 16, Text.literal("Templates"), (button, value) -> {
			showTemplates = value;
			if(!showTemplates) {
				if(importedSkin != null) {
					NativeImage skinCopy = new NativeImage(NativeImage.Format.RGBA, 64, 64, true);
					skinCopy.copyFrom(importedSkin);
					MinecraftClient.getInstance().getTextureManager().registerTexture(SKIN_OVERRIDE_ID, new NativeImageBackedTexture(skinCopy));
				}
				if(importedWing != null) {
					NativeImage wingCopy = new NativeImage(NativeImage.Format.RGBA, 20, 16, true);
					wingCopy.copyFrom(importedWing);
					MinecraftClient.getInstance().getTextureManager().registerTexture(WING_OVERRIDE_ID, new NativeImageBackedTexture(wingCopy));
				}
			}
			update();
		}));
		addDrawableChild(ButtonWidget.builder(Text.literal("Import Skin"), (button) -> importSkin()).dimensions(2+bw+2+bw+2+bw+2, height-18, bw, 16).build());
		addDrawableChild(ButtonWidget.builder(Text.literal("Import Wings"), (button) -> importWings()).dimensions(2+bw+2+bw+2+bw+2+bw+2, height-18, bw, 16).build());
		
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
			NativeImage skinImage = new NativeImage(NativeImage.Format.RGBA, 64, 64, true);
			skinImage.copyFrom(EarsMod.getSkinImage());
			NativeImage wingImage = new NativeImage(NativeImage.Format.RGBA, 20, 16, true);
			NativeImage wingSrcImage = importedWing != null ? importedWing : EarsMod.getWingImage();
			wingImage.copyFrom(wingSrcImage);
			
			// add templates
			NativeImageAdapter modifiedSkinImage = new NativeImageAdapter(skinImage);
			NativeImageAdapter modifiedWingImage = new NativeImageAdapter(wingImage);
			EarsRenderer.addTemplates(new EarsSkinImages(modifiedSkinImage, modifiedWingImage), features);
			
			// register
			MinecraftClient.getInstance().getTextureManager().registerTexture(SKIN_OVERRIDE_ID, new NativeImageBackedTexture(skinImage));
			MinecraftClient.getInstance().getTextureManager().registerTexture(WING_OVERRIDE_ID, new NativeImageBackedTexture(wingImage));
		}
	}
	
	private void exportSkin(EarsFeatures features) {
		EarsSkinImages images = EarsMod.getCopyOfSkin();
		if(images != null) {
			try {
				if(showTemplates) EarsRenderer.addTemplates(images, features);
				EarsFeaturesWriterV1.write(features, images.skin());
				Files.write(QDPNG.write(images.skin()), new File(getBaseDir(), showTemplates ? "ears-skin-template.png" : "ears-skin.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void exportWings(EarsFeatures features) {
		if(features.alfalfa != null && features.alfalfa.data.containsKey("wing")) {
			try {
				byte[] data = features.alfalfa.data.get("wing").toByteArray();
				Files.write(data, new File(getBaseDir(), "ears-wing.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void importSkin() {
		File file = new File(getBaseDir(), "ears-skin.png");
		if(file.exists()) {
			try {
				byte[] data = Files.toByteArray(file);
				NativeImage image = NativeImage.read(AbstractEarsRenderDelegate.toNativeBuffer(data));
				if(image == null) return;
				if(image.getWidth() == 64 && image.getHeight() == 64) {
					if(importedSkin != null) importedSkin.close();
					importedSkin = image;
					if(importedWing != null) importedWing.close();
					importedWing = null;
					
					EarsImage earsImage = new NativeImageAdapter(importedSkin);
					AlfalfaData alfalfa = Alfalfa.read(earsImage);
					features = EarsFeaturesParser.detect(earsImage, alfalfa, png -> new NativeImageAdapter(NativeImage.read(AbstractEarsRenderDelegate.toNativeBuffer(png))));
					
					NativeImage skinCopy = new NativeImage(NativeImage.Format.RGBA, 64, 64, true);
					skinCopy.copyFrom(importedSkin);
					MinecraftClient.getInstance().getTextureManager().registerTexture(SKIN_OVERRIDE_ID, new NativeImageBackedTexture(skinCopy));
					
					//TODO: if alfalfa has wing, load as override
					if(alfalfa.data.containsKey("wing")) {
						importedWing = NativeImage.read(AbstractEarsRenderDelegate.toNativeBuffer(alfalfa.data.get("wing").toByteArray()));
						
						NativeImage wingCopy = new NativeImage(NativeImage.Format.RGBA, 20, 16, true);
						wingCopy.copyFrom(importedWing);
						MinecraftClient.getInstance().getTextureManager().registerTexture(WING_OVERRIDE_ID, new NativeImageBackedTexture(wingCopy));
					}

					update();
				} else {
					image.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void importWings() {
		File file = new File(getBaseDir(), "ears-wing.png");
		if(file.exists()) {
			try {
				byte[] data = Files.toByteArray(file);
				NativeImage image = NativeImage.read(AbstractEarsRenderDelegate.toNativeBuffer(data));
				if(image == null) return;
				if(image.getWidth() == 20 && image.getHeight() == 16) {
					if(importedWing != null) importedWing.close();
					importedWing = image;
					
					NativeImage wingCopy = new NativeImage(NativeImage.Format.RGBA, 20, 16, true);
					wingCopy.copyFrom(importedWing);
					MinecraftClient.getInstance().getTextureManager().registerTexture(WING_OVERRIDE_ID, new NativeImageBackedTexture(wingCopy));
					
					Map<String, Slice> alfalfa = new HashMap<>(features.alfalfa.data);
					alfalfa.put("wing", new Slice(data));
					features = EarsFeatures.builder(features).alfalfa(new AlfalfaData(features.alfalfa.version, alfalfa)).build();
					
					update();
				} else {
					image.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("resource")
	private File getBaseDir() {
		return MinecraftClient.getInstance().runDirectory;
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		super.renderBackground(context, mouseX, mouseY, delta);
		
		context.getMatrices().push();
		context.getMatrices().translate(0, 0, 120);
		EarsMod.overrideFeatures = features;
		if(showTemplates || importedSkin != null) EarsMod.overrides.put(TexSource.SKIN, SKIN_OVERRIDE_ID);
		if(showTemplates || importedWing != null) EarsMod.overrides.put(TexSource.WING, WING_OVERRIDE_ID);
		InventoryScreen.drawEntity(context, 0, 0, width/2, height, 100, 0.0625F, mouseX, mouseY, client.player);
		EarsMod.overrides.clear();
		EarsMod.overrideFeatures = null;
		context.getMatrices().pop();
	}
	
	public void close() {
		client.setScreen(parent);
	}
}
