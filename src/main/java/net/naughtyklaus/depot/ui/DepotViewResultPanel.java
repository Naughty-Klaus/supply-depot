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

import lombok.Getter;
import net.naughtyklaus.depot.JRichTextPane;
import net.naughtyklaus.depot.model.item.OrderContainer;
import net.runelite.api.ItemComposition;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.http.api.item.ItemStats;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class DepotViewResultPanel extends JPanel {

    private static final Dimension ICON_SIZE = new Dimension(32, 32);

    @Getter
    private final DepotPanel mainPanel;

    public DepotViewResultPanel(DepotPanel mainPanel, OrderContainer orderContainer) {
        this.mainPanel = mainPanel;

        BorderLayout layout = new BorderLayout();
        layout.setHgap(5);
        setLayout(layout);
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        setPreferredSize(new Dimension((int) getSize().getWidth(), 96));

        Color background = getBackground();
        List<JPanel> panels = new ArrayList<>();
        panels.add(this);

        MouseAdapter itemPanelMouseListener = new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                for (JPanel panel : panels)
                {
                    matchComponentBackground(panel, ColorScheme.DARK_GRAY_HOVER_COLOR);
                }
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                for (JPanel panel : panels)
                {
                    matchComponentBackground(panel, background);
                }
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        };

        addMouseListener(itemPanelMouseListener);
        setBorder(new EmptyBorder(5, 5, 5, 0));

        ItemComposition itemComp = mainPanel.getItemManager().getItemComposition(orderContainer.getOrder().getItemId());
        ItemStats itemStats = mainPanel.getItemManager().getItemStats(orderContainer.getOrder().getItemId(), false);
        AsyncBufferedImage itemImage = mainPanel.getItemManager().getImage(orderContainer.getOrder().getItemId());

        // Icon
        JLabel itemIcon = new JLabel();
        itemIcon.setPreferredSize(ICON_SIZE);
        if (itemImage != null)
        {
            itemImage.addTo(itemIcon);
        }
        add(itemIcon, BorderLayout.LINE_START);

        // Item details panel
        JPanel rightPanel = new JPanel(new GridLayout(4, 1));
        panels.add(rightPanel);
        rightPanel.setBackground(background);

        JRichTextPane displayName = new JRichTextPane();
        displayName.setMaximumSize(new Dimension(0, 0));        // to limit the label's size for
        displayName.setPreferredSize(new Dimension(0, 0));    // items with longer names
        displayName.setText("Requested by: " + orderContainer.getDisplayName());

        // Item name
        JRichTextPane itemName = new JRichTextPane();
        itemName.setMaximumSize(new Dimension(0, 0));        // to limit the label's size for
        itemName.setPreferredSize(new Dimension(0, 0));    // items with longer names
        itemName.setText("Item: " + itemComp.getName());

        JRichTextPane quantity = new JRichTextPane();
        quantity.setMaximumSize(new Dimension(0, 0));        // to limit the label's size for
        quantity.setPreferredSize(new Dimension(0, 0));    // items with longer names
        quantity.setText("Quantity: x" + orderContainer.getOrder().getQuantity());

        JRichTextPane price = new JRichTextPane();
        price.setMaximumSize(new Dimension(0, 0));        // to limit the label's size for
        price.setPreferredSize(new Dimension(0, 0));    // items with longer names
        price.setText("Pay: " + orderContainer.getOrder().getOfferPrice());

        rightPanel.add(displayName);
        rightPanel.add(itemName);
        rightPanel.add(quantity);
        rightPanel.add(price);

        add(rightPanel, BorderLayout.CENTER);
    }

    private void matchComponentBackground(JPanel panel, Color color)
    {

        panel.setBackground(color);
        for (Component c : panel.getComponents())
        {
            c.setBackground(color);
        }
    }

}
