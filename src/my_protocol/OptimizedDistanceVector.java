package my_protocol;

import framework.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Antoine Moghaddar, Simon van Eldik
 */
public class OptimizedDistanceVector implements IRoutingProtocol {
    private LinkLayer linkLayer;

    //Number of instances to run
    private static final int NODE_COUNT = 6;

    //The potential Forwarding Table
    private final ConcurrentHashMap<Integer, Map<Integer, DummyRoute>> pft = new ConcurrentHashMap<>();

    //The forwarding table
    private final HashMap<Integer, DummyRoute> ft = new HashMap<>();

    @Override
    public void init(LinkLayer linkLayer) {
        this.linkLayer = linkLayer;
    }

    public void processDataTable(PacketWithLinkCost packet) {
        DataTable dt = packet.getPacket().getDataTable();
        for (int index = 0; index < dt.getNRows(); index++) {
            if (dt.get(index, 2) < NODE_COUNT) {  // Make sure that we don't have packets running around
                Map<Integer, DummyRoute> routes;
                if (pft.containsKey(dt.get(index, 0))) {
                    routes = pft.get(dt.get(index, 0));
                } else {
                    routes = new HashMap<>();
                    pft.put(dt.get(index, 0), routes);  // Put the new information (vectors) to the collection
                }
                DummyRoute r = new DummyRoute(dt.get(index, 0), packet.getPacket().getSourceAddress(), dt.get(index, 1) + packet.getLinkCost(), (dt.get(index, 2) + 1));
                routes.put(packet.getPacket().getSourceAddress(), r);
            }
        }
    }

    @Override
    public void tick(PacketWithLinkCost[] packets) {
        // Since the topology changes we also restart the collections every iteration
        ft.clear();
        pft.clear();

        // Loop through the packets
        for (PacketWithLinkCost packet : packets) {
            if (packet.getLinkCost() != -1) {
                processDataTable(packet);
            }
        }

        // Add the node itself to the table so it also contains that
        DummyRoute r = new DummyRoute(
                this.linkLayer.getOwnAddress(),
                this.linkLayer.getOwnAddress(),
                0,
                1
        );
        ft.put(this.linkLayer.getOwnAddress(), r);


        for (Map.Entry<Integer, Map<Integer, DummyRoute>> entry : pft.entrySet()) {
            for (Map.Entry<Integer, DummyRoute> route : entry.getValue().entrySet()) { // Looping in the inner map of the fort
                if (ft.containsKey(entry.getKey())) {
                    if (ft.get(entry.getKey()).getCost() > route.getValue().getCost())
                        ft.put(entry.getKey(), route.getValue());
                } else { // The entry is not in the table so just put it there
                    ft.put(entry.getKey(), route.getValue());
                }
            }
        }


        for (int j = 1; j <= NODE_COUNT; j++) { // Here we also know the number of nodes but oh well...
            if (j != this.linkLayer.getOwnAddress()) {
                DataTable dt = new DataTable(3);
                for (Map.Entry<Integer, DummyRoute> route : ft.entrySet()) {
                    if (route.getValue().getNextHop() != j) {
                        Integer[] row = {
                                route.getValue().getDestination(),
                                route.getValue().getCost(),
                                route.getValue().getHops()
                        };
                        dt.addRow(row); // Build the data structure that we are going to send
                    }
                }
                Packet pkt = new Packet(this.linkLayer.getOwnAddress(), j, dt); // Send the data to all the nodes one by one (We don't use dest = 0)
                this.linkLayer.transmit(pkt);
            }
        }
    }

    // Makes the result presentable to the server
    @Override
    public HashMap<Integer, Integer> getForwardingTable() {
        HashMap<Integer, Integer> res = new HashMap<>();
        for (DummyRoute current : ft.values()) {
            res.put(current.getDestination(), current.getNextHop());
        }
        return res;
    }
}
