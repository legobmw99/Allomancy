package com.entropicdreams.darva.handlers;

import java.util.LinkedList;
import java.util.Random;

import com.entropicdreams.darva.ModMain;
import com.entropicdreams.darva.util.AllomancyConfig;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;

public class OreGenerator implements IWorldGenerator {
	
	@Override
	public void generate(Random random, int xChunk, int zChunk, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		int xPos,yPos,zPos;
		if (world.provider.dimensionId != 0) //Only generate in the main world.
			return;
		if(AllomancyConfig.generateCopper)
		{
			for (int q = 0; q <= AllomancyConfig.copperDensity; q++)
			{
				xPos = xChunk + random.nextInt(16);
				yPos = AllomancyConfig.copperMinY + random.nextInt(AllomancyConfig.copperMaxY - AllomancyConfig.copperMinY);
				zPos = zChunk + random.nextInt(16);
				new WorldGenMinable(ModMain.oreCopper.blockID, 4, 6).generate(world, random, xPos, yPos, zPos);
			}
		}
		if(AllomancyConfig.generateTin)
		{
			for (int q = 0; q <= AllomancyConfig.tinDensity; q++)
			{
				xPos = xChunk + random.nextInt(16);
				yPos = AllomancyConfig.tinMinY + random.nextInt(AllomancyConfig.tinMaxY - AllomancyConfig.tinMinY);
				zPos = zChunk + random.nextInt(16);
				new WorldGenMinable(ModMain.oreTin.blockID, 4, 6).generate(world, random, xPos, yPos, zPos);
			}
		}
		if(AllomancyConfig.generateLead)
		{
			for (int q = 0; q <= AllomancyConfig.leadDensity; q++)
			{
				xPos = xChunk + random.nextInt(16);
				yPos = AllomancyConfig.leadMinY + random.nextInt(AllomancyConfig.leadMaxY - AllomancyConfig.leadMinY);
				zPos = zChunk + random.nextInt(16);
				System.out.println(xPos);
				System.out.println(yPos);
				System.out.println(zPos);
				new WorldGenMinable(ModMain.oreLead.blockID, 100, 6).generate(world, random, xPos, yPos, zPos);
			}
		}
		if(AllomancyConfig.generateZinc)
		{
			for (int q = 0; q <= AllomancyConfig.copperDensity; q++)
			{
				xPos = xChunk + random.nextInt(16);
				yPos = AllomancyConfig.zincMinY + random.nextInt(AllomancyConfig.zincMaxY - AllomancyConfig.zincMinY);
				zPos = zChunk + random.nextInt(16);
				new WorldGenMinable(ModMain.oreZinc.blockID, 4, 6).generate(world, random, xPos, yPos, zPos);
			}
		}
	}
}
