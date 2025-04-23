public class RoomManager {
    private static Room currentRoom;

    public static void setCurrentRoom(Room room) {
        currentRoom = room;
    }

    public static Room getCurrentRoom() {
        return currentRoom;
    }
}