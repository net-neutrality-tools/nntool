package at.alladin.nntool.shared.map;

public class HeatmapTileParameters extends MapTileParameters {

    @Override
    public boolean isNoCache() {
        return false;
    }

    @Override
    public MapTileType getMapTileType() {
        return MapTileType.HEATMAP;
    }
}
