/*
 * Copyright (c) 2018 Abex
 * Copyright (c) 2018, Psikoi <https://github.com/psikoi>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.naughtyklaus.depot.ui;

/*
 * Copyright (c) 2018, Seth <https://github.com/sethtroll>
 * Copyright (c) 2018, Psikoi <https://github.com/psikoi>
 * Copyright (c) 2024, Naughty Klaus <https://github.com/Naughty-Klaus>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.naughtyklaus.depot.DepotPlugin;
import net.naughtyklaus.depot.events.*;
import net.naughtyklaus.depot.model.item.ItemResource;
import net.naughtyklaus.depot.model.item.OrderContainer;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.client.account.SessionManager;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ImageUtil;
import net.runelite.http.api.item.ItemPrice;
import net.runelite.http.api.item.ItemStats;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import okhttp3.*;

@Getter
public class DepotPanel extends PluginPanel {

    @Inject
    public Gson gson;

    @Getter
    @Inject
    private OkHttpClient httpClient;

    public OrderContainer[] getOrderContainer() throws IOException {
        String url = "http://depot.naughtyklaus.net:43658/api/orders";
        Request request = new Request.Builder().url(url).build();

        try (Response response = getHttpClient().newCall(request).execute()) {

            // Deserialize JSON string into an array of objects
            return gson.fromJson(response.body().string(), OrderContainer[].class);
        }
    }

    public void makeOrder(OrderContainer container) {
        if(container == null)
            return;

        String url = "http://depot.naughtyklaus.net:43658/api/order/" + container.getDisplayName();

        String jsonString = gson.toJson(container);

        // Create RequestBody
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonString);

        // Create PATCH request
        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .build();

        // Execute the request
        try(Response response = getHttpClient().newCall(request).execute()) {
            // Check if request was successful
            if (response.isSuccessful()) {
                System.out.println("PATCH request successful");
                System.out.println("Response: " + response.body().string());
            } else {
                System.out.println("PATCH request failed with code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancelOrder() {
        String url = "http://depot.naughtyklaus.net:43658/api/cancel/" + getClient().getLocalPlayer().getName();

        // Create RequestBody
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), "{}");

        // Create PATCH request
        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .build();

        // Execute the request
        try(Response response = getHttpClient().newCall(request).execute()) {
            // Check if request was successful
            if (response.isSuccessful()) {
                System.out.println("PATCH request successful");
                System.out.println("Response: " + response.body().string());
            } else {
                System.out.println("PATCH request failed with code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Getter
    @Setter
    public DepotOrderItemPanel lastSelectedOrderItem;

    @Inject
    public DepotPanel(DepotPlugin plugin, @Nullable Client client, EventBus eventBus, SessionManager sessionManager, ScheduledExecutorService executor)
    {
        this.plugin = plugin;
        this.client = client;
        this.eventBus = eventBus;
        this.executor = executor;

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setBorder(new EmptyBorder(10, 0, 10, 0));

        eventBus.register(this);

        getEventBus().post(new ConstructDepotPanel());
    }

    /**
     *  Taken from the Grand Exchange plugin, modified for my own use.
     */
    public boolean updateSearch()
    {
        String lookup = getOrderPanel().getSearchField().getText();

        if (Strings.isNullOrEmpty(lookup))
        {
            getOrderPanel().getSearchItemsPanel().removeAll();
            SwingUtilities.invokeLater(getOrderPanel().getSearchItemsPanel()::updateUI);
            return false;
        }

        // Input is not empty, add searching label
        getOrderPanel().getSearchItemsPanel().removeAll();
        getOrderPanel().getSearchField().setBackground(ColorScheme.DARKER_GRAY_COLOR);
        getOrderPanel().getSearchField().setEditable(false);
        getOrderPanel().getSearchField().setIcon(IconTextField.Icon.LOADING);
        return true;
    }

    /**
     *  Taken from the Grand Exchange plugin, modified for my own use.
     */
    public void itemLookup(boolean exactMatch)
    {
        if (!updateSearch())
        {
            return;
        }

        java.util.List<ItemPrice> result = itemManager.search(getOrderPanel().getSearchField().getText());
        if (result.isEmpty())
        {
            getOrderPanel().getSearchField().setIcon(IconTextField.Icon.ERROR);
            //errorPanel.setContent("No results found.", "No items were found with that name, please try again.");
            getOrderPanel().cardLayout.show(getOrderPanel().centerPanel, "ERROR_PANEL");
            getOrderPanel().getSearchField().setEditable(true);
            return;
        }

        // move to client thread to lookup item composition
        clientThread.invokeLater(() -> processResult(result, getOrderPanel().getSearchField().getText(), exactMatch));
    }



    private void processResult(java.util.List<ItemPrice> result, String lookup, boolean exactMatch)
    {
        final List<ItemResource> itemsList = new ArrayList<>();

        getOrderPanel().cardLayout.show(getOrderPanel().centerPanel, "RESULTS_PANEL");

        int count = 0;

        for (ItemPrice item : result)
        {
            if (count++ > MAX_SEARCH_ITEMS)
            {
                // Cap search
                break;
            }

            if(item.getName().equalsIgnoreCase("Manta ray"))
                System.out.println("Manta ray: " + item.getId());

            int itemId = item.getId();

            ItemComposition itemComp = itemManager.getItemComposition(itemId);
            ItemStats itemStats = itemManager.getItemStats(itemId, false);
            AsyncBufferedImage itemImage = itemManager.getImage(itemId);

            itemsList.add(new ItemResource(itemComp, itemStats, itemImage));

            // If using hotkey to lookup item, stop after finding match.
            if (exactMatch && item.getName().equalsIgnoreCase(lookup))
            {
                break;
            }
        }

        SwingUtilities.invokeLater(() ->
        {
            int index = 0;
            for (ItemResource item : itemsList)
            {
                DepotOrderItemPanel panel = new DepotOrderItemPanel(this, item, null);

				/*
				Add the first item directly, wrap the rest with margin. This margin hack is because
				gridbaglayout does not support inter-element margins.
				 */
                if (index++ > 0)
                {
                    JPanel marginWrapper = new JPanel(new BorderLayout());
                    marginWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
                    marginWrapper.setBorder(new EmptyBorder(5, 0, 0, 0));
                    marginWrapper.add(panel, BorderLayout.NORTH);
                    getOrderPanel().getSearchItemsPanel().add(marginWrapper, getOrderPanel().searchConstraints);
                }
                else
                {
                    getOrderPanel().getSearchItemsPanel().add(panel, getOrderPanel().searchConstraints);
                }

                getOrderPanel().searchConstraints.gridy++;
            }

            // if exactMatch was set, then it came from the applet, so don't lose focus
            if (!exactMatch)
            {
                getOrderPanel().getSearchItemsPanel().requestFocusInWindow();
            }
            getOrderPanel().getSearchField().setEditable(true);

            // Remove searching label after search is complete
            if (!itemsList.isEmpty())
            {
                getOrderPanel().getSearchField().setIcon(IconTextField.Icon.SEARCH);
            }
        });
    }

    public void deconstruct() {
        eventBus.unregister(this);
    }

    @Subscribe
    private void onConstructOrderPanel(ConstructOrderPanel event) {
        setOrderPanel(new DepotOrderPanel(this));

        getOrderPanel().setLayout(new BorderLayout(10, 10));
        getOrderPanel().setBorder(new EmptyBorder(0, 10, 10, 10));

        getOrderPanel().build();

        getTabbedPane().addTab("Make Order", getNoteIcon(), getOrderPanel());
        getTabbedPane().setMnemonicAt(0, KeyEvent.VK_1);
    }



    @Subscribe
    private void onConstructRequestsPanel(ConstructRequestsPanel event) {
        setViewPanel(new DepotViewPanel(this));

        getViewPanel().setLayout(new BorderLayout(10, 10));
        getViewPanel().setBorder(new EmptyBorder(0, 10, 10, 10));

        getViewPanel().build();

        getTabbedPane().addTab("View Orders", getGrandExchangeIcon(), getViewPanel());
        getTabbedPane().setMnemonicAt(1, KeyEvent.VK_2);
    }

    public void refreshOrders() {
        clientThread.invokeLater(() -> {
            getViewPanel().getResultsPanel().removeAll();

            try {
                OrderContainer[] containers = getOrderContainer();

                for (OrderContainer container : containers) {
                    if (container != null) {
                        if (container.getDisplayName() != null) {
                            getViewPanel().getResultsPanel().add(new DepotViewResultPanel(this, container), getViewPanel().getOrdersConstraints());
                            getViewPanel().getOrdersConstraints().gridy++;
                        } else {
                            System.out.println("name is null");
                        }
                    } else {
                        System.out.println("container is null");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Subscribe
    private void onPreInitDepotTabbePane(PreInitDepotTabbePane event) {
        setTabbedPane(new JTabbedPane());

        eventBus.post(new ConstructOrderPanel());
        eventBus.post(new ConstructRequestsPanel());
    }
    @Subscribe
    private void onPostInitDepotTabbePane(PostInitDepotTabbePane event) {
        add(getTabbedPane(), BorderLayout.CENTER);
    }

    @Subscribe
    private void onConstructDepotPanel(ConstructDepotPanel event)
    {
        eventBus.post(new PreInitDepotTabbePane());
        eventBus.post(new PostInitDepotTabbePane());
    }

    private static JPanel buildAdvertisement(ImageIcon icon) {
        JPanel container = new JPanel();
        container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        container.setLayout(new BorderLayout());
        container.setBorder(new EmptyBorder(0, 0, 0, 0));

        JLabel iconLabel = new JLabel(icon);
        container.add(iconLabel, BorderLayout.CENTER);

        return container;
    }

    private final Client client;
    private final DepotPlugin plugin;
    private final EventBus eventBus;

    private SessionManager sessionManager;
    public ScheduledExecutorService executor;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ItemManager itemManager;

    /* Tabbed Panels */
    @Setter
    private JTabbedPane tabbedPane;
    @Setter
    private DepotOrderPanel orderPanel;
    @Setter
    private DepotViewPanel viewPanel;

    @Getter
    @Setter
    public static ImageIcon grandExchangeIcon;
    @Getter
    @Setter
    public static ImageIcon noteIcon;
    @Getter
    @Setter
    public static ImageIcon moneyIcon;
    @Getter
    @Setter
    public static ImageIcon calcIcon;

    private static final int MAX_SEARCH_ITEMS = 100;

    static {
        setMoneyIcon(new ImageIcon(ImageUtil.loadImageResource(DepotPanel.class, "/money.png")));
        Image image = getMoneyIcon().getImage(); // transform it
        Image newimg = image.getScaledInstance(16, 15, Image.SCALE_SMOOTH); // scale it the smooth way
        setMoneyIcon(new ImageIcon(newimg));  // transform it back
        setNoteIcon(new ImageIcon(ImageUtil.loadImageResource(DepotPanel.class, "/note_icon.png")));
        setGrandExchangeIcon(new ImageIcon(ImageUtil.loadImageResource(DepotPanel.class, "/ge_icon.png")));
        setCalcIcon(new ImageIcon(ImageUtil.loadImageResource(DepotPanel.class, "/calc.png")));
    }
}
