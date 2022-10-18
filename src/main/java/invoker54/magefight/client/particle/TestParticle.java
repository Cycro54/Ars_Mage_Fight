package invoker54.magefight.client.particle;

import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.TexturedParticle;
import net.minecraft.client.world.ClientWorld;

public class TestParticle extends TexturedParticle {
    protected TestParticle(ClientWorld p_i232423_1_, double p_i232423_2_, double p_i232423_4_, double p_i232423_6_) {
        super(p_i232423_1_, p_i232423_2_, p_i232423_4_, p_i232423_6_);
    }

    @Override
    protected float getU0() {
        return 0;
    }

    @Override
    protected float getU1() {
        return 0;
    }

    @Override
    protected float getV0() {
        return 0;
    }

    @Override
    protected float getV1() {
        return 0;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return null;
    }


}
