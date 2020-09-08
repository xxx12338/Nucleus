/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.ServerLocation;
import org.spongepowered.math.vector.Vector3d;
import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.core.NucleusPlayerMetadataService;
import io.github.nucleuspowered.nucleus.core.CoreKeys;
import io.github.nucleuspowered.nucleus.scaffold.service.ServiceBase;
import io.github.nucleuspowered.nucleus.scaffold.service.annotations.APIService;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.IUserDataObject;
import io.github.nucleuspowered.nucleus.services.interfaces.IStorageManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

@APIService(NucleusPlayerMetadataService.class)
public class PlayerMetadataService implements NucleusPlayerMetadataService, ServiceBase {

    private final IStorageManager storageManager;

    @Inject
    public PlayerMetadataService(final INucleusServiceCollection serviceCollection) {
        this.storageManager = serviceCollection.storageManager();
    }

    @Override public Optional<Result> getUserData(final UUID uuid) {
        return this.storageManager.getUserService().get(uuid).join().map(x -> new ResultImpl(uuid, x));
    }

    public static final class ResultImpl implements Result {

        // private final User user;

        private final UUID uuid;
        @Nullable private final Instant login;
        @Nullable private final Instant logout;
        @Nullable private final String lastIP;

        private ResultImpl(final UUID uuid, final IUserDataObject udo) {
            // this.user = userService.getUser();

            this.uuid = uuid;
            this.login = udo.get(CoreKeys.LAST_LOGIN).orElse(null);
            this.logout = udo.get(CoreKeys.LAST_LOGOUT).orElse(null);
            this.lastIP = udo.get(CoreKeys.IP_ADDRESS).orElse(null);
        }

        @Override public Optional<Instant> getLastLogin() {
            return Optional.ofNullable(this.login);
        }

        @Override public Optional<Instant> getLastLogout() {
            return Optional.ofNullable(this.logout);
        }

        @Override public Optional<String> getLastIP() {
            return Optional.ofNullable(this.lastIP);
        }

        @Override public Optional<Tuple<WorldProperties, Vector3d>> getLastLocation() {
            final Optional<ServerPlayer> pl = Sponge.getServer().getPlayer(this.uuid);
            if (pl.isPresent()) {
                final ServerLocation l = pl.get().getServerLocation();
                return Optional.of(Tuple.of(
                    l.getWorld().getProperties(),
                    l.getPosition()
                ));
            }

            return Optional.empty();
        }
    }
}