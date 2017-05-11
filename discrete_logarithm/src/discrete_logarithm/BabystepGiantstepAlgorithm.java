package discrete_logarithm;

import java.math.BigInteger;

public class BabystepGiantstepAlgorithm {

	/* a = x^y mod m
	 * */
	private static long babystepGiantstepAlg(int x, int a, int m){
		long ord = (long) CyclicGroup.cycle(x, m);
		long n = (long)Math.ceil(Math.sqrt(ord));
		double[][] table1 = new double[2][(int)n];
		double[][] table2 = new double[2][(int)n];

		for (int i = 0; i < (int)n; i++){
			table1 [0][i] = i;
			table1 [1][i] = Math.pow(x, i) % m; 
		}		
		BigInteger x1 =  BigInteger.valueOf((long)x);
		x1 = x1.modPow(BigInteger.valueOf(ord - n), BigInteger.valueOf(m));	
		for(int j = 0; j<n; j++){
			table2[0][j] = j;
			table2[1][j] = x1.pow(j).multiply(BigInteger.valueOf(a)).mod(BigInteger.valueOf(m)).doubleValue();
			for(int k = 0; k < (int)n; k++){
				if(table2[1][j] == table1[1][k]){
					return j*n + k;
				}
			}
		}
		return 0;
	}
	
	public static void main(String [] args){
		System.out.println(babystepGiantstepAlg(19,3,29));	
		System.out.println(babystepGiantstepAlg(71,210,251));		

	}
}
