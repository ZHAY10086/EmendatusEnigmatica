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

package com.ridanisaurus.emendatusenigmatica.loader.parser.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public class MaterialColorsModel {
	public static final Codec<MaterialColorsModel> CODEC = RecordCodecBuilder.create(x -> x.group(
			Codec.STRING.optionalFieldOf("highlightColor").forGetter(i -> i.highlightColor),
			Codec.STRING.optionalFieldOf("baseColor").forGetter(i -> i.baseColor),
			Codec.STRING.optionalFieldOf("shadeColor").forGetter(i -> i.shadeColor),
			Codec.STRING.optionalFieldOf("fluidColor").forGetter(i -> i.fluidColor),
			Codec.STRING.optionalFieldOf("particlesColor").forGetter(i -> i.particlesColor)
	).apply(x, MaterialColorsModel::new));

	private final Optional<String> highlightColor;
	private final Optional<String> baseColor;
	private final Optional<String> shadeColor;
	private final Optional<String> fluidColor;
	private final Optional<String> particlesColor;

	public MaterialColorsModel(Optional<String> highlightColor, Optional<String> baseColor, Optional<String> shadeColor, Optional<String> fluidColor, Optional<String> particlesColor) {
		this.highlightColor = highlightColor;
		this.baseColor = baseColor;
		this.shadeColor = shadeColor;
		this.fluidColor = fluidColor;
		this.particlesColor = particlesColor;
	}

	public MaterialColorsModel() {
		this.highlightColor = Optional.empty();
		this.baseColor = Optional.empty();
		this.shadeColor = Optional.empty();
		this.fluidColor = Optional.empty();
		this.particlesColor = Optional.empty();
	}

	public int getHighlightColor() {
		return highlightColor.map(x -> Integer.parseInt(x, 16)).orElse(-1);
	}

	public int getBaseColor() {
		return baseColor.map(x -> Integer.parseInt(x, 16)).orElse(-1);
	}

	public int getShadeColor() {
		return shadeColor.map(x -> Integer.parseInt(x, 16)).orElse(-1);
	}

	public int getFluidColor() {
//		Long L = Long.decode(fluidColor);
//		return L.intValue();
		return fluidColor.map(x -> Integer.parseInt(x, 16)).orElse(-1);
	}

	public int getParticlesColor() {
		return particlesColor.map(x -> Integer.parseInt(x, 16)).orElse(-1);
	}
}