package net.cdnbcn.simpleblockbreaker.block.entity

import net.cdnbcn.simpleblockbreaker.block.inventory.gui.BreakerBlockMenu
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.state.BlockState


class PlacerBlockEntity(pos: BlockPos, state: BlockState) : BaseBlockEntity<PlacerBlockEntity>(
    BlockEntityTypes.PLACER_BLOCK.get(), pos, state,
    Component.translatable("block.simple_block_breaker.placer"),
    Component.literal("Block Placer"),
    { cId, inv, ctr -> BreakerBlockMenu(cId, inv, ctr) }
)