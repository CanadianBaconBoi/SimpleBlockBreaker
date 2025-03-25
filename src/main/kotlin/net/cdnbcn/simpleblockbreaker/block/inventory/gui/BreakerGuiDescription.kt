package net.cdnbcn.simpleblockbreaker.block.inventory.gui

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.cdnbcn.simpleblockbreaker.ScreenHandlerTypes
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerContext

class BreakerGuiDescription(syncId: Int, playerInventory: PlayerInventory, context: ScreenHandlerContext) :
    SyncedGuiDescription(
        ScreenHandlerTypes.BREAKER_HANDLER,
        syncId,
        playerInventory,
        getBlockInventory(context, INVENTORY_SIZE),
        getBlockPropertyDelegate(context)
    ) {
    init {
        val root = WGridPanel()
        setRootPanel(root)
        root.setInsets(Insets.ROOT_PANEL)

        for (y in 0..2) {
            for (x in 0..2) {
                val itemSlot = WItemSlot.of(blockInventory, x+(y*3))
                root.add(itemSlot, 3+x, 1+y)
            }
        }

        root.add(this.createPlayerInventoryPanel(), 0, 4)

        root.validate(this)
    }

    companion object {
        private const val INVENTORY_SIZE = 9
    }
}