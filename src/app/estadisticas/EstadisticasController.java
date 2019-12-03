package app.estadisticas;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class EstadisticasController {

	@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	public static void Statistics(int[] salida, int[] arribo, int[] irrupcion, String titulo) {

		/*
		 * PIE CHART USO CPU
		 * 
		 */
		int tTotal = salida[salida.length - 1];
		int tOcioso = salida[0];
		int tCpu = tTotal - tOcioso;

		double porcentajeUsoCpu = (double) (tCpu * 100) / tTotal;
		double porcentajeTOcioso = (double) (tOcioso * 100) / tTotal;

		ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
				new PieChart.Data("Uso CPU", porcentajeUsoCpu), new PieChart.Data("Tiempo Ocioso", porcentajeTOcioso));

		final PieChart chart = new PieChart(data);
		chart.setTitle("% Utilización CPU");

		/*
		 * BAR CHART TIEMPO RETORNO TIEMPO ESPERA
		 * 
		 */
		final CategoryAxis xProceso = new CategoryAxis();
		xProceso.setLabel("Procesos");
		final NumberAxis yTiempo = new NumberAxis();
		yTiempo.setLabel("Tiempo");
		final BarChart<String, Number> bar = new BarChart<String, Number>(xProceso, yTiempo);

		XYChart.Series seriesTR = new XYChart.Series();
		seriesTR.setName("Tiempo Retorno");

		XYChart.Series seriesTE = new XYChart.Series();
		seriesTE.setName("Tiempo Espera");

		/*
		 * ARMO EL GRAFICO DE BARRAS
		 * 
		 */
		int tRetornoTotal = 0; // Sumatoria t retorno
		int tEsperaTotal = 0; // Sumatoria t espera

		for (int i = 1; i < salida.length; i++) {

			int retorno = salida[i] - arribo[i];
			tRetornoTotal += retorno;
			seriesTR.getData().add(new XYChart.Data("" + i, retorno));

			int espera = retorno - irrupcion[i];
			tEsperaTotal += espera;
			seriesTE.getData().add(new XYChart.Data("" + i, espera));

		}

		bar.getData().addAll(seriesTR, seriesTE);

		/*
		 * CALCULO LOS TIEMPOS DE RETORNO Y ESPERA PROMEDIOS
		 * 
		 */
		int n = salida.length - 1;
		double tRetornoPromedio = (double) tRetornoTotal / n;
		double tEsperaPromedio = (double) tEsperaTotal / n;
		bar.setTitle("TRP = " + tRetornoPromedio + " seg. - TEP = " + tEsperaPromedio + " seg.");

		/*
		 * LAYOUT
		 * 
		 */
		HBox hbox = new HBox();
		hbox.getChildren().addAll(chart, bar);
		hbox.setHgrow(chart, Priority.ALWAYS);
		hbox.setHgrow(bar, Priority.ALWAYS);

		Scene scene = new Scene(hbox, 800, 400);

		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setTitle(titulo);
		stage.show();

	}

}
