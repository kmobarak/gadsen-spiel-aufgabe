package bots;

import com.badlogic.gdx.math.Vector2;
import com.gats.manager.*;
import com.gats.simulation.*;
import com.gats.simulation.weapons.Weapon;

import java.util.ArrayList;

public class MyBot extends Bot {

    @Override
    public String getStudentName() {
        return "Khaled Mobarak";
    }

    @Override
    public int getMatrikel() {
        return 231935;
    }

    @Override
    public String getName() {
        return "KhaledBot";
    }

    @Override
    protected void init(GameState state) {
        System.out.println("Loaded Khaled's bot!");
    }

    @Override
    protected void executeTurn(GameState state, Controller controller) {
        Vector2 position = controller.getGameCharacter().getPlayerPos();
        ArrayList<GameCharacter> opponents = getAliveOpponents(state, controller.getGameCharacter());

        GameCharacter target = getClosestOpponent(position, opponents);

        if (target != null) {
            Vector2 targetPos = target.getPlayerPos();
            float shootingAngle = calculatePredictiveAim(position, targetPos);
            shootWithPreferredWeapon(controller, targetPos, shootingAngle);
        }
    }

    private void shootWithPreferredWeapon(Controller controller, Vector2 targetPos, float shootingAngle) {
        GameCharacter gameCharacter = controller.getGameCharacter();
        Weapon preferredWeapon = getPreferredWeapon(gameCharacter, targetPos);

        if (preferredWeapon != null) {
            controller.shoot(shootingAngle, 1, preferredWeapon.getType());
        } else {
            controller.shoot(convertToLinearAim(controller.getGameCharacter().getPlayerPos(), targetPos), 1, WeaponType.WATER_PISTOL);
        }
    }

    private Weapon getPreferredWeapon(GameCharacter gameCharacter, Vector2 targetPos) {
        float distanceToTarget = gameCharacter.getPlayerPos().dst(targetPos);
        Weapon preferredWeapon = null;

        // Define the weapon priorities based on distance to the target
        if (distanceToTarget > 10) {
            // Far targets
            preferredWeapon = getWeaponOfType(gameCharacter, WeaponType.WOOL);
            if (preferredWeapon == null) {
                preferredWeapon = getWeaponOfType(gameCharacter, WeaponType.MIOJLNIR);
            }
            if (preferredWeapon == null) {
                preferredWeapon = getWeaponOfType(gameCharacter, WeaponType.GRENADE);
            }
        } else if (distanceToTarget > 5) {
            // Medium range targets
            preferredWeapon = getWeaponOfType(gameCharacter, WeaponType.WATER_PISTOL);
            if (preferredWeapon == null) {
                preferredWeapon = getWeaponOfType(gameCharacter, WeaponType.WATERBOMB);
            }
        } else {
            // Close range targets
            preferredWeapon = getWeaponOfType(gameCharacter, WeaponType.WATERBOMB);
            if (preferredWeapon == null) {
                preferredWeapon = getWeaponOfType(gameCharacter, WeaponType.CLOSE_COMBAT);
            }
        }

        return preferredWeapon;
    }

    private Weapon getWeaponOfType(GameCharacter gameCharacter, WeaponType weaponType) {
        for (int i = 0; i < gameCharacter.getWeaponAmount(); i++) {
            Weapon weapon = gameCharacter.getWeapon(i);
            if (weapon.getAmmo() > 0 && weapon.getType() == weaponType) {
                return weapon;
            }
        }
        return null;
    }

    private float calculatePredictiveAim(Vector2 currentPosition, Vector2 targetPosition) {
        float dx = targetPosition.x - currentPosition.x;
        float dy = targetPosition.y - currentPosition.y;
        return (float) Math.toDegrees(Math.atan2(dy, dx));
    }

    private float convertToLinearAim(Vector2 position, Vector2 target) {
        return (float) (Math.atan2(target.y - position.y, target.x - position.x) * (180 / Math.PI));
    }

    private ArrayList<GameCharacter> getAliveOpponents(GameState state, GameCharacter myChar) {
        ArrayList<GameCharacter> opponents = new ArrayList<>();
        for (int i = 0; i < state.getTeamCount(); i++) {
            GameCharacter character = state.getCharacterFromTeams(i, 0);
            if (character != null && character.isAlive() && character.getTeam() != myChar.getTeam()) {
                opponents.add(character);
            }
        }
        return opponents;
    }

    private GameCharacter getClosestOpponent(Vector2 position, ArrayList<GameCharacter> opponents) {
        GameCharacter closestOpponent = null;
        float minDistance = Float.MAX_VALUE;

        for (GameCharacter opponent : opponents) {
            Vector2 opponentPos = opponent.getPlayerPos();
            float distance = position.dst(opponentPos);

            if (distance < minDistance) {
                minDistance = distance;
                closestOpponent = opponent;
            }
        }

        return closestOpponent;
    }
}
