package com.mortmann.andja.creator.util;

public class Vector2 {
	public float x;
	public float y;
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	@Override
	public int hashCode() {
	    return (int) (x * 31 + y);
	}
	@Override
	public String toString() {
		return x + "|" + y;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Vector2 == false)
			return false;
		Vector2 v2 = (Vector2) obj;
		return x == v2.x && y == v2.y;
	}
}