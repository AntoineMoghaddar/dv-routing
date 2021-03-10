package algorithm;

public class Vertex {
    final private int id;
    final private String name;

    public Vertex(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.id == ((Vertex) o).id;
    }

    @Override
    public String toString() {
        return name;
    }
}
