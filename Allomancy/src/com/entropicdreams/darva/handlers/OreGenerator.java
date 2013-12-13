package com.entropicdreams.darva.handlers;

import java.util.LinkedList;
import java.util.Random;

import com.entropicdreams.darva.ModMain;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;

public class OreGenerator implements IWorldGenerator {

	public class OreData
	{
		public int maxHeight;
		public int minHeight;
		public int maxCluster;
		public int minCluster;
		public int clusterPerChunk;
		public int oreType;
		
		public OreData(int MaxHeight, int MinHeight, int MaxCluster, int MinCluster, int PerChunk, int OreType)
		{
			maxHeight = MaxHeight;
			minHeight = MinHeight;
			maxCluster = MaxCluster;
			minCluster = MinCluster;
			clusterPerChunk = PerChunk;
			oreType = OreType;
		}
	}
	
	private LinkedList<OreData> oreList;
	
	public OreGenerator()
	{
		oreList = new LinkedList<OreData>();
		OreData data ;
		
		data = new OreData(50, 30,6,2,3,ModMain.oreCopper.blockID);
		data = new OreData(64, 40,6,2,3,ModMain.oreTin.blockID);
		data = new OreData(40, 20,6,2,3,ModMain.oreLead.blockID);
		data = new OreData(40, 20,6,2,3,ModMain.oreTin.blockID);
		

		
		oreList.add(data);
		
	}
	
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

		int x,y,z;
		int numOre;
		int numCluster;
		if (world.provider.dimensionId != 0) //Only generate in the main world.
			return;
		
		for(OreData data : oreList)
		{
			numCluster = random.nextInt(data.maxCluster);
			if (numCluster == 0 && data.maxCluster !=0)
				numCluster  = 1;
			for(int count = 0; count < numCluster; count++)
			{
				x = random.nextInt(16);
				z = random.nextInt(16);
				y = random.nextInt(70);
				numOre = MathHelper.clamp_int(random.nextInt(data.maxCluster), data.minCluster, data.maxCluster);
				new WorldGenMinable(data.oreType, numOre, Block.stone.blockID).generate(world, random, x, y, z);
			}
		}
		
	}

}
