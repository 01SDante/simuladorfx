package app.gantt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import app.gantt.GanttChart.ExtraData;
import app.modelo.Proceso;
import app.modelo.ProcesoGantt;
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

@SuppressWarnings({ "rawtypes", "unchecked" })
public class GanttController {

	public static String[] colores = { "", "status-red", "status-orange", "status-yellow", "status-green",
			"status-cyan", "status-blue", "status-magenta", "status-silver", "status-saddlebrown", "status-teal" };
	public static String[] coloresLeyenda = { "", "red", "orange", "yellow", "green", "cyan", "blue", "magenta",
			"silver", "saddlebrown", "teal" };

	public static void GanttFCFS(ArrayList<ProcesoGantt> ganttCpu, ArrayList<ProcesoGantt> ganttEs) {
		
		// HashMap<Integer, Proceso> ganttCpuHM, HashMap<Integer, Proceso> ganttEsHM
		
		Stage stage = new Stage();
		stage.setTitle("Gantt");

		String[] machines = new String[] { "ES", "CPU" };

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
		 *  ES
		 * 
		 */
		machine = machines[0];
		XYChart.Series series1 = new XYChart.Series();
		
		int i = 0;
		for (ProcesoGantt p: ganttEs) {
			if (p.getId() != 0) {
				// Inicio, CPU, Cantidad, Color
				series1.getData().add(new XYChart.Data(i, machine, new ExtraData(1, colores[p.getId()])));
			}
			i++;
		}
		
//		for (Map.Entry<Integer, Proceso> entry : ganttEsHM.entrySet()) {
//
//			int t = entry.getKey();
//			Proceso p = entry.getValue();
//
//			// Inicio, CPU, Cantidad, Color
//			series1.getData().add(new XYChart.Data(t, machine, new ExtraData(p.getEs1(), colores[p.getId()])));
//
//		}
		
		/*
		 *  CPU
		 * 
		 */

		machine = machines[1];
		XYChart.Series series2 = new XYChart.Series();

		HBox leyenda = new HBox();
		leyenda.setAlignment(Pos.CENTER);
		
		int j = 0;
		for (ProcesoGantt p: ganttCpu) {
			if (p.getId() != 0) {
				// Inicio, CPU, Cantidad, Color
				series2.getData().add(new XYChart.Data(j, machine, new ExtraData(1, colores[p.getId()])));
			}
			j++;
			
		}

//		for (Map.Entry<Integer, Proceso> entry: ganttCpuHM.entrySet()) {
//			
//			int t = entry.getKey();
//			Proceso p = entry.getValue();
//			
//			// Inicio, CPU, Cantidad, Color
//			series2.getData().add(new XYChart.Data(t, machine, new ExtraData(p.getCpu1(), colores[p.getId()])));
//			
//			/*
//			 * Armo la leyenda
//			 * 
//			 */
//			Label label = new Label("   " + p.getId() + "   ");
//			label.setStyle("-fx-background-color: " + coloresLeyenda[p.getId()]
//					+ "; -fx-text-fill: white; -fx-font-weight: bold;");
//			leyenda.getChildren().add(label);
//			
//		}
		
		/*
		 * 
		 */
		chart.getData().addAll(series1, series2);
//		chart.getData().addAll(series2);

		chart.getStylesheets().add("/app/gantt/gantt.css");

		/*
		 * 
		 */
		VBox vbox = new VBox();
		vbox.getChildren().addAll(chart, leyenda);
		vbox.setVgrow(chart, Priority.ALWAYS);
		Scene scene = new Scene(vbox, 620, 350);
//		Scene scene = new Scene(chart, 620, 350);
		stage.setScene(scene);
		stage.show();

	}

	/*
	 * DEFAULT
	 */
	public static void GanttDiagram() {

		Stage stage = new Stage();
		stage.setTitle("Gantt");

		String[] machines = new String[] { "Cola n", "E/S", "CPU" };

		final NumberAxis xAxis = new NumberAxis();
		final CategoryAxis yAxis = new CategoryAxis();

		final GanttChart<Number, String> chart = new GanttChart<Number, String>(xAxis, yAxis);
		xAxis.setLabel("");
		xAxis.setTickLabelFill(Color.CHOCOLATE);
		xAxis.setMinorTickCount(4);

		yAxis.setLabel("");
		yAxis.setTickLabelFill(Color.CHOCOLATE);
		yAxis.setTickLabelGap(10);
		yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(machines)));

		chart.setTitle("Diagrama de Gantt");
		chart.setLegendVisible(false);
		chart.setBlockHeight(50);
		String machine;

		machine = machines[0];
		XYChart.Series series1 = new XYChart.Series();
		series1.getData().add(new XYChart.Data(0, machine, new ExtraData(1, "status-green")));
		series1.getData().add(new XYChart.Data(1, machine, new ExtraData(1, "status-green")));
		series1.getData().add(new XYChart.Data(2, machine, new ExtraData(1, "status-red")));
//		series1.getData().add(new XYChart.Data(3, machine, new ExtraData(1, "status-yellow")));

		machine = machines[1];
		XYChart.Series series2 = new XYChart.Series();
		series2.getData().add(new XYChart.Data(0, machine, new ExtraData(1, "status-yellow")));
		series2.getData().add(new XYChart.Data(1, machine, new ExtraData(1, "status-red")));
		series2.getData().add(new XYChart.Data(3, machine, new ExtraData(1, "status-blue")));

		machine = machines[2];
		XYChart.Series series3 = new XYChart.Series();
		series3.getData().add(new XYChart.Data(0, machine, new ExtraData(1, "status-red")));
		series3.getData().add(new XYChart.Data(1, machine, new ExtraData(2, "status-blue")));
		series3.getData().add(new XYChart.Data(3, machine, new ExtraData(1, "status-green")));

		chart.getData().addAll(series1, series2, series3);

//		chart.getStylesheets().add(getClass().getResource("gantt.css").toExternalForm());
		chart.getStylesheets().add("/application/gantt/gantt.css");

		Scene scene = new Scene(chart, 620, 350);
		stage.setScene(scene);
		stage.show();
	}

}
