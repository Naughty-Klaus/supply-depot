package net.naughtyklaus.depot.model.item;

import lombok.Getter;

@Getter
public class OrderContainer {

    private final String displayName;
    private final Order order;



    public OrderContainer(String displayName, Order order) {
        this.displayName = displayName;
        this.order = order;
    }

}
