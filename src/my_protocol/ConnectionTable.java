package my_protocol;

import algorithm.Util;
import framework.PacketWithLinkCost;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConnectionTable implements Serializable {

    private final List<Connection> connections;

    public ConnectionTable() {
        connections = new ArrayList<>();
    }

    public boolean isEmpty() {
        return connections.isEmpty();
    }

    public byte[] serialize() {
        return Util.serializeRoutingTable(this);
    }

    public Connection get(int hostA, int hostB) {
        for (Connection connection : connections)
            if (connection.matches(hostA, hostB))
                return connection;
        return null;
    }

    public void add(int hostA, int hostB, int cost, int tick) {
        connections.add(new Connection(hostA, hostB, cost, tick));
    }

    public void update(PacketWithLinkCost packet) {
        ConnectionTable theirTable = (ConnectionTable) Util.getForwardingTableFromPacket(packet.getPacket());

        for (Connection connection : theirTable.connections) {
            Connection other = theirTable.get(connection.hostA, connection.hostB);
            if (other != null && other.tick >= connection.tick) {
                connection.cost = other.cost;
                connection.tick = other.tick;
            }
        }
    }

    public void removeAllLinksTo(int address) {
        for (Connection connection : connections) {
            if (connection.matches(address))
                connection.cost = Integer.MAX_VALUE;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Connection connection : connections) {
            sb.append(connection.toString());
            sb.append("\t");
        }
        return sb.toString();
    }

    public List<Connection> getConnections() {
        return connections;
    }

    static class Connection implements Serializable {
        int hostA;
        int hostB;
        int cost;
        int tick;

        public Connection(int hostA, int hostB, int cost, int tick) {
            this.hostA = hostA;
            this.hostB = hostB;
            this.cost = cost;
            this.tick = tick;
        }

        public boolean matches(int hostA, int hostB) {
            return this.hostA == hostA && this.hostB == hostB || this.hostB == hostA && this.hostA == hostB;
        }

        public boolean matches(int host) {
            return hostA == host || hostB == host;
        }

        @Override
        public String toString() {
            return hostA + " - " + hostB + " > " + cost + " : " + tick;
        }
    }
}
