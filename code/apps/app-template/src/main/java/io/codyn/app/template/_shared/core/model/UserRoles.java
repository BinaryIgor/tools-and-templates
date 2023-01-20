package io.codyn.app.template._shared.core.model;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public record UserRoles(Set<UserRole> roles) {

    public static UserRoles of(UserRole... roles) {
        return new UserRoles(Set.of(roles));
    }

    public static UserRoles of(Set<UserRole> roles) {
        return new UserRoles(roles);
    }

    public static UserRoles of(Collection<UserRole> roles) {
        return new UserRoles(new LinkedHashSet<>(roles));
    }

    public static UserRoles empty() {
        return new UserRoles(Set.of());
    }

    public boolean containsModeratorOrAdmin() {
        return containsModerator() || containsAdmin();
    }

    public boolean containsModerator() {
        return roles.contains(UserRole.MODERATOR);
    }

    public boolean containsAdmin() {
        return roles.contains(UserRole.ADMIN);
    }

    public boolean containsOneOf(UserRole... toCheck) {
        for (var r : toCheck) {
            if (roles.contains(r)) {
                return true;
            }
        }
        return false;
    }

    public boolean doesNotContainAnyOf(UserRole... toCheck) {
        return !containsOneOf(toCheck);
    }

    public boolean containsAll(UserRole... toCheck) {
        return roles.containsAll(List.of(toCheck));
    }

    public boolean contains(UserRole role) {
        return roles.contains(role);
    }
}
