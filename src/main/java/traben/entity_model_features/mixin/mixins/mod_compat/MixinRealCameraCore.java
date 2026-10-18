package traben.entity_model_features.mixin.mixins.mod_compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.animation.state.EMFState;
import traben.entity_texture_features.features.state.ETFState;

@Pseudo
@Mixin(targets = "com.xtracr.realcamera.RealCameraCore", remap = false)
public class MixinRealCameraCore {
    // RealCamera builds its own vertex catcher, skips the normal entity render flow, and manually
    // submits the player via EntityRenderDispatcher.submit().
//#if MC == 1.20.1 && !NEOFORGE || MC >= 1.21.1 && MC <= 1.21.4 && !FORGE || MC >= 26.1

    // The following methods invoke EntityRenderDispatcher.submit().
    @Inject(method = "computeCamera", at = @At("HEAD"))
    private static void EFM$onComputeCameraHead(Minecraft client, float partialTicks, CallbackInfo ci) {
        EMFManager.getInstance().entityRenderCount++;
        var state = EMFEntityRenderState.manualPlayerState();
        if (state != null) ETFState.mount(state);
    }

    @Inject(method = "computeCamera", at = @At("RETURN"))
    private static void EFM$onComputeCameraReturn(Minecraft client, float partialTicks, CallbackInfo ci) {
        var state = EMFState.state();
        if (state != null && state.isManualPlayerState()) ETFState.unMount();
    }

    @Inject(method = "renderCameraEntity", at = @At("HEAD"))
    private static void EFM$onRenderCameraEntityHead(Minecraft client, float partialTicks, SubmitNodeCollector submitNodeCollector, Matrix4f modelView, CallbackInfo ci) {
        EMFManager.getInstance().entityRenderCount++;
        var state = EMFEntityRenderState.manualPlayerState();
        if (state != null) ETFState.mount(state);
    }

    @Inject(method = "renderCameraEntity", at = @At("RETURN"))
    private static void EFM$onRenderCameraEntityReturn(Minecraft client, float partialTicks, SubmitNodeCollector submitNodeCollector, Matrix4f modelView, CallbackInfo ci) {
        var state = EMFState.state();
        if (state != null && state.isManualPlayerState()) ETFState.unMount();
    }
//#endif
}