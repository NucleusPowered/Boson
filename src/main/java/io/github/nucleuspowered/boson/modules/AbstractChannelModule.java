/*
 * This file is part of NucleusBoson, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.boson.modules;

import io.github.nucleuspowered.boson.Boson;
import io.github.nucleuspowered.boson.messages.IndexedMessage;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.Message;

public abstract class AbstractChannelModule<T> {

    final T service;
    final ChannelBinding.IndexedMessageChannel channelBinding;
    final Boson boson;

    public AbstractChannelModule(Boson boson, T service, ChannelBinding.IndexedMessageChannel channelBinding) {
        this.service = service;
        this.channelBinding = channelBinding;
        this.boson = boson;
    }

    public abstract void init();

    public final int getId(Class<? extends Message> cm) {
        return cm.getAnnotation(IndexedMessage.class).value();
    }

}
