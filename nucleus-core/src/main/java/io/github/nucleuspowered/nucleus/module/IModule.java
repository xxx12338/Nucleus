/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.module;

import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.SyncTaskBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.playerinformation.NucleusProvider;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public interface IModule {

    void init(INucleusServiceCollection serviceCollection);

    default void postLoad(final INucleusServiceCollection serviceCollection) {
    }

    Collection<Class<? extends ICommandExecutor>> getCommands();

    Optional<Class<?>> getPermissions();

    Collection<Class<? extends ListenerBase>> getListeners();

    default Collection<Class<? extends TaskBase>> getAsyncTasks() {
        return Collections.emptyList();
    }

    default Collection<Class<? extends SyncTaskBase>> getSyncTasks() {
        return Collections.emptyList();
    }

    default Optional<NucleusProvider> getInfoProvider() {
        return Optional.empty();
    }

    interface Configurable<T> extends IModule {

        Class<T> getConfigClass();

        default Collection<ConfigurationTransformation> getTransformations() {
            return Collections.emptyList();
        }

        @Nullable
        default TypeSerializerCollection moduleTypeSerializers() {
            return null;
        }

        default T createInstance() {
            try {
                return this.getConfigClass().newInstance();
            } catch (final InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

    }

}