package net.cdnbcn.simpleblockbreaker.block

import net.cdnbcn.simpleblockbreaker.BlockBreakerMod
import net.cdnbcn.simpleblockbreaker.block.entity.BlockEntityTypes
import net.cdnbcn.simpleblockbreaker.block.entity.PlacerBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.common.util.FakePlayerFactory
import kotlin.jvm.optionals.getOrNull

class PlacerBlock(settings: Properties) :
    BaseBlock<PlacerBlock, PlacerBlockEntity>(settings, ::PlacerBlock, ::PlacerBlockEntity) {
    override fun tick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        val blockEntity = world.getBlockEntity(pos, BlockEntityTypes.PLACER_BLOCK.get()).getOrNull() ?: return

        val i: Int = blockEntity.getRandomSlot(world.random)
        if (i < 0) {
            world.playSound(null, pos, SoundEvents.DISPENSER_LAUNCH, SoundSource.BLOCKS, 0.15f, 1f)
            return
        }

        val itemStack: ItemStack = blockEntity.getItem(i)
        val facing: Direction = state.getValue(FACING)
        val targetPos = pos.relative(facing)

        if (world.getBlockState(targetPos).isAir) {
            if (itemStack.item is BlockItem) {
                val block = (itemStack.item as BlockItem).block
                val flag =
                    BlockBreakerMod.PROCESSED_CONFIG.placerListType == BlockBreakerMod.Config.ListType.WHITELIST
                if (BlockBreakerMod.PROCESSED_CONFIG.placerListItems.contains(block) == flag) {
                    world.setBlock(targetPos, block.defaultBlockState(), UPDATE_ALL)
                    world.playSound(null, targetPos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5f, 1f)
                    itemStack.count -= 1
                    blockEntity.setChanged()
                } else {
                    world.playSound(null, pos, SoundEvents.DISPENSER_LAUNCH, SoundSource.BLOCKS, 0.15f, 1f)
                }
            }
            return
        }

        val fakePlayer = FakePlayerFactory.get(world, BlockBreakerMod.FAKE_PLAYER_PROFILE)
        val result = itemStack.useOn(
            UseOnContext(
                world,
                fakePlayer,
                InteractionHand.MAIN_HAND,
                itemStack,
                BlockHitResult(targetPos.center, facing.opposite, targetPos, true)
            )
        )
        if (result == InteractionResult.CONSUME) {
            world.playSound(null, pos, SoundEvents.DISPENSER_LAUNCH, SoundSource.BLOCKS, 0.15f, 1f)
            itemStack.count -= 1
            blockEntity.setChanged()
        } else {
            world.playSound(null, pos, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 0.15f, 1f)
        }
    }
}