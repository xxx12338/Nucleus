/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.powertool.commands;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.modules.powertool.PowertoolPermissions;
import io.github.nucleuspowered.nucleus.modules.powertool.services.PowertoolService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@EssentialsEquivalent({"powertool", "pt"})
@Command(
        aliases = {"powertool", "pt"},
        basePermission = PowertoolPermissions.BASE_POWERTOOL,
        commandDescriptionKey = "powertool"
)
public class PowertoolCommand implements ICommandExecutor {

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                NucleusParameters.OPTIONAL_COMMAND
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Player src = context.getCommandSourceRoot();
        final ItemStack itemStack = src.getItemInHand(HandTypes.MAIN_HAND)
                .orElseThrow(() -> context.createException("command.powertool.noitem"));

        final Optional<String> command = context.getOne(NucleusParameters.Keys.COMMAND, String.class);
        return command
                .map(s -> setPowertool(context, src, itemStack.getType(), s))
                .orElseGet(() -> viewPowertool(context, src, itemStack));
    }

    private ICommandResult viewPowertool(final ICommandContext context, final Player src, final ItemStack item) {
        final Optional<List<String>> cmds = context.getServiceCollection().getServiceUnchecked(PowertoolService.class)
                .getPowertoolForItem(src.getUniqueId(), item.getType());
        if (cmds.isPresent() && !cmds.get().isEmpty()) {
            Util.getPaginationBuilder(src)
                    .contents(cmds.get().stream().map(f -> Text.of(TextColors.YELLOW, f)).collect(Collectors.toList()))
                    .title(context.getMessage("command.powertool.viewcmdstitle", Text.of(item), Text.of(item.getType().getId())))
                    .sendTo(src);
        } else {
            src.sendMessage(context.getMessage("command.powertool.nocmds", Text.of(item)));
        }

        return context.successResult();
    }

    private ICommandResult setPowertool(final ICommandContext context, final Player src, final ItemType item, String command) {
        // For consistency, if a command starts with "/", remove it, but just
        // once. WorldEdit commands can be input using "//"
        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        context.getServiceCollection().getServiceUnchecked(PowertoolService.class).setPowertool(src.getUniqueId(), item, Lists.newArrayList(command));
        context.sendMessage("command.powertool.set", item.getId(), command);
        return context.successResult();
    }
}
