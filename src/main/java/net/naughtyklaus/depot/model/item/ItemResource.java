package net.naughtyklaus.depot.model.item;

import lombok.Getter;
import net.runelite.api.ItemComposition;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.http.api.item.ItemStats;

public class ItemResource {

    @Getter
    private final ItemComposition itemComposition;
    @Getter
    private final ItemStats itemStats;

    @Getter
    private AsyncBufferedImage itemImage;

    public ItemResource(ItemComposition itemComposition, ItemStats itemStats, AsyncBufferedImage itemImage) {
        this.itemComposition = itemComposition;
        this.itemStats = itemStats;
        this.itemImage = itemImage;
    }

}
