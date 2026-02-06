package net.cdnbcn.simpleblockbreaker.recipe

import net.cdnbcn.simpleblockbreaker.BlockBreakerMod
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeSerializer
import net.neoforged.neoforge.registries.DeferredRegister

object RecipeSerializerTypes {
    val RECIPE_SERIALIZERS: DeferredRegister<RecipeSerializer<*>> =
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, BlockBreakerMod.ID)

    val BREAKER_WITH_TOOL = RECIPE_SERIALIZERS.register("breaker_with_tool") { ->
        BreakerWithToolRecipe.Serializer
    }

    fun initialize() {}
}