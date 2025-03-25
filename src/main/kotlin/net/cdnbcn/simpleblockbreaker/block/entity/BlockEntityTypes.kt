package net.cdnbcn.simpleblockbreaker.block.entity

import net.cdnbcn.simpleblockbreaker.block.BlockTypes
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

object BlockEntityTypes {
    private fun <T: BlockEntityType<*>> register(path: String, blockEntityType: T): T {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("simpleblockbreaker", path), blockEntityType)
    }

    val BREAKER_BLOCK = register(
        "breaker",
        FabricBlockEntityTypeBuilder.create({bp: BlockPos, bs: BlockState -> BreakerBlockEntity(bp, bs)}, BlockTypes.BREAKER_BLOCK.block).build()
    )

    val PLACER_BLOCK = register(
        "placer",
        FabricBlockEntityTypeBuilder.create({bp: BlockPos, bs: BlockState -> PlacerBlockEntity(bp, bs)}, BlockTypes.PLACER_BLOCK.block).build()
    )

    fun initialize() {
    }
}