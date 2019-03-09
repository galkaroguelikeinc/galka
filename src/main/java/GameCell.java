import java.util.List;

public class GameCell {
    private GameMapCellType gameMapCellType;
    private AliveObject aliveObject;
    private Item cellItem;

    public boolean hasAliveObject() {
        return aliveObject != null;
    }

    public boolean hasItem() {
        return cellItem != null;
    }

    public Item takeCellItem() {
        Item cellItem = this.cellItem;
        this.cellItem = null;
        return cellItem;
    }
}
