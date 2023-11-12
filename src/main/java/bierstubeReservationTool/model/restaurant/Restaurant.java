package bierstubeReservationTool.model.restaurant;


import bierstubeReservationTool.util.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * A restaurant.
 */
@Component
public class Restaurant {
    private static final int ID = 1;
    private static final String NAME = "Bierstube";

    private static final List<OpenHourSlot> OPENING_HOURS;

    public static List<RestaurantTable> getTables() {
        return Tables;
    }


    static {
        List<OpenHourSlot> opens = new ArrayList<>();
        for (DayOfWeek d : DayOfWeek.values()) {
            if (d == DayOfWeek.MONDAY
                    || d == DayOfWeek.TUESDAY
                    || d == DayOfWeek.WEDNESDAY
                    || d == DayOfWeek.THURSDAY
                    || d == DayOfWeek.FRIDAY
                    || d == DayOfWeek.SATURDAY) {
                opens.add(new OpenHourSlot(d, LocalTime.of(19, 0), LocalTime.of(00, 30)));
            }
        }
        OPENING_HOURS = opens;
    }
    static List<RestaurantTable> Tables =
            IntStream.range(1, 10).mapToObj(i -> {
                RestaurantTable table = new RestaurantTable();
                table.setId(i);
                table.setCapacity(8);
                return table;
            }).toList();
    static String fancySVG;

    static {
        try {
            fancySVG = ResourceLoader.mustLoadString("/static/fancy-tables.svg");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static final List<RestaurantTable> TABLES=Tables;
    private static final String LAYOUTSVG=fancySVG;

    public RestaurantDTO toDTO() {
        return new RestaurantDTO(NAME, OPENING_HOURS, TABLES,LAYOUTSVG);
    }

}
