package com.legobmw99.allomancy.modules.extras.mixin;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import net.minecraft.util.datafix.fixes.BannerPatternFormatFix;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * TEMPORARY: Used to port 1.20 banners to 1.21
 */
@Mixin(BannerPatternFormatFix.class)
public class BannerDFUMixin {
    @Mutable
    @Final
    @Shadow
    private static Map<String, String> PATTERN_ID_MAP;

    @Inject(at = @At("RETURN"), method = "<clinit>")
    private static void onConstruct(CallbackInfo info) {

        Allomancy.LOGGER.info("Injecting to banner DFU");
        var patternMap = new HashMap<>(PATTERN_ID_MAP);

        for (Metal mt : Metal.values()) {
            String name = mt.getName();
            Allomancy.LOGGER.info("Redirecting banner pattern for {}", name);
            patternMap.put("ALLOMANCY" + name.toUpperCase(Locale.ROOT), Allomancy.rl(name).toString());
        }

        PATTERN_ID_MAP = patternMap;
    }
}
