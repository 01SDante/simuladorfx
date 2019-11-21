package application.gantt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import application.gantt.GanttChart.ExtraData;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GanttFCFS {

	public static String[] colores = { "", "status-red", "status-orange", "status-yellow", "status-green",
			"status-cyan", "status-blue", "status-magenta", "status-silver", "status-saddlebrown", "status-teal" };
	public static String[] coloresLeyenda = { "", "red", "orange", "yellow", "green", "cyan", "blue", "magenta",
			"silver", "saddlebrown", "teal" };

	@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	public static void generarGanttFCFS(ArrayList<Integer> ganttCpu) {

		Stage stage = new Stage();
		stage.setTitle("Gantt");

		String[] machines = new String[] { "CPU" };

		final NumberAxis xAxis = new NumberAxis();
		final CategoryAxis yAxis = new CategoryAxis();

		final GanttChart<Number, String> chart = new GanttChart<Number, String>(xAxis, yAxis);
		xAxis.setLabel("");
		xAxis.setTickLabelFill(Color.CHOCOLATE);
		xAxis.setMinorTickCount(1);

		yAxis.setLabel("");
		yAxis.setTickLabelFill(Color.CHOCOLATE);
		yAxis.setTickLabelGap(10);
		yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(machines)));

		chart.setTitle("Diagrama de Gantt");
		chart.setLegendVisible(false);
		chart.setBlockHeight(50);
		String machine;

		/*
		 * CPU
		 */
		machine = machines[0];
		XYChart.Series series1 = new XYChart.Series();

		/*
		 * Leyenda
		 */
		HBox leyendaHBox = new HBox();
		leyendaHBox.setAlignment(Pos.CENTER);

		TreeSet<Integer> leyenda = new TreeSet<Integer>(); // Guardo los IDs de los procesos ignorando repetidos

		for (int i = 0; i < ganttCpu.size() - 1; i++) {

			// Inicio, CPU, Cantidad, Color
			series1.getData().add(new XYChart.Data(i, machine, new ExtraData(1, colores[ganttCpu.get(i)])));

			leyenda.add(ganttCpu.get(i));

		}

		for (int i : leyenda) {
			if (i != 0) {
				/*
				 * Armo la leyenda
				 * 
				 */
				Label label = new Label("   " + i + "   ");
				label.setStyle("-fx-background-color: " + coloresLeyenda[i]
						+ "; -fx-text-fill: white; -fx-font-weight: bold;");
				leyendaHBox.getChildren().add(label);
			}
		}

		/*
		 * 
		 */
		chart.getData().addAll(series1);

		chart.getStylesheets().add("/application/gantt/gantt.css");

		/*
		 * 
		 */
		VBox vbox = new VBox();
		vbox.getChildren().addAll(chart, leyendaHBox);
		vbox.setVgrow(chart, Priority.ALWAYS);
		Scene scene = new Scene(vbox, 620, 350);
		stage.setScene(scene);
		stage.show();

	}

}
