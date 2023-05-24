package eist.aammn.model.restaurant;


import java.util.List;

public class RestaurantDTO {
    private String name;
    private List<OpenHourSlot> openingHours;
    private List<RestaurantTable> tables;

    public String getLAYOUT_SVG() {
        return LAYOUT_SVG;
    }

    public void setLAYOUT_SVG(String LAYOUT_SVG) {
        this.LAYOUT_SVG = LAYOUT_SVG;
    }

    private String LAYOUT_SVG;

    public RestaurantDTO(String name, List<OpenHourSlot> openingHours, List<RestaurantTable> tables, String layout) {
        this.name = name;
        this.openingHours = openingHours;
        this.tables = tables;
        this.LAYOUT_SVG=layout;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OpenHourSlot> getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(List<OpenHourSlot> openingHours) {
        this.openingHours = openingHours;
    }

    public List<RestaurantTable> getTables() {
        return tables;
    }

    public void setTables(List<RestaurantTable> tables) {
        this.tables = tables;
    }
}

