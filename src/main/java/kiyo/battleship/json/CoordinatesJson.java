package kiyo.battleship.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import kiyo.battleship.model.Coord;

/**
 * General serializable record for successful hits, report damage, take shots, and
 *
 * @param coordinates The list of successful hits
 */
public record CoordinatesJson(@JsonProperty("coordinates") List<Coord> coordinates) {
}
