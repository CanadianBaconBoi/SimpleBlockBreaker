package net.cdnbcn.simpleblockbreaker.block

import net.cdnbcn.simpleblockbreaker.BlockBreakerMod
import net.cdnbcn.simpleblockbreaker.block.entity.BlockEntityTypes
import net.cdnbcn.simpleblockbreaker.block.entity.BreakerBlockEntity
import net.cdnbcn.simpleblockbreaker.item.BreakerToolComponent
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import kotlin.jvm.optionals.getOrNull
import kotlin.math.min

class BreakerBlock(settings: Properties) :
    BaseBlock<BreakerBlock, BreakerBlockEntity>(settings, ::BreakerBlock, ::BreakerBlockEntity) {

    companion object {
        private val IRON_PICKAXE = ItemStack(Items.IRON_PICKAXE)
    }

    override fun getDrops(state: BlockState, params: LootParams.Builder): List<ItemStack> {
        val drops = super.getDrops(state, params)

        val be = params.getParameter(LootContextParams.BLOCK_ENTITY)
        val tool = (be as? BreakerBlockEntity)?.tool ?: return drops

        for (i in drops.indices) {
            val stack = drops[i]
            if (stack.`is`(state.block.asItem())) {
                drops[i] = BreakerToolComponent.withTool(stack, tool, params.level.registryAccess())
                break
            }
        }

        return drops
    }

    override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        super.setPlacedBy(level, pos, state, placer, stack)
        val blockEntity = level.getBlockEntity(pos, BlockEntityTypes.BREAKER_BLOCK.get()).getOrNull() ?: return
        blockEntity.tool = BreakerToolComponent.getTool(stack, level.registryAccess())
        blockEntity.setChanged()
    }

    override fun tick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        val blockEntity = world.getBlockEntity(pos, BlockEntityTypes.BREAKER_BLOCK.get()).getOrNull() ?: return

        val targetPos = pos.relative(state.getValue(FACING))
        val targetState = world.getBlockState(targetPos)

        if (targetState.isAir) return
        world.addDestroyBlockEffect(targetPos, targetState)

        val config = BlockBreakerMod.PROCESSED_CONFIG

        val flag = config.breakerListType == BlockBreakerMod.Config.ListType.WHITELIST
        if (config.breakerListItems.contains(targetState.block) != flag) return

        // Blocks with a negative destroy time are unbreakable
        if (targetState.block.defaultDestroyTime() < 0.0f) {
            if (!config.canBreakUnbreakable) return

            val flag1 = config.unbreakableListType == BlockBreakerMod.Config.ListType.WHITELIST
            if (config.unbreakableListItems.contains(targetState.block) != flag1) return
        }

        val stacks = targetState.getDrops(
            LootParams.Builder(world)
                .withParameter(LootContextParams.ORIGIN, targetPos.center)
                .withParameter(LootContextParams.TOOL, (blockEntity.tool.takeIf { !it.isEmpty } ?: IRON_PICKAXE))
        )

        val items = blockEntity.getItems()
        var changed = false

        for (stack in stacks) {
            if (stack.isEmpty) continue
            if (insertVanillaLike(items, stack)) changed = true
            for (stack in stacks) {
                if (stack.isEmpty) continue
                val dPos = targetPos.center
                world.addFreshEntity(
                    ItemEntity(
                        world, dPos.x, dPos.y, dPos.z,
                        stack, 0.0, 0.15, 0.0
                    )
                )
            }
            world.destroyBlock(targetPos, false)
        }

        if (changed) blockEntity.setChanged()
    }

    private fun insertVanillaLike(items: NonNullList<ItemStack>, stack: ItemStack): Boolean {
        var changed = false

        // Pass 1: merge into existing stacks
        for (i in 0..8) {
            if (stack.isEmpty) return changed

            val slot = items[i]
            if (slot.isEmpty) continue
            if (!ItemStack.isSameItemSameComponents(stack, slot)) continue
            if (slot.count >= slot.maxStackSize) continue

            val take = min(stack.count, slot.maxStackSize - slot.count)
            slot.count += take
            stack.count -= take
            changed = true
        }

        // Pass 2: place the remainder into empty slots
        for (i in 0..8) {
            if (stack.isEmpty) return changed

            if (items[i].isEmpty) {
                items[i] = stack.copyAndClear()
                return true
            }
        }

        return changed
    }
}