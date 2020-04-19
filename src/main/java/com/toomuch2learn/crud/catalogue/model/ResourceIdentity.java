package com.toomuch2learn.crud.catalogue.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class ResourceIdentity {

    @NonNull private Long id;
}
