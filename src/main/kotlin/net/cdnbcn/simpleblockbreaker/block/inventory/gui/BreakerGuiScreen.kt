package net.cdnbcn.simpleblockbreaker.block.inventory.gui

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text


class BreakerGuiScreen(gui: BreakerGuiDescription?, player: PlayerEntity?, title: Text?) :
    CottonInventoryScreen<BreakerGuiDescription?>(gui, player, title)