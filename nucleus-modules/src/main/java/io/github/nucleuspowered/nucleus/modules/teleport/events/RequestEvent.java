/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport.events;

import io.github.nucleuspowered.nucleus.api.teleport.event.NucleusTeleportEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.text.Text;

import java.util.Optional;

import javax.annotation.Nullable;

public abstract class RequestEvent extends AbstractEvent implements NucleusTeleportEvent.Request {

    @Nullable private TextComponent cancelMessage;
    private boolean isCancelled = false;

    private final Cause cause;
    private final Player targetEntity;

    private RequestEvent(final Cause cause, final Player targetEntity) {
        this.cause = cause;
        this.targetEntity = targetEntity;
    }

    @Override public Optional<Text> getCancelMessage() {
        return Optional.ofNullable(this.cancelMessage);
    }

    @Override public void setCancelMessage(@Nullable final TextComponent message) {
        this.cancelMessage = message;
    }

    @Override public Player getTargetEntity() {
        return this.targetEntity;
    }

    @Override public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override public void setCancelled(final boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override public Cause getCause() {
        return this.cause;
    }

    public static class CauseToPlayer extends RequestEvent implements NucleusTeleportEvent.Request.CauseToPlayer {

        public CauseToPlayer(final Cause cause, final Player targetEntity) {
            super(cause, targetEntity);
        }
    }

    public static class PlayerToCause extends RequestEvent implements NucleusTeleportEvent.Request.PlayerToCause {

        public PlayerToCause(final Cause cause, final Player targetEntity) {
            super(cause, targetEntity);
        }
    }
}
