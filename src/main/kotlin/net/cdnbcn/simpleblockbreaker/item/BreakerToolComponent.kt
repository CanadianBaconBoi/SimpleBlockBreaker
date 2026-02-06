package net.cdnbcn.simpleblockbreaker.item

import com.mojang.serialization.DynamicOps
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.resources.RegistryOps
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData

object BreakerToolComponent {
    private const val KEY = "sbb_tool"

    fun withTool(out: ItemStack, tool: ItemStack): ItemStack {
        // Fallback for callers that can't provide registries (best-effort).
        return withTool(out, tool, NbtOps.INSTANCE)
    }

    fun withTool(out: ItemStack, tool: ItemStack, registries: HolderLookup.Provider): ItemStack {
        return withTool(out, tool, registries.createSerializationContext(NbtOps.INSTANCE))
    }

    fun withTool(out: ItemStack, tool: ItemStack, registryAccess: RegistryAccess): ItemStack {
        return withTool(out, tool, RegistryOps.create(NbtOps.INSTANCE, registryAccess))
    }

    private fun withTool(out: ItemStack, tool: ItemStack, ops: DynamicOps<Tag>): ItemStack {
        val baseTag: CompoundTag = out.get(DataComponents.CUSTOM_DATA)?.copyTag() ?: CompoundTag()

        val encoded = ItemStack.CODEC.encodeStart(ops, tool.copyWithCount(1))
            .result()
            .orElse(null)

        if (encoded != null) {
            baseTag.put(KEY, encoded)
            out.set(DataComponents.CUSTOM_DATA, CustomData.of(baseTag))
        }

        return out
    }

    fun getTool(stack: ItemStack): ItemStack {
        // Fallback for callers that can't provide registries (best-effort).
        return getTool(stack, NbtOps.INSTANCE)
    }

    fun getTool(stack: ItemStack, registries: HolderLookup.Provider): ItemStack {
        return getTool(stack, registries.createSerializationContext(NbtOps.INSTANCE))
    }

    fun getTool(stack: ItemStack, registryAccess: RegistryAccess): ItemStack {
        return getTool(stack, RegistryOps.create(NbtOps.INSTANCE, registryAccess))
    }

    private fun getTool(stack: ItemStack, ops: DynamicOps<Tag>): ItemStack {
        val tag = stack.get(DataComponents.CUSTOM_DATA)?.copyTag() ?: return ItemStack.EMPTY
        val toolTag = tag.get(KEY) ?: return ItemStack.EMPTY

        return ItemStack.CODEC.parse(ops, toolTag)
            .result()
            .orElse(ItemStack.EMPTY)
    }
}