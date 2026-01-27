package net.cdnbcn.simpleblockbreaker.block.inventory.gui

import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class BreakerBlockMenu : AbstractContainerMenu {
    private val breaker: Container

    constructor(syncId: Int, playerInventory: Inventory) : this(syncId, playerInventory, SimpleContainer(9))

    constructor(syncId: Int, playerInventory: Inventory, container: Container) : super(MenuType.GENERIC_3x3, syncId) {
        checkContainerSize(container, 9)
        this.breaker = container
        container.startOpen(playerInventory.player)
        this.add3x3GridSlots(container, 62, 17)
        this.addStandardInventorySlots(playerInventory, 8, 84)

    }

    fun add3x3GridSlots(container: Container, x: Int, y: Int) {
        for (i in 0..2) {
            for (j in 0..2) {
                val k = j + i * 3
                this.addSlot(Slot(container, k, x + j * 18, y + i * 18))
            }
        }
    }

    //? if =1.21.1 {
    /*
    private fun addInventoryHotbarSlots(p_363100_: Container, p_362615_: Int, p_365310_: Int) {
        for (i in 0..8) {
            this.addSlot(Slot(p_363100_, i, p_362615_ + i * 18, p_365310_))
        }
    }

    private fun addInventoryExtendedSlots(p_360848_: Container, p_361259_: Int, p_361419_: Int) {
        for (i in 0..2) {
            for (j in 0..8) {
                this.addSlot(Slot(p_360848_, j + (i + 1) * 9, p_361259_ + j * 18, p_361419_ + i * 18))
            }
        }
    }

    private fun addStandardInventorySlots(p_362897_: Container, p_364704_: Int, p_361176_: Int) {
        this.addInventoryExtendedSlots(p_362897_, p_364704_, p_361176_)
        val i = 4
        val j = 58
        this.addInventoryHotbarSlots(p_362897_, p_364704_, p_361176_ + 58)
    }
     *///?}

    override fun stillValid(player: Player): Boolean {
        return this.breaker.stillValid(player)
    }

    override fun quickMoveStack(player: Player, slotIdx: Int): ItemStack {
        var itemStack = ItemStack.EMPTY
        val slot = this.slots[slotIdx]
        if (slot.hasItem()) {
            itemStack = slot.item.copy()
            if (slotIdx < 9) {
                if (!this.moveItemStackTo(slot.item, 9, 45, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.moveItemStackTo(slot.item, 0, 9, false)) {
                return ItemStack.EMPTY
            }

            if (slot.item.isEmpty) {
                slot.setByPlayer(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }

            if (slot.item.count == itemStack.count) {
                return ItemStack.EMPTY
            }

            slot.onTake(player, slot.item)
        }

        return itemStack
    }

    override fun removed(player: Player) {
        super.removed(player)
        this.breaker.stopOpen(player)
    }
}