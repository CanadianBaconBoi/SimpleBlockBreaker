package net.cdnbcn.simpleblockbreaker.block

import com.mojang.serialization.MapCodec
import net.cdnbcn.simpleblockbreaker.block.entity.BaseBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Containers
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.Property
//? if >=1.21.11
import net.minecraft.world.level.redstone.Orientation
import net.minecraft.world.phys.BlockHitResult

abstract class BaseBlock<B : Block, E : BaseBlockEntity<E>>(
    settings: Properties,
    val blockFactory: (Properties) -> B,
    val entityFactory: (BlockPos, BlockState) -> E
) : Block(settings), EntityBlock {
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

    override fun codec(): MapCodec<out B> = simpleCodec(blockFactory)

    override fun newBlockEntity(pos: BlockPos, state: BlockState): E = entityFactory(pos, state)

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

    public override fun hasAnalogOutputSignal(state: BlockState): Boolean = true

    public override fun getAnalogOutputSignal(
        state: BlockState, world: Level, pos: BlockPos,
        //? if =1.21.11
        direction: Direction
    ): Int = AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos))


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

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState =
        defaultBlockState().setValue(FACING, ctx.nearestLookingDirection.opposite)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(*arrayOf<Property<*>>(FACING, POWERED))
    }

    override fun rotate(state: BlockState, rotation: Rotation): BlockState =
        state.setValue(FACING, rotation.rotate(state.getValue(FACING)))

    override fun mirror(state: BlockState, mirror: Mirror): BlockState =
        state.setValue(FACING, mirror.rotation().rotate(state.getValue(FACING)))

    public override fun getMenuProvider(state: BlockState, world: Level, pos: BlockPos): MenuProvider {
        val blockEntity = world.getBlockEntity(pos) as BaseBlockEntity<*>
        return SimpleMenuProvider({ containerId, playerInventory, player ->
            blockEntity.createMenu(containerId, playerInventory, player)
        }, blockEntity.displayName)
    }
}