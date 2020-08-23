/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.nickname.events;

import io.github.nucleuspowered.nucleus.api.module.nickname.event.NucleusChangeNicknameEvent;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.text.Text;

import java.util.Optional;

import javax.annotation.Nullable;

public class ChangeNicknameEventPost extends AbstractEvent implements NucleusChangeNicknameEvent.Post {

    private final Cause cause;
    private final User target;
    @Nullable private final TextComponent previousNickname;
    @Nullable private final TextComponent newNickname;

    public ChangeNicknameEventPost(final Cause cause, @Nullable final TextComponent previousNickname, @Nullable final TextComponent newNickname, final User target) {
        this.cause = cause;
        this.previousNickname = previousNickname;
        this.newNickname = newNickname;
        this.target = target;
    }

    @Override public User getUser() {
        return this.target;
    }

    @Override
    public Optional<Text> getPreviousNickname() {
        return Optional.ofNullable(this.previousNickname);
    }

    @Override public Optional<Component> getNickname() {
        return Optional.ofNullable(this.newNickname);
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }
}
