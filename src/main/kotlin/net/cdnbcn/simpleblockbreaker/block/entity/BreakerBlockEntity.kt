package net.cdnbcn.simpleblockbreaker.block.entity

import net.cdnbcn.simpleblockbreaker.block.inventory.gui.BaseMenu
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.state.BlockState


class BreakerBlockEntity(pos: BlockPos, state: BlockState) : BaseBlockEntity<BreakerBlockEntity>(
    BlockEntityTypes.BREAKER_BLOCK.get(), pos, state,
    Component.translatable("block.simple_block_breaker.breaker"),
    Component.literal("Block Breaker"),
    ::BaseMenu
)