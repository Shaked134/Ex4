package src;


import java.util.ArrayList;
import java.util.List;

public class Range2D {
private CellEntry Index2D_a ;
    private CellEntry  Index2D_b;

    // Constructor that parses a string like "A1:C3" into two CellEntry objects

    public  Range2D( String  range){
    String[] parts = range.split(":");
    this.Index2D_a= new CellEntry( parts[0].toUpperCase());
    this.Index2D_b=  new CellEntry( parts[1].toUpperCase());

}
    public Index2D getStartCell() {
    return Index2D_a;
}
    public Index2D getEndCell() {
    return Index2D_b;
}

    // Returns a list of all cells within the rectangular range between start and end

    public List<CellEntry> getCellsInRange() {
        List<CellEntry> cells = new ArrayList<>();
        int startX = Index2D_a.getX();
        int startY = Index2D_a.getY();
        int endX = Index2D_b.getX();
        int endY = Index2D_b.getY();

        if (startX > endX) {
            int temp = startX;
            startX = endX;
            endX = temp;
        }
        if (startY > endY) {
            int temp = startY;
            startY = endY;
            endY = temp;
        }
        // Iterate over all cells in the rectangular range and add them to the list

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                cells.add(new CellEntry(x, y));
            }
        }

        return cells;
    }

    // Returns a string representation of the range (e.g., "A1:C3")

@Override
public String toString() {
    return Index2D_a.toString() + ":" + Index2D_b.toString();
}

}
