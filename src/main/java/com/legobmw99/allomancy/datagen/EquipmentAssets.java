package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

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

    private static void add(BiConsumer<Identifier, EquipmentClientInfo> registrar) {
        registrar.accept(CombatSetup.WOOL.identifier(),

                         EquipmentClientInfo
                                 .builder()
                                 .addLayers(EquipmentClientInfo.LayerType.HUMANOID,
                                            new EquipmentClientInfo.Layer(Allomancy.id("mistcloak"), Optional.empty(),
                                                                          false))
                                 .build());

        registrar.accept(CombatSetup.ALUMINUM.identifier(),

                         EquipmentClientInfo
                                 .builder()
                                 .addLayers(EquipmentClientInfo.LayerType.HUMANOID,
                                            new EquipmentClientInfo.Layer(Allomancy.id("aluminum"), Optional.empty(),
                                                                          false))
                                 .build());

        registrar.accept(ExtrasSetup.BRONZE.identifier(),

                         EquipmentClientInfo
                                 .builder()
                                 .addLayers(EquipmentClientInfo.LayerType.HUMANOID,
                                            new EquipmentClientInfo.Layer(Allomancy.id("bronze_earring"),
                                                                          Optional.empty(), false))
                                 .build());
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Map<Identifier, EquipmentClientInfo> map = new HashMap<>();
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
