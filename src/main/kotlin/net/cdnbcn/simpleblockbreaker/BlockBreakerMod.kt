package net.cdnbcn.simpleblockbreaker

import net.cdnbcn.simpleblockbreaker.block.BlockTypes
import net.cdnbcn.simpleblockbreaker.block.entity.BlockEntityTypes
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer

class BlockBreakerMod : ModInitializer, ClientModInitializer {
    override fun onInitialize() {
        BlockTypes.initialize()
        BlockEntityTypes.initialize()
        ScreenHandlerTypes.initialize()
    }

    override fun onInitializeClient() {
        ScreenHandlerTypes.initializeClient()
    }
}