/*
 * This file is part of NucleusBoson, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.boson.messages.inbound;

import io.github.nucleuspowered.boson.messages.IndexedMessage;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.Message;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
@IndexedMessage(100)
public class C100RequestNameMapping implements Message {

    @Override public void readFrom(ChannelBuf buf) {

    }

    @Override public void writeTo(ChannelBuf buf) {

    }
}
