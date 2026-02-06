package net.cdnbcn.simpleblockbreaker.compat.jei

import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration
import net.cdnbcn.simpleblockbreaker.BlockBreakerMod
import net.cdnbcn.simpleblockbreaker.recipe.BreakerWithToolRecipe
import net.minecraft.resources.Identifier
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.display.SlotDisplay

class BreakerWithToolRecipeCraftingExtension: ICraftingCategoryExtension<BreakerWithToolRecipe>
{
    private val recipeCache: MutableMap<Identifier, List<SlotDisplay>> = mutableMapOf()

    override fun getIngredients(recipeHolder: RecipeHolder<BreakerWithToolRecipe>): List<SlotDisplay> {
        return recipeCache.getOrPut(recipeHolder.id.registry()) {
            recipeHolder.value.placementInfo().ingredients().stream().map { ingredient ->
                ingredient.display()
            }.toList()
        }
    }
}

@JeiPlugin
class SbbJeiPlugin : IModPlugin {
    companion object {
        val UID: Identifier = Identifier.fromNamespaceAndPath(BlockBreakerMod.ID, "jei_plugin")
    }

    override fun getPluginUid(): Identifier = UID

    override fun registerVanillaCategoryExtensions(registration: IVanillaCategoryExtensionRegistration) {
        registration.craftingCategory.addExtension(BreakerWithToolRecipe::class.java,
            BreakerWithToolRecipeCraftingExtension()
        )
    }
}