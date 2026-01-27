package net.cdnbcn.simpleblockbreaker.block.entity

import net.cdnbcn.simpleblockbreaker.BlockBreakerMod
import net.cdnbcn.simpleblockbreaker.block.BlockTypes
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.registries.DeferredRegister

object BlockEntityTypes {
    val BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, BlockBreakerMod.ID)

    val BREAKER_BLOCK = BLOCK_ENTITY_TYPES.register("breaker") { ->
        //? =1.21.11 {
        BlockEntityType(
            ::BreakerBlockEntity,
            false,
            BlockTypes.BREAKER_BLOCK.get()
        )
        //?} elif =1.21.1 {
        /*
        BlockEntityType.Builder.of(
            ::BreakerBlockEntity,
            BlockTypes.BREAKER_BLOCK.get()
        ).build(null)
         *///?}
    }

    val PLACER_BLOCK = BLOCK_ENTITY_TYPES.register("placer") { ->
        //? =1.21.11 {
        BlockEntityType(
            ::PlacerBlockEntity,
            false,
            BlockTypes.PLACER_BLOCK.get()
        )
        //?} elif =1.21.1 {
        /*
        BlockEntityType.Builder.of(
            ::PlacerBlockEntity,
            BlockTypes.PLACER_BLOCK.get()
        ).build(null)
         *///?}
    }

    fun initialize() {
    }
}