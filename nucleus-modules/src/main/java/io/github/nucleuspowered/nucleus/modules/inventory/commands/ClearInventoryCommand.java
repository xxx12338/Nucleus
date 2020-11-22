/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.inventory.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.modules.inventory.InventoryPermissions;
import io.github.nucleuspowered.nucleus.modules.inventory.events.ClearInventoryEvent;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.Carrier;

import java.util.UUID;

@Command(
        aliases = {"clear", "clearinv", "clearinventory", "ci", "clearinvent"},
        basePermission = InventoryPermissions.BASE_CLEAR,
        commandDescriptionKey = "clear",
        associatedPermissions = InventoryPermissions.OTHERS_CLEAR
)
@EssentialsEquivalent({"clearinventory", "ci", "clean", "clearinvent"})
public class ClearInventoryCommand implements ICommandExecutor {

    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[] {
                Flag.of("a", "all")
        };
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.commandElementSupplier().createOnlyOtherUserPermissionElement(InventoryPermissions.OTHERS_CLEAR)
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final User user = context.getUserFromArgs();
        final boolean all = context.hasFlag("a");
        final Carrier target;
        if (user.getPlayer().isPresent()) {
            target = user.getPlayer().get();
        } else {
            target = user;
        }

        try {
            return this.clear(context, target, user.getUniqueId(), user.getName(), all);
        } catch (final UnsupportedOperationException ex) {
            return context.errorResult("command.clearinventory.offlinenotsupported");
        }
    }

    private ICommandResult clear(final ICommandContext context, final Carrier target, final UUID uuid, final String name, final boolean all) {
        if (Sponge.getEventManager().post(new ClearInventoryEvent.Pre(Sponge.getServer().getCauseStackManager().getCurrentCause(), uuid, all))) {
            return context.errorResult("command.clearinventory.cancelled", name);
        }
        if (all) {
            target.getInventory().clear();
        } else {
            Util.getStandardInventory(target).clear();
        }
        Sponge.getEventManager().post(new ClearInventoryEvent.Post(Sponge.getServer().getCauseStackManager().getCurrentCause(), uuid, all));
        context.sendMessage("command.clearinventory.success", name);
        return context.successResult();
    }
}