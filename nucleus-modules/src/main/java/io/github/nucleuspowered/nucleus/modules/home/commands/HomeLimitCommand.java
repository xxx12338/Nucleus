/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.home.commands;

import io.github.nucleuspowered.nucleus.modules.home.HomePermissions;
import io.github.nucleuspowered.nucleus.modules.home.services.HomeService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.User;
@Command(
        aliases = {"limit"},
        basePermission = HomePermissions.BASE_HOME_LIMIT,
        commandDescriptionKey = "home.limit",
        parentCommand = HomeCommand.class,
        async = true,
        associatedPermissions = HomePermissions.OTHERS_LIMIT
)
public class HomeLimitCommand implements ICommandExecutor {

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                serviceCollection.commandElementSupplier().createOnlyOtherUserPermissionElement(false, HomePermissions.OTHERS_LIMIT)
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final User user = context.getUserFromArgs();
        final HomeService service = context.getServiceCollection().getServiceUnchecked(HomeService.class);
        final int current = service.getHomeCount(user);
        final int max = service.getMaximumHomes(user);
        if (context.is(user)) {
            if (max == Integer.MAX_VALUE) {
                context.sendMessage("command.home.limit.selfu", current);
            } else {
                context.sendMessage("command.home.limit.self", current, max);
            }
        } else {
            if (max == Integer.MAX_VALUE) {
                context.sendMessage("command.home.limit.otheru", user.getName(), current);
            } else {
                context.sendMessage("command.home.limit.other", user.getName(), current, max);
            }
        }

        return context.successResult();
    }
}
