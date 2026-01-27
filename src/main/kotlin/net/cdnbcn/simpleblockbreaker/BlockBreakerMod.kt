package net.cdnbcn.simpleblockbreaker

import com.mojang.authlib.GameProfile
import net.cdnbcn.simpleblockbreaker.block.BlockItemTypes
import net.cdnbcn.simpleblockbreaker.block.BlockTypes
import net.cdnbcn.simpleblockbreaker.block.entity.BlockEntityTypes
import net.minecraft.core.DefaultedRegistry
import net.minecraft.core.registries.BuiltInRegistries
//? if =1.21.11
import net.minecraft.resources.Identifier
//? if =1.21.1
// import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.level.block.Block
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.config.ModConfigEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.common.ModConfigSpec
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import java.util.*


@Mod(BlockBreakerMod.ID)
class BlockBreakerMod(container: ModContainer) {
    companion object {
        const val ID = "simple_block_breaker"
        val LOGGER: Logger = LogManager.getLogger(ID)
        val FAKE_PLAYER_PROFILE =
            GameProfile(UUID.fromString("a71ac3cb-1c21-47c9-9219-aa4268e5aed0"), "BlockBreakerPlayer")

        var INSTANCE: BlockBreakerMod? = null

        lateinit var CONFIG: Config
        lateinit var CONFIG_SPEC: ModConfigSpec
        lateinit var PROCESSED_CONFIG: ProcessedConfig
    }

    init {
        INSTANCE = this

        MOD_BUS.addListener(::loadConfig)
        MOD_BUS.addListener(::reloadConfig)
        MOD_BUS.addListener(::buildContents)

        BlockTypes.BLOCKS.register(MOD_BUS)
        BlockItemTypes.BLOCK_ITEMS.register(MOD_BUS)
        BlockEntityTypes.BLOCK_ENTITY_TYPES.register(MOD_BUS)
        ScreenHandlerTypes.MENUS.register(MOD_BUS)

        val pair = ModConfigSpec.Builder().configure(::Config)
        CONFIG = pair.getLeft()
        CONFIG_SPEC = pair.getRight()

        container.registerConfig(ModConfig.Type.COMMON, CONFIG_SPEC)
    }

    @Suppress("unused")
    @SubscribeEvent
    fun onInitialize(event: FMLCommonSetupEvent) {
        BlockTypes.initialize()
        BlockItemTypes.initialize()
        BlockEntityTypes.initialize()
        ScreenHandlerTypes.initialize()
    }

    fun buildContents(event: BuildCreativeModeTabContentsEvent) {
        // Is this the tab we want to add to?
        if (event.tabKey === CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(BlockItemTypes.BREAKER_BLOCK_ITEM.get())
            event.accept(BlockItemTypes.PLACER_BLOCK_ITEM.get())
        }
    }

    private fun loadConfig(event: ModConfigEvent.Loading) {
        if (event.config.modId != ID)
            return
        LOGGER.info("Loading config")
        PROCESSED_CONFIG = ProcessedConfig.fromConfig(CONFIG)
    }

    private fun reloadConfig(event: ModConfigEvent.Reloading) {
        if (event.config.modId != ID)
            return
        LOGGER.info("Reloading config")
        PROCESSED_CONFIG = ProcessedConfig.fromConfig(CONFIG)
    }

    class ProcessedConfig(
        val canBreakUnbreakable: Boolean,
        val unbreakableListType: Config.ListType,
        val unbreakableListItems: HashSet<Block>,
        val breakerListType: Config.ListType,
        val breakerListItems: HashSet<Block>,
        val placerListType: Config.ListType,
        val placerListItems: HashSet<Block>
    ) {
        companion object {
            fun fromConfig(config: Config): ProcessedConfig {
                return ProcessedConfig(
                    config.canBreakUnbreakable.get(),
                    config.unbreakableListType.get(),
                    convertStringListToRegistrySet(config.unbreakableListItems.get(), BuiltInRegistries.ITEM) {
                        return@convertStringListToRegistrySet if (it is BlockItem) {
                            it.block
                        } else {
                            null
                        }
                    },
                    config.breakerListType.get(),
                    convertStringListToRegistrySet(config.breakerListItems.get(), BuiltInRegistries.ITEM) {
                        return@convertStringListToRegistrySet if (it is BlockItem) {
                            it.block
                        } else {
                            null
                        }
                    },
                    config.placerListType.get(),
                    convertStringListToRegistrySet(config.placerListItems.get(), BuiltInRegistries.ITEM) {
                        return@convertStringListToRegistrySet if (it is BlockItem) {
                            it.block
                        } else {
                            null
                        }
                    }
                )
            }

            fun <T : DefaultedRegistry<I>, I, R> convertStringListToRegistrySet(
                input: List<String>,
                registry: T,
                typeGetter: (I) -> R?
            ): HashSet<R> {
                val ret = HashSet<R>()
                for (item in input) {
                    registry.
                        //? if =1.21.11
                    get(
                        //? if =1.21.1
                        // getHolder(
                        //? if =1.21.11
                        Identifier
                            //? if =1.21.1
                            // ResourceLocation
                            .bySeparator(item, ':')
                    ).ifPresent {
                        val item = it.value()
                        val gotItem = typeGetter(item)
                        if (gotItem != null) {
                            ret.add(gotItem)
                        }
                    }
                }
                return ret
            }

            @Suppress("unused")
            fun <T : DefaultedRegistry<I>, I> convertStringListToRegistrySet(
                input: List<String>,
                registry: T
            ): HashSet<I> {
                return convertStringListToRegistrySet(input, registry) { i -> i }
            }
        }
    }

    class Config(builder: ModConfigSpec.Builder) {
        val canBreakUnbreakable: ModConfigSpec.ConfigValue<Boolean> =
            builder.comment("Can the breaker break unbreakable blocks?").define("can_breaker_break_unbreakable", false)
        val unbreakableListType: ModConfigSpec.ConfigValue<ListType> =
            builder.comment("Should unbreakable blocks be on a white or blacklist")
                .defineEnum("unbreakable_list_type", ListType.BLACKLIST)
        val unbreakableListItems: ModConfigSpec.ConfigValue<List<String>> =
            builder.comment("White/blacklisted unbreakable blocks (use item path)")
                .defineListAllowEmpty("unbreakable_listed_blocks", emptyList(), { "minecraft:bedrock" }) { true }

        val breakerListType: ModConfigSpec.ConfigValue<ListType> =
            builder.comment("Should the breaker be on a white or blacklist")
                .defineEnum("breaker_list_type", ListType.BLACKLIST)
        val breakerListItems: ModConfigSpec.ConfigValue<List<String>> =
            builder.comment("Breaker's white/blacklisted blocks (use item path)")
                .defineListAllowEmpty("breaker_listed_blocks", emptyList(), { "minecraft:dirt" }) { true }

        val placerListType: ModConfigSpec.ConfigValue<ListType> =
            builder.comment("Should the placer be on a white or blacklist")
                .defineEnum("placer_list_type", ListType.BLACKLIST)
        val placerListItems: ModConfigSpec.ConfigValue<List<String>> =
            builder.comment("Placer's white/blacklisted blocks (use item path)")
                .defineListAllowEmpty("placer_listed_blocks", emptyList(), { "minecraft:dirt" }) { true }

        enum class ListType {
            WHITELIST, BLACKLIST
        }
    }
}