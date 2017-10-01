/*
 * This file is part of NucleusBoson, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.boson;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.boson.modules.AbstractChannelModule;
import io.github.nucleuspowered.boson.modules.NicknameModule;
import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;

import javax.inject.Inject;

@Plugin(
        id = "boson",
        name = "Nucleus Boson",
        version = "1.0.0-SNAPSHOT",
        description = "An addon to Nucleus to allow client mods to read information",
        dependencies = {
            @Dependency(id = "nucleus")
        }
)
public class Boson {

    private static final String CHANNEL_NAME = "NUCLEUS";
    private final Logger logger;

    private final List<AbstractChannelModule<?>> modules = Lists.newArrayList();

    @Inject
    public Boson(Logger logger) {
        this.logger = logger;
    }

    // During init complete
    @Listener
    public void onPostInit(GamePostInitializationEvent event) {
        final ChannelBinding.IndexedMessageChannel channel = Sponge.getChannelRegistrar().createChannel(this, CHANNEL_NAME);

        // Check for services
        NucleusAPI.getNicknameService().ifPresent(x -> modules.add(new NicknameModule(this, x, channel)));

        if (this.modules.isEmpty()) {
            // message
            return;
        }

        this.modules.forEach(x -> {
            x.init();
            Sponge.getEventManager().registerListeners(this, x);
        });
    }

}
