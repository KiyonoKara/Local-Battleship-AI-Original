package kiyo.battleship.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import kiyo.battleship.view.BattleSalvoView;

/**
 * Abstraction of the Player implementation
 */
public abstract class AbstractPlayer implements Player {
  private final Random random;
  protected Board board;
  protected boolean[][] alreadyTaken;
  protected List<Coord> coordsLikely;
  protected List<Coord> shootableCoords;

  /**
   * Constructor for our abstract player
   */
  public AbstractPlayer() {
    this.random = new Random();
    this.coordsLikely = new ArrayList<>();
    this.shootableCoords = new ArrayList<>();
  }

  /**
   * Get the player's name.
   *
   * @return the player's name
   */
  @Override
  public abstract String name();

  /**
   * Given the specifications for a BattleSalvo board, return a list of ships with their locations
   * on the board.
   *
   * @param height         the height of the board, range: [6, 15] inclusive
   * @param width          the width of the board, range: [6, 15] inclusive
   * @param specifications a map of ship type to the number of occurrences each ship should
   *                       appear on the board
   * @return the placements of each ship on the board
   */
  @Override
  public List<Ship> setup(int height, int width, Map<ShipType, Integer> specifications) {
    List<Ship> fleet = new ArrayList<>();

    for (ShipType st : specifications.keySet()) {
      int size = st.size();
      for (int i = 0; i < specifications.get(st); i++) {
        Coord[] coords = generateShipCoords(height, width, size);
        while (overlaps(fleet, coords)) {
          coords = generateShipCoords(height, width, size);
        }
        fleet.add(new Ship(st, coords));
      }
    }
    this.createShootableCoords(height, width);
    this.board = new Board(height, width, fleet);
    this.alreadyTaken = new boolean[width][height];
    for (boolean[] bool : this.alreadyTaken) {
      Arrays.fill(bool, false);
    }
    return fleet;
  }

  /**
   * Creates a lists of every other coord on the board
   *
   * @param height the height of the board
   * @param width the width of the board
   */
  private void createShootableCoords(int height, int width) {
    boolean placeCoord;
    int alternate = 1;
    for (int i = 0; i < height; i++) {
      placeCoord = alternate % 2 == 1;
      alternate++;
      for (int j = 0; j < width; j++) {
        if (placeCoord) {
          shootableCoords.add(new Coord(j, i));
          placeCoord = false;
        } else {
          placeCoord = true;
        }
      }
    }
  }

  /**
   * Generates coordinates within the game's bounds for a single ship
   *
   * @param height Board height
   * @param width  Board width
   * @param size   Ship size
   * @return Fixed array of coords
   */
  private Coord[] generateShipCoords(int height, int width, int size) {
    Coord[] coords = new Coord[size];
    int x;
    int y;

    // True = Vertical
    // False = Horizontal
    boolean orientation = this.random.nextBoolean();

    // Vertical
    if (orientation) {
      x = this.random.nextInt(width);
      y = this.random.nextInt(height - size + 1);
      for (int i = 0; i < size; i++) {
        coords[i] = new Coord(x, y + i);
      }
      // Horizontal
    } else {
      x = this.random.nextInt(width - size + 1);
      y = this.random.nextInt(height);
      for (int i = 0; i < size; i++) {
        coords[i] = new Coord(x + i, y);
      }
    }

    return coords;
  }

  /**
   * Checks if coordinates overlap with other ships' coordinates
   *
   * @param ships  List of ships
   * @param coords Fixed array of coordinates
   * @return Whether there is an overlap or not
   */
  private boolean overlaps(List<Ship> ships, Coord[] coords) {
    for (Ship s : ships) {
      for (Coord shipCoords : s.coords()) {
        for (Coord coord : coords) {
          if (shipCoords.x() == coord.x() && shipCoords.y() == coord.y()) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Returns this player's shots on the opponent's board. The number of shots returned should
   * equal the number of ships on this player's board that have not sunk.
   *
   * @return the locations of shots on the opponent's board
   */
  @Override
  public abstract List<Coord> takeShots();

  /**
   * Given the list of shots the opponent has fired on this player's board, report which
   * shots hit a ship on this player's board.
   *
   * @param opponentShotsOnBoard the opponent's shots on this player's board
   * @return a filtered list of the given shots that contain all locations of shots that hit a
   *         ship on this board
   */
  @Override
  public List<Coord> reportDamage(List<Coord> opponentShotsOnBoard) {
    // Hits
    List<Coord> hits = new ArrayList<>();
    // Hits that affect the player
    List<Coord> myHits = new ArrayList<>();
    // Misses that affect the player
    List<Coord> misses = new ArrayList<>();

    for (Coord shipCoord : this.board.shipLocations.keySet()) {
      for (Coord oppCoord : opponentShotsOnBoard) {
        if (shipCoord.x() == oppCoord.x() && shipCoord.y() == oppCoord.y()) {
          myHits.add(oppCoord);
          hits.add(shipCoord);
        } else {
          misses.add(oppCoord);
        }
      }
    }

    this.board.setShots(myHits, Impact.HIT);
    this.board.setShots(misses, Impact.MISS);

    return hits;
  }

  /**
   * Reports to this player what shots in their previous volley returned from takeShots()
   * successfully hit an opponent's ship. (This method isn't needed, so it is empty)
   *
   * @param shotsThatHitOpponentShips the list of shots that successfully hit the opponent's ships
   */
  @Override
  public void successfulHits(List<Coord> shotsThatHitOpponentShips) {
    for (Coord c : shotsThatHitOpponentShips) {
      if (c.x() > 0) {
        coordsLikely.add(new Coord(c.x() - 1, c.y()));
      }
      if (c.x() < board.grid.length - 1) {
        coordsLikely.add(new Coord(c.x() + 1, c.y()));
      }
      if (c.y() > 0) {
        coordsLikely.add(new Coord(c.x(), c.y() - 1));
      }
      if (c.y() < board.grid[0].length - 1) {
        coordsLikely.add(new Coord(c.x(), c.y() + 1));
      }
    }
  }

  /**
   * Notifies the player that the game is over.
   * Win, lose, and draw should all be supported
   *
   * @param result if the player has won, lost, or forced a draw
   * @param reason the reason for the game ending
   */
  @Override
  public void endGame(GameResult result, String reason) {
    BattleSalvoView bsv = new BattleSalvoView();
    switch (result) {
      case WIN -> bsv.showSuccess(this.name() + " wins the game!");
      case LOSE -> bsv.showFailure(this.name() + " lost...");
      default -> bsv.showText(this.name() + " had a draw.");
    }
    bsv.showText(reason);
  }
}
