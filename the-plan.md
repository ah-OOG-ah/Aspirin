# The plan

Arthritis aims to reduce loading times by caching classes and more to disk, then reloading from disk instead of regenerating everything.
In orfer to achieve this, we need to get the state of Creaking objects after mod loading, cache it, then inject it before mod loading.

### Stage 0: Profiling

Figure out what actually takes time when loading... ugh.

This is the Gradle load process:

- Run ((GradleStartCommon) GradleStart).launch(String[] args):
  - Run GradleStart.preLaunch(Map<String, String> argMap, List<String> extras):
    - Attempt to login
    - Setup assets
  - Run ((GradleStartCommon) GradleStart).searchCoremods()
    and add the found coremods.
  - Run net.minecraft.launchwrapper.Launch.main(args)

Now we go to LaunchWrapper, the process from here should be similar across all launch methods.

- Run Launch.launch(args):
  - This applies tweakers, whatever those are, then starts the true target:
    `net.minecraft.client.Main.main()`

1. This then runs `Minecraft minecraft.run()`, where we can finally start messing with things.
  - Inside, we run `minecraft.startGame()`. ~~AFAICT this doesn't activate the Forge mod loading hooks~~ I'm super dumb, ALL the loading hooks are called here.

At `Minecraft.java:522`, <br>

call: `FMLClientHandler.instance().beginMinecraftLoading(this, this.defaultResourcePacks, this.mcResourceManager);`
* then `FMLCommonHandler.instance().beginLoading(this);`
  * then `callForgeMethod("initialize");`
    * then `FluidRegistry.validateFluidRegistry();`
  * then `callForgeMethod("registerCrashCallable");`
    * then `???`
* then `Loader.instance().loadMods();` **Important!**
  This begins mod loading Phase 0: Constructing Mods.
  * then `initializeLoader();`
    * then `readInjectedDependencies()`
  * then `modController.transition(LoaderState.LOADING, false);`
  * then `modController.distributeStateMessage(FMLLoadEvent.class);`
  * then `modController.transition(LoaderState.CONSTRUCTING, false);`
  * then `modController.distributeStateMessage(LoaderState.CONSTRUCTING, modClassLoader, discoverer.getASMTable(), reverseDependencies);`
  * then `progressBar.step("Initializing mods Phase 1");`
  * then `modController.transition(LoaderState.PREINITIALIZATION, false);` **Important!** This begins mod loading Phase 1: Pre-initialization. **This is where 99% of mod code (to my knowledge) starts doing things.**

### Stage I: It's mixin' time!

Objective: Find where

<style>
  li {list-style-type: none;}
</style>
