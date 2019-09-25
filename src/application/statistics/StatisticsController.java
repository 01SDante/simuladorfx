package application.statistics;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class StatisticsController {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void Statistics() {

		// PIE CHART USO CPU
		ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
				new PieChart.Data("Uso CPU", 85),
		new PieChart.Data("Tiempo Ocioso", 15));

		final PieChart chart = new PieChart(data);
		chart.setTitle("% Utilización CPU");

		// BAR CHART TIEMPO RETORNO TIEMPO ESPERA
		final CategoryAxis xProceso = new CategoryAxis();
		xProceso.setLabel("Procesos");
		final NumberAxis yTiempo = new NumberAxis();
		yTiempo.setLabel("Tiempo");
		final BarChart<String, Number> bar = new BarChart<String, Number>(xProceso, yTiempo);

		XYChart.Series seriesTR = new XYChart.Series();
		seriesTR.setName("Tiempo Retorno");
		seriesTR.getData().add(new XYChart.Data("P1", 19));
		seriesTR.getData().add(new XYChart.Data("P2", 2));
		seriesTR.getData().add(new XYChart.Data("P3", 7));
		seriesTR.getData().add(new XYChart.Data("P4", 4));
		seriesTR.getData().add(new XYChart.Data("P5", 14));

		XYChart.Series seriesTE = new XYChart.Series();
		seriesTE.setName("Tiempo Espera");
		seriesTE.getData().add(new XYChart.Data("P1", 0));
		seriesTE.getData().add(new XYChart.Data("P2", 10));
		seriesTE.getData().add(new XYChart.Data("P3", 11));
		seriesTE.getData().add(new XYChart.Data("P4", 13));
		seriesTE.getData().add(new XYChart.Data("P5", 14));

		bar.getData().addAll(seriesTR, seriesTE);
		bar.setTitle("TRP = 9.2 seg. - TEP = 9.6 seg.");

		// LAYOUT
		HBox hbox = new HBox();
		hbox.getChildren().addAll(chart, bar);

		Scene scene = new Scene(hbox, 800, 400);

		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setTitle("Estadísticas");
		stage.show();

	}

}
