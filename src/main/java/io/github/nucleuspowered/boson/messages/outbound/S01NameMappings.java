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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@NonnullByDefault
@IndexedMessage(1)
public class S01NameMappings implements Message {

    // Number of entries, nicknames

    private final Map<UUID, Text> nicknames = new HashMap<>();

    public S01NameMappings() {
    }

    public S01NameMappings(Map<UUID, Text> nicknames) {
        this.nicknames.putAll(nicknames);
    }

    @Override public void readFrom(ChannelBuf buf) {
        final int count = buf.readInteger();
        this.nicknames.clear();

        for (int i = 0; i < count; i++) {
            this.nicknames.put(buf.readUniqueId(), TextSerializers.JSON.deserialize(buf.readString()));
        }
    }

    @Override public void writeTo(ChannelBuf buf) {
        buf.writeInteger(this.nicknames.size());

        this.nicknames.forEach((uuid, text) -> {
            buf.writeUniqueId(uuid);
            buf.writeString(TextSerializers.JSON.serialize(text));
        });
    }
}
