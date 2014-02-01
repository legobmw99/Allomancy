package com.entropicdreams.darva.util;

//Fucking seriously?!?
public class vector3 {

	public int X;
	public int Y;
	public int Z;

	public vector3(int x, int y, int z) {
		this.X = x;
		this.Y = y;
		this.Z = z;
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
