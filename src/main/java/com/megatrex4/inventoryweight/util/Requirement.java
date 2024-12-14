package com.megatrex4.inventoryweight.util;

public interface Requirement<T, C> {

	T getTarget();
	Boolean isMet(C check);
	String getId();
}
