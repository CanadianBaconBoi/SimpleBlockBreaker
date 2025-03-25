package net.cdnbcn.simpleblockbreaker.block.inventory

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList

interface BreakerBlockInventory: Inventory {
    fun getItems(): DefaultedList<ItemStack>

    override fun size() = getItems().size

    override fun isEmpty(): Boolean {
        for (i in 0..<size()) {
            val stack = getStack(i)
            if (!stack.isEmpty) {
                return false
            }
        }
        return true
    }

    override fun getStack(slot: Int) = getItems()[slot]

    override fun removeStack(slot: Int, count: Int): ItemStack {
        val result = Inventories.splitStack(getItems(), slot, count)
        if (!result.isEmpty)
            markDirty()
        return result
    }

    override fun removeStack(slot: Int): ItemStack = Inventories.removeStack(getItems(), slot)

    override fun setStack(slot: Int, stack: ItemStack) {
        getItems()[slot] = stack
        if (stack.count > stack.maxCount) {
            stack.count = stack.maxCount
        }
    }

    override fun clear() = getItems().clear()

    override fun markDirty() {
    }

    override fun canPlayerUse(player: PlayerEntity) = true
}