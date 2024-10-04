package engine;

import java.util.logging.Logger;

/**
 * Implements an object that stores a single game's difficulty settings.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class GameSettings {

	/** Width of the level's enemy formation. */
	private int formationWidth;
	/** Height of the level's enemy formation. */
	private int formationHeight;
	/** Speed of the enemies, function of the remaining number. */
	private int baseSpeed;
	/** Frequency of enemy shootings, +/- 30%. */
	private int shootingFrecuency;

	/**
	 * Constructor.
	 * 
	 * @param formationWidth
	 *            Width of the level's enemy formation.
	 * @param formationHeight
	 *            Height of the level's enemy formation.
	 * @param baseSpeed
	 *            Speed of the enemies.
	 * @param shootingFrecuency
	 *            Frecuency of enemy shootings, +/- 30%.
	 */
	public GameSettings(final int formationWidth, final int formationHeight,
			final int baseSpeed, final int shootingFrecuency) {
		this.formationWidth = formationWidth;
		this.formationHeight = formationHeight;
		this.baseSpeed = baseSpeed;
		this.shootingFrecuency = shootingFrecuency;
	}

	/**
	 * @return the formationWidth
	 */
	public final int getFormationWidth() {
		return formationWidth;
	}

	/**
	 * @return the formationHeight
	 */
	public final int getFormationHeight() {
		return formationHeight;
	}

	/**
	 * @return the baseSpeed
	 */
	public final int getBaseSpeed() {
		return baseSpeed;
	}

	/**
	 * @return the shootingFrecuency
	 */
	public final int getShootingFrecuency() {
		return shootingFrecuency;
	}

	public GameSettings LevelSettings(int formationWidth, int formationHeight,
									  int baseSpeed, int shootingFrecuency, int level, int difficulty) {
		return switch (difficulty) {
			case 1 -> {
				if(level%3 == 0 && level < 5){
					if(formationWidth == formationHeight){
						formationWidth += 1;
                    } else {
						formationHeight += 1;
                    }
                    baseSpeed -= 10;
                    shootingFrecuency -= 100;
                }else if(level % 2 == 0){
					if(formationWidth == formationHeight){
						formationWidth += 1;
					} else {
						formationHeight += 1;
					}
					baseSpeed -= 10;
					shootingFrecuency -= 100;
				}
                yield new GameSettings(formationWidth, formationHeight, baseSpeed, shootingFrecuency);
			}
			case 2 -> {
				if(level%2 == 0 && level < 5){
					if(formationWidth == formationHeight){
						formationWidth += 1;
					} else {
						formationHeight += 1;
					}
					baseSpeed -= 10;
					shootingFrecuency -= 200;
				}else {
					if(formationWidth == formationHeight){
						formationWidth += 1;
					} else {
						formationHeight += 1;
					}
					baseSpeed -= 20;
					shootingFrecuency -= 300;
				}
                yield new GameSettings(formationWidth, formationHeight, baseSpeed, shootingFrecuency);
			}
			case 3 -> {
				if(level%2 == 0 && level < 5){
					if(formationWidth == formationHeight){
						formationWidth += 1;
					} else {
						formationHeight += 1;
					}
					baseSpeed -= 20;
					shootingFrecuency -= 300;
				}else{
					if(formationWidth == formationHeight){
						formationWidth += 2;
					} else {
						formationHeight += 2;
					}
					baseSpeed -= 20;
					shootingFrecuency -= 400;
				}
                yield new GameSettings(formationWidth, formationHeight, baseSpeed, shootingFrecuency);
			}
			default -> null;
		};
	}

}
