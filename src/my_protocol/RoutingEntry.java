package my_protocol;

import java.io.Serializable;

/**
 * Simple object which describes a route entry in the forwarding table.
 * Can be extended to include additional data.
 */
public class RoutingEntry implements Serializable {
    public int nextHop;
    public int cost;
    public int finalDestination;

    public RoutingEntry(int nextHop, int cost, int finalDestination) {
        this.nextHop = nextHop;
        this.cost = cost;
        this.finalDestination = finalDestination;
    }

    public RoutingEntry setCost(int cost) {
        this.cost = cost;
        return this;
    }
}