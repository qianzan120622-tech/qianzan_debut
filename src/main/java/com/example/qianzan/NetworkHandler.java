package com.example.qianzan;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class NetworkHandler {

    // 定义包类型 ID
    public static final CustomPacketPayload.Type<ActionPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(QianZan.MODID, "action_packet"));

    // 注册方法 (由主类调用)
    public static void register(RegisterPayloadHandlersEvent event) {
        final var registrar = event.registrar("1");
        registrar.playBidirectional(
                TYPE,
                ActionPacket.STREAM_CODEC,
                new ActionPacket.Handler()
        );
    }

    // 数据包定义
    public record ActionPacket(int actionId) implements CustomPacketPayload {
        public static final StreamCodec<ByteBuf, ActionPacket> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, ActionPacket::actionId, ActionPacket::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() { return TYPE; }

        // 服务端处理逻辑
        public static class Handler implements net.neoforged.neoforge.network.handling.IPayloadHandler<ActionPacket> {
            @Override
            public void handle(ActionPacket payload, IPayloadContext context) {
                context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer player) {
                        handleAction(player, payload.actionId());
                    }
                });
            }
        }
    }

    // 技能触发逻辑
    private static void handleAction(ServerPlayer player, int actionId) {
        // 检查手持物品是否为“千斩”剑
        if (!player.getMainHandItem().is(QianZan.QIANZAN_SWORD.get())) return;

        var cooldowns = player.getCooldowns();
        var swordItem = player.getMainHandItem().getItem();

        if (actionId == 0) {
            // === R键：发射普通剑气 ===
            if (cooldowns.isOnCooldown(swordItem)) return;

            QianZanBeamEntity beam = new QianZanBeamEntity(player.level(), player);
            beam.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());

            Vec3 look = player.getLookAngle();
            beam.shoot(look.x, look.y, look.z, 1.5f, 1.0f); // 速度1.5

            player.level().addFreshEntity(beam);
            cooldowns.addCooldown(swordItem, 40); // 2秒冷却

        } else if (actionId == 1) {
            // === C键：护体剑气 ===
            if (cooldowns.isOnCooldown(swordItem)) return;

            for (int i = 0; i < 3; i++) {
                QianZanBeamEntity orbitBeam = new QianZanBeamEntity(player.level(), player);
                // 设置环绕参数 (0, 120, 240度)
                orbitBeam.setOrbit((float) (i * (Math.PI * 2 / 3)));
                player.level().addFreshEntity(orbitBeam);
            }
            cooldowns.addCooldown(swordItem, 200); // 10秒冷却
        }
    }
}