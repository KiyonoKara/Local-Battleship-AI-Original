package kiyo.battleship.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A better AI player implementation
 */
public class BetterAiPlayer extends AbstractPlayer {

  /**
   * Get the player's name.
   *
   * @return the player's name
   */
  @Override
  public String name() {
    return "BAI";
  }

  /**
   * Returns this player's shots on the opponent's board. The number of shots returned should
   * equal the number of ships on this player's board that have not sunk.
   *
   * @return the locations of shots on the opponent's board
   */
  @Override
  public List<Coord> takeShots() {
    Random random = new Random();
    int x;
    int y;
    int width = board.grid.length;
    int height = board.grid[0].length;
    List<Coord> takenShots = new ArrayList<>();
    int maxAllowed = getMaxAllowedShots();
    int remainingShots = Math.min(board.standingShips.size(), maxAllowed);
    while (remainingShots > 0) {
      if (!coordsLikely.isEmpty()) {
        Coord currentCoord = coordsLikely.get(0);
        if (!this.alreadyTaken[currentCoord.x()][currentCoord.y()]) {
          this.alreadyTaken[currentCoord.x()][currentCoord.y()] = true;
          takenShots.add(coordsLikely.remove(0));
          remainingShots--;
        } else {
          coordsLikely.remove(0);
        }
      } else {
        if (!shootableCoords.isEmpty()) {
          int indexOfCoordToShootAt = random.nextInt(shootableCoords.size());
          Coord shootHere = shootableCoords.remove(indexOfCoordToShootAt);
          if (!this.alreadyTaken[shootHere.x()][shootHere.y()]) {
            this.alreadyTaken[shootHere.x()][shootHere.y()] = true;
            takenShots.add(shootHere);
            remainingShots--;
          }
        } else {
          x = random.nextInt(width);
          y = random.nextInt(height);
          while (this.alreadyTaken[x][y]) {
            x = random.nextInt(width);
            y = random.nextInt(height);
          }
          takenShots.add(new Coord(x, y));
          this.alreadyTaken[x][y] = true;
          remainingShots--;
        }
      }
    }
    return takenShots;
  }

  /**
   * Get the max iterations allowed when taking shots.
   *
   * @return The max allowed iterations
   */
  private int getMaxAllowedShots() {
    int maxAllowed = 0;
    for (boolean[] bool : super.alreadyTaken) {
      for (boolean b : bool) {
        if (!b) {
          maxAllowed++;
        }
      }
    }
    return maxAllowed;
  }
}
