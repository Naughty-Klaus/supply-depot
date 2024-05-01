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
import lombok.Setter;
import net.naughtyklaus.depot.DepotPlugin;
import net.naughtyklaus.depot.model.item.Order;
import net.naughtyklaus.depot.model.item.OrderContainer;
import net.naughtyklaus.depot.util.IntegerDocumentFilter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.IconTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DepotOrderPanel extends JPanel {

    @Getter
    @Setter
    private IconTextField searchField;
    @Getter
    @Setter
    private IconTextField offerField;
    @Getter
    @Setter
    private IconTextField quantityField;

    public CardLayout cardLayout = new CardLayout();
    public JPanel centerPanel = new JPanel(cardLayout);
    public final GridBagConstraints searchConstraints = new GridBagConstraints();
    public final GridBagConstraints constraints = new GridBagConstraints();

    private final JPanel wrapper = new JPanel(new BorderLayout());
    @Getter
    private final JPanel panelWrapper = new JPanel(new BorderLayout());
    @Getter
    private final JPanel cartWrapper = new JPanel(new BorderLayout());
    @Getter
    private final JPanel searchItemsPanel = new JPanel();

    @Setter
    @Getter
    private JScrollPane resultsScrollPane;
    @Getter
    private final DepotPanel mainPanel;

    public DepotOrderPanel(DepotPanel panel) {
        super(false);
        this.mainPanel = panel;
    }

    @Getter
    @Setter
    private DepotOrderItemPanel selectedDepotOrderItemPanel;

    public void build() {

        JButton deleteOldOrder = new JButton("Cancel Last Order");
        deleteOldOrder.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getMainPanel().cancelOrder();
            }
        });

        setQuantityField(new IconTextField());

        getQuantityField().setIcon(DepotPanel.getCalcIcon());
        getQuantityField().setToolTipText("Set your requested quantity.");
        getQuantityField().setPreferredSize(new Dimension(100, 30));
        getQuantityField().setBackground(ColorScheme.DARKER_GRAY_COLOR);
        getQuantityField().setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        getQuantityField().setText("1");

        PlainDocument doc1 = (PlainDocument) getQuantityField().getDocument();
        doc1.setDocumentFilter(new IntegerDocumentFilter());

        //

        setOfferField(new IconTextField());

        getOfferField().setIcon(DepotPanel.getMoneyIcon());
        getOfferField().setToolTipText("Set your buying price.");
        getOfferField().setPreferredSize(new Dimension(100, 30));
        getOfferField().setBackground(ColorScheme.DARKER_GRAY_COLOR);
        getOfferField().setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        getOfferField().setText("0");

        PlainDocument doc = (PlainDocument) getOfferField().getDocument();
        doc.setDocumentFilter(new IntegerDocumentFilter());

        setSearchField(new IconTextField());
        getSearchField().setIcon(IconTextField.Icon.SEARCH);
        getSearchField().setToolTipText("Search for items by partial name.");
        getSearchField().setPreferredSize(new Dimension(100, 30));
        getSearchField().setBackground(ColorScheme.DARKER_GRAY_COLOR);
        getSearchField().setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        getSearchField().addActionListener(e -> getMainPanel().executor.execute(() -> getMainPanel().itemLookup(false)));
        getSearchField().addClearListener(getMainPanel()::updateSearch);

        getSearchField().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if(getSearchField().getText().equalsIgnoreCase("Search..."))
                    getSearchField().setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(getSearchField().getText().equalsIgnoreCase(""))
                    getSearchField().setText("Search...");
            }
        });

        searchItemsPanel.setLayout(new GridBagLayout());
        searchItemsPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

        panelWrapper.setLayout(new GridBagLayout());
        panelWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);

        cartWrapper.setLayout(new GridBagLayout());
        cartWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);

        searchConstraints.fill = GridBagConstraints.HORIZONTAL;
        searchConstraints.weightx = 1;
        searchConstraints.gridx = 0;
        searchConstraints.gridy = 0;

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 0, 0, 0);

        wrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
        wrapper.add(searchItemsPanel, BorderLayout.NORTH);

        setResultsScrollPane(new JScrollPane(wrapper)); //new JScrollPane(new JList<>(listModel)));
        getResultsScrollPane().setBackground(ColorScheme.DARK_GRAY_COLOR);
        getResultsScrollPane().getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        getResultsScrollPane().getVerticalScrollBar().setBorder(new EmptyBorder(0, 5, 0, 0));

        centerPanel.add(getResultsScrollPane(), "RESULTS_PANEL");

        //populateResults(null);

        panelWrapper.add(deleteOldOrder, constraints);
        constraints.gridy++;
        panelWrapper.add(getOfferField(), constraints);
        constraints.gridy++;
        panelWrapper.add(getQuantityField(), constraints);
        constraints.gridy++;
        panelWrapper.add(getCartWrapper(), constraints);
        constraints.gridy++;

        JButton makeOrder = new JButton("Send Order");
        makeOrder.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(getMainPanel().getOrderPanel().getSelectedDepotOrderItemPanel() != null) {
                    System.out.println("Something is happening!");
                    DepotOrderItemPanel it = getMainPanel().getOrderPanel().getSelectedDepotOrderItemPanel();
                    getMainPanel().makeOrder(
                            new OrderContainer(
                                    getMainPanel().getClient().getLocalPlayer().getName(),
                                    new Order(
                                            it.getItem().getItemComposition().getId(),
                                            Integer.parseInt(getQuantityField().getText()),
                                            Integer.parseInt(getOfferField().getText())
                                    )
                            ));
                } else {
                    System.out.println("Something isn't right...");
                }
            }
        });
        panelWrapper.add(makeOrder, constraints);
        constraints.gridy++;
        panelWrapper.add(getSearchField(), constraints);

        add(panelWrapper, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        //add(getResultsScrollPane());
    }
}
