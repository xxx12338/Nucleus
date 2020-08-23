/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.info.commands;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.io.TextFileController;
import io.github.nucleuspowered.nucleus.modules.info.InfoPermissions;
import io.github.nucleuspowered.nucleus.modules.info.config.InfoConfig;
import io.github.nucleuspowered.nucleus.modules.info.parameter.InfoArgument;
import io.github.nucleuspowered.nucleus.modules.info.services.InfoHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.inject.Inject;

@Command(
        aliases = {"info", "einfo"},
        async = true,
        basePermission = InfoPermissions.BASE_INFO,
        commandDescriptionKey = "info",
        associatedPermissions = InfoPermissions.INFO_LIST
)
@EssentialsEquivalent({"info", "ifo", "news", "about", "inform"})
public class InfoCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private final InfoHandler infoService;
    private InfoConfig infoConfig = new InfoConfig();

    private final String key = "section";

    @Inject
    public InfoCommand(final INucleusServiceCollection serviceCollection) {
        this.infoService = serviceCollection.getServiceUnchecked(InfoHandler.class);
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        this.infoConfig = serviceCollection.moduleDataProvider().getModuleConfig(InfoConfig.class);
    }

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
            GenericArguments.flags()
                    .valueFlag(serviceCollection.commandElementSupplier().createPermissionParameter(
                            GenericArguments.markTrue(Text.of("list")),
                            InfoPermissions.INFO_LIST,
                            false
                    ), "l", "-list")
                    .buildWith(
                        GenericArguments.optional(new InfoArgument(Text.of(this.key), this.infoService, serviceCollection)))
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        Optional<InfoArgument.Result> oir = context.getOne(this.key, InfoArgument.Result.class);
        if (this.infoConfig.isUseDefaultFile() && !oir.isPresent() && !context.hasAny("l")) {
            // Do we have a default?
            final String def = this.infoConfig.getDefaultInfoSection();
            final Optional<TextFileController> list = this.infoService.getSection(def);
            if (list.isPresent()) {
                oir = Optional.of(new InfoArgument.Result(
                        this.infoService.getInfoSections().stream().filter(def::equalsIgnoreCase).findFirst().get(), list.get()));
            }
        }

        if (oir.isPresent()) {
            final TextFileController controller = oir.get().text;
            final TextComponent def = TextSerializers.FORMATTING_CODE.deserialize(oir.get().name);
            final TextComponent title = context.getMessage("command.info.title.section", controller.getTitle(context.getCommandSourceRoot()).orElseGet(() -> Text.of(def)));

            controller.sendToPlayer(context.getCommandSourceRoot(), title);
            return context.successResult();
        }

        // Create a list of pages to load.
        final Set<String> sections = this.infoService.getInfoSections();
        if (sections.isEmpty()) {
            return context.errorResult("command.info.none");
        }

        // Create the text.
        final List<Text> s = Lists.newArrayList();
        sections.forEach(x -> {
            final Text.Builder tb = Text.builder().append(Text.builder(x)
                    .color(TextColors.GREEN).style(TextStyles.ITALIC)
                    .onHover(TextActions.showText(context.getMessage("command.info.hover", x)))
                    .onClick(TextActions.runCommand("/info " + x)).build());

            // If there is a title, then add it.
            this.infoService.getSection(x).get().getTitle(context.getCommandSourceRoot()).ifPresent(sub ->
                tb.append(Text.of(TextColors.GOLD, " - ")).append(sub)
            );

            s.add(tb.build());
        });

        Util.getPaginationBuilder(context.getCommandSourceRoot()).contents()
                .header(context.getMessage("command.info.header.default"))
                .title(context.getMessage("command.info.title.default"))
                .contents(s.stream().sorted(Comparator.comparing(Text::toPlain)).collect(Collectors.toList()))
                .padding(Text.of(TextColors.GOLD, "-"))
                .sendTo(context.getCommandSourceRoot());
        return context.successResult();
    }
}
