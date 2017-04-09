package BayesianCurveFitting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class readFile {
	private int N;
	private int count;
	boolean isStock;
	private String company;
	private double[] prices;
	private String[] tmp;

	/**
	 * construct function used to read file
	 * @param N
	 * @param company
	 * @param isStock
	 */
	public readFile(int N, String company, boolean isStock) {
		this.N = N;
		this.company = company;
		this.isStock = isStock;
		read();
	}

	/**
	 * construct function used to get the number of data
	 * @param company
	 * @param isStock
	 */
	public readFile(String company, boolean isStock) {
		this.company = company;
		this.isStock = isStock;
		getNum();
	}

	/**
	 * read file to get the close price of stocks or the value of our own data
	 */
	public void read() {
		prices = new double[N];
		String fileName;
		if (isStock) {
			fileName = "historicalStock/" + company + ".csv";
		} else {
			fileName = "historicalStock/data " + company + ".csv";
		}
		tmp = new String[300];
		String line = "";
		count = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			if (isStock){
				line = reader.readLine();
			}
			line = reader.readLine();
			while (line != null) {
				String[] item = line.split(",");
				if (isStock) {
					// get the 5th column of csv file
					tmp[count] = item[4];
				} else {
					// get the 1th column of csv file
					tmp[count] = item[1];
				}
				count++;
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (isStock) {
			// reverse prices
			for (int i = 0; i < N; i++) {
				prices[i] = Double.parseDouble(tmp[count - i - 1]);
			}
		}else{
			for (int i = 0; i < N; i++) {
				prices[i] = Double.parseDouble(tmp[i]);
			}
		}
	}

	/**
	 * get the length of a data file
	 * @return length
	 */
	public int getNum() {
		String fileName;
		if (isStock) {
			fileName = "historicalStock/" + company + ".csv";
		} else {
			fileName = "historicalStock/data " + company + ".csv";
		}
		String line = "";
		count = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			if (isStock){
				line = reader.readLine();
			}
			while (line != null) {
				count++;
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * get company name
	 * @return company
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * get prices of a certain company
	 * @return prices
	 */
	public double[] getPrices() {
		return prices;
	}

	/**
	 * get the actual price of the day we predict
	 * @return actual price
	 */
	public double getActualPrice() {
		double actual = 0.0;
		if(isStock){
			actual = Double.parseDouble(tmp[count - N - 1]);
		}else{
			actual = Double.parseDouble(tmp[N]);
		}
		return actual;
	}
}
