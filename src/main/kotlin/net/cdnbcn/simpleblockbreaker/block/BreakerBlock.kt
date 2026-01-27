package net.cdnbcn.simpleblockbreaker.block

import com.mojang.serialization.MapCodec
import net.cdnbcn.simpleblockbreaker.BlockBreakerMod
import net.cdnbcn.simpleblockbreaker.block.entity.BreakerBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.Containers
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.Property
//? if >=1.21.11
import net.minecraft.world.level.redstone.Orientation
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.BlockHitResult
import kotlin.math.min

class BreakerBlock(settings: Properties) : Block(settings), EntityBlock {
    companion object {
        val POWERED: BooleanProperty = BlockStateProperties.POWERED
        val FACING: EnumProperty<Direction> = BlockStateProperties.FACING
    }

    init {
        registerDefaultState(defaultBlockState().apply {
            this.setValue(POWERED, false)
            this.setValue(FACING, Direction.NORTH)
        })
    }

    override fun codec(): MapCodec<out Block> {
        return simpleCodec { properties -> BreakerBlock(properties) }
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return BreakerBlockEntity(pos, state)
    }

    override fun useWithoutItem(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hit: BlockHitResult
    ): InteractionResult {
        if (!world.isClientSide) {
            val screenHandlerFactory: MenuProvider? = state.getMenuProvider(world, pos)
            if (screenHandlerFactory != null) {
                player.openMenu(screenHandlerFactory)
            }
        }
        return InteractionResult.SUCCESS
    }

    //? if =1.21.11 {
    override fun affectNeighborsAfterRemoval(state: BlockState, world: ServerLevel, pos: BlockPos, moved: Boolean) {
        Containers.updateNeighboursAfterDestroy(state, world, pos)
    }
    //?} elif =1.12.1 {
    /*
    override fun onRemove(state: BlockState, world: Level, pos: BlockPos, newBlockState: BlockState, moved: Boolean) {
        Containers.dropContentsOnDestroy(state, newBlockState, world, pos)
    }
     *///?}

    public override fun hasAnalogOutputSignal(state: BlockState): Boolean {
        return true
    }

    public override fun getAnalogOutputSignal(
        state: BlockState, world: Level, pos: BlockPos,
        //? if =1.21.11
        direction: Direction
    ): Int {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos))
    }


    override fun neighborChanged(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        sourceBlock: Block,
        wireOrientation:
        //? if =1.21.11 {
        Orientation?,
        //?} elif =1.21.1 {
        /*
        BlockPos,
        *///?}
        notify: Boolean
    ) {
        val bl = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above())
        val bl2 = state.getValue(POWERED)
        if (bl && !bl2) {
            world.scheduleTick(pos, this, 1)
            world.setBlock(pos, state.setValue(POWERED, true), UPDATE_ALL)
        } else if (!bl && bl2) {
            world.setBlock(pos, state.setValue(POWERED, false), UPDATE_ALL)
        }
    }

    override fun tick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity !is BreakerBlockEntity) return
        val facing: Direction = state.getValue(PlacerBlock.FACING)
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

        for (stack in stacks) {
            if (!stack.isEmpty) {
                val itemEntity = ItemEntity(
                    world,
                    targetPos.x.toDouble(),
                    targetPos.y.toDouble(),
                    targetPos.z.toDouble(),
                    stack,
                    0.0,
                    0.5,
                    0.0
                )
                world.addFreshEntity(itemEntity)
            }
        }
        world.destroyBlock(targetPos, false)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(FACING, ctx.nearestLookingDirection.opposite)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(*arrayOf<Property<*>>(FACING, POWERED))
    }

    override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)))
    }

    override fun mirror(state: BlockState, mirror: Mirror): BlockState {
        return state.setValue(FACING, mirror.rotation().rotate(state.getValue(FACING)))
    }

    public override fun getMenuProvider(state: BlockState, world: Level, pos: BlockPos): MenuProvider {
        val blockEntity = world.getBlockEntity(pos) as BreakerBlockEntity
        return SimpleMenuProvider({ containerId, playerInventory, player ->
            blockEntity.createMenu(containerId, playerInventory, player)
        }, blockEntity.displayName)
    }

}