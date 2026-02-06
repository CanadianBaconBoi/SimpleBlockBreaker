package net.cdnbcn.simpleblockbreaker

import net.cdnbcn.simpleblockbreaker.block.inventory.gui.BaseMenu
import net.minecraft.core.registries.Registries
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.MenuType
import net.neoforged.neoforge.registries.DeferredRegister


object ScreenHandlerTypes {
    val MENUS = DeferredRegister.create(Registries.MENU, BlockBreakerMod.ID)

    @Suppress("unused")
    val BASE_MENU = MENUS.register("base_menu") { -> MenuType(::BaseMenu, FeatureFlags.DEFAULT_FLAGS) }

    //Dead method call causes access to occur thus initializing the vals
    fun initialize() {}
}