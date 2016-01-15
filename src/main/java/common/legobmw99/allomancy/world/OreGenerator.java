package common.legobmw99.allomancy.world;

import java.util.LinkedList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import common.legobmw99.allomancy.blocks.OreBlock;
import common.legobmw99.allomancy.util.AllomancyConfig;

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

		public OreData(int MaxHeight, int MinHeight, int MaxCluster,
				int MinCluster, int PerChunk, Block OreType, boolean Config) {
			this.maxHeight = MaxHeight;
			this.minHeight = MinHeight;
			this.maxCluster = MaxCluster;
			this.minCluster = MinCluster;
			this.clusterPerChunk = PerChunk;
			this.oreType = OreType;
			this.config = Config;
		}
	}

	private LinkedList<OreData> oreList;

	public OreGenerator() {
		this.oreList = new LinkedList<OreData>();
		OreData data;

		data = new OreData(AllomancyConfig.copperMaxY,
				AllomancyConfig.copperMinY, 8, 4,
				AllomancyConfig.copperDensity, OreBlock.oreCopper,
				AllomancyConfig.generateCopper);
		this.oreList.add(data);
		data = new OreData(AllomancyConfig.tinMaxY, AllomancyConfig.tinMinY, 8,
				4, AllomancyConfig.tinDensity, OreBlock.oreTin,
				AllomancyConfig.generateTin);
		this.oreList.add(data);
		data = new OreData(AllomancyConfig.leadMaxY, AllomancyConfig.leadMinY,
				8, 4, AllomancyConfig.leadDensity, OreBlock.oreLead,
				AllomancyConfig.generateLead);
		this.oreList.add(data);
		data = new OreData(AllomancyConfig.zincMaxY, AllomancyConfig.zincMinY,
				8, 4, AllomancyConfig.zincDensity, OreBlock.oreZinc,
				AllomancyConfig.generateZinc);
		this.oreList.add(data);

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		int x, y, z;
		int numOre;
		int numCluster;
		if (world.provider.getDimensionId() != 0) {
			return;
		}

		for (OreData data : this.oreList) {
			numCluster = random.nextInt(5);
			if ((numCluster == 0) && (data.clusterPerChunk != 0)) {
				numCluster = 1;
			}
			
				for (int count = 0; count < numCluster; count++) {
					x = random.nextInt(16);
					z = random.nextInt(16);
					y = random.nextInt(40);
					x = x + (16 * chunkX);
					z = z + (16 * chunkZ);
					y = y + data.minHeight;
					numOre = MathHelper.clamp_int(
							random.nextInt(data.maxCluster), data.minCluster,
							data.maxCluster);
					
					this.generateOre(world, random, x, y, z, data.oreType,
							numOre);
				
			}
		}
	}

	private void generateOre(World world, Random random, int x, int y, int z,
			Block block, int ntg) {
		int lx, ly, lz;
		lx = x;
		ly = y;
		lz = z;
        BlockPos pos1 = new BlockPos(lx, ly, lz);
          (new WorldGenMinable(block.getDefaultState(), ntg*2)).generate(world, random, pos1);
	}
}