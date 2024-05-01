package net.naughtyklaus.depot.ui;

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
