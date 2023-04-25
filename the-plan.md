# The plan

Arthritis aims to reduce loading times by caching classes and more to disk, then reloading from disk instead of regenerating everything.
In orfer to achieve this, we need to get the state of Creaking objects after mod loading, cache it, then inject it before/during mod loading on subsequent runs.

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
    `net.minecraft.client.Main.main()`... but only on the client. The following steps occur on client launches; servers come later.

- This then runs `(Minecraft) minecraft.run()`, where we can finally start messing with things.
  - Inside, we run `minecraft.startGame()`. ~~AFAICT this doesn't activate the Forge mod loading hooks~~ I'm super dumb, ALL the loading hooks are called here.

At `Minecraft.java:522`, <br>

* call: `FMLClientHandler.instance().beginMinecraftLoading(this, this.defaultResourcePacks, this.mcResourceManager);`
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
  * then `Loader.instance().preinitializeMods();`
    * In here, ObjectHolders and ItemStackHolders are injected. I suspect this is where registries are loaded.
    * **Important!** At the end of this method begins mod loading Phase 2: Initialization. **This is where a ton of MC data &such is loaded or created.**

  We're back in startGame, but the fun's just begun --- it's only stage 2! </br>

* then set the texture and skin managers an a reload listener. Actually, loads a **TON** of rendering/resource stuff here. </br>
then `FMLClientHandler.instance().finishMinecraftLoading();` **Important!** Several phases are contained here, the first of which is Phase 2: Initialization **This is where IMC happens, GameData is frozen, and registries are dumped.** </br>
* then `FMLClientHandler.instance().onInitializationComplete();` **Important!** We're done here, MC is loaded... but I thought there were 4 stages?

But what if you wanted to launch a server? Then it goes:
* Same launch process all the way till `net.minecraft.Main.main()`. This main method is selected by the primary tweaker, and on a server it selects `net.minecraft.server.MinecraftServer#main` instead.
  * then `net.minecraft.server.MinecraftServer#startServerThread` into `net.minecraft.server.MinecraftServer#run`
    * then `net.minecraft.server.MinecraftServer#startServer`. On a dedicated server, this calls `net.minecraft.server.dedicated.DedicatedServer#startServer`.
      * then `cpw.mods.fml.common.FMLCommonHandler#onServerStart`
        * then `cpw.mods.fml.server.FMLServerHandler#beginServerLoading`
          * then `cpw.mods.fml.common.Loader#loadMods` **Important!** This begins Phase 0: Constructing Mods. **This is where bytecode is loaded and LateMixins fire**
          * then `cpw.mods.fml.common.Loader#preinitializeMods` **Important!** This begins Phase 1: Pre-initialization
            * Inject Object and Item holder registries.
        * then Set a bunch of properties
        * then after a few calls, `cpw.mods.fml.common.Loader#initializeMods` **Important!** This begins Phase 2: Initialization. At the end, GameData is frozen and registries are dumped.

Technically, loading is done now. After this is world loading, which falls in the scope of this mod because I said so.

        * then `cpw.mods.fml.common.Loader#serverAboutToStart`
        * then `cpw.mods.fml.common.Loader#serverStarting`

Your world is now running!

### Stage I: It's mixin' time!

Objective: Find where to best inject timers so profiling can actually happen.

<ol>
<li>Client:
  <ul>
    <li>[x] Start the timer
    <li>[x] Phase -1: Begin MC Loading
    <li>[x] Phase 0: Constructing Mods
    <li>[x] Phase 1: Pre-initialization
    <li>[x] Phase 2: Initialization
    <li>[x] Stop the timer
  </ul>
</li>
<li>Server:
  <ul>
    <li>[ ] Start the timer
    <li>[ ] Phase -1: Begin MC Loading
    <li>[ ] Phase 0: Constructing Mods
    <li>[ ] Phase 1: Pre-initialization
    <li>[ ] Phase 2: Initialization
    <li>[ ] Stop the timer
  </ul>
</li>
</ol>
<!--<style>
   {list-style-type: none;}
</style>-->
