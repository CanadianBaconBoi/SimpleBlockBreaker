package net.cdnbcn.simpleblockbreaker.block

import com.mojang.serialization.MapCodec
import net.cdnbcn.simpleblockbreaker.block.entity.BlockEntityTypes
import net.cdnbcn.simpleblockbreaker.block.entity.BreakerBlockEntity
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.loot.context.LootWorldContext
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.state.property.Property
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.minecraft.world.block.WireOrientation
import kotlin.math.min

class BreakerBlock(settings: Settings) : BlockWithEntity(settings) {
    companion object {
        val POWERED: BooleanProperty = Properties.POWERED
        val FACING: EnumProperty<Direction> = Properties.FACING
    }

    init {
        defaultState = defaultState.with(POWERED, false).with(FACING, Direction.NORTH)
    }

    override fun getCodec(): MapCodec<out BlockWithEntity> = createCodec { settings -> BreakerBlock(settings) }
    override fun createBlockEntity(pos: BlockPos, state: BlockState) = BreakerBlockEntity(pos, state)
    override fun getRenderType(state: BlockState) = BlockRenderType.MODEL

    override fun <T : BlockEntity?> getTicker(world: World?, state: BlockState?, type: BlockEntityType<T>?) =
        validateTicker(type, BlockEntityTypes.BREAKER_BLOCK, BreakerBlockEntity::tick)

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
        if (blockEntity !is BreakerBlockEntity) return
        val facing: Direction = state.get(PlacerBlock.FACING)
        val targetPos = pos.offset(facing)
        val blockState = world.getBlockState(targetPos)
        if (!blockState.isAir) {
            val stacks = blockState.getDroppedStacks(LootWorldContext.Builder(world).add(LootContextParameters.ORIGIN, pos.toCenterPos()).add(LootContextParameters.TOOL, ItemStack(Items.IRON_PICKAXE)))
            for (stack in stacks) {
                val items = blockEntity.getItems()
                for (i in 0..<9) {
                    val slot = items[i]
                    if (stack.item == slot.item && slot.count < slot.maxCount) {
                        val maxTake = slot.maxCount - slot.count
                        val take = min(stack.count, maxTake)
                        slot.count += take
                        stack.count -= take
                    } else if (slot.isEmpty) {
                        items[i] = stack.copyAndEmpty()
                    }
                }
            }

            for (stack in stacks) {
                if (!stack.isEmpty) {
                    val itemEntity = ItemEntity(world, targetPos.x.toDouble(), targetPos.y.toDouble(), targetPos.z.toDouble(), stack, 0.0, 0.5, 0.0)
                    world.spawnEntity(itemEntity)
                }
            }

            world.breakBlock(targetPos, false)
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