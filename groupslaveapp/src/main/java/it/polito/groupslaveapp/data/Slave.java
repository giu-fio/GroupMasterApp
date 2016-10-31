package it.polito.groupslaveapp.data;

/**
 * Created by giuseppe on 12/10/16.
 */

public class Slave {
    private String name;
    private String id;

    public Slave(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public Slave(String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Slave slave = (Slave) o;

        return id != null ? id.equals(slave.id) : slave.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Slave{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
