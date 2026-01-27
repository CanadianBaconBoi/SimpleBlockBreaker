package net.cdnbcn.simpleblockbreaker

import net.cdnbcn.simpleblockbreaker.block.inventory.gui.BreakerBlockMenu
import net.minecraft.core.registries.Registries
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.MenuType
import net.neoforged.neoforge.registries.DeferredRegister


object ScreenHandlerTypes {
    val MENUS = DeferredRegister.create(Registries.MENU, BlockBreakerMod.ID)

    @Suppress("unused")
    val BREAKER_MENU = MENUS.register("breaker") { -> MenuType(::BreakerBlockMenu, FeatureFlags.DEFAULT_FLAGS) }

    fun initialize() {}
}