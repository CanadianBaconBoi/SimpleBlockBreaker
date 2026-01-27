package net.cdnbcn.simpleblockbreaker.block

import com.mojang.serialization.MapCodec
import net.cdnbcn.simpleblockbreaker.BlockBreakerMod
import net.cdnbcn.simpleblockbreaker.block.entity.PlacerBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.*
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
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
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.common.util.FakePlayer

class PlacerBlock(settings: Properties) : Block(settings), EntityBlock {
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
        return simpleCodec { properties -> PlacerBlock(properties) }
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return PlacerBlockEntity(pos, state)
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
        if (blockEntity !is PlacerBlockEntity) return
        val i: Int = blockEntity.getRandomSlot(world.random)
        if (i < 0) {
            world.playSound(null, pos, SoundEvents.DISPENSER_LAUNCH, SoundSource.BLOCKS, 0.15f, 1f)
        } else {
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
                    } else {
                        world.playSound(null, pos, SoundEvents.DISPENSER_LAUNCH, SoundSource.BLOCKS, 0.15f, 1f)
                    }
                }
            } else {
                val fakePlayer = FakePlayer(world, BlockBreakerMod.FAKE_PLAYER_PROFILE)
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
                    itemStack.count -= 1
                }
            }
        }
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
        val blockEntity = world.getBlockEntity(pos) as PlacerBlockEntity
        return SimpleMenuProvider({ containerId, playerInventory, player ->
            blockEntity.createMenu(containerId, playerInventory, player)
        }, blockEntity.displayName)
    }
}