/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.commands;

import io.github.nucleuspowered.nucleus.api.util.data.NamedLocation;
import io.github.nucleuspowered.nucleus.modules.jail.JailParameters;
import io.github.nucleuspowered.nucleus.modules.jail.JailPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

@Command(
        aliases = "tp",
        basePermission = JailPermissions.BASE_JAILS_TP,
        commandDescriptionKey = "jails.tp",
        parentCommand = JailsCommand.class
)
public class JailTeleportCommand implements ICommandExecutor {

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                JailParameters.JAIL.get(serviceCollection)
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final NamedLocation location = context.requireOne(JailParameters.JAIL_KEY, NamedLocation.class);
        final Transform<World> location1 = location.getTransform().orElseThrow(() -> context.createException("command.jails.tp.noworld",
                location.getName()));

        final Player player = context.getIfPlayer();
        player.setTransform(location1);
        context.sendMessage("command.jails.tp.success", location.getName());
        return context.successResult();
    }
}
