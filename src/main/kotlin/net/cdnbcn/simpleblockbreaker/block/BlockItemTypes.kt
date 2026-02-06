package net.cdnbcn.simpleblockbreaker.block

import net.cdnbcn.simpleblockbreaker.BlockBreakerMod
import net.cdnbcn.simpleblockbreaker.item.BreakerItem
import net.minecraft.world.item.BlockItem
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister

object BlockItemTypes {
    val BLOCK_ITEMS: DeferredRegister.Items = DeferredRegister.createItems(BlockBreakerMod.ID)
    val BREAKER_BLOCK_ITEM: DeferredItem<BlockItem> = BLOCK_ITEMS.registerItem("breaker") { properties ->
        BreakerItem(
            BlockTypes.BREAKER_BLOCK.get(),
            properties
        )
    }

    val PLACER_BLOCK_ITEM: DeferredItem<BlockItem> = BLOCK_ITEMS.registerSimpleBlockItem(
        "placer", BlockTypes.PLACER_BLOCK
    )

    //Dead method call causes access to occur thus initializing the vals
    fun initialize() {}
}