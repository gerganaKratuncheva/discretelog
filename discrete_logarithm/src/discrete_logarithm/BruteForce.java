package discrete_logarithm;

import java.math.BigInteger;

public class BruteForce {
	/* x^y = n (mod m)
	 * */
	public static long bruteForce(BigInteger x, long n, long m){
		for(long i = 0; i < m; i++){
			if(x.modPow(BigInteger.valueOf(i), BigInteger.valueOf(m)).equals(BigInteger.valueOf(n))){
				return i;
			}else{
				continue;
			}
		}
		
		return 0;
	}
	
	public static void main(String [] args){
		System.out.println(bruteForce(BigInteger.valueOf(19),3,29));		
	}

}
