/*
 * This file is part of NucleusBoson, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.boson.messages.outbound;

import io.github.nucleuspowered.boson.messages.IndexedMessage;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.Message;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.UUID;

@NonnullByDefault
@IndexedMessage(0)
public class S00NameChangeMapping implements Message {

    // Packet: UUID then Nickname

    private UUID uuid;
    private Text text;

    public S00NameChangeMapping() {
    }

    public S00NameChangeMapping(UUID uuid, Text text) {
        this.uuid = uuid;
        this.text = text;
    }

    @Override public void readFrom(ChannelBuf buf) {
        this.uuid = buf.readUniqueId();
        this.text = TextSerializers.JSON.deserialize(buf.readString());
    }

    @Override public void writeTo(ChannelBuf buf) {
        buf.writeUniqueId(this.uuid);
        buf.writeString(TextSerializers.JSON.serialize(this.text));
    }
}
