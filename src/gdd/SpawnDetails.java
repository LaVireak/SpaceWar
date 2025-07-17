package gdd;

public class SpawnDetails {
    public String type;
    public int x;
    public int y;
    public int health = 10; // Default health
    public String movementPattern = "Straight"; // Default movement pattern
    public String attackPattern = "SingleShot"; // Default attack pattern
    public String powerUpDrop = ""; // Empty means no power-up drop

    public SpawnDetails(String type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    // Getters and setters for additional fields
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public String getMovementPattern() {
        return movementPattern;
    }

    public void setMovementPattern(String movementPattern) {
        this.movementPattern = movementPattern;
    }

    public String getAttackPattern() {
        return attackPattern;
    }

    public void setAttackPattern(String attackPattern) {
        this.attackPattern = attackPattern;
    }

    public String getPowerUpDrop() {
        return powerUpDrop;
    }

    public void setPowerUpDrop(String powerUpDrop) {
        this.powerUpDrop = powerUpDrop;
    }
}
