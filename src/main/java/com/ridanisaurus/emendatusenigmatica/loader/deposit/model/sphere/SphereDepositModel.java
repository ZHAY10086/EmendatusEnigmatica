package com.ridanisaurus.emendatusenigmatica.loader.deposit.model.sphere;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ridanisaurus.emendatusenigmatica.loader.deposit.model.common.CommonBlockDefinitionModel;
import com.ridanisaurus.emendatusenigmatica.loader.deposit.model.common.CommonDepositModelBase;
import com.ridanisaurus.emendatusenigmatica.loader.deposit.model.sample.SampleBlockDefinitionModel;

import java.util.List;

public class SphereDepositModel extends CommonDepositModelBase {
	public static final Codec<SphereDepositModel> CODEC = RecordCodecBuilder.create(x -> x.group(
			Codec.STRING.fieldOf("type").forGetter(it -> it.type),
			Codec.STRING.fieldOf("dimension").forGetter(it -> it.dimension),
			Codec.list(Codec.STRING).fieldOf("biomes").orElse(List.of()).forGetter(it -> it.biomes),
			Codec.STRING.fieldOf("registryName").forGetter(it -> it.name),
			SphereDepositConfigModel.CODEC.fieldOf("config").forGetter(it -> it.config)
	).apply(x, SphereDepositModel::new));

	private final SphereDepositConfigModel config;

	public SphereDepositModel(String type, String dimension, List<String> biomes, String name, SphereDepositConfigModel config) {
		super(type, dimension, biomes, name);
		this.config = config;
	}

	public SphereDepositConfigModel getConfig() {
		return config;
	}
	public String getType() {
		return super.getType();
	}

	public int getChance() {
		if (config.chance < 1 || config.chance > 100) throw new IllegalArgumentException("Chance for " + name + " is out of Range [1 - 100]");
		return config.chance;
	}

	public int getPlacementChance() {
		return (100 - getChance()) + 1;
	}

	public int getMaxYLevel() {
		if (config.maxYLevel < -64 || config.maxYLevel > 320) throw new IllegalArgumentException("Max Y for " + name + " is out of Range [-64 - 320]");
		return config.maxYLevel;
	}

	public int getMinYLevel() {
		if (config.minYLevel < -64 || config.minYLevel > 320) throw new IllegalArgumentException("Min Y for " + name + " is out of Range [-64 - 320]");
		return config.minYLevel;
	}

	public List<CommonBlockDefinitionModel> getBlocks() {
		if (config.blocks.isEmpty()) throw new IllegalArgumentException("Blocks for " + name + " cannot be empty.");
		return config.blocks;
	}

	public List<String> getFillerTypes() {
		if (config.fillerTypes.isEmpty()) throw new IllegalArgumentException("Filler Types for " + name + " cannot be empty.");
		return config.fillerTypes;
	}

	public int getRadius() {
		if (config.radius < 1 || config.radius > 16) throw new IllegalArgumentException("Radius for " + name + " is out of Range [1 - 16]");
		return config.radius;
	}

	public boolean getGenerateSamples() {
		return config.generateSamples;
	}

	public List<SampleBlockDefinitionModel> getSampleBlocks() {
		if (getGenerateSamples() && config.sampleBlocks.isEmpty()) throw new IllegalArgumentException("Sample Blocks for " + name + " cannot be empty if generateSamples is set to true.");
		return config.sampleBlocks;
	}
}