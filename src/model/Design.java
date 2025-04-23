import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Design {
    private String name;
    private Room room;
    private List<FurnitureItem> items;
    private Date creationDate;
    private Date lastModifiedDate;

    public Design(String name, Room room) {
        this.name = name;
        this.room = room;
        this.items = new ArrayList<>();
        this.creationDate = new Date();
        this.lastModifiedDate = new Date();
    }

    // Methods to manage furniture items
    public void addItem(FurnitureItem item) {
        items.add(item);
        lastModifiedDate = new Date();
    }

    public void removeItem(FurnitureItem item) {
        items.remove(item);
        lastModifiedDate = new Date();
    }

    // Scaling method
    public void scaleToFit() {
        // Logic to scale furniture to fit room
        lastModifiedDate = new Date();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        lastModifiedDate = new Date();
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
        lastModifiedDate = new Date();
    }

    public List<FurnitureItem> getItems() {
        return items;
    }

    public void setItems(List<FurnitureItem> items) {
        this.items = items;
        lastModifiedDate = new Date();
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}