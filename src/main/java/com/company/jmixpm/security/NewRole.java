package com.company.jmixpm.security;

import io.jmix.security.role.annotation.ResourceRole;

import javax.annotation.Nonnull;

@Nonnull
@ResourceRole(name = "NewRole", code = "new-role")
public interface NewRole {
}