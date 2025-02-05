package com.legobmw99.allomancy.test.util;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class TattleTaleStack extends CommandSourceStack {
    public static TattleTaleStack createCommandSourceStack(ServerPlayer player) {
        return new TattleTaleStack(CommandSource.NULL, player.position(), player.getRotationVector(),
                                   player.serverLevel(), player.getPermissionLevel(), player.getName().getString(),
                                   player.getDisplayName(), player.level().getServer(), player);
    }

    public TattleTaleStack(CommandSource p_81302_,
                           Vec3 p_81303_,
                           Vec2 p_81304_,
                           ServerLevel p_81305_,
                           int p_81306_,
                           String p_81307_,
                           Component p_81308_,
                           MinecraftServer p_81309_,
                           Entity p_81310_) {
        super(p_81302_, p_81303_, p_81304_, p_81305_, p_81306_, p_81307_, p_81308_, p_81309_, p_81310_);
    }

    final List<Component> errors = new ArrayList<>();
    final List<Component> results = new ArrayList<>();

    public boolean hadError() {
        return !errors.isEmpty();
    }

    public String errors() {
        return commaSeparate(this.errors);

    }

    public String results() {
        return commaSeparate(this.results);
    }

    @Override
    public void sendSuccess(Supplier<Component> messageSupplier, boolean allowLogging) {
        var comp = messageSupplier.get();
        results.add(comp);

        super.sendSuccess(() -> comp, allowLogging);
    }

    @Override
    public void sendSystemMessage(Component message) {
        results.add(message);

        super.sendSystemMessage(message);
    }

    @Override
    public void sendFailure(Component failure) {
        errors.add(failure);
        super.sendFailure(failure);
    }

    static String commaSeparate(List<Component> components) {
        StringBuilder s = new StringBuilder();
        for (var r : components) {
            s.append(r.getString());
            s.append(", ");
        }
        var len = s.length();
        if (len > 2) {
            s.delete(len - 2, len);
        }
        return s.toString();
    }
}
