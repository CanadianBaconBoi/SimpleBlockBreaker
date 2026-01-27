package net.cdnbcn.simpleblockbreaker.block.entity

import net.cdnbcn.simpleblockbreaker.block.inventory.gui.BreakerBlockMenu
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.util.RandomSource
import net.minecraft.world.ContainerHelper
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.WorldlyContainerHolder
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
//? if =1.21.11 {
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

//?} elif =1.21.1 {
/*
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
 *///?}

class PlacerBlockEntity(pos: BlockPos, state: BlockState) :
    BaseContainerBlockEntity(BlockEntityTypes.PLACER_BLOCK.get(), pos, state), WorldlyContainer {
    private var items: NonNullList<ItemStack> = NonNullList.withSize(9, ItemStack.EMPTY)
    public override fun getItems(): NonNullList<ItemStack> = items
    override fun setItems(items: NonNullList<ItemStack>) {
        this.items = items
    }

    fun getRandomSlot(random: RandomSource): Int {
        var i = -1
        var j = 1

        for (k in this.items.indices) {
            if (!this.items[k].isEmpty && random.nextInt(j++) == 0) {
                i = k
            }
        }

        return i
    }


    override fun getContainerSize(): Int {
        return 9
    }

    override fun isEmpty(): Boolean {
        for (i in items) {
            if (!i.isEmpty) {
                return false
            }
        }
        return true
    }

    override fun getItem(slot: Int): ItemStack {
        return items[slot]
    }

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        val stack = ContainerHelper.removeItem(this.items, slot, amount)
        this.setChanged()
        return stack
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        val stack = ContainerHelper.takeItem(this.items, slot)
        this.setChanged()
        return stack

    }

    override fun setItem(slot: Int, stack: ItemStack) {
        stack.limitSize(this.getMaxStackSize(stack))
        this.items[slot] = stack
        this.setChanged()
    }

    override fun stillValid(p0: Player): Boolean {
        return true
    }

    //? if =1.21.11 {
    override fun loadAdditional(data: ValueInput) {
        super.loadAdditional(data)
        ContainerHelper.loadAllItems(data, items)
    }

    override fun saveAdditional(data: ValueOutput) {
        super.saveAdditional(data)
        ContainerHelper.saveAllItems(data, items)
    }
    //?} elif =1.21.1 {
    /*
    override fun loadAdditional(data: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(data, registries)
        ContainerHelper.loadAllItems(data, items, registries)
    }

    override fun saveAdditional(data: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(data, registries)
        ContainerHelper.saveAllItems(data, items, registries)
    }
     *///?}

    override fun getSlotsForFace(side: Direction): IntArray {
        return IntArray(9) { i -> i }
    }

    override fun canPlaceItemThroughFace(slot: Int, stack: ItemStack, dir: Direction?): Boolean {
        return true
    }

    override fun canTakeItemThroughFace(slot: Int, stack: ItemStack, dir: Direction): Boolean {
        return true
    }

    companion object : BlockEntityTicker<PlacerBlockEntity>, WorldlyContainerHolder {
        override fun tick(world: Level, pos: BlockPos, state: BlockState, blockEntity: PlacerBlockEntity) {
        }

        override fun getContainer(state: BlockState, world: LevelAccessor, pos: BlockPos): WorldlyContainer {
            val blockEntity = world.getBlockEntity(pos)
            if (blockEntity !is PlacerBlockEntity) return null!!
            return blockEntity
        }
    }

    override fun createMenu(containerId: Int, inventory: Inventory): AbstractContainerMenu {
        return BreakerBlockMenu(containerId, inventory, this)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("block.simple_block_breaker.placer")
    }

    override fun getDefaultName(): Component {
        return Component.literal("Block Placer")
    }

    override fun clearContent() {
        items.clear()
        this.setChanged()
    }
}