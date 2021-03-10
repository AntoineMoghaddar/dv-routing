package my_protocol;

/**
 * Simple object which describes a route entry in the forwarding table.
 * Can be extended to include additional data.
 *
 * Copyright University of Twente, 2013-2019
 *
 **************************************************************************
 *                            Copyright notice                            *
 *                                                                        *
 *             This file may ONLY be distributed UNMODIFIED.              *
 * In particular, a correct solution to the challenge must NOT be posted  *
 * in public places, to preserve the learning effect for future students. *
 **************************************************************************
 */
/**
 * Simple object which describes a route entry in the forwarding table.
 * Can be extended to include additional data.
 */
public class DummyRoute {

    private final int destination;
    private final int nextHop;
    private final int cost;
    private final int hops;


    public DummyRoute(int destination, int nextHop, int cost, int hops) {
        this.cost = cost;
        this.destination = destination;
        this.nextHop = nextHop;
        this.hops = hops;
    }

    public int getCost() {
        return cost;
    }

    public int getDestination() {
        return destination;
    }

    public int getNextHop() {
        return nextHop;
    }

    public int getHops() {
        return hops;
    }

}
