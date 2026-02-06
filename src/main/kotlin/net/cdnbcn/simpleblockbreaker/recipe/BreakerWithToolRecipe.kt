package net.cdnbcn.simpleblockbreaker.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.cdnbcn.simpleblockbreaker.item.BreakerToolComponent
import net.minecraft.core.HolderLookup
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
//? if >=1.21.11 {
import net.minecraft.world.item.crafting.display.RecipeDisplay
//?} elif <=1.21.1
//import net.minecraft.core.NonNullList

operator fun CraftingInput.get(row: Int, col: Int): ItemStack = this.getItem(row, col)

class BreakerWithToolRecipe(
    private val group: String,
    private val category: CraftingBookCategory = CraftingBookCategory.REDSTONE,
    pattern: ShapedRecipePattern,
    private val result: ItemStack,
    private val showNotification: Boolean
) : ShapedRecipe(group, category, pattern, result, showNotification) {

    //? if >=1.21.11 {
    private val recipe: ShapedRecipe by lazy {
        ShapedRecipe(
            "",
            category,
            pattern,
            result
        )
    }

    override fun placementInfo(): PlacementInfo = recipe.placementInfo()
    override fun display(): List<RecipeDisplay> = recipe.display()

    //?} elif <=1.21.1 {
    /*
    fun getResult() = result.copy()

    override fun getIngredients(): NonNullList<Ingredient> = pattern.ingredients()

    override fun canCraftInDimensions(width: Int, height: Int): Boolean = width == 3 && height == 3

    override fun getResultItem(p0: HolderLookup.Provider): ItemStack = result
     *///?}

    override fun assemble(input: CraftingInput, registries: HolderLookup.Provider): ItemStack {
        val tool = input.items().first {
            it.`is`(ItemTags.PICKAXES)
        }
        val out = result.copy()

        return BreakerToolComponent.withTool(out, tool, registries)
    }

    override fun getSerializer() = RecipeSerializerTypes.BREAKER_WITH_TOOL.get()

    override fun category(): CraftingBookCategory = category

    object Serializer : RecipeSerializer<BreakerWithToolRecipe> {

        private val CODEC: MapCodec<BreakerWithToolRecipe> =
            RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    Codec.STRING.optionalFieldOf(
                        "group",
                        ""
                    ).forGetter { it.group },
                    CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC)
                        .forGetter { it.category },
                    ShapedRecipePattern.MAP_CODEC.forGetter { it!!.pattern },
                    ItemStack.STRICT_CODEC.fieldOf("result")
                        .forGetter { it.result },
                    Codec.BOOL.optionalFieldOf("show_notification", true)
                        .forGetter { it.showNotification }
                ).apply(
                    instance
                ) { group, category, pattern, result, showNotification ->
                    BreakerWithToolRecipe(group, category, pattern, result, showNotification)
                }
            }

        private val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, BreakerWithToolRecipe> =
            StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, { it.group },
                CraftingBookCategory.STREAM_CODEC, { it.category },
                ShapedRecipePattern.STREAM_CODEC, { it.pattern },
                ItemStack.STREAM_CODEC, { it.result },
                ByteBufCodecs.BOOL, { it.showNotification },
                ::BreakerWithToolRecipe
            )

        override fun codec(): MapCodec<BreakerWithToolRecipe> = CODEC

        @Deprecated("Deprecated in Java")
        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, BreakerWithToolRecipe> = STREAM_CODEC
    }
}