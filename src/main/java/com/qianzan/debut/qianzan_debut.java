package com.qianzan.debut;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(qianzan_debut.MODID)
public class qianzan_debut {
    // 模组 ID
    public static final String MODID = "qianzan_debut";
    public static final Logger LOGGER = LogUtils.getLogger();

    // 1. 创建物品注册表
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    // 2. 注册你的第一个物品！名字叫 "my_first_item"
    public static final DeferredItem<Item> MY_FIRST_ITEM = ITEMS.register("my_first_item", () -> new Item(new Item.Properties()));

    // 构造函数：模组初始化
    public qianzan_debut(IEventBus modEventBus) {
        // 注册物品表到总线
        ITEMS.register(modEventBus);
        // 注册创造模式物品栏事件
        modEventBus.addListener(this::addCreative);
    }

    // 3. 把物品加到“原材料”，这样你在创造模式就能拿到了
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(MY_FIRST_ITEM);
        }
    }
}