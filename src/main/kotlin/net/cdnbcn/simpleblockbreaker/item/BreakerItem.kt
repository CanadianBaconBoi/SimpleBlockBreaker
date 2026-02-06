package net.cdnbcn.simpleblockbreaker.item

import net.minecraft.ChatFormatting
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.block.Block
import net.minecraft.world.item.enchantment.ItemEnchantments
import java.util.Optional
import java.util.function.Consumer
//? if >=1.21.11
import net.minecraft.world.item.component.TooltipDisplay

class BreakerItem(block: Block, properties: Properties) : BlockItem(block, properties) {
    //? if >=1.21.11 {
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipDisplay: TooltipDisplay,
        tooltipAdder: Consumer<Component>,
        flag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag)

        val tool = context.registries()?.let { regs -> BreakerToolComponent.getTool(stack, regs) } ?: BreakerToolComponent.getTool(stack)

        val toolText = if (tool.isEmpty)
            Component.translatable("tooltip.simple_block_breaker.tool.none")
        else {
            tool.hoverName.copy().withStyle(ChatFormatting.AQUA)
        }

        val enchantments: ItemEnchantments? = if (!tool.isEmpty) {
            val enchantmentRegistry = context.registries()?.lookup(Registries.ENCHANTMENT) ?: Optional.empty()
            if (enchantmentRegistry.isPresent) {
                tool.getAllEnchantments(enchantmentRegistry.get())
            } else {
                null
            }
        } else null

        tooltipAdder.accept(
            Component.translatable("tooltip.simple_block_breaker.tool", toolText)
        )

        enchantments?.addToTooltip(
            context,
            tooltipAdder,
            flag,
            stack
        )
    }
    //?} elif <=1.21.1 {
    /*
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)

        val tool = context.registries()?.let { regs -> BreakerToolComponent.getTool(stack, regs) } ?: BreakerToolComponent.getTool(stack)

        val toolText = if (tool.isEmpty)
            Component.translatable("tooltip.simple_block_breaker.tool.none")
        else
            tool.hoverName.copy().withStyle(ChatFormatting.AQUA)

        tooltipComponents.add(
            Component.translatable("tooltip.simple_block_breaker.tool", toolText)
        )

        val enchantments: ItemEnchantments? = if (!tool.isEmpty) {
            val enchantmentRegistry = context.registries()?.lookup(Registries.ENCHANTMENT) ?: Optional.empty()
            if (enchantmentRegistry.isPresent) {
                tool.getAllEnchantments(enchantmentRegistry.get())
            } else {
                null
            }
        } else null

        val consumer: Consumer<Component> = Consumer {
            component: Component -> tooltipComponents.add(component)
        }

        enchantments?.addToTooltip(
            context,
            consumer,
            tooltipFlag
        )
    }
     *///?}
}