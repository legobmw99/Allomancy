package common.legobmw99.allomancy.util;

import net.minecraft.util.math.BlockPos;

public class vector3 {

	public int X;
	public int Y;
	public int Z;
	public BlockPos pos;

	public vector3(int x, int y, int z) {
		this.X = x;
		this.Y = y;
		this.Z = z;
	}

	public vector3(BlockPos pos1) {
		this.pos = pos1;
		this.X = pos1.getX();
		this.Y = pos1.getY();
		this.Z = pos1.getZ();

	}

	@Override
	public boolean equals(Object paramObject) {
		vector3 vec = (vector3) paramObject;
		if ((vec.X == this.X) && (vec.Y == this.Y) && (vec.Z == this.Z)) {
			return true;
		}
		return false;
	}
}
