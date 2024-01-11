/*
 *  MIT License
 *
 *  Copyright (c) 2020 Ridanisaurus
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.ridanisaurus.emendatusenigmatica.integration;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ridanisaurus.emendatusenigmatica.loader.deposit.EEDeposits;
import com.ridanisaurus.emendatusenigmatica.loader.deposit.IDepositProcessor;
import com.ridanisaurus.emendatusenigmatica.loader.deposit.model.common.CommonBlockDefinitionModel;
import com.ridanisaurus.emendatusenigmatica.loader.deposit.model.common.CommonDepositModelBase;
import com.ridanisaurus.emendatusenigmatica.loader.deposit.processsors.*;
import com.ridanisaurus.emendatusenigmatica.registries.EETags;
import com.ridanisaurus.emendatusenigmatica.util.Reference;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import javax.annotation.Nullable;
import java.util.*;

public class WorldGenRecipeCategory implements IRecipeCategory<WorldGenRecipeCategory.WorldGenWrapper> {
	private static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "world_gen");
	public static final RecipeType<WorldGenRecipeCategory.WorldGenWrapper> RECIPE = new RecipeType<>(ID, WorldGenRecipeCategory.WorldGenWrapper.class);
	private final IGuiHelper guiHelper;
	private final Component title;

	public WorldGenRecipeCategory(IGuiHelper guiHelper, String title) {
		this.guiHelper = guiHelper;
		this.title = Component.translatable(title);
	}

//	public static List<WorldGenWrapper> getWorldGenRecipes(MaterialModel material, IDepositProcessor activeProcessor) {
	public static List<WorldGenWrapper> getWorldGenRecipes(IDepositProcessor activeProcessor) {
		List<WorldGenRecipeCategory.WorldGenWrapper> recipes = new ArrayList<>();
		if (activeProcessor instanceof VanillaDepositProcessor) {
			recipes.add(new WorldGenWrapper(activeProcessor));
		}
		if (activeProcessor instanceof GeodeDepositProcessor) {
			recipes.add(new WorldGenWrapper(activeProcessor));
		}
		if (activeProcessor instanceof SphereDepositProcessor) {
			recipes.add(new WorldGenWrapper(activeProcessor));
		}
		if (activeProcessor instanceof DikeDepositProcessor) {
			recipes.add(new WorldGenWrapper(activeProcessor));
		}
		if (activeProcessor instanceof DenseDepositProcessor) {
			recipes.add(new WorldGenWrapper(activeProcessor));
		}
		return recipes;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, WorldGenWrapper recipe, IFocusGroup focuses) {
//		for (int i = 0; i < recipe.getBlocks().size(); i++) {
//			builder.addSlot(RecipeIngredientRole.INPUT, 6, 6)
//					.addItemStack(recipe.getBlocks().get(i));
//			builder.addSlot(RecipeIngredientRole.OUTPUT, 39, 6)
//					.addItemStack(recipe.getDrops(recipe.getBlocks()).get(i));
//		}

		builder.addSlot(RecipeIngredientRole.INPUT, 6, 6)
				.addItemStacks(recipe.getBlocks());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 39, 6)
				.addItemStacks(recipe.getDrops(recipe.getBlocks()));

//		builder.addSlot(RecipeIngredientRole.INPUT, 6, 6)
//				.addItemStacks(recipe.getBlocks());
//		builder.addSlot(RecipeIngredientRole.OUTPUT, 6, 6)
//				.addItemStacks(recipe.getBlocks());
	}

	@Override
	public RecipeType<WorldGenWrapper> getRecipeType() {
		return RECIPE;
	}

	@Override
	public void draw(WorldGenWrapper recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrix, double mouseX, double mouseY) {
		String featureType = ChatFormatting.DARK_AQUA + "Type: " + ChatFormatting.DARK_GRAY + recipe.getTypeName();
		String minY = ChatFormatting.DARK_AQUA + "Min Y: " + ChatFormatting.DARK_GRAY + recipe.getMinY();
		String maxY = ChatFormatting.DARK_AQUA + "Max Y: " + ChatFormatting.DARK_GRAY + recipe.getMaxY();
		String chance = ChatFormatting.DARK_AQUA + "Chance: " + ChatFormatting.DARK_GRAY + recipe.getChance() + "%";
		// TODO: Add Rarity?
		Minecraft.getInstance().font.draw(matrix, featureType, 5, 30, 0);
		Minecraft.getInstance().font.draw(matrix, minY, 5, 30 + 12, 0);
		Minecraft.getInstance().font.draw(matrix, maxY, 75, 30 + 12, 0);
		Minecraft.getInstance().font.draw(matrix, chance, 5, 30 + 24, 0);

		RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MOD_ID, "textures/gui/world_gen.png"));

		if (recipe.getDimension().equals("Overworld")) {
			Minecraft.getInstance().screen.blit(matrix, 74, 8, 134, 0, 12, 12);
		} else if (recipe.getDimension().equals("The Nether")) {
			Minecraft.getInstance().screen.blit(matrix, 74, 8, 134, 12, 12, 12);
		} else if (recipe.getDimension().equals("The End")) {
			Minecraft.getInstance().screen.blit(matrix, 74, 8, 134, 24, 12, 12);
		} else {
			Minecraft.getInstance().screen.blit(matrix, 74, 8, 134, 36, 12, 12);
		}

		if (recipe.getBiomes().contains("#" + BiomeTags.IS_DEEP_OCEAN.location().toString())) {
			Minecraft.getInstance().screen.blit(matrix, 91, 8, 146, 0, 12, 12);
		} else if (recipe.getBiomes().contains("#" + BiomeTags.IS_OCEAN.location().toString())) {
			Minecraft.getInstance().screen.blit(matrix, 91, 8, 146, 12, 12, 12);
		} else if (recipe.getBiomes().contains("#" + BiomeTags.IS_BEACH.location().toString())) {
			Minecraft.getInstance().screen.blit(matrix, 91, 8, 146, 24, 12, 12);
		} else if (recipe.getBiomes().contains("#" + BiomeTags.IS_RIVER.location().toString())) {
			Minecraft.getInstance().screen.blit(matrix, 91, 8, 146, 36, 12, 12);
		} else if (recipe.getBiomes().contains("#" + BiomeTags.IS_MOUNTAIN.location().toString())) {
			Minecraft.getInstance().screen.blit(matrix, 91, 8, 146, 48, 12, 12);
		} else if (recipe.getBiomes().contains("#" + BiomeTags.IS_BADLANDS.location().toString())) {
			Minecraft.getInstance().screen.blit(matrix, 91, 8, 146, 60, 12, 12);
		} else if (recipe.getBiomes().contains("#" + BiomeTags.IS_HILL.location().toString())) {
			Minecraft.getInstance().screen.blit(matrix, 91, 8, 146, 72, 12, 12);
		} else if (recipe.getBiomes().contains("#" + BiomeTags.IS_TAIGA.location().toString())) {
			Minecraft.getInstance().screen.blit(matrix, 91, 8, 146, 84, 12, 12);
		} else if (recipe.getBiomes().contains("#" + BiomeTags.IS_JUNGLE.location().toString())) {
			Minecraft.getInstance().screen.blit(matrix, 91, 8, 146, 96, 12, 12);
		} else if (recipe.getBiomes().contains("#" + BiomeTags.IS_FOREST.location().toString())) {
			Minecraft.getInstance().screen.blit(matrix, 91, 8, 146, 108, 12, 12);
		} else if (recipe.getBiomes().contains("#" + BiomeTags.IS_SAVANNA.location().toString())) {
			Minecraft.getInstance().screen.blit(matrix, 91, 8, 146, 120, 12, 12);
		} else {
			Minecraft.getInstance().screen.blit(matrix, 91, 8, 146, 132, 12, 12);
		}
		if (!recipe.getType().equals(EEDeposits.DepositType.VANILLA.getType())) {
			if (recipe.hasSurfaceSample()) {
				Minecraft.getInstance().screen.blit(matrix, 108, 8, 158, 0, 12, 12);
			} else {
				Minecraft.getInstance().screen.blit(matrix, 108, 8, 158, 12, 12, 12);
			}
		}
	}

	@Override
	public List<Component> getTooltipStrings(WorldGenWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		if (mouseX > 74 && mouseX < 86 && mouseY > 8 && mouseY < 20) {
			List<Component> dimension = new ArrayList<>();
			dimension.add(Component.literal(ChatFormatting.GOLD + "Dimension:"));
			dimension.add(Component.literal(recipe.getDimension()));
			return dimension;
		}
		if (mouseX > 91 && mouseX < 103 && mouseY > 8 && mouseY < 20) {
			List<Component> biomes = new ArrayList<>();
			biomes.add(Component.literal(ChatFormatting.GOLD + "Biomes:"));
			if (recipe.getBiomes().size() == 0) biomes.add(Component.literal("- Any"));
			else {
				for (String biome : recipe.getBiomes()) {
					biomes.add(Component.literal("- ").append(Component.literal(biome)));
				}
			}
			return biomes;
		}
		if (mouseX > 108 && mouseX < 120 && mouseY > 8 && mouseY < 20 && !recipe.getType().equals(EEDeposits.DepositType.VANILLA.getType())) {
			List<Component> surfaceSamples = new ArrayList<>();
			surfaceSamples.add(Component.literal(ChatFormatting.GOLD + "Generate Surface Samples:"));
			if (recipe.hasSurfaceSample()) {
				surfaceSamples.add(Component.literal("True"));
			} else {
				surfaceSamples.add(Component.literal("False"));
			}
			return surfaceSamples;
		}
		// TODO: Look into implementing this without using a Material if possible.
//		if (mouseX > 26 && mouseX < 35 && mouseY > 6 && mouseY < 22) {
//			List<Component> oreDropInfo = new ArrayList<>();
//			oreDropInfo.add(Component.literal(ChatFormatting.GOLD + "Ore Drop Info:"));
//			oreDropInfo.add(Component.literal(ChatFormatting.DARK_AQUA + "Min: " + ChatFormatting.WHITE + recipe.material.getOreDrop().getMin()));
//			oreDropInfo.add(Component.literal(ChatFormatting.DARK_AQUA + "Max: " + ChatFormatting.WHITE + recipe.material.getOreDrop().getMax()));
//			return oreDropInfo;
//		}
		if (mouseX > 5 && mouseX < 129 && mouseY > 42 && mouseY < 49) {
			List<Component> optimalY = new ArrayList<>();
			optimalY.add(Component.literal(ChatFormatting.GOLD + "Optimal Y:"));
			optimalY.add(Component.literal(String.valueOf((recipe.getMinY() + recipe.getMaxY()) / 2)));
			return optimalY;
		}
		return Collections.emptyList();
	}

	@Override
	public Component getTitle() {
		return title;
	}

	@Override
	public IDrawable getBackground() {
		return guiHelper.drawableBuilder(new ResourceLocation(Reference.MOD_ID, "textures/gui/world_gen.png"), 0, 0, 134, 66).addPadding(0, 0 ,0 ,0).build();
	}

	@Nullable
	@Override
	public IDrawable getIcon() {
		return guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.IRON_PICKAXE));
	}

	public static class WorldGenWrapper {
		private IDepositProcessor activeProcessor;
		public WorldGenWrapper(IDepositProcessor activeProcessor) {
			this.activeProcessor = activeProcessor;
		}

		public static Optional<Level> getServerLevel() {
			Minecraft minecraft = Minecraft.getInstance();
			return Optional.of(minecraft)
					.map(Minecraft::getSingleplayerServer)
					.map(integratedServer -> integratedServer.getLevel(Level.OVERWORLD));
		}
		public static Level getLevel() {
			Minecraft minecraft = Minecraft.getInstance();
			return minecraft.level;
		}

		public List<ItemStack> getBlocks() {
			List<ItemStack> blocks = new LinkedList<>();
			if (!(activeProcessor instanceof VanillaDepositProcessor)) {
				for (CommonBlockDefinitionModel blockDefinitionModel : activeProcessor.getBlocks()) {
					if (blockDefinitionModel.getBlock() != null && !blockDefinitionModel.getBlock().equals("minecraft:air")) {
						ResourceLocation depositBlocks = new ResourceLocation(blockDefinitionModel.getBlock());
						ItemStack itemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(depositBlocks));
						blocks.add(itemStack);
					}
					if (blockDefinitionModel.getTag() != null) {
						ResourceLocation depositTags = new ResourceLocation(blockDefinitionModel.getTag());
						TagKey<Item> itemTag = EETags.getItemTag(depositTags);
						ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(itemTag);
						for (Item item : tag) {
							ItemStack itemStack = new ItemStack(item);
							blocks.add(itemStack);
						}
					}
					if (blockDefinitionModel.getMaterial() != null) {
						ResourceLocation depositMaterials = EETags.MATERIAL_ORE.apply(blockDefinitionModel.getMaterial()).location();
						TagKey<Item> materialTag = EETags.getItemTag(depositMaterials);
						ITag<Item> itemTag = ForgeRegistries.ITEMS.tags().getTag(materialTag);
						for (Item item : itemTag) {
							ItemStack itemStack = new ItemStack(item);
							blocks.add(itemStack);
						}
					}
				}
			}
			if (activeProcessor instanceof VanillaDepositProcessor) {
				if (((VanillaDepositProcessor) activeProcessor).getVanillaModel().getBlock() != null && !((VanillaDepositProcessor) activeProcessor).getVanillaModel().getBlock().equals("minecraft:air")) {
					String block = ((VanillaDepositProcessor) activeProcessor).getVanillaModel().getBlock();
					ResourceLocation depositBlocks = new ResourceLocation(block);
					ItemStack itemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(depositBlocks));
					blocks.add(itemStack);
				}
				if (((VanillaDepositProcessor) activeProcessor).getVanillaModel().getMaterial() != null) {
					String block = ((VanillaDepositProcessor) activeProcessor).getVanillaModel().getMaterial();
					ResourceLocation depositMaterials = EETags.MATERIAL_ORE.apply(block).location();
					TagKey<Item> materialTag = EETags.getItemTag(depositMaterials);
					ITag<Item> itemTag = ForgeRegistries.ITEMS.tags().getTag(materialTag);
					for (Item item : itemTag) {
						ItemStack itemStack = new ItemStack(item);
						blocks.add(itemStack);
					}
				}
			}
			return blocks;
		}

//		public ItemStack getBlock(int i) {
//			return getBlocks().get(i);
//		}

		private List<ItemStack> getDrops(List<ItemStack> blockItems) {
			Level world = getServerLevel().orElseGet(WorldGenWrapper::getLevel);
			LootTables lootTables = world.getServer().getLootTables();
			List<ItemStack> outputDrops = new ArrayList<>();
			for (ItemStack blockItem : blockItems) {
				Block block = Block.byItem(blockItem.getItem());
				ResourceLocation lootTableLocation = block.getLootTable();
				if (block != null) {
					LootTable lootTable = lootTables.get(lootTableLocation);
					LootContext.Builder lootContextBuilder = new LootContext.Builder(world.getServer().overworld())
							.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(new BlockPos(0, 0, 0)))
							.withOptionalParameter(LootContextParams.TOOL, ItemStack.EMPTY)
							.withParameter(LootContextParams.BLOCK_STATE, block.defaultBlockState());

					List<ItemStack> drops = lootTable.getRandomItems(lootContextBuilder.create(LootContextParamSets.BLOCK));
					for (ItemStack drop : drops) {
						drop.setCount(1);
						outputDrops.add(drop);
					}
				}
			}
			return outputDrops;
		}

//		private ItemStack getDrop(List<ItemStack> drops, int i) {
//			ItemStack copy = drops.get(i).copy();
//			copy.setCount(1);
//			return copy;
//		}

//		public List<ItemStack> getBlocks() {
//			List<ItemStack> blocks = new LinkedList<>();
//			if (!(activeProcessor instanceof VanillaDepositProcessor)) {
//				for (CommonBlockDefinitionModel blockDefinitionModel : activeProcessor.getBlocks()) {
//					if (blockDefinitionModel.getBlock() != null) {
//						ResourceLocation depositBlocks = new ResourceLocation(blockDefinitionModel.getBlock());
//						ItemStack itemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(depositBlocks));
//						blocks.add(itemStack);
//					}
//					if (blockDefinitionModel.getTag() != null) {
//						ResourceLocation depositTags = new ResourceLocation(blockDefinitionModel.getTag());
//						TagKey<Item> itemTag = EETags.getItemTag(depositTags);
//						ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(itemTag);
//						for (Item item : tag) {
//							ItemStack itemStack = new ItemStack(item);
//							blocks.add(itemStack);
//						}
//					}
//					if (blockDefinitionModel.getMaterial() != null) {
//						ResourceLocation depositMaterials = EETags.MATERIAL_ORE.apply(blockDefinitionModel.getMaterial()).location();
//						TagKey<Item> materialTag = EETags.getItemTag(depositMaterials);
//						ITag<Item> itemTag = ForgeRegistries.ITEMS.tags().getTag(materialTag);
//						for (Item item : itemTag) {
//							ItemStack itemStack = new ItemStack(item);
//							blocks.add(itemStack);
//						}
//					}
//				}
//			}
//			if (activeProcessor instanceof VanillaDepositProcessor) {
//				if (((VanillaDepositProcessor) activeProcessor).getVanillaModel().getBlock() != null) {
//					String block = ((VanillaDepositProcessor) activeProcessor).getVanillaModel().getBlock();
//					ResourceLocation depositBlocks = new ResourceLocation(block);
//					ItemStack itemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(depositBlocks));
//					blocks.add(itemStack);
//				}
//				if (((VanillaDepositProcessor) activeProcessor).getVanillaModel().getMaterial() != null) {
//					String block = ((VanillaDepositProcessor) activeProcessor).getVanillaModel().getMaterial();
//					ResourceLocation depositMaterials = EETags.MATERIAL_ORE.apply(block).location();
//					TagKey<Item> materialTag = EETags.getItemTag(depositMaterials);
//					ITag<Item> itemTag = ForgeRegistries.ITEMS.tags().getTag(materialTag);
//					for (Item item : itemTag) {
//						ItemStack itemStack = new ItemStack(item);
//						blocks.add(itemStack);
//					}
//				}
//			}
//			return blocks;
//		}

		public String getType() {
			return activeProcessor.getType();
		}

		public String getTypeName() {
			return stripString(getType());
		}

		public CommonDepositModelBase getCommonModel() {
			return activeProcessor.getCommonModel();
		}

		public String getDeposit() {
			return stripString(getCommonModel().getName());
		}

		public String getDimension() {
			return stripString(getCommonModel().getDimension());
		}

		public List<String> getBiomes() {
			return getCommonModel().getBiomes();
		}

		public boolean hasSurfaceSample() {
			if (activeProcessor instanceof IDepositProcessor) {
				return activeProcessor.hasSurfaceSample();
			} else {
				return false;
			}
		}

		public int getMinY() {
			if (activeProcessor instanceof IDepositProcessor) {
				return activeProcessor.getMinY();
			} else {
				return -1;
			}
		}

		public int getMaxY() {
			if (activeProcessor instanceof IDepositProcessor) {
				return activeProcessor.getMaxY();
			} else {
				return -1;
			}
		}

		public int getChance() {
			if (activeProcessor instanceof IDepositProcessor) {
				return activeProcessor.getChance();
			} else {
				return -1;
			}
		}

		private String stripString(String input) {
			int index = input.indexOf(":");
			if (index != -1) {
				input = input.substring(index + 1);
			}
			input = input.replaceAll("_", " ");
//			input = input.substring(0, 1).toUpperCase() + input.substring(1);
			String[] words = input.split(" ");
			StringBuilder sb = new StringBuilder();
			for (String word : words) {
				sb.append(Character.toUpperCase(word.charAt(0)));
				sb.append(word.substring(1));
				sb.append(" ");
			}
			return input = sb.toString().trim();
		}
	}
}
