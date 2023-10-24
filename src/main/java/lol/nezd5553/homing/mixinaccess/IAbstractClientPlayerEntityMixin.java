package lol.nezd5553.homing.mixinaccess;

public interface IAbstractClientPlayerEntityMixin {

    void startHomingAnimation();

    void stopAnimations();

    boolean isBoosting();

    void setBoosting(boolean boosting);
}
