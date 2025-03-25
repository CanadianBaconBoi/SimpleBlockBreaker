package net.cdnbcn.simpleblockbreaker.block.entity

import net.cdnbcn.simpleblockbreaker.block.inventory.BreakerBlockInventory
import net.cdnbcn.simpleblockbreaker.block.inventory.gui.BreakerGuiDescription
import net.minecraft.block.BlockState
import net.minecraft.block.InventoryProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.WorldAccess


class BreakerBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(BlockEntityTypes.BREAKER_BLOCK, pos, state), BreakerBlockInventory, SidedInventory,
    NamedScreenHandlerFactory {
    private val items: DefaultedList<ItemStack> = DefaultedList.ofSize(9, ItemStack.EMPTY)
    override fun getItems(): DefaultedList<ItemStack> = items

    override fun size(): Int {
        return 9
    }

    override fun readNbt(nbt: NbtCompound?, registries: RegistryWrapper.WrapperLookup?) {
        super.readNbt(nbt, registries)
        Inventories.readNbt(nbt, items, registries)
    }

    override fun writeNbt(nbt: NbtCompound?, registries: RegistryWrapper.WrapperLookup?) {
        Inventories.writeNbt(nbt, items, registries)
        return super.writeNbt(nbt, registries)
    }

    override fun markDirty() {
        super<BlockEntity>.markDirty()
    }

    override fun getAvailableSlots(side: Direction?): IntArray {
        return IntArray(9) {i -> i}
    }

    override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
        return true
    }

    override fun canExtract(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
        return true
    }

    companion object: BlockEntityTicker<BreakerBlockEntity>, InventoryProvider {
        override fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: BreakerBlockEntity) {
        }

        override fun getInventory(state: BlockState, world: WorldAccess, pos: BlockPos): SidedInventory? {
            val blockEntity = world.getBlockEntity(pos)
            if (blockEntity !is BreakerBlockEntity) return null
            return blockEntity
        }
    }

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return BreakerGuiDescription(syncId, playerInventory, ScreenHandlerContext.create(world, pos))
    }

    override fun getDisplayName(): Text {
        return Text.literal("Block Breaker")
    }
}