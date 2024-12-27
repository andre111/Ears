package com.unascribed.ears.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EarsConfigScreen extends Screen {
	private static final Identifier SKIN_OVERRIDE_ID = Identifier.of("ears", "skinoverride");
	private static final Identifier WING_OVERRIDE_ID = Identifier.of("ears", "wingoverride");
	
	private final Screen parent;
	
	private EarsFeatures features;
	private boolean showTemplates;
	
	private NativeImage skinImage;
	private NativeImage wingImage;

	@SuppressWarnings({ "resource", "deprecation" })
	public EarsConfigScreen(Screen parent, EarsFeatures sourceFeatures, NativeImage skin, NativeImage wing) {
		super(Text.literal("Ears Configurator"));
		this.parent = parent;
		
		if(sourceFeatures == null) sourceFeatures = EarsFeaturesStorage.INSTANCE.getById(MinecraftClient.getInstance().player.getUuid());
		features = EarsFeatures.builder(sourceFeatures).build();
		showTemplates = false;
		skinImage = skin != null ? skin : new NativeImage(NativeImage.Format.RGBA, 64, 64, true);
		wingImage = wing != null ? wing : new NativeImage(NativeImage.Format.RGBA, 20, 16, true);
		update(true);
	}

	@Override
	public boolean shouldPause() {
		return false;
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
		
		int bw = (width-6*2)/5;
		int bx = (width-bw*5-4*2) / 2;
		addDrawableChild(ButtonWidget.builder(Text.literal("Export Skin"), (button) -> exportSkin(features)).dimensions(bx, height-18, bw, 16).build());
		addDrawableChild(ButtonWidget.builder(Text.literal("Export Wings"), (button) -> exportWings(features)).dimensions(bx+bw+2, height-18, bw, 16).build());
		addDrawableChild(CyclingButtonWidget.onOffBuilder(false).build(bx+bw+2+bw+2, height-18, bw, 16, Text.literal("Templates"), (button, value) -> {
			showTemplates = value;
			update(true);
		}));
		addDrawableChild(ButtonWidget.builder(Text.literal("Import Skin"), (button) -> importSkin()).dimensions(bx+bw+2+bw+2+bw+2, height-18, bw, 16).build());
		addDrawableChild(ButtonWidget.builder(Text.literal("Import Wings"), (button) -> importWings()).dimensions(bx+bw+2+bw+2+bw+2+bw+2, height-18, bw, 16).build());
		
		int w = width/4 - 2 - 5;
		int h = 16;
		GridWidget grid = new GridWidget(0, 0);
		grid.setSpacing(2);
		GridWidget.Adder gridAdder = grid.createAdder(2);
		for(EarsFeature feature : EarsCommon.FEATURES) {
			if(feature.getConfig() == null) continue;

			gridAdder.add(new TextWidget(Text.empty(), getTextRenderer()), 2);
			TextWidget featureTitle = new TextWidget(Text.literal(feature.getConfig().name()), getTextRenderer());
			featureTitle.setDimensions(w*2+2, h);
			gridAdder.add(featureTitle, 2);
			
			for(var config : feature.getConfig().efcs()) {
				switch(config) {
				case EFCBoolean bool: {
					gridAdder.add(CyclingButtonWidget.onOffBuilder(bool.get(features)).build(0, 0, w, h, Text.literal(bool.getDisplay()), (button, value) -> {
						features = bool.with(features, value);
						update();
					}));
					break;
				}
				case EFCEnum<?> enm: {
					createEnumButtons(enm, w, h, gridAdder);
					break;
				}
				case EFCFloat flt: {
					gridAdder.add(new SliderWidget(0, 0, w, h, Text.literal(flt.getDisplay()), (flt.get(features) - flt.getMin()) / (flt.getMax() - flt.getMin())) {
						{
							updateMessage();
						}
						
						@Override
						protected void updateMessage() {
							setMessage(Text.literal("%s: %.2f".formatted(flt.getDisplay(), getScaledValue())));
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
					gridAdder.add(new SliderWidget(0,0, w, h, Text.literal(integer.getDisplay()), (integer.get(features) - integer.getMin()) / (double) (integer.getMax() - integer.getMin())) {
						{
							updateMessage();
						}
						
						@Override
						protected void updateMessage() {
							setMessage(Text.literal(integer.getDisplay()+": "+getIntValue()));
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
			}
		}
		ListWidget listWidget = new ListWidget(grid, width/2-bx, height-32*2);
		listWidget.setPosition(width/2, 32);
		addDrawableChild(listWidget);
	}
	
	private <E extends Enum<E>> void createEnumButtons(EFCEnum<E> enm, int width, int height, GridWidget.Adder gridAdder) {
		int count = enm.getValues().length;
		if(enm.getDisplay() != null && !enm.getDisplay().isBlank()) {
			gridAdder.add(new TextWidget(Text.literal(enm.getDisplay()), getTextRenderer())).setDimensions(width, height);
			count++;
		}
		
		List<ButtonWidget> buttons = new ArrayList<>();
		for(E value : enm.getValues()) {
			buttons.add(gridAdder.add(ButtonWidget.builder(Text.literal(value.name()), (button) -> {
				for(ButtonWidget other : buttons) other.setAlpha(0.5f);
				button.setAlpha(1f);
				
				features = enm.with(features, value);
				update();
			}).size(width, height).build()));
			buttons.getLast().setAlpha(enm.get(features) == value ? 1f : 0.5f);
		}
		
		if(count % 2 != 0) {
			gridAdder.add(new TextWidget(Text.literal(""), getTextRenderer()));
		}
	}
	
	private void update() {
		update(false);
	}
	
	private void update(boolean updateTextures) {
		if(updateTextures || showTemplates) {
			EarsSkinImages<NativeImageAdapter> images = createNewImages();
			
			MinecraftClient.getInstance().getTextureManager().registerTexture(SKIN_OVERRIDE_ID, new NativeImageBackedTexture(images.skin().getNativeImage()));
			MinecraftClient.getInstance().getTextureManager().registerTexture(WING_OVERRIDE_ID, new NativeImageBackedTexture(images.wing().getNativeImage()));
		}
	}
	
	private void exportSkin(EarsFeatures features) {
		try {
			EarsSkinImages<NativeImageAdapter> images = createNewImages();
			EarsFeaturesWriterV1.write(features, images.skin());
			Files.write(QDPNG.write(images.skin()), new File(EarsMod.getBaseDir(), showTemplates ? "ears-skin-template.png" : "ears-skin.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void exportWings(EarsFeatures features) {
		if(features.alfalfa != null && features.alfalfa.data.containsKey("wing")) {
			try {
				byte[] data = features.alfalfa.data.get("wing").toByteArray();
				Files.write(data, new File(EarsMod.getBaseDir(), "ears-wing.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private EarsSkinImages<NativeImageAdapter> createNewImages() {
		NativeImage skinCopy = new NativeImage(NativeImage.Format.RGBA, 64, 64, true);
		skinCopy.copyFrom(skinImage);
		NativeImage wingCopy = new NativeImage(NativeImage.Format.RGBA, 20, 16, true);
		if(wingImage != null) wingCopy.copyFrom(wingImage);
		
		EarsSkinImages<NativeImageAdapter> images = new EarsSkinImages<>(new NativeImageAdapter(skinCopy), new NativeImageAdapter(wingCopy));
		if(showTemplates) EarsRenderer.addTemplates(images, features);
		return images;
	}
	
	private void importSkin() {
		File file = new File(EarsMod.getBaseDir(), "ears-skin.png");
		if(file.exists()) {
			try {
				byte[] data = Files.toByteArray(file);
				NativeImage image = NativeImage.read(AbstractEarsRenderDelegate.toNativeBuffer(data));
				if(image == null) return;
				if(image.getWidth() == 64 && image.getHeight() == 64) {
					skinImage = image;
					wingImage = null;
					
					EarsImage earsImage = new NativeImageAdapter(skinImage);
					AlfalfaData alfalfa = Alfalfa.read(earsImage);
					features = EarsFeaturesParser.detect(earsImage, alfalfa, png -> new NativeImageAdapter(NativeImage.read(AbstractEarsRenderDelegate.toNativeBuffer(png))));
					
					if(alfalfa.data.containsKey("wing")) {
						wingImage = NativeImage.read(AbstractEarsRenderDelegate.toNativeBuffer(alfalfa.data.get("wing").toByteArray()));
					}

					// replace screen to reinitialize with imported features
					client.setScreen(new EarsConfigScreen(parent, features, skinImage, wingImage));
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
		File file = new File(EarsMod.getBaseDir(), "ears-wing.png");
		if(file.exists()) {
			try {
				byte[] data = Files.toByteArray(file);
				NativeImage image = NativeImage.read(AbstractEarsRenderDelegate.toNativeBuffer(data));
				if(image == null) return;
				if(image.getWidth() == 20 && image.getHeight() == 16) {
					wingImage = image;
					
					Map<String, Slice> alfalfa = new HashMap<>(features.alfalfa.data);
					alfalfa.put("wing", new Slice(data));
					features = EarsFeatures.builder(features).alfalfa(new AlfalfaData(features.alfalfa.version, alfalfa)).build();
					
					update(true);
				} else {
					image.close();
				}
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
		EarsMod.overrides.put(TexSource.SKIN, SKIN_OVERRIDE_ID);
		EarsMod.overrides.put(TexSource.WING, WING_OVERRIDE_ID);
		InventoryScreen.drawEntity(context, 0, 0, width/2, height, 100, 0.0625F, mouseX, mouseY, client.player);
		EarsMod.overrides.clear();
		EarsMod.overrideFeatures = null;
		context.getMatrices().pop();
	}
	
	public void close() {
		client.setScreen(parent);
	}
	
	@Environment(EnvType.CLIENT)
	public class ListWidget extends ContainerWidget {
		private final List<ClickableWidget> children = new ArrayList<>();
		private final LayoutWidget layout;

		public ListWidget(final LayoutWidget layout, final int width, final int height) {
			super(0, 0, width, height, ScreenTexts.EMPTY);
			this.layout = layout;
			layout.forEachChild(this::add);
		}

		public void add(ClickableWidget child) {
			children.add(child);
		}

		@Override
		protected int getContentsHeightWithPadding() {
			return layout.getHeight();
		}

		@Override
		protected double getDeltaYPerScroll() {
			return 10.0;
		}

		@Override
		protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
			context.enableScissor(getX(), getY(), getX() + width, getY() + height);

			for (ClickableWidget clickableWidget : children) {
				clickableWidget.render(context, mouseX, mouseY, delta);
			}

			context.disableScissor();
			drawScrollbar(context);
		}

		@Override
		protected void appendClickableNarrations(NarrationMessageBuilder builder) {
		}

		@Override
		public ScreenRect getBorder(NavigationDirection direction) {
			return new ScreenRect(getX(), getY(), width, getContentsHeightWithPadding());
		}

		@Override
		public void setFocused(@Nullable Element focused) {
			super.setFocused(focused);
			//TODO: this code does not match current scroll behaviour -> update
			/*if (focused != null) {
				ScreenRect screenRect = getNavigationFocus();
				ScreenRect screenRect2 = focused.getNavigationFocus();
				
				int i = (int)(screenRect2.getTop() - getScrollY() - screenRect.getTop());
				int j = (int)(screenRect2.getBottom() - getScrollY() - screenRect.getBottom());
				
				if (i < 0) {
					setScrollY(getScrollY() + i - 14.0);
				} else if (j > 0) {
					setScrollY(getScrollY() + j + 14.0);
				}
			}*/
		}

		@Override
		public List<? extends Element> children() {
			return children;
		}

		@Override
		public void setX(int x) {
			super.setX(x);
			layout.setX(x);
			layout.refreshPositions();
		}

		@Override
		public void setY(int y) {
			super.setY(y);
			layout.setY(getY() - (int) getScrollY());
			layout.refreshPositions();
		}
		
		@Override
		public void setScrollY(double scrollY) {
			super.setScrollY(scrollY);
			this.layout.setY(getY() - (int) getScrollY());
		}

		@Override
		public Collection<? extends Selectable> getNarratedParts() {
			return children;
		}
	}
}
