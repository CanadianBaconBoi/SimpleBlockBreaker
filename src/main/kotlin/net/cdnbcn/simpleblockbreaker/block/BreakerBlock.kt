package net.cdnbcn.simpleblockbreaker.block

import net.cdnbcn.simpleblockbreaker.BlockBreakerMod
import net.cdnbcn.simpleblockbreaker.block.entity.BreakerBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import kotlin.math.min

class BreakerBlock(settings: Properties) : BaseBlock<BreakerBlock, BreakerBlockEntity>(settings, ::BreakerBlock, ::BreakerBlockEntity) {
    override fun tick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity !is BreakerBlockEntity) return
        val facing: Direction = state.getValue(FACING)
        val targetPos = pos.relative(facing)
        val blockState = world.getBlockState(targetPos)
        world.addDestroyBlockEffect(targetPos, blockState)

        val config = BlockBreakerMod.PROCESSED_CONFIG

        if (blockState.isAir) {
            return
        }
        val flag = config.breakerListType == BlockBreakerMod.Config.ListType.WHITELIST
        if (config.breakerListItems.contains(blockState.block) != flag) {
            return
        }

        if (blockState.block.defaultDestroyTime() < 0.0f) {
            if (!config.canBreakUnbreakable) {
                return
            }
            val flag1 = config.unbreakableListType == BlockBreakerMod.Config.ListType.WHITELIST
            if (config.unbreakableListItems.contains(blockState.block) != flag1) {
                return
            }
        }

        val stacks = blockState.getDrops(
            LootParams.Builder(world).withParameter(LootContextParams.ORIGIN, pos.center)
                .withParameter(LootContextParams.TOOL, ItemStack(Items.IRON_PICKAXE))
        )
        for (stack in stacks) {
            val items = blockEntity.getItems()
            for (i in 0..<9) {
                val slot = items[i]
                if (stack.item == slot.item && slot.count < slot.maxStackSize) {
                    val maxTake = slot.maxStackSize - slot.count
                    val take = min(stack.count, maxTake)
                    slot.count += take
                    stack.count -= take
                } else if (slot.isEmpty) {
                    items[i] = stack.copyAndClear()
                }
            }
        }

        for (stack in stacks.filter { !it.isEmpty }) {
            val dPos = targetPos.center
            val itemEntity = ItemEntity(
                world, dPos.x, dPos.y, dPos.z,
                stack, 0.0, 0.5, 0.0
            )
            world.addFreshEntity(itemEntity)
        }
        world.destroyBlock(targetPos, false)
    }
}