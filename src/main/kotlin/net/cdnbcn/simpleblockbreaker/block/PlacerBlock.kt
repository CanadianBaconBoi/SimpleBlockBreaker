package net.cdnbcn.simpleblockbreaker.block

import com.mojang.serialization.MapCodec
import net.cdnbcn.simpleblockbreaker.block.entity.BlockEntityTypes
import net.cdnbcn.simpleblockbreaker.block.entity.PlacerBlockEntity
import net.fabricmc.fabric.api.entity.FakePlayer
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.state.property.Property
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.minecraft.world.block.WireOrientation

class PlacerBlock(settings: Settings) : BlockWithEntity(settings) {
    companion object {
        val POWERED: BooleanProperty = Properties.POWERED
        val FACING: EnumProperty<Direction> = Properties.FACING
    }

    init {
        defaultState = defaultState.with(BreakerBlock.POWERED, false).with(BreakerBlock.FACING, Direction.NORTH)
    }

    override fun getCodec(): MapCodec<out BlockWithEntity> = createCodec { settings -> PlacerBlock(settings) }
    override fun createBlockEntity(pos: BlockPos, state: BlockState) = PlacerBlockEntity(pos, state)
    override fun getRenderType(state: BlockState) = BlockRenderType.MODEL

    override fun <T : BlockEntity?> getTicker(world: World?, state: BlockState?, type: BlockEntityType<T>?) =
        validateTicker(type, BlockEntityTypes.PLACER_BLOCK, PlacerBlockEntity::tick)

    public override fun onUse(state: BlockState, world: World, pos: BlockPos?, player: PlayerEntity, hit: BlockHitResult?): ActionResult {
        if (!world.isClient) {
            val screenHandlerFactory: NamedScreenHandlerFactory? = state.createScreenHandlerFactory(world, pos)

            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory)
            }
        }
        return ActionResult.SUCCESS
    }

    public override fun onStateReplaced(state: BlockState, world: ServerWorld, pos: BlockPos?, moved: Boolean) {
        ItemScatterer.onStateReplaced(state, world, pos)
    }

    public override fun hasComparatorOutput(state: BlockState?): Boolean {
        return true
    }

    public override fun getComparatorOutput(state: BlockState?, world: World, pos: BlockPos?): Int {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos))
    }

    override fun neighborUpdate(
        state: BlockState,
        world: World,
        pos: BlockPos,
        sourceBlock: Block?,
        wireOrientation: WireOrientation?,
        notify: Boolean
    ) {
        val bl = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up())
        val bl2 = state.get(POWERED) as Boolean
        if (bl && !bl2) {
            world.scheduleBlockTick(pos, this, 1)
            world.setBlockState(pos, state.with(POWERED, true))
        } else if (!bl && bl2) {
            world.setBlockState(pos, state.with(POWERED, false))
        }
    }

    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity !is PlacerBlockEntity) return
        val i: Int = blockEntity.chooseNonEmptySlot(world.random)
        if (i < 0) {
            world.playSound(null, pos, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.BLOCKS, 0.15f, 1f)
        } else {
            val itemStack: ItemStack = blockEntity.getStack(i)
            val facing: Direction = state.get(FACING)
            val targetPos = pos.offset(facing)
            if (world.getBlockState(targetPos).isAir) {
                if (itemStack.item is BlockItem) {
                    val block = (itemStack.item as BlockItem).block
                    world.setBlockState(targetPos, block.defaultState)
                    world.playSound(null, targetPos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5f, 1f)
                    itemStack.count -= 1
                }
            } else {
                val fakePlayer: FakePlayer = FakePlayer.get(world)
                val result = itemStack.useOnBlock(ItemUsageContext(world, fakePlayer, Hand.MAIN_HAND, itemStack, BlockHitResult(targetPos.toCenterPos(), facing.opposite, targetPos, true)))
                if (result == ActionResult.CONSUME) {
                    itemStack.count -= 1
                }
            }
        }
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        return defaultState.with(FACING, ctx.side.opposite) as BlockState
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(*arrayOf<Property<*>>(FACING, POWERED))
    }

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
        return state.with(FACING, rotation.rotate(state.get(FACING))) as BlockState
    }

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState {
        return state.rotate(mirror.getRotation(state.get(FACING)))
    }

}