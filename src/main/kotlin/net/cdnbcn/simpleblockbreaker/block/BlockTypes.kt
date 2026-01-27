package net.cdnbcn.simpleblockbreaker.block

import net.cdnbcn.simpleblockbreaker.BlockBreakerMod
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister

object BlockTypes {
    val BLOCKS: DeferredRegister.Blocks = DeferredRegister.createBlocks(BlockBreakerMod.ID)

    val BREAKER_BLOCK: DeferredBlock<BreakerBlock> = BLOCKS.registerBlock("breaker") { properties ->
        BreakerBlock(
            properties
                .strength(1.0f)
                .requiresCorrectToolForDrops()
        )
    }

    val PLACER_BLOCK: DeferredBlock<PlacerBlock> = BLOCKS.registerBlock("placer") { properties ->
        PlacerBlock(
            properties
                .strength(1.0f)
                .requiresCorrectToolForDrops()
        )
    }

    fun initialize() {
    }
}