package net.cdnbcn.simpleblockbreaker.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.util.RandomSource
import net.minecraft.world.Container
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
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
//? if =1.21.11 {
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

//?} elif =1.21.1 {
/*
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
 *///?}

abstract class BaseBlockEntity<T : BlockEntity>(
    bet: BlockEntityType<T>,
    pos: BlockPos,
    state: BlockState,
    val translatableName: Component,
    val defaultScreenName: Component,
    val menuSupplier: (Int, Inventory, Container) -> AbstractContainerMenu
) : BaseContainerBlockEntity(bet, pos, state), WorldlyContainer {
    fun getRandomSlot(random: RandomSource): Int {
        var i = -1
        var j = 1

        for (k in this._items.indices) {
            if (!this._items[k].isEmpty && random.nextInt(j++) == 0) {
                i = k
            }
        }

        return i
    }

    companion object : BlockEntityTicker<BreakerBlockEntity>, WorldlyContainerHolder {
        override fun tick(world: Level, pos: BlockPos, state: BlockState, blockEntity: BreakerBlockEntity) {
        }

        override fun getContainer(state: BlockState, world: LevelAccessor, pos: BlockPos): WorldlyContainer {
            val blockEntity = world.getBlockEntity(pos)
            if (blockEntity !is BreakerBlockEntity) return null!!
            return blockEntity
        }
    }

    private var _items: NonNullList<ItemStack> = NonNullList.withSize(9, ItemStack.EMPTY)
    public override fun getItems(): NonNullList<ItemStack> = _items
    override fun setItems(items: NonNullList<ItemStack>) {
        this._items = items
    }

    override fun getContainerSize(): Int = 9

    override fun isEmpty(): Boolean = _items.all { item -> item.isEmpty }

    override fun getItem(slot: Int): ItemStack = _items[slot]

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        val stack = ContainerHelper.removeItem(this._items, slot, amount)
        this.setChanged()
        return stack
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        val stack = ContainerHelper.takeItem(this._items, slot)
        this.setChanged()
        return stack

    }

    override fun setItem(slot: Int, stack: ItemStack) {
        stack.limitSize(this.getMaxStackSize(stack))
        this._items[slot] = stack
        this.setChanged()
    }

    override fun stillValid(p0: Player): Boolean = true

    //? if =1.21.11 {
    override fun loadAdditional(data: ValueInput) {
        super.loadAdditional(data)
        ContainerHelper.loadAllItems(data, _items)
    }

    override fun saveAdditional(data: ValueOutput) {
        super.saveAdditional(data)
        ContainerHelper.saveAllItems(data, _items)
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

    override fun getSlotsForFace(side: Direction): IntArray = IntArray(9) { i -> i }

    override fun canPlaceItemThroughFace(slot: Int, stack: ItemStack, dir: Direction?): Boolean = true

    override fun canTakeItemThroughFace(slot: Int, stack: ItemStack, dir: Direction): Boolean = true

    override fun createMenu(containerId: Int, inventory: Inventory): AbstractContainerMenu =
        menuSupplier(containerId, inventory, this)

    override fun getDisplayName(): Component = translatableName

    override fun getDefaultName(): Component = defaultScreenName

    override fun clearContent() {
        _items.clear()
        this.setChanged()
    }
}