package net.naughtyklaus.depot;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.naughtyklaus.depot.ui.DepotPanel;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
        name = "Supply Depot",
        description = "Enable the Supply Depot",
        loadWhenOutdated = true
)
public class DepotPlugin extends Plugin {

    @Inject
    private Client client;

    @Inject
    private ClientToolbar clientToolbar;

    private DepotPanel panel;
    private NavigationButton navButton;

    @Override
    protected void startUp() throws Exception {
        panel = injector.getInstance(DepotPanel.class);

        System.out.println(getClass().getPackage().getName().replace('.', '/') + "/");

        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "info_icon.png");

        navButton = NavigationButton.builder()
                .tooltip("Supply Depot")
                .icon(icon)
                .priority(10)
                .panel(panel)
                .build();

        clientToolbar.addNavigation(navButton);
    }

    @Override
    protected void shutDown() {
        panel.deconstruct();
        clientToolbar.removeNavigation(navButton);
        panel = null;
        navButton = null;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			//panel.isLogggedIn = true;
		}
    }
}
