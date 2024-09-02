package com.unascribed.ears.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HumanoidArmorLayer.class)
public interface AccessorHumanoidArmorLayer<T extends LivingEntity, A extends HumanoidModel<T>> {

    @Accessor("innerModel")
    HumanoidModel<?> ears$getLeggingsModel();
    @Accessor("outerModel")
    HumanoidModel<?> ears$getBodyModel();

    @Invoker("getArmorModel")
    HumanoidModel<?> ears$getArmor(EquipmentSlot slot);
    @Invoker("usesInnerModel")
    boolean ears$usesSecondLayer(EquipmentSlot slot);

    @Invoker("renderModel")
    void ears$renderArmorParts(PoseStack matrices, MultiBufferSource vertexConsumers, int light, A model, int color, ResourceLocation overlay);
}
