package net.cdnbcn.simpleblockbreaker.compat.jei

import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.ingredient.ICraftingGridHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration
import net.cdnbcn.simpleblockbreaker.BlockBreakerMod
import net.cdnbcn.simpleblockbreaker.recipe.BreakerWithToolRecipe
import net.minecraft.core.NonNullList
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeHolder

class BreakerWithToolRecipeCraftingExtension: ICraftingCategoryExtension<BreakerWithToolRecipe> {
    private class RecipeCacheEntry(val width: Int, val height: Int, val ingredients: NonNullList<Ingredient>, val outputs: List<ItemStack>)
    private val recipeCache: MutableMap<ResourceLocation, RecipeCacheEntry> = mutableMapOf()

    override fun setRecipe(
        recipeHolder: RecipeHolder<BreakerWithToolRecipe>,
        builder: IRecipeLayoutBuilder,
        craftingGridHelper: ICraftingGridHelper,
        focuses: IFocusGroup
    ) {
        val recipe = recipeCache.getOrPut(recipeHolder.id) {
            val recipe = recipeHolder.value
            RecipeCacheEntry(recipe.getWidth(), recipe.getHeight(), recipe.ingredients, listOf(recipe.getResult()))
        }

        craftingGridHelper.createAndSetIngredients(builder, recipe.ingredients, recipe.width, recipe.height)
        craftingGridHelper.createAndSetOutputs(builder, recipe.outputs)
    }

    override fun getWidth(recipeHolder: RecipeHolder<BreakerWithToolRecipe>): Int {
        return recipeHolder.value.getWidth()
    }

    override fun getHeight(recipeHolder: RecipeHolder<BreakerWithToolRecipe>): Int {
        return recipeHolder.value.getHeight()
    }

    override fun isHandled(recipeHolder: RecipeHolder<BreakerWithToolRecipe>): Boolean {
        return !recipeHolder.value().isSpecial
    }

}

@JeiPlugin
class SbbJeiPlugin : IModPlugin {
    companion object {
        private val UID: ResourceLocation = ResourceLocation.fromNamespaceAndPath(BlockBreakerMod.ID, "jei_plugin")
    }

    override fun getPluginUid(): ResourceLocation = UID

    override fun registerVanillaCategoryExtensions(registration: IVanillaCategoryExtensionRegistration) {
        registration.craftingCategory.addExtension(BreakerWithToolRecipe::class.java, BreakerWithToolRecipeCraftingExtension())
    }
}