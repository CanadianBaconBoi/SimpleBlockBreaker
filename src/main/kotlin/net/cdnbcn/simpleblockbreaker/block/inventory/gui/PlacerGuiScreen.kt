package net.cdnbcn.simpleblockbreaker.block.inventory.gui

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text


class PlacerGuiScreen(gui: PlacerGuiDescription?, player: PlayerEntity?, title: Text?) :
    CottonInventoryScreen<PlacerGuiDescription?>(gui, player, title)