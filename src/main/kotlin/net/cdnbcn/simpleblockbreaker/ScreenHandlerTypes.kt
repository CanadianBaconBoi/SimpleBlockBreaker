package net.cdnbcn.simpleblockbreaker

import net.cdnbcn.simpleblockbreaker.block.inventory.gui.BreakerGuiDescription
import net.cdnbcn.simpleblockbreaker.block.inventory.gui.BreakerGuiScreen
import net.cdnbcn.simpleblockbreaker.block.inventory.gui.PlacerGuiDescription
import net.cdnbcn.simpleblockbreaker.block.inventory.gui.PlacerGuiScreen
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.text.Text
import net.minecraft.util.Identifier


object ScreenHandlerTypes {
    val BREAKER_HANDLER = register("breaker") { syncId: Int, inventory: PlayerInventory ->
        BreakerGuiDescription(
            syncId,
            inventory,
            ScreenHandlerContext.EMPTY
        )
    }
    val PLACER_HANDLER = register("placer") { syncId: Int, inventory: PlayerInventory ->
        PlacerGuiDescription(
            syncId,
            inventory,
            ScreenHandlerContext.EMPTY
        )
    }

    private fun <T: ScreenHandler> register(
        path: String,
        factory: (Int, PlayerInventory) -> T,
    ): ScreenHandlerType<T> {
        val identifier: Identifier = Identifier.of("simpleblockbreaker", path)
        val registryKey: RegistryKey<ScreenHandlerType<*>> = RegistryKey.of(RegistryKeys.SCREEN_HANDLER, identifier)

        val screenHandler = Registry.register(Registries.SCREEN_HANDLER, registryKey, ScreenHandlerType(factory, FeatureFlags.VANILLA_FEATURES))
        return screenHandler
    }

    fun initialize() {
    }

    fun initializeClient() {
        HandledScreens.register<BreakerGuiDescription, BreakerGuiScreen>(BREAKER_HANDLER)
        { gui: BreakerGuiDescription?, inventory: PlayerInventory, title: Text? ->
            BreakerGuiScreen(
                gui,
                inventory.player,
                title
            )
        }

        HandledScreens.register<PlacerGuiDescription, PlacerGuiScreen>(PLACER_HANDLER)
        { gui: PlacerGuiDescription?, inventory: PlayerInventory, title: Text? ->
            PlacerGuiScreen(
                gui,
                inventory.player,
                title
            )
        }
    }
}