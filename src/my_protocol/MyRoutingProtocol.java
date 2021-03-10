package my_protocol;

import algorithm.DijkstraAlgorithm;
import algorithm.Edge;
import algorithm.Graph;
import algorithm.Vertex;
import framework.IRoutingProtocol;
import framework.LinkLayer;
import framework.Packet;
import framework.PacketWithLinkCost;

import java.util.*;

/**
 * @version 12-03-2019
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
public class MyRoutingProtocol implements IRoutingProtocol {
    private LinkLayer linkLayer;
    private ConnectionTable cTable;

    private List<Vertex> vertex;
    private List<Edge> edges;

    private int currentTick;

    public MyRoutingProtocol() {
        cTable = new ConnectionTable();
        currentTick = 0;
    }

    @Override
    public void init(LinkLayer linkLayer) {
        this.linkLayer = linkLayer;
    }


    @Override
    public void tick(PacketWithLinkCost[] packetsWithLinkCosts) {
        currentTick++;
        System.out.print("pack: " + packetsWithLinkCosts.length);
        cTable.removeAllLinksTo(linkLayer.getOwnAddress());
        for (PacketWithLinkCost packet : packetsWithLinkCosts) {
            //
            ConnectionTable.Connection link = cTable.get(packet.getPacket().getSourceAddress(), linkLayer.getOwnAddress());

            if (link != null) {
                link.cost = packet.getLinkCost();
                link.tick = currentTick;
            } else {
                cTable.add(packet.getPacket().getSourceAddress(), linkLayer.getOwnAddress(), packet.getLinkCost(), currentTick);
            }
            //
            if (packet.getPacket().getRawData().length > 0) {
                cTable.update(packet);
            }
        }

        if (cTable.isEmpty()) {
            Packet pct = new Packet(linkLayer.getOwnAddress(), 0, new byte[0]);
            this.linkLayer.transmit(pct);
        } else {
            Packet pct = new Packet(linkLayer.getOwnAddress(), 0, cTable.serialize());
            this.linkLayer.transmit(pct);
        }

        System.out.println("\n" + cTable.toString());
    }

    public Map<Integer, Integer> getForwardingTable() {
        vertex = new ArrayList<>();
        edges = new ArrayList<>();
        List<Integer> nodes = new ArrayList<>();


        for (ConnectionTable.Connection connection : cTable.getConnections()) {
            if (!nodes.contains(connection.hostA))
                nodes.add(connection.hostA);
            if (!nodes.contains(connection.hostB))
                nodes.add(connection.hostB);

            for (Integer node : nodes) {
                vertex.add(new Vertex(node, "Node " + node));
            }
        }

        HashMap<Integer, Vertex> vertexMap = new HashMap<>();
        for (Vertex vert : vertex) {
            vertexMap.put(vert.getId(), vert);
        }

        for (ConnectionTable.Connection connection : cTable.getConnections()) {
            edges.add(new Edge(vertexMap.get(connection.hostA), vertexMap.get(connection.hostB), connection.cost));
        }
        System.out.println("this is the size of the edges list: " + edges.size());
        Graph graph = new Graph(vertex, edges);
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(vertexMap.get(linkLayer.getOwnAddress()));

        HashMap<Integer, Integer> ft = new HashMap<>();
        for (Map.Entry<Integer, Vertex> entry : vertexMap.entrySet()) {
            LinkedList<Vertex> path = dijkstra.getPath(entry.getValue());

            if (path != null && path.size() > 0)
                ft.put(entry.getKey(), path.get(0).getId());
        }

        return ft;
    }
}
