package kiyo.battleship.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import kiyo.battleship.model.ShipType;

/**
 * Serializable set-up adapter record to get the game specs and fleet
 *
 * @param width  The width
 * @param height The height
 * @param fleetSpec  Map of ships and their counts
 */
public record SetupAdapter(@JsonProperty("width") int width,
                        @JsonProperty("height") int height,
                        @JsonProperty("fleet-spec") Map<ShipType, Integer> fleetSpec) {
}
