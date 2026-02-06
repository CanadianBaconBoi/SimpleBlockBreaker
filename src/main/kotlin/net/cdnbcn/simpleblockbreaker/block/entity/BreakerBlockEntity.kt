package net.cdnbcn.simpleblockbreaker.block.entity

import net.cdnbcn.simpleblockbreaker.block.inventory.gui.BaseMenu
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState

//? if >=1.21.11 {
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import kotlin.jvm.optionals.getOrDefault

//?} elif <=1.21.1 {
/*
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
 *///?}

class BreakerBlockEntity(pos: BlockPos, state: BlockState) : BaseBlockEntity<BreakerBlockEntity>(
    BlockEntityTypes.BREAKER_BLOCK.get(), pos, state,
    Component.translatable("block.simple_block_breaker.breaker"),
    Component.literal("Block Breaker"),
    ::BaseMenu
) {
    var tool: ItemStack = ItemStack.EMPTY

    //? if >=1.21.11 {
    override fun saveAdditional(data: ValueOutput) {
        super.saveAdditional(data)
        data.storeNullable("sbb_tool", ItemStack.CODEC, tool)
    }

    override fun loadAdditional(data: ValueInput) {
        super.loadAdditional(data)
        this.tool = data.read("sbb_tool", ItemStack.CODEC).getOrDefault(ItemStack.EMPTY)
    }
    //?} elif <=1.21.1 {
    /*
    override fun saveAdditional(data: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(data, registries)
        val toolTag = this.tool.saveOptional(registries)
        data.put("sbb_tool", toolTag)
    }

    override fun loadAdditional(data: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(data, registries)
        val tool = data.get("sbb_tool")?.let {
            ItemStack.parse(registries, it)
                .orElse(ItemStack.EMPTY)
        } ?: ItemStack.EMPTY
        this.tool = tool
    }
      *///?}
}