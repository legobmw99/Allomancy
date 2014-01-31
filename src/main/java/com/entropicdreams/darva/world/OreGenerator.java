package com.entropicdreams.darva.world;

import java.util.LinkedList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import com.entropicdreams.darva.common.Registry;
import com.entropicdreams.darva.util.AllomancyConfig;

import cpw.mods.fml.common.IWorldGenerator;

public class OreGenerator implements IWorldGenerator {

	public class OreData {
		public int maxHeight;
		public int minHeight;
		public int maxCluster;
		public int minCluster;
		public int clusterPerChunk;
		public int oreType;
		public boolean config;

		public OreData(int MaxHeight, int MinHeight, int MaxCluster,
				int MinCluster, int PerChunk, int OreType, boolean Config) {
			maxHeight = MaxHeight;
			minHeight = MinHeight;
			maxCluster = MaxCluster;
			minCluster = MinCluster;
			clusterPerChunk = PerChunk;
			oreType = OreType;
			config = Config;
		}
	}

	private LinkedList<OreData> oreList;

	public OreGenerator() {
		oreList = new LinkedList<OreData>();
		OreData data;

		data = new OreData(AllomancyConfig.copperMaxY,
				AllomancyConfig.copperMinY, 8, 4,
				AllomancyConfig.copperDensity, Registry.oreCopper.blockID,
				AllomancyConfig.generateCopper);
		oreList.add(data);
		data = new OreData(AllomancyConfig.tinMaxY, AllomancyConfig.tinMinY, 8,
				4, AllomancyConfig.tinDensity, Registry.oreTin.blockID,
				AllomancyConfig.generateTin);
		oreList.add(data);
		data = new OreData(AllomancyConfig.leadMaxY, AllomancyConfig.leadMinY,
				8, 4, AllomancyConfig.leadDensity, Registry.oreLead.blockID,
				AllomancyConfig.generateLead);
		oreList.add(data);
		data = new OreData(AllomancyConfig.zincMaxY, AllomancyConfig.zincMinY,
				8, 4, AllomancyConfig.zincDensity, Registry.oreZinc.blockID,
				AllomancyConfig.generateZinc);
		oreList.add(data);

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		int x, y, z;
		int numOre;
		int numCluster;
		int tcount = 0;
		if (world.provider.dimensionId != 0) // Only generate in the main world.
			return;

		for (OreData data : oreList) {
			tcount++;
			numCluster = random.nextInt(data.clusterPerChunk);
			if (numCluster == 0 && data.clusterPerChunk != 0)
				numCluster = 1;
			if (data.config) {
				for (int count = 0; count < numCluster; count++) {
					x = random.nextInt(16);
					z = random.nextInt(16);
					y = random.nextInt(data.maxHeight - data.minHeight);
					x = x + (16 * chunkX);
					z = z + (16 * chunkZ);
					y = y + data.minHeight;
					numOre = MathHelper.clamp_int(
							random.nextInt(data.maxCluster), data.minCluster,
							data.maxCluster);

					generateOre(world, random, x, y, z, data.oreType, numOre);

				}
			}
		}

	}

	private void generateOre(World world, Random random, int x, int y, int z,
			int blockID, int ntg) {
		int lx, ly, lz;
		lx = x;
		ly = y;
		lz = z;
		int id;
		id = world.getBlockId(lx, ly, lz);
		if (id != Block.stone.blockID ) {
			return;
		}
		for (int i = 0; i < ntg; i++) {

			id = world.getBlockId(lx, ly, lz);

			world.setBlock(lx, ly, lz, blockID);

			switch (random.nextInt(3)) {
			case 0:
				lx = lx + (random.nextInt(4) - 2);
				break;
			case 1:
				ly = ly + (random.nextInt(4) - 2);
				break;
			case 2:
				lz = lz + (random.nextInt(4) - 2);
				break;
			}

		}

	}

}