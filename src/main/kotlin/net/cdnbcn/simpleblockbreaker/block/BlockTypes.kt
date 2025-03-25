package net.cdnbcn.simpleblockbreaker.block

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemGroups
import net.minecraft.item.Items
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import java.util.function.Function


object BlockTypes {
    val BREAKER_BLOCK: ItemBlock = register("breaker", {settings -> BreakerBlock(settings)}, AbstractBlock.Settings.create().strength(1.0f).requiresTool(), ItemGroups.REDSTONE)
    val PLACER_BLOCK: ItemBlock = register("placer", {settings -> PlacerBlock(settings)}, AbstractBlock.Settings.create().strength(1.0f).requiresTool(), ItemGroups.REDSTONE)

    private fun register(
        path: String,
        factory: Function<AbstractBlock.Settings, Block>,
        settings: AbstractBlock.Settings,
        itemGroup: RegistryKey<ItemGroup>
    ): ItemBlock {
        val identifier: Identifier = Identifier.of("simpleblockbreaker", path)
        val registryKey: RegistryKey<Block> = RegistryKey.of(RegistryKeys.BLOCK, identifier)

        val block = Blocks.register(registryKey, factory, settings)
        val item = Items.register(block)

        ItemGroupEvents.modifyEntriesEvent(itemGroup).register(ModifyEntries { ig: FabricItemGroupEntries -> ig.add(item) })
        return ItemBlock(item, block)
    }

    fun initialize() {
    }
}

class ItemBlock (val item: Item, val block: Block)