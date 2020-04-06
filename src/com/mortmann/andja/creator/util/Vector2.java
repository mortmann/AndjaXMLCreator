package com.mortmann.andja.creator.util;

public class Vector2 {
	float one;
	float two;
	public Vector2(float x, float y) {
		this.one = x;
		this.two = y;
	}
	@Override
	public int hashCode() {
	    return (int) (one * 31 + two);
	}
	@Override
	public String toString() {
		return one + "|" + two;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Vector2 == false)
			return false;
		Vector2 v2 = (Vector2) obj;
		return one == v2.one && two == v2.two;
	}
}