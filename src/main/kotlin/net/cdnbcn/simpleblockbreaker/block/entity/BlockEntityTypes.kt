package net.cdnbcn.simpleblockbreaker.block.entity

import net.cdnbcn.simpleblockbreaker.BlockBreakerMod
import net.cdnbcn.simpleblockbreaker.block.BlockTypes
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.registries.DeferredRegister

object BlockEntityTypes {
    val BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, BlockBreakerMod.ID)

    val BREAKER_BLOCK = BLOCK_ENTITY_TYPES.register("breaker") { ->
        newBlockEntity(::BreakerBlockEntity, BlockTypes.BREAKER_BLOCK.get())
    }

    val PLACER_BLOCK = BLOCK_ENTITY_TYPES.register("placer") { ->
        newBlockEntity(::PlacerBlockEntity, BlockTypes.PLACER_BLOCK.get())
    }


    private fun <T : BlockEntity> newBlockEntity(
        factory: BlockEntityType.BlockEntitySupplier<T>,
        block: Block,
        onlyOpCanSetNbt: Boolean = false
    ): BlockEntityType<T> =
        //? if >=1.21.11 {
        BlockEntityType(factory, onlyOpCanSetNbt, block)
        //? } elif <=1.21.1 {
        // BlockEntityType.Builder.of(factory, block).build(null)
        //? }

    //Dead method call causes access to occur thus initializing the vals
    fun initialize() {}
}