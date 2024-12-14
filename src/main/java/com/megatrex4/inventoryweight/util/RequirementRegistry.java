package com.megatrex4.inventoryweight.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequirementRegistry implements Registry<Requirement<?, ?>> {

	private RequirementRegistry() {
	}

	public static RequirementRegistry REGISTRY = new RequirementRegistry();

	private final List<Requirement<?, ?>> requirements = new ArrayList<>();

	@Override
	public Requirement<?, ?> register(Requirement<?, ?> entry) {
		requirements.add(entry);
		return entry;
	}

	@Override
	public Optional<Requirement<?, ?>> get(String id) {
		for (Requirement<?, ?> requirement : requirements) if (requirement.getId().equals(id)) return Optional.of(requirement);
		return Optional.empty();
	}
}
