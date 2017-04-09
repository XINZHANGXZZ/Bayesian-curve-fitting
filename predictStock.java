package BayesianCurveFitting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class predictStock {

	static double[] actualPrices;
	static double[] predictPrices;

	public static void main(String[] args) {

		readFile reader;
		curveFitting cf;
		String s = "Y";
		int num = 0;
		int m;
		double absMeanErr = 0.0;
		double relErr = 0.0;
		double sumErr = 0.0;
		double sumPrice = 0.0;
		
		String[] companies = { "AAPL", "AMZN", "BABA", "FB", "GOOG", "GPRO", "MSFT", "NFLX", "TSLA", "TWTR" };
		int len = companies.length;

		// continue predict while s = 'y'
		while (s.equals("Y") || s.equals("y")) {
	
			System.out.println("Please choose one of the stocks (1 ~ 15):");

			for (int i = 0; i < len; i++) {
				System.out.print((i + 1) + "." + companies[i] + "  ");
			}
			System.out.println();
			for (int i = 11; i <= 15; i++) {
				System.out.print(i + ". " + "data " + (i - 10) + "  ");
			}
			System.out.println();
			// input your choice
			Scanner sc = new Scanner(System.in);
			int choice = sc.nextInt();

			// bound inspection
			while (choice <= 0 || choice > len + 6) {
				System.out.println("Input out of bound! Please enter again:");
				choice = sc.nextInt();
			}

			// read file to get the lines of data
			if (choice < len + 1) {
				reader = new readFile(companies[choice - 1], true);
			} else {
				reader = new readFile(String.valueOf(choice - 10), false);
			}
			int N = 0;
			if(choice < len + 1){
				System.out.println("Please enter the number of data (1 ~ " + (reader.getNum() - 2) + "):");
				N = sc.nextInt();
	
				// bound inspection
				while (N < 1 || N > (reader.getNum() - 2)) {
					System.out.println("Input out of bound! Please enter again:");
					N = sc.nextInt();
				}
			}else{
				System.out.println("Please enter the number of data (1 ~ " + (reader.getNum() - 1) + "):");
				N = sc.nextInt();
	
				// bound inspection
				while (N < 1 || N > (reader.getNum() - 1)) {
					System.out.println("Input out of bound! Please enter again:");
					N = sc.nextInt();
				}
			}
			// define x[]
			double[] x = new double[N];
			for (int i = 0; i < N; i++) {
				x[i] = i + 1;
			}

			// set the value of m
			if (choice < companies.length + 1) {
				m = 9;
			} else {
				m = 5;
			}

			// read file
			if (choice < companies.length + 1) {
				reader = new readFile(N, companies[choice - 1], true);
			} else {
				reader = new readFile(N, String.valueOf(choice - 10), false);
			}

			// computing
			double[] stockPrices = reader.getPrices();
//			for(int i=0;i<stockPrices.length;i++){
//				System.out.println(stockPrices[i]);
//			}
			cf = new curveFitting(x, stockPrices, m);
			double prediction = cf.getMx(N + 1);
			double s2x = cf.getS2(N + 1);
			double actualPrice = 0.0;

			if (choice < len + 1 || (choice > len && N < 10)) {
				actualPrice = reader.getActualPrice();
			}
			num++;
			sumErr += Math.abs(actualPrice - prediction);
			sumPrice += actualPrice;
			absMeanErr = sumErr / num;
			relErr = sumErr / sumPrice;

			// print the output
			System.out.println("Here is the predict result:");
			System.out.println("-------------------------------------");
			if (choice < len + 1) {
				System.out.println("Stock: " + companies[choice - 1]);
			} else {
				System.out.println("Data: data " + (choice - 10));
			}
			System.out.printf("predicted stock price: %.2f\n", prediction);
			
			if (choice < len + 1 || (choice > len && N < 10)) {
				System.out.println("actual price: " + actualPrice);
				System.out.println("absolute mean error: " + absMeanErr);
				System.out.println("average relative error: " + relErr);
				System.out.println("-------------------------------------");
			}

			System.out.println("variation of prediction: " + s2x);
			
			if (N > 9 && choice < len + 1) {
				System.out.println("N > 9, Drawing graph......");
				// draw the graph
				getData(N, choice);
				// 步骤1：创建CategoryDataset对象（准备数据）
				CategoryDataset dataset = createDataset();
				// 步骤2：根据Dataset 生成JFreeChart对象，以及做相应的设置
				JFreeChart freeChart = createChart(dataset);
				// 步骤3：将JFreeChart对象输出到文件，Servlet输出流等
				saveAsFile(freeChart, "line/" + companies[choice - 1] + "_" + N + ".jpg", 600, 400);
			}
			System.out.println("Continue? (Y/N)");
			s = sc.next();
		}
	}

	/**
	 * get actual and predict prices used to draw the graph
	 * 
	 * @param N
	 *            Nth data
	 * @param choice
	 *            company number
	 */
	public static void getData(int N, int choice) {
		readFile reader;
		curveFitting cf;
		int m;

		String[] companies = { "AAPL", "AMZN", "BABA", "FB", "GOOG", "GPRO", "MSFT", "NFLX", "TSLA", "TWTR" };

		reader = new readFile(companies[choice - 1], true);

		// double[] stockPrices = null;
		List<Double> predicts = new ArrayList<Double>();
		for (int n = 0; n < N - 1; n++) {
			// define x[]
			double[] x = new double[n];
			for (int i = 0; i < n; i++) {
				x[i] = i + 1;
			}
			m = 9;
			reader = new readFile(n, companies[choice - 1], true);

			// computing
			actualPrices = reader.getPrices();
			cf = new curveFitting(x, actualPrices, m);
			double prediction = cf.getMx(n + 1);
			predicts.add(prediction);
		}

		predictPrices = new double[predicts.size()];
		for (int i = 0; i < predicts.size(); i++) {
			predictPrices[i] = predicts.get(i);
		}

	}

	/**
	 * save the graph to file
	 * 
	 * @param chart
	 * @param outputPath
	 * @param weight
	 * @param height
	 */
	public static void saveAsFile(JFreeChart chart, String outputPath, int weight, int height) {
		FileOutputStream out = null;
		try {
			File outFile = new File(outputPath);
			if (!outFile.getParentFile().exists()) {
				outFile.getParentFile().mkdirs();
			}
			out = new FileOutputStream(outputPath);
			// save to JPEG
			ChartUtilities.writeChartAsJPEG(out, chart, 600, 400);
			out.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {

				}
			}
		}
	}

	/**
	 * create JFreeChart instance
	 * 
	 * @param categoryDataset
	 * @return
	 */
	public static JFreeChart createChart(CategoryDataset categoryDataset) {
		// 创建JFreeChart对象：ChartFactory.createLineChart
		JFreeChart jfreechart = ChartFactory.createLineChart("predict VS actual", // title
				"time", // categoryAxisLabel （category轴，横轴，X轴标签）
				"prices", // valueAxisLabel（value轴，纵轴，Y轴的标签）
				categoryDataset, // dataset
				PlotOrientation.VERTICAL, true, // legend
				false, // tooltips
				false); // URLs
		// 使用CategoryPlot设置各种参数。以下设置可以省略。
		CategoryPlot plot = (CategoryPlot) jfreechart.getPlot();
		// 背景色 透明度
		plot.setBackgroundAlpha(0.5f);
		// 前景色 透明度
		plot.setForegroundAlpha(0.5f);
		// 其他设置 参考 CategoryPlot类
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
		renderer.setBaseShapesVisible(false); // series 点（即数据点）可见
		renderer.setBaseLinesVisible(true); // series 点（即数据点）间有连线可见
		renderer.setUseSeriesOffset(true); // 设置偏移量
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setBaseItemLabelsVisible(false);
		return jfreechart;
	}

	/**
	 * create CategoryDataset instance
	 * 
	 */
	public static CategoryDataset createDataset() {

		DefaultCategoryDataset dd = new DefaultCategoryDataset();
		for (int i = 9; i < predictPrices.length; i++) {
			dd.addValue(predictPrices[i], "predict", String.valueOf(i));
		}
		for (int i = 9; i < actualPrices.length; i++) {
			dd.addValue(actualPrices[i], "actual", String.valueOf(i));
		}

		return dd;
	}
}
