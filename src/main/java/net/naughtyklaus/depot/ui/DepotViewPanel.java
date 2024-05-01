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
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

public class DepotViewPanel extends JPanel {

    @Getter
    private final DepotPanel mainPanel;

    @Getter
    @Setter
    private JPanel panelWrapper = new JPanel(new GridBagLayout());

    @Getter
    @Setter
    private JPanel ordersWrapper = new JPanel(new BorderLayout());

    @Getter
    private final JPanel resultsPanel = new JPanel(new GridBagLayout());

    @Getter
    @Setter
    private GridBagConstraints panelConstraints = new GridBagConstraints();

    @Getter
    @Setter
    private GridBagConstraints ordersConstraints = new GridBagConstraints();


    public DepotViewPanel(DepotPanel panel) {
        super(false);
        this.mainPanel = panel;
    }

    public void build() {

        getPanelWrapper().setAutoscrolls(true);

        getPanelConstraints().fill = GridBagConstraints.HORIZONTAL;
        getPanelConstraints().weightx = 1;
        getPanelConstraints().gridx = 0;
        getPanelConstraints().gridy = 0;
        getPanelConstraints().insets = new Insets(10, 0, 0, 0);

        resultsPanel.setLayout(new GridBagLayout());
        resultsPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

        getOrdersConstraints().fill = GridBagConstraints.HORIZONTAL;
        getOrdersConstraints().weightx = 1;
        getOrdersConstraints().gridx = 0;
        getOrdersConstraints().gridy = 0;

        JButton refreshPanel = new JButton("Refresh Orders");
        refreshPanel.addActionListener(e -> getMainPanel().executor.execute(() -> getMainPanel().refreshOrders()));
        getPanelWrapper().add(refreshPanel, getPanelConstraints());
        getPanelConstraints().gridy++;

        JScrollPane jScrollPane = new JScrollPane(getResultsPanel());

        //getOrdersWrapper().add(jScrollPane);

        getPanelWrapper().add(jScrollPane, getPanelConstraints());

        add(getPanelWrapper(), BorderLayout.NORTH);
    }

}
