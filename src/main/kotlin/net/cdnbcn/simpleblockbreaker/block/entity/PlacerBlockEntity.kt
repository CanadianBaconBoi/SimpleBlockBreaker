package net.cdnbcn.simpleblockbreaker.block.entity

import net.cdnbcn.simpleblockbreaker.block.inventory.PlacerBlockInventory
import net.cdnbcn.simpleblockbreaker.block.inventory.gui.PlacerGuiDescription
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
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.minecraft.world.WorldAccess


class PlacerBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(BlockEntityTypes.PLACER_BLOCK, pos, state), PlacerBlockInventory, SidedInventory,
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

    companion object: BlockEntityTicker<PlacerBlockEntity>, InventoryProvider {
        override fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: PlacerBlockEntity) {
        }

        override fun getInventory(state: BlockState, world: WorldAccess, pos: BlockPos): SidedInventory? {
            val blockEntity = world.getBlockEntity(pos)
            if (blockEntity !is PlacerBlockEntity) return null
            return blockEntity
        }
    }

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return PlacerGuiDescription(syncId, playerInventory, ScreenHandlerContext.create(world, pos))
    }

    override fun getDisplayName(): Text {
        return Text.literal("Block Placer")
    }

    fun chooseNonEmptySlot(random: Random): Int {
        var i = -1
        var j = 1

        for (k in items.indices) {
            if (!items[k].isEmpty && random.nextInt(j++) == 0) {
                i = k
            }
        }

        return i
    }
}