package com.ishland.fabric.rsls.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Dynamic
    @WrapOperation(
            method = {"onPlaySound", "method_11104"},
            at = {
                    @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;method_11074(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V"),
                    @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/network/PacketApplyBatcher;)V"),
            },
            require = 1
    )
    private <T extends PacketListener> void handleSoundsAsync(Packet<T> packet, T listener, @Coerce Object thirdParam, Operation<Void> original,
                                                              @Share("rsls$thirdParam") LocalRef<Object> rsls$thirdParam,
                                                              @Share("rsls$originalOp") LocalRef<Operation<Void>> rsls$originalOperation) {
        // no-op as we don't want this to be executed sync
        rsls$originalOperation.set(original);
    }

    @Dynamic
    @ModifyExpressionValue(method = {"onPlaySound", "method_11104"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;world:Lnet/minecraft/client/world/ClientWorld;", opcode = Opcodes.GETFIELD))
    private <T extends PacketListener> ClientWorld checkWorldExistence(ClientWorld world, PlaySoundS2CPacket packet,
                                                                       @Share("rsls$thirdParam") LocalRef<Object> rsls$thirdParam,
                                                                       @Share("rsls$originalOp") LocalRef<Operation<Void>> rsls$originalOperation) {
        if (world == null) {
            rsls$originalOperation.get().call(packet, this, rsls$thirdParam.get());
            return null; // unreachable
        }
        return world;
    }

}
