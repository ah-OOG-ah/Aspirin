package klaxon.klaxon.aspirin.mixinhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import cpw.mods.fml.relauncher.FMLLaunchHandler;

public enum Mixins {

    TIMER_START(new Builder("Start the launch timer").setSide(Side.CLIENT).setApplyIf(() -> true)
            .addTargetedMod(TargetedMod.VANILLA).setPhase(Phase.EARLY).addMixinClasses("minecraft.MixinMainClient")),
    FMLCH_TIMERS(new Builder("Run FMLClienthandler timers").setSide(Side.CLIENT).setApplyIf(() -> true)
            .addTargetedMod(TargetedMod.VANILLA).setPhase(Phase.EARLY)
            .addMixinClasses("minecraftforge.MixinFMLClientHandler")),
    MCCLIENT_TIMERS(new Builder("Run Minecraft client timers").setSide(Side.CLIENT).setApplyIf(() -> true)
            .addTargetedMod(TargetedMod.VANILLA).setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinMinecraftClient")),
    MC_SERVER_TIMERS(new Builder("Timer injection to MinecraftServer").setSide(Side.SERVER).setApplyIf(() -> true)
            .addTargetedMod(TargetedMod.VANILLA).setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.MixinMinecraftServer"));/*
                                                                 * LOADER_TIMERS(new
                                                                 * Builder("Inject timers into Loader").setSide(Side.
                                                                 * SERVER).setApplyIf(() -> true)
                                                                 * .addTargetedMod(TargetedMod.VANILLA).setPhase(Phase.
                                                                 * EARLY).addMixinClasses("fml.MixinLoader"));
                                                                 */

    public final String name;
    public final List<String> mixinClasses;
    private final Supplier<Boolean> applyIf;
    public final Phase phase;
    private final Side side;
    public final List<TargetedMod> targetedMods;
    public final List<TargetedMod> excludedMods;

    private static class Builder {

        private final String name;
        private final List<String> mixinClasses = new ArrayList<>();
        private Supplier<Boolean> applyIf;
        private Side side = Side.BOTH;
        private Phase phase = Phase.LATE;
        private final List<TargetedMod> targetedMods = new ArrayList<>();
        private final List<TargetedMod> excludedMods = new ArrayList<>();

        public Builder(String name) {
            this.name = name;
        }

        public Builder addMixinClasses(String... mixinClasses) {
            this.mixinClasses.addAll(Arrays.asList(mixinClasses));
            return this;
        }

        public Builder setPhase(Phase phase) {
            this.phase = phase;
            return this;
        }

        public Builder setSide(Side side) {
            this.side = side;
            return this;
        }

        public Builder setApplyIf(Supplier<Boolean> applyIf) {
            this.applyIf = applyIf;
            return this;
        }

        public Builder addTargetedMod(TargetedMod mod) {
            this.targetedMods.add(mod);
            return this;
        }

        public Builder addExcludedMod(TargetedMod mod) {
            this.excludedMods.add(mod);
            return this;
        }
    }

    Mixins(Builder builder) {
        this.name = builder.name;
        this.mixinClasses = builder.mixinClasses;
        this.applyIf = builder.applyIf;
        this.side = builder.side;
        this.targetedMods = builder.targetedMods;
        this.excludedMods = builder.excludedMods;
        this.phase = builder.phase;
        if (this.targetedMods.isEmpty()) {
            throw new RuntimeException("No targeted mods specified for " + this.name);
        }
        if (this.applyIf == null) {
            throw new RuntimeException("No ApplyIf function specified for " + this.name);
        }
    }

    private boolean shouldLoadSide() {
        return (side == Side.BOTH || (side == Side.SERVER && FMLLaunchHandler.side().isServer())
                || (side == Side.CLIENT && FMLLaunchHandler.side().isClient()));
    }

    private boolean allModsLoaded(List<TargetedMod> targetedMods, Set<String> loadedCoreMods, Set<String> loadedMods) {
        if (targetedMods.isEmpty()) return false;

        for (TargetedMod target : targetedMods) {
            if (target == TargetedMod.VANILLA) continue;

            // Check coremod first
            if (!loadedCoreMods.isEmpty() && target.coreModClass != null
                    && !loadedCoreMods.contains(target.coreModClass))
                return false;
            else if (!loadedMods.isEmpty() && target.modId != null && !loadedMods.contains(target.modId)) return false;
        }

        return true;
    }

    private boolean noModsLoaded(List<TargetedMod> targetedMods, Set<String> loadedCoreMods, Set<String> loadedMods) {
        if (targetedMods.isEmpty()) return true;

        for (TargetedMod target : targetedMods) {
            if (target == TargetedMod.VANILLA) continue;

            // Check coremod first
            if (!loadedCoreMods.isEmpty() && target.coreModClass != null
                    && loadedCoreMods.contains(target.coreModClass))
                return false;
            else if (!loadedMods.isEmpty() && target.modId != null && loadedMods.contains(target.modId)) return false;
        }

        return true;
    }

    public boolean shouldLoad(Set<String> loadedCoreMods, Set<String> loadedMods) {
        return (shouldLoadSide() && applyIf.get()
                && allModsLoaded(targetedMods, loadedCoreMods, loadedMods)
                && noModsLoaded(excludedMods, loadedCoreMods, loadedMods));
    }

    enum Side {
        BOTH,
        CLIENT,
        SERVER
    }

    public enum Phase {
        EARLY,
        LATE,
    }
}
