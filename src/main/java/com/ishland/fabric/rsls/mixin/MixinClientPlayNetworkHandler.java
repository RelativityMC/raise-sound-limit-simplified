package com.ishland.fabric.rsls.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.util.thread.ThreadExecutor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Dynamic
    @Redirect(method = {"onPlaySound", "method_11104"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V"), require = 1)
    private <T extends PacketListener> void handleSoundsAsync(Packet<T> packet, T listener, ThreadExecutor<?> engine, @Share("rsls$client") LocalRef<ThreadExecutor<?>> rsls$client) {
        // no-op as we don't want this to be executed sync
        rsls$client.set(engine);
    }

    @Dynamic
    @ModifyExpressionValue(method = {"onPlaySound", "method_11104"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;world:Lnet/minecraft/client/world/ClientWorld;", opcode = Opcodes.GETFIELD))
    private ClientWorld checkWorldExistence(ClientWorld world, PlaySoundS2CPacket packet, @Share("rsls$client") LocalRef<ThreadExecutor<?>> rsls$client) {
        if (world == null) {
            NetworkThreadUtils.forceMainThread(packet, (ClientPlayNetworkHandler) (Object) this, rsls$client.get()); // force sync
        }
        return world;
    }

}
