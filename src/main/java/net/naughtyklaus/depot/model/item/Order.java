package net.naughtyklaus.depot.model.item;

import lombok.Getter;

@Getter
public class Order {

    private final int itemId, quantity, offerPrice;

    public Order(int itemId, int quantity, int offerPrice) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.offerPrice = offerPrice;
    }

}
