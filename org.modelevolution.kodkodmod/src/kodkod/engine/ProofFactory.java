package kodkod.engine;

import kodkod.engine.fol2sat.TranslationLog;
import kodkod.engine.satlab.SATProver;

/**
 * A simple factory to create various proof objects.
 * 
 * @author Sebastian Gabmeyer
 * @deprecated This class is not used in the final version that employs the IC3 reference
 *             implementation. It is used in the {@link PacmanRunner#main(String[])} example to
 *             extract an UNSAT proof.
 */
@Deprecated
public final class ProofFactory {

	/**
	 * 
	 */
	public static Proof createResolutionBasedProof(SATProver solver,
			TranslationLog log) {
		return new ResolutionBasedProof(solver, log);
	}
}
