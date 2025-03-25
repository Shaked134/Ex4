package src;


import java.util.ArrayList;
import java.util.List;

public class Range2D {
private CellEntry Index2D_a ;
    private CellEntry  Index2D_b;

public  Range2D( String  range){
    String[] parts = range.split(":");
    this.Index2D_a= new CellEntry( parts[0]);
    this.Index2D_b=  new CellEntry( parts[1]);

}
    public Index2D getStartCell() {
    return Index2D_a;
}
    public Index2D getEndCell() {
    return Index2D_b;
}


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
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                cells.add(new CellEntry(x, y));
            }
        }

        return cells;
    }


@Override
public String toString() {
    return Index2D_a.toString() + ":" + Index2D_b.toString();
}

}
