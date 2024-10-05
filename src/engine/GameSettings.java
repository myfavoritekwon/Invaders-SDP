package engine;

/**
 * Implements an object that stores a single game's difficulty settings.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class GameSettings {


	private static int difficulty;
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
		GameSettings.difficulty = difficulty;
		return switch (difficulty) {
			case 0 -> {
				if(level%3 == 0 && level < 5){
					if(formationWidth == formationHeight){
						if(formationWidth < 14) formationWidth += 1;
                    } else {
						if(formationHeight < 10) formationHeight += 1;
                    }
                    if(baseSpeed-10 > -150)baseSpeed -= 10;
					else baseSpeed = -150;
                    if(shootingFrecuency-100 > 100) shootingFrecuency -= 100;
					else shootingFrecuency = 100;
                }else if(level % 2 == 0 && level >= 5){
					if(formationWidth == formationHeight){
						if(formationWidth < 14) formationWidth += 1;
					} else {
						if(formationHeight < 10) formationHeight += 1;
					}
					if(baseSpeed-10 > -150)baseSpeed -= 10;
					else baseSpeed = -150;
					if(shootingFrecuency-100 > 100) shootingFrecuency -= 100;
					else shootingFrecuency = 100;
				}
                yield new GameSettings(formationWidth, formationHeight, baseSpeed, shootingFrecuency);
			}
			case 1 -> {
				if(level%2 == 0 && level < 5){
					if(formationWidth == formationHeight){
						if(formationWidth < 14) formationWidth += 1;
					} else {
						if(formationHeight < 10) formationHeight += 1;
					}
					if(baseSpeed-10 > -150)baseSpeed -= 10;
					else baseSpeed = -150;
					if(shootingFrecuency-200 > 200) shootingFrecuency -= 200;
					else shootingFrecuency = 100;
				}else if(level >= 5){
					if(formationWidth == formationHeight){
						if(formationWidth < 14) formationWidth += 1;
					} else {
						if(formationHeight < 10) formationHeight += 1;
					}
					if(baseSpeed-20 > -150)baseSpeed -= 20; //스피드 조절
					else baseSpeed = -150;
					if(shootingFrecuency-300 > 300) shootingFrecuency -= 300; //발사 간격 조절
					else shootingFrecuency = 100;
				}
                yield new GameSettings(formationWidth, formationHeight, baseSpeed, shootingFrecuency);
			}
			case 2 -> {
				if(level%2 == 0 && level < 5){
					if(formationWidth == formationHeight){
						if(formationWidth < 14) formationWidth += 1;
					} else {
						if(formationHeight < 10) formationHeight += 1;
					}
					if(baseSpeed-20 > -150)baseSpeed -= 20;
					else baseSpeed = -150;
					if(shootingFrecuency-300 > 300) shootingFrecuency -= 300;
					else shootingFrecuency = 100;
				}else if(level >= 5){
					if(formationWidth == formationHeight){
						if(formationWidth < 14) formationWidth += 2;
					} else {
						if(formationHeight < 10) formationHeight += 2;
					}
					if(baseSpeed-20 > -150)baseSpeed -= 20;
					else baseSpeed = -150;
					if(shootingFrecuency-400 > 400) shootingFrecuency -= 400;
					else shootingFrecuency = 100;
				}
                yield new GameSettings(formationWidth, formationHeight, baseSpeed, shootingFrecuency);
			}
			default -> {
				yield null;
			}
		};
	}

	public static int getDifficulty() {
		return difficulty;
	}

}
