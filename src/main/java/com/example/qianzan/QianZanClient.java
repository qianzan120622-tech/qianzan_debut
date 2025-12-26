package com.example.qianzan;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public class QianZanClient {

    // 按键定义保持不变
    public static final KeyMapping KEY_SHOOT = new KeyMapping(
            "key.qianzan.shoot", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories.qianzan");

    public static final KeyMapping KEY_ORBIT = new KeyMapping(
            "key.qianzan.orbit", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, "key.categories.qianzan");

    // ==================== MOD 总线事件 (初始化用) ====================

    // 注册渲染器 (将在主类中通过 addListener 调用)
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(QianZan.QIANZAN_BEAM.get(), ThrownItemRenderer::new);
    }

    // 注册按键 (将在主类中通过 addListener 调用)
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(KEY_SHOOT);
        event.register(KEY_ORBIT);
    }

    // ==================== GAME 总线事件 (游戏运行时用) ====================

    // 这是一个静态内部类，专门用来监听游戏运行时的事件（如每刻刷新）
    // 我们将在主类中通过 NeoForge.EVENT_BUS.register 加载它
    public static class ClientGameEvents {

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            // 检测 R 键
            while (KEY_SHOOT.consumeClick()) {
                PacketDistributor.sendToServer(new NetworkHandler.ActionPacket(0));
            }
            // 检测 C 键
            while (KEY_ORBIT.consumeClick()) {
                PacketDistributor.sendToServer(new NetworkHandler.ActionPacket(1));
            }
        }
    }
}