/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.commandelement;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.ICommandElementSupplier;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.Locale;
import java.util.Optional;

@Singleton
public class CommandElementSupplier implements ICommandElementSupplier {

    private final INucleusServiceCollection serviceCollection;

    @Inject
    public CommandElementSupplier(final INucleusServiceCollection serviceCollection) {
        this.serviceCollection = serviceCollection;
    }

    @Override
    public Parameter.Value<Locale> createLocaleElement(final String key) {
        return Parameter.builder(Locale.class).setKey(key).parser(new LocaleElement(this.serviceCollection)).build();
    }

    @Override
    public Parameter.Value<User> createOnlyOtherUserPermissionElement(final String permission) {
        return Parameter.userOrSource().setKey(NucleusParameters.ONE_USER.getKey()).setRequiredPermission(permission).build();
    }

    @Override
    public Parameter.Value<ServerPlayer> createOnlyOtherPlayerPermissionElement(final String permission) {
        return Parameter.playerOrSource().setKey(NucleusParameters.ONE_PLAYER.getKey()).setRequiredPermission(permission).build();
    }

    @Override
    public User getUserFromParametersElseSelf(final ICommandContext context) throws CommandException {
        final Optional<? extends User> user = context.getOne(NucleusParameters.Keys.USER, User.class).filter(context::isNot);
        if (!user.isPresent()) {
            return context.getIfPlayer().getUser();
        }

        // If not self, we set no cooldown etc.
        context.setCooldown(0);
        context.setCost(0);
        context.setWarmup(0);
        return user.get();
    }

}
