package discrete_logarithm;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PohligHellmanAlgorithm {

	public static final BigInteger ONE = BigInteger.ONE;
	public static final BigInteger ZERO = BigInteger.ZERO;
	
	public PohligHellmanAlgorithm() {
	}
	private static Map<Integer, Integer> primeFactors(BigInteger number)throws Exception {
		int factorsOf = number.intValue();
		int mult = 1;
		Map<Integer, Integer> factors = new HashMap<Integer, Integer>();
		for (Integer i = 2; i <= factorsOf / i; i++) {
			while (factorsOf % i == 0) {
				Integer currCount = factors.get(i);
				if (currCount == null) {
					factors.put(i, 1);
				} else {
					factors.put(i, currCount + 1);
				}
				mult *= i;
				factorsOf /= i;
			}
		}
		if (factorsOf > 1) {
			Integer currCount = factors.get(factorsOf);
			if (currCount == null) {
				factors.put(factorsOf, 1);
			} else {
				factors.put(factorsOf, currCount + 1);
			}
			mult *= factorsOf;
		}
		if (number.intValue() != mult)
			throw new Exception("Number  " + number + " is not a smooth prime");

		return factors;
	}
	
	public static BigInteger solveChineseRemainderTheorem(BigInteger[] reminder, BigInteger[] moduli) {
		if (reminder.length > moduli.length){
			throw new IllegalArgumentException(
					"More reminder than moduli values.");
		}else if(reminder.length < moduli.length){
			throw new IllegalArgumentException(
					"More moduli than reminder values.");
		}

		for (int i = 0; i < moduli.length - 1; i++) {
			for (int j = i + 1; j < moduli.length; j++) {
				if (!(moduli[i].gcd(moduli[j]).equals(ONE)))
					throw new IllegalArgumentException(
							"Moduli are not coprime.");
			}
		}

		BigInteger M = new BigInteger("1");
		for (int i = 0; i < moduli.length; i++)
			M = M.multiply(moduli[i]);
		BigInteger solution = new BigInteger("0");
		for (int i = 0; i < moduli.length; i++) {
			BigInteger Mi = M.divide(moduli[i]);
			solution = solution.add(reminder[i].multiply(Mi).multiply(Mi.modInverse(moduli[i])));
		}
		solution = leastNonnegativeResidue(solution, M);
		
	
		return solution;
	}
	// Computes the least nonnegative residue of b mod m, where m>0.
		public static BigInteger leastNonnegativeResidue(BigInteger b, BigInteger m) {
			if (m.compareTo(ZERO) <= 0)
				throw new IllegalArgumentException("Modulus must be positive.");
			BigInteger answer = b.mod(m);
			return (answer.compareTo(ZERO) < 0) ? answer.add(m) : answer;
		}
	

    
	private static String pohligHellman(BigInteger p, BigInteger g, BigInteger beta) throws Exception{
		BigInteger order = p.subtract(ONE);
		System.out.println("p\t:\t" + p);
		System.out.println("g\t:\t" + g);
		System.out.println("beta\t:\t" + beta);
		System.out.println("order\t:\t" + order);

		PohligHellmanAlgorithm main = new PohligHellmanAlgorithm();
		// Prime factors of p-1
		Map<Integer, Integer> factors = main.primeFactors(order);

		// These two lists are needed for CRT function at the very end.
		// The lists store computed X value for each 'q' and their corresponding
		// mod
		List<BigInteger> finalX = new ArrayList<BigInteger>();
		List<BigInteger> finalMod = new ArrayList<BigInteger>();

		for (Map.Entry<Integer, Integer> e : factors.entrySet()) {
			
			BigInteger q = new BigInteger(Integer.toString(e.getKey()));
			int r = e.getValue();
			BigInteger pMinus1DivByQ = order.divide(q);
			BigInteger beta_pminus1byq = beta.modPow(pMinus1DivByQ, p);
			
			System.out.print("q=" + e.getKey() + "\tr=" + e.getValue()
					+ "\tp^r=" + q.pow(r) + "\t(p-1/q)=" + pMinus1DivByQ
					+ "\tb^(p-1/q)(modp)=" + beta_pminus1byq + "\t");

			BigInteger g_pMinus1DivByQ = g.modPow(pMinus1DivByQ, p);

			// Start generating x_0, x_1, etc...
			List<BigInteger> equationXValues = new ArrayList<BigInteger>();
			// Current beta
			BigInteger aBeta = beta;
			// Previous beta
			BigInteger pBeta = null;
		
			for (int i = 1; i <= r; i++) {
				// Beta^((p-1)/(q^i))
				BigInteger beta_pminus1byqexp = aBeta.modPow(
						order.divide(q.pow(i)), p);
				BigInteger x_ = null;
				// Exhastive check for all Beta(...) === g^((p-1)/q)^k ; 0 <= k
				// <= q-1
				BigInteger j = ZERO ;
				for (; j.compareTo(q) < 0; j = j.add(ONE)) {
					BigInteger leTry = g_pMinus1DivByQ.modPow(j, p);
					
					if (leTry.equals(beta_pminus1byqexp)) {
						x_ = j;
					//	System.out.println(" ");
						break;
						
					}
				}
				// Sanity check
				if (x_ == null)
					throw new Exception("Didn't find a matching exponenet.");
				else {
					equationXValues.add(x_);
					// Prepare for next x_
					pBeta = aBeta;
					// previousBeta * alpha^(-1)^(x_0)
					
					if(j.subtract(BigInteger.ONE).intValue()>=0)
					aBeta = pBeta.multiply(g.modInverse(p).modPow(x_.multiply(q.pow(i-1)), p).mod(p));
					
				}

			}
			BigInteger theX = ZERO;
			int idx = 0;
			for (BigInteger x : equationXValues) {
				theX = theX.add(x.multiply(q.pow(idx))).mod(q.pow(r));
				System.out.print("x" + idx + "=" + x + "\t");
				idx++;
			}
			System.out.println(" X=" + theX + " (mod " + q.pow(r) + ") ");
			finalX.add(theX);
			finalMod.add(q.pow(r));

		}
		BigInteger result = solveChineseRemainderTheorem(
				finalX.toArray(new BigInteger[finalX.size()]),
				finalMod.toArray(new BigInteger[finalMod.size()]));
		BigInteger computedBeta = g.modPow(result, p);
		if (computedBeta.equals(beta)) {
			System.out.println("Asserted. Everything went well. :)");
		} else {
			System.out.println("Assertion failed. :(");
			System.out.println("given beta\t= " + beta + "\nour beta\t="
					+ computedBeta);
		};
		return "Result: " + result;
	}
	public static void main(String[] args) throws Exception{
		System.out.println(pohligHellman(BigInteger.valueOf(29),BigInteger.valueOf(11),BigInteger.valueOf(3)));
		System.out.println(pohligHellman(BigInteger.valueOf(251),BigInteger.valueOf(71),BigInteger.valueOf(210)));

	}
}

