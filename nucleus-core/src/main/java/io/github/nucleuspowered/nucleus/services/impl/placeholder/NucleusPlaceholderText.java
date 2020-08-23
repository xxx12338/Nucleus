/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.placeholder;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.placeholder.PlaceholderContext;
import org.spongepowered.api.text.placeholder.PlaceholderParser;
import org.spongepowered.api.text.placeholder.PlaceholderText;

import java.util.Collection;
import java.util.function.Function;

public class NucleusPlaceholderTextComponent implements PlaceholderTextComponent {

    private final PlaceholderContext context;
    private final PlaceholderParser parser;
    private final Collection<Function<Text, Text>> modifiers;

    public NucleusPlaceholderText(final PlaceholderContext context, final PlaceholderParser parser, final Collection<Function<Text, Text>> modifiers) {
        this.context = context;
        this.parser = parser;
        this.modifiers = modifiers;
    }

    @Override
    public PlaceholderContext getContext() {
        return this.context;
    }

    @Override
    public PlaceholderParser getParser() {
        return this.parser;
    }

    @Override
    public TextComponent toText() {
        TextComponent result = this.parser.parse(this.context);
        if (!result.isEmpty()) {
            for (final Function<Text, Text> modifier : this.modifiers) {
                result = modifier.apply(result);
            }
        }
        return result;
    }

}
