package kiyo.battleship;

import java.util.ArrayList;
import java.util.List;
import kiyo.battleship.model.AbstractPlayer;
import kiyo.battleship.model.Coord;

/**
 * Mock manual player for testing the game and controller
 */
public class MockManualPlayer extends AbstractPlayer {
  public int internalCount = 0;

  /**
   * Get the player's name.
   *
   * @return the player's name
   */
  @Override
  public String name() {
    return "Mock Manual";
  }

  /**
   * Returns this player's shots on the opponent's board. The number of shots returned should
   * equal the number of ships on this player's board that have not sunk.
   *
   * @return the locations of shots on the opponent's board
   */
  @Override
  public List<Coord> takeShots() {
    List<Coord> takenShots = new ArrayList<>();

    int maxAllowed = 0;
    for (boolean[] bool : super.alreadyTaken) {
      for (boolean b : bool) {
        if (!b) {
          maxAllowed++;
        }
      }
    }

    int limit = Math.min(super.board.standingShips.size(), maxAllowed);

    for (int row = 0; row < limit; row++) {
      takenShots.add(new Coord(row, this.internalCount));
    }

    for (Coord coord : takenShots) {
      this.alreadyTaken[coord.x()][coord.y()] = true;
    }

    if (this.internalCount < limit) {
      this.internalCount++;
    }

    return takenShots;
  }
}
