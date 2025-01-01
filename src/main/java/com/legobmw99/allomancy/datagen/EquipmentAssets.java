package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

class EquipmentAssets implements DataProvider {

    private final PackOutput.PathProvider path;

    public EquipmentAssets(PackOutput output) {
        this.path = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "equipment");
    }

    private static void add(BiConsumer<ResourceLocation, EquipmentClientInfo> registrar) {
        registrar.accept(CombatSetup.WOOL.location(),

                         EquipmentClientInfo
                                 .builder()
                                 .addLayers(EquipmentClientInfo.LayerType.HUMANOID,
                                            new EquipmentClientInfo.Layer(Allomancy.rl("mistcloak"), Optional.empty(),
                                                                          false))
                                 .build());

        registrar.accept(CombatSetup.ALUMINUM.location(),

                         EquipmentClientInfo
                                 .builder()
                                 .addLayers(EquipmentClientInfo.LayerType.HUMANOID,
                                            new EquipmentClientInfo.Layer(Allomancy.rl("aluminum"), Optional.empty(),
                                                                          false))
                                 .build());
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Map<ResourceLocation, EquipmentClientInfo> map = new HashMap<>();
        EquipmentAssets.add((name, model) -> {
            if (map.putIfAbsent(name, model) != null) {
                throw new IllegalStateException("Tried to register equipment model twice for id: " + name);
            }
        });
        return DataProvider.saveAll(cache, EquipmentClientInfo.CODEC, this.path, map);
    }


    @Override
    public String getName() {
        return "Allomancy equipment";
    }
}
