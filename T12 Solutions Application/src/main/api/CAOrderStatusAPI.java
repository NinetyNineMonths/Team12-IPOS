package main.api;

import java.time.LocalDateTime;

/**
 * Interface for retrieving order status information from the CA subsystem.
 *
 * This interface supports checking the current status of a specific order
 * and listing orders that have been updated since a given timestamp.
 */

public interface CAOrderStatusAPI {

    /**
     * Returns the current status of a specific order.
     */
    String getOrderStatus(String orderId);

    /**
     * Returns updated orders since a given point in time.
     */
    LocalDateTime listUpdatedOrders(LocalDateTime since);
}