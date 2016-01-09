package common.legobmw99.allomancy.util;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

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
	}

	@Override
	public boolean equals(Object paramObject) {
		// TODO Auto-generated method stub

		vector3 vec = (vector3) paramObject;
		if ((vec.X == this.X) && (vec.Y == this.Y) && (vec.Z == this.Z)) {
			System.out.println("True");
			return true;
		}
		System.out.println("False");
		return false;
	}
}
