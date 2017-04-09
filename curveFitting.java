package BayesianCurveFitting;

import Jama.Matrix;

public class curveFitting {
	private final double a = 0.005; // alpha
	private final double b = 11.1; // beta
	private final int M;
	private final int N;
	private double[] x;
	private double[] t;
	private Matrix S;
	private Matrix I;

	/**
	 * construct function
	 * 
	 * @param x
	 * @param t
	 * @param m
	 */
	public curveFitting(double[] x, double[] t, int m) {
		this.x = x;
		this.t = t;
		M = m;
		N = x.length;
		I = Matrix.identity(M + 1, M + 1);
		getS();
	}

	/**
	 * compute matrix phi(x)
	 * 
	 * @param x
	 * @return
	 */
	private Matrix phi(double x) {
		double[] phiVal = new double[M + 1];
		for (int i = 0; i <= M; i++) {
			phiVal[i] = Math.pow(x, i);
		}
		Matrix phi = new Matrix(phiVal, M + 1);
		return phi;
	}

	/**
	 * compute S^-1
	 */
	private void getS() {
		Matrix sum = new Matrix(M + 1, M + 1);
		for (int i = 0; i < N; i++) {
			Matrix phi = phi(x[i]);
			sum = sum.plus(phi.times(phi.transpose()));
		}
		sum = sum.times(b);
		S = I.times(a).plus(sum);
		S = S.inverse();
	}

	/**
	 * compute s^2(x)
	 * 
	 * @param x
	 * @return
	 */
	public double getS2(double x) {
		Matrix s = phi(x).transpose().times(S).times(phi(x));
		double sVal = s.get(0, 0);
		double s2x = 1 / b + sVal;
		return s2x;
	}

	/**
	 * compute m(x)
	 * 
	 * @param x
	 * @return
	 */
	public double getMx(double x) {
		Matrix sum = new Matrix(M + 1, 1);
		for (int i = 0; i < N; i++) {
			Matrix phi = phi(this.x[i]);
			sum = sum.plus(phi.times(t[i]));
		}
		Matrix mx = phi(x).transpose().times(b);
		mx = mx.times(S);
		mx = mx.times(sum);
		return mx.get(0, 0);
	}

}
