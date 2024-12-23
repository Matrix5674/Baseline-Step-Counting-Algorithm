import Plot.PlotWindow;
import Plot.ScatterPlot;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        double rmse = testAllFiles("2024/");

        System.out.println("The mean error is: " + rmse + " steps.");

    }


    public static ScatterPlot createGraph(ScatterPlot plt, int index, String color, ArrayList<Double> x, ArrayList<Double> y, ArrayList<Double> z){

        for (int line = 200; line < 1000; line++) {
            double xValue = x.get(line);
            double yValue = y.get(line);
            double zValue = z.get(line);
            double distance = Math.sqrt(  ( (xValue*xValue) + (yValue*yValue) + (zValue*zValue) )  );
            plt.plot(index, line, distance).strokeColor(color).strokeWeight(2).style("-");
        }
        ArrayList<Double> peaks = findPeaks(x, y, z);
        for (int p = 0; p < peaks.size(); p++){
            plt.plot(index+4, p, peaks.get(p)).strokeColor("black").strokeWeight(5).style(".");
        }

        return plt;
    }

    public static ArrayList<Double> findPeaks(ArrayList<Double> x, ArrayList<Double> y, ArrayList<Double> z){
        ArrayList<Double> peaks = new ArrayList<>();
        for (int line = 1; line < x.size()-1; line++) {
            double distance = Math.sqrt(  ( (x.get(line)*x.get(line)) + (y.get(line)*y.get(line)) + (z.get(line)*z.get(line)) )  );
            double previousDistance = Math.sqrt(  ( (x.get(line-1)*x.get(line-1)) + (y.get(line-1)*y.get(line-1)) + (z.get(line-1)*z.get(line-1)) )  );
            double nextDistance = Math.sqrt(  ( (x.get(line+1)*x.get(line+1)) + (y.get(line+1)*y.get(line+1)) + (z.get(line+1)*z.get(line+1)) )  );

            if (previousDistance < distance && nextDistance < distance){
                peaks.add(distance);
            }
        }
        return peaks;
    }









    public static void graphValues(ArrayList<Double> xAcc, ArrayList<Double> yAcc, ArrayList<Double> zAcc, ArrayList<Double> gryoX, ArrayList<Double> gyroY, ArrayList<Double> gyroZ){
        ScatterPlot plt = new ScatterPlot(100,100, 1100, 700);
        plt = createGraph(plt, 0, "red", xAcc, yAcc, zAcc);
        plt = createGraph(plt, 1, "blue", gryoX, gyroY, gyroZ);
        PlotWindow window = PlotWindow.getWindowFor(plt, 2000, 800);
        window.show();

    }

    public static ArrayList<Double> extractColumnData(ArrayList<String> lines, int col){
        ArrayList<Double> columnData = new ArrayList<>();

        for (int line = 1; line < lines.size(); line++) {
            String l = lines.get(line).split(",")[col];
            double data = Double.parseDouble(l);
            columnData.add(data);
        }
        for (int i = 0; i < 30; i++){
            columnData = smoothData(columnData);
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

    public static ArrayList<Double> smoothData(ArrayList<Double> column) {
        ArrayList<Double> smoothed = new ArrayList<>();
        for (int i = 1; i < column.size()-1; i++) {
            double sum = column.get(i) + column.get(i-1) + column.get(i+1);
            double average = sum/3;


            smoothed.add(average);


        }
        return smoothed;
    }

    private static double testAllFiles(String folderPath) {
        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(folderPath), "*.csv");
            double errorSum = 0;
            int numFiles = 0;

            System.out.printf("%-50s %-10s %-10s %-10s%n", "file name", "predicted", "actual", "error");
            for (Path filePath : directoryStream) {
                String fileName = filePath.toString();
                ArrayList<String> content = readFile(fileName);

                int predictedSteps = countSteps(content);  //    ← this is your method

                int realSteps = getRealStepsFromFilename(filePath);
                numFiles++;

                int error = realSteps - predictedSteps;
                errorSum += error * error;       // to calcualte mean *squared* error

                System.out.printf("%-50s %-10s %-10s %-10s%n",filePath,predictedSteps, realSteps, error);
            }

            double meanSquaredError = Math.sqrt(errorSum / numFiles);
            return meanSquaredError;
        } catch (IOException e) {
            System.err.println("Error reading files: " + e.getMessage());
        }

        return -1;
    }

    private static int getRealStepsFromFilename(Path filePath) {
        String filename = filePath.getFileName().toString().toLowerCase();
        String numString = filename.replaceAll("[^\\d]", "");
        int realSteps = Integer.parseInt(numString);
        return realSteps;
    }

    private static int countSteps(ArrayList<String> content) {
        // you do this!  This is your code that predicts the number of steps.
        ArrayList<Double> xAcc = extractColumnData(content, 0);
        ArrayList<Double> yAcc = extractColumnData(content, 1);
        ArrayList<Double> zAcc = extractColumnData(content, 2);

        return findPeaks(xAcc, yAcc, zAcc).size();
    }


}
