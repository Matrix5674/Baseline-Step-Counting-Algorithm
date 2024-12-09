import Plot.PlotWindow;
import Plot.ScatterPlot;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String PATH = "Assets/1-200-step-regular.csv";
        ArrayList<String> lines = readFile(PATH);

        ArrayList<Double> xAcc = extractColumnData(lines, 0);
        ArrayList<Double> yAcc = extractColumnData(lines, 1);
        ArrayList<Double> zAcc = extractColumnData(lines, 2);
        ArrayList<Double> gyroX = extractColumnData(lines, 3);
        ArrayList<Double> gyroY = extractColumnData(lines, 4);
        ArrayList<Double> gyroZ = extractColumnData(lines, 5);

        graphValues(xAcc, yAcc, zAcc, gyroX, gyroY, gyroZ);

    }

    public static void graphValues(ArrayList<Double> xAcc, ArrayList<Double> yAcc, ArrayList<Double> zAcc, ArrayList<Double> gryoX, ArrayList<Double> gyroY, ArrayList<Double> gyroZ){
        ScatterPlot plt = new ScatterPlot(100,100, 1100, 700);
        plt = createGraph(plt, 0, "red", xAcc, yAcc, zAcc);
        plt = createGraph(plt, 1, "blue", gryoX, gyroY, gyroZ);
        PlotWindow window = PlotWindow.getWindowFor(plt, 2000, 800);
        window.show();

    }

    public static ScatterPlot createGraph(ScatterPlot plt, int index, String color, ArrayList<Double> x, ArrayList<Double> y, ArrayList<Double> z){

        for (int line = 200; line < 1000; line++) {
            double xValue = x.get(line);
            double yValue = y.get(line);
            double zValue = z.get(line);
            double distance = Math.sqrt(  ( (xValue*xValue) + (yValue*yValue) + (zValue*zValue) )  );
            plt.plot(index, line, distance).strokeColor(color).strokeWeight(2).style("-");
        }
        for (int line = 200; line < 1000; line++) {
            double distance = Math.sqrt(  ( (x.get(line)*x.get(line)) + (y.get(line)*y.get(line)) + (z.get(line)*z.get(line)) )  );
            double previousDistance = Math.sqrt(  ( (x.get(line-1)*x.get(line-1)) + (y.get(line-1)*y.get(line-1)) + (z.get(line-1)*z.get(line-1)) )  );
            double nextDistance = Math.sqrt(  ( (x.get(line+1)*x.get(line+1)) + (y.get(line+1)*y.get(line+1)) + (z.get(line+1)*z.get(line+1)) )  );

            if (previousDistance < distance && nextDistance < distance){
                plt.plot(index+4, line, distance).strokeColor("black").strokeWeight(5).style(".");
            }
        }

        return plt;
    }

    public static ArrayList<Double> extractColumnData(ArrayList<String> lines, int col){
        ArrayList<Double> columnData = new ArrayList<>();

        for (int line = 1; line < lines.size(); line++) {
            String l = lines.get(line).split(",")[col];
            double data = Double.parseDouble(l);
            columnData.add(data);
        }

        return columnData;

    }
    public static ArrayList<String> readFile (String PATH){
        ArrayList<String> lines = new ArrayList<>();
        try {
            List<String> contents = Files.readAllLines(Paths.get(PATH));
            lines.addAll(contents);
        } catch (IOException e){
            e.printStackTrace();
        }
        return lines;
    }

}
