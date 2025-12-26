package com.example.qianzan;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(QianZan.MODID)
public class QianZan {
    public static final String MODID = "qianzan";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

    public static final Tier QIANZAN_TIER = new Tier() {
        @Override public int getUses() { return 244; }
        @Override public float getSpeed() { return 8.0F; }
        @Override public float getAttackDamageBonus() { return 3.0F; }
        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_DIAMOND_TOOL; }
        @Override public int getEnchantmentValue() { return 15; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(Items.AMETHYST_SHARD); }
    };

    public static final DeferredItem<Item> QIANZAN_SWORD = ITEMS.register("qianzan",
            () -> new SwordItem(QIANZAN_TIER, new Item.Properties()
                    .attributes(SwordItem.createAttributes(QIANZAN_TIER, 3, -2.4f)))
    );

    public static final DeferredItem<Item> QIANZAN_BEAM_SPRITE = ITEMS.registerSimpleItem("qianzan_beam_sprite", new Item.Properties());

    public static final DeferredHolder<EntityType<?>, EntityType<QianZanBeamEntity>> QIANZAN_BEAM = ENTITY_TYPES.register("qianzan_beam",
            () -> EntityType.Builder.<QianZanBeamEntity>of(QianZanBeamEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .build("qianzan_beam")
    );

    // ==================== 创造模式栏 ====================
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> QIANZAN_TAB = CREATIVE_MODE_TABS.register("qianzan_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.qianzan"))
            .icon(() -> QIANZAN_SWORD.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(QIANZAN_SWORD.get());
            }).build());

    public QianZan(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.addListener(NetworkHandler::register);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(QianZanClient::registerRenderers);
            modEventBus.addListener(QianZanClient::registerKeys);
            NeoForge.EVENT_BUS.register(QianZanClient.ClientGameEvents.class);
        }
    }
}