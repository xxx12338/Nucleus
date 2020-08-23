/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.placeholder;

import net.kyori.adventure.text.ComponentLike;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.service.permission.Subject;

/**
 * Provides a way to determine how Nucleus uses placeholders.
 */
public interface NucleusPlaceholderService {

    ComponentLike parse(@Nullable Object sender, String input);

    /**
     * Gets the parser associated with the provided token name, if any,
     * prefixing un-namespaced tokens with the Nucleus prefix.
     *
     * @param token The token name
     * @return The {@link PlaceholderParser}, if any
     */
    Optional<PlaceholderParser> getParser(String token);

    /**
     * Gets the Nucleus placeholder parser for displaying
     * {@link Subject#getOption(String)}
     *
     * @return The parser.
     */
    PlaceholderParser optionParser();

    /**
     * Creates a {@link PlaceholderText} for displaying a {@link Subject}s
     * option.
     *
     * @return The {@link PlaceholderText}
     */
    PlaceholderTextComponent textForSubjectAndOption(Subject subject, String option);

}
