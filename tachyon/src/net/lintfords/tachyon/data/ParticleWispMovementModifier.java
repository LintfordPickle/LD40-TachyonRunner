package net.lintfords.tachyon.data;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.particles.Particle;
import net.lintford.library.core.graphics.particles.modifiers.IParticleModifier;
import net.lintford.library.core.maths.RandomNumbers;

public class ParticleWispMovementModifier implements IParticleModifier {

	// --------------------------------------
	// Variables
	// --------------------------------------
	
	// --------------------------------------
	// Constructor
	// --------------------------------------
	
	public ParticleWispMovementModifier(){
		
	}
	
	// --------------------------------------
	// Methods
	// --------------------------------------
	
	@Override
	public void initialise(Particle pParticle) {
		// Give each Wisp particle a random direction
		pParticle.roy = (float)(RandomNumbers.RANDOM.nextFloat() * Math.PI * 2f);
		
		
	}

	@Override
	public void update(LintfordCore pCore) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateParticle(Particle pParticle, LintfordCore pCore) {

		// We will use the un-used rox, roy variables of a particle to track the time and 
		// angle of the particle over time. 
		
		pParticle.roy += RandomNumbers.RANDOM.nextFloat() * 10f;
		
		pParticle.dx += (float)Math.cos(pParticle.roy) * 0.01f;
		pParticle.dy += (float)Math.sin(pParticle.roy) * 0.01f;
		
	}

}