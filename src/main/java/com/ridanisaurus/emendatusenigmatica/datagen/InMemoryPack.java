package com.ridanisaurus.emendatusenigmatica.datagen;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.ridanisaurus.emendatusenigmatica.EmendatusEnigmatica;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryPack implements IResourcePack {
	private final Path path;

	public InMemoryPack(Path path) {
		EmendatusEnigmatica.generate();
		this.path = path;
	}

	private static String getFullPath(ResourcePackType type, ResourceLocation location) {
		return String.format("%s/%s/%s", type.getDirectoryName(), location.getNamespace(), location.getPath());
	}

	@Override
	public InputStream getRootResourceStream(String fileName) throws IOException {
		Path resolved = path.resolve(fileName);
		return Files.newInputStream(resolved);
	}

	@Override
	public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException {
		Path resolved = path.resolve(getFullPath(type, location));
		return Files.newInputStream(resolved);
	}

	@Override
	public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespaceIn, String pathIn, int maxDepthIn, Predicate<String> filterIn) {
		List<ResourceLocation> result = new ArrayList<>();
		getChildResourceLocations(result, 0, maxDepthIn, filterIn, path.resolve(type.getDirectoryName() + "/" + namespaceIn + "/" + pathIn), namespaceIn, pathIn);
		return result;
	}

	private void getChildResourceLocations(List<ResourceLocation> result, int depth, int maxDepth, Predicate<String> filter, Path current, String currentRLNS, String currentRLPath) {
		if (depth >= maxDepth) {
			return;
		}
		try {
			if (!Files.exists(current) || !Files.isDirectory(current)) {
				return;
			}
			Stream<Path> list = Files.list(current);
			for (Path child : list.collect(Collectors.toList())) {
				if (!Files.isDirectory(child)) {
					result.add(new ResourceLocation(currentRLNS, currentRLPath + "/" + child.getFileName()));
					continue;
				}
				getChildResourceLocations(result, depth + 1, maxDepth, filter, child, currentRLNS, currentRLPath + "/" + child.getFileName());
			}
		} catch (IOException ignored) {
			ignored.printStackTrace();
		}
	}


	@Override
	public boolean resourceExists(ResourcePackType type, ResourceLocation location) {
		Path finalPath = path.resolve(type.getDirectoryName() + "/" + location.getNamespace() + "/" + location.getPath());
		return Files.exists(finalPath);
	}

	@Override
	public Set<String> getResourceNamespaces(ResourcePackType type) {
		Set<String> result = new HashSet<>();
		try {
			Stream<Path> list = Files.list(path.resolve(type.getDirectoryName()));
			for (Path resultingPath : list.collect(Collectors.toList())) {
				result.add(resultingPath.getFileName().toString());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Nullable
	@Override
	public <T> T getMetadata(IMetadataSectionSerializer<T> deserializer) throws IOException {
		/*InputStream inputStream = Files.newInputStream(path.resolve("pack.mcmeta"));*/
		JsonObject jsonobject = new JsonObject();
        /*try (BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            jsonobject = JSONUtils.fromJson(bufferedreader);
        } catch (JsonParseException | IOException ioexception) {
            return null;
        }*/

		JsonObject packObject = new JsonObject();
		packObject.addProperty("pack_format", 6);
		packObject.addProperty("description", "emendatusenigmatica");
		jsonobject.add("pack", packObject);
		if (!jsonobject.has(deserializer.getSectionName())) {
			return null;
		} else {
			try {
				return deserializer.deserialize(JSONUtils.getJsonObject(jsonobject, deserializer.getSectionName()));
			} catch (JsonParseException jsonparseexception) {
				return null;
			}
		}
	}

	@Override
	public String getName() {
		return "In Memory Pack";
	}

	@Override
	public void close() {

	}
}
