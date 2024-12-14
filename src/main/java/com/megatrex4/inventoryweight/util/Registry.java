package com.megatrex4.inventoryweight.util;

import java.util.Optional;

public interface Registry<T> {

	T register(T entry);
	Optional<T> get(String id);
}
