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
		
		data = new OreData(50, 30,8,4,3,ModMain.oreCopper.blockID);
		oreList.add(data);
		data = new OreData(64, 40,8,4,3,ModMain.oreTin.blockID);
		oreList.add(data);
		data = new OreData(40, 20,8,4,3,ModMain.oreLead.blockID);
		oreList.add(data);
		data = new OreData(40, 20,8,4,3,ModMain.oreZinc.blockID);
		oreList.add(data);
		
	}
	
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		int x,y,z;
		int numOre;
		int numCluster;
		int tcount = 0;
		if (world.provider.dimensionId != 0) //Only generate in the main world.
			return;
		
		for(OreData data : oreList)
		{
			System.out.println("count " + tcount);
			tcount++;
			numCluster = random.nextInt(data.clusterPerChunk);
			if (numCluster == 0 && data.clusterPerChunk !=0)
				numCluster  = 1;
			//System.out.println(numCluster + "Clusters Generated");
			
			for(int count = 0; count < numCluster; count++)
			{
				x = random.nextInt(16);
				z = random.nextInt(16);
				y = random.nextInt(data.maxHeight - data.minHeight);
				x = x + (16 * chunkX);
				z = z + (16 * chunkZ);
				y = y + data.minHeight;
				numOre = MathHelper.clamp_int(random.nextInt(data.maxCluster), data.minCluster, data.maxCluster);
				
				generateOre(world, random, x,y ,z,data.oreType, numOre);

			}
			
		}
		
	}
	private void generateOre(World world, Random random, int x, int y, int z, int blockID, int ntg)
	{
		int lx,ly,lz;
		lx = x;
		ly = y;
		lz = z;
		int id;
		id = world.getBlockId(lx, ly, lz);
		if (id != Block.stone.blockID && id != Block.dirt.blockID )
		{
			return;
		}
		for (int i = 0; i < ntg; i++)
		{
			
			id = world.getBlockId(lx, ly, lz);
			
				world.setBlock(lx, ly, lz, blockID);
				System.out.println("Block ID: " + blockID );
			lx = lx + (random.nextInt(4) -2);
			System.out.println(random.nextInt(4)-2);
			ly = ly + (random.nextInt(4) -2);
			lz = lz + (random.nextInt(4) -2);
			
		}
		
	}

}
