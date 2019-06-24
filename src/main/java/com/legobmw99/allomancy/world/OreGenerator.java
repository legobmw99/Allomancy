package com.legobmw99.allomancy.world;

import com.legobmw99.allomancy.util.AllomancyConfig;
import com.legobmw99.allomancy.util.Registry;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.ArrayList;
import java.util.Random;

public class OreGenerator implements IWorldGenerator {
	
	public class OreData {
		public int maxHeight;
		public int minHeight;
		public int maxCluster;
		public int minCluster;
		public int clusterPerChunk;
		public Block oreType;
		public Block curblock;
		public boolean config;

		/**
		 * Construct an OreData with the given parameters
		 * 
		 * @param MaxHeight
		 *            the maximum height it can generate at
		 * @param MinHeight
		 *            the minumum height it can generate at
		 * @param MaxCluster
		 *            the largest grouping possible
		 * @param MinCluster
		 *            the smallest grouping possible
		 * @param PerChunk
		 *            number of times it can generate per chunk
		 * @param OreType
		 *            the block to generate
		 * @param Config
		 *            whether or not it is enabled in the configuration file
		 */
		public OreData(int MaxHeight, int MinHeight, int MaxCluster, int MinCluster, int PerChunk, Block OreType,
				boolean Config) {
			this.maxHeight = MaxHeight;
			this.minHeight = MinHeight;
			this.maxCluster = MaxCluster;
			this.minCluster = MinCluster;
			this.clusterPerChunk = PerChunk;
			this.oreType = OreType;
			this.config = Config;
		}
	}

	private ArrayList<OreData> oreList;

	public OreGenerator() {
		this.oreList = new ArrayList<OreData>();
		OreData data;

		data = new OreData(AllomancyConfig.copper_max_y, AllomancyConfig.copper_min_y, 8, 4, AllomancyConfig.copper_density,
				Registry.copper_ore, AllomancyConfig.generate_copper);
		this.oreList.add(data);
		data = new OreData(AllomancyConfig.tin_max_y, AllomancyConfig.tin_min_y, 8, 4, AllomancyConfig.tin_density,
				Registry.tin_ore, AllomancyConfig.generate_tin);
		this.oreList.add(data);
		data = new OreData(AllomancyConfig.lead_max_y, AllomancyConfig.lead_min_y, 8, 4, AllomancyConfig.lead_density,
				Registry.lead_ore, AllomancyConfig.generate_lead);
		this.oreList.add(data);
		data = new OreData(AllomancyConfig.zinc_max_y, AllomancyConfig.zinc_min_y, 8, 4, AllomancyConfig.zinc_density,
				Registry.zinc_ore, AllomancyConfig.generate_zinc);
		this.oreList.add(data);

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, ChunkGenerator chunkGenerator,
			AbstractChunkProvider chunkProvider) {
		int x, y, z;
		int numOre;
		int numCluster;
		// Only generate in overworld
		if (world.getDimension().getType() != DimensionType.OVERWORLD) {
			return;
		}

		for (OreData data : this.oreList) {
			numCluster = random.nextInt(5);
			if ((numCluster == 0) && (data.clusterPerChunk != 0)) {
				numCluster = 1;
			}
			if (data.config) { // Check that you don't have the ore disabled
				for (int count = 0; count < numCluster; count++) {
					x = random.nextInt(16);
					z = random.nextInt(16);
					y = random.nextInt(data.maxHeight);
					x = x + (16 * chunkX);
					z = z + (16 * chunkZ);
					y = y + data.minHeight;
					numOre = MathHelper.clamp(random.nextInt(data.maxCluster), data.minCluster, data.maxCluster);
					BlockPos pos = new BlockPos(x, y, z);

					//todo (new IWorldGenerator(data.oreType.getDefaultState(), numOre * 2)).generate(world, random, pos);
				}
			}
		}
	}

}