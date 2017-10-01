/*
 * This file is part of NucleusBoson, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.boson.modules;

import io.github.nucleuspowered.boson.Boson;
import io.github.nucleuspowered.boson.messages.inbound.C100RequestNameMapping;
import io.github.nucleuspowered.boson.messages.outbound.S00NameChangeMapping;
import io.github.nucleuspowered.boson.messages.outbound.S01NameMappings;
import io.github.nucleuspowered.nucleus.api.events.NucleusChangeNicknameEvent;
import io.github.nucleuspowered.nucleus.api.service.NucleusNicknameService;

import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Identifiable;

import java.util.WeakHashMap;
import java.util.stream.Collectors;

public class NicknameModule extends AbstractChannelModule<NucleusNicknameService> {

    private WeakHashMap<RemoteConnection, Long> timestamp = new WeakHashMap<>();

    public NicknameModule(Boson boson, NucleusNicknameService service, ChannelBinding.IndexedMessageChannel channelBinding) {
        super(boson, service, channelBinding);
    }

    @Override
    public void init() {
        this.channelBinding.registerMessage(C100RequestNameMapping.class, getId(C100RequestNameMapping.class), Platform.Type.SERVER,
                (message, connection, side) -> {

            // 5 second throttle
            if (side.isServer() && this.timestamp.getOrDefault(connection, 0L) + 5000L < System.currentTimeMillis()) {
                if (connection instanceof PlayerConnection) {
                    this.channelBinding.sendTo(((PlayerConnection) connection).getPlayer(), getMappingMessage());
                }

                this.timestamp.put(connection, System.currentTimeMillis());
                // Otherwise, nothing to send to
            }
        });

        this.channelBinding.registerMessage(S00NameChangeMapping.class, getId(S00NameChangeMapping.class));
        this.channelBinding.registerMessage(S01NameMappings.class, getId(S01NameMappings.class));
    }

    private S00NameChangeMapping getMappingMessage(Player player) {
        return new S00NameChangeMapping(player.getUniqueId(), this.service.getNickname(player).orElseGet(() -> Text.of(player.getName())));
    }

    private S01NameMappings getMappingMessage() {
        return new S01NameMappings(
                Sponge.getServer().getOnlinePlayers().stream().collect(Collectors.toMap(
                        Identifiable::getUniqueId,
                        v -> this.service.getNickname(v).orElseGet(() -> Text.of(v.getName()))
                ))
        );
    }

    @Listener
    public void onPlayerLogin(ClientConnectionEvent.Join event) {
        Task.builder().async().execute(t -> {
            this.channelBinding.sendTo(event.getTargetEntity(), getMappingMessage());
            this.channelBinding.sendToAll(getMappingMessage(event.getTargetEntity()));
        }).submit(this.boson);
    }

    @Listener(order = Order.POST)
    public void onPlayerChangeNickname(NucleusChangeNicknameEvent event, @Getter("getTargetUser") Player player) {
        Task.builder().async().execute(t -> this.channelBinding.sendToAll(getMappingMessage(player))).submit(this.boson);
    }

}
