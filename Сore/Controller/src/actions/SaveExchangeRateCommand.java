package actions;

import annotation.Action;
import annotation.ContextAnnotation;
import annotation.Parameter;
import app.AbstractAction;
import entity.ExchangeRate;

import java.io.*;
import java.util.List;

@Action
@ContextAnnotation(list = {
        @Parameter(key = "exchangeRate", type = ExchangeRate.class),
        @Parameter(key = "storageFilePath"),
        @Parameter(key = "reUpdate", type = Boolean.class),
        @Parameter(key = "currencyList", type = List.class)
})
public class SaveExchangeRateCommand extends AbstractAction<Void, Void> {

    private ExchangeRate rate;

    @Override
    public Void call() throws Exception {
        File targetFile = new File((String) context.getValue("storageFilePath"));
        StringBuilder result = new StringBuilder();
        rate = (ExchangeRate) context.getValue("exchangeRate");
        if (!targetFile.exists()) {
            createTargetFile(targetFile);
            firstDataUpdate(targetFile);
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(targetFile))) {
            result.append(reader.readLine()).append("\n").append(reader.readLine()).append("\n\t");
            String countTag = reader.readLine();
            if ((Boolean) context.getValue("reUpdate") == true) {
                int skipCount = getLastUpdateCount(countTag);
                for (int i = 0; i < skipCount + 2; i++) {
                    reader.readLine();
                }
            }
            result.append(appendNewData());
            String s;
            while ((s = reader.readLine()) != null)
                result.append(s).append("\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
        writeAllDataToFile(targetFile, result.toString());
        return null;
    }

    private int getLastUpdateCount(String row) {
        return Integer.parseInt(row.substring(row.indexOf(">") + 1, row.lastIndexOf("<")));
    }

    private void createTargetFile(File f) {
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String setLastUpdateCount() {
        int count = (int) Math.ceil(((ExchangeRate) context.getValue("exchangeRate")).getRate().size() / 2.0);
        return String.format("<lastUpdateCount>%d</lastUpdateCount>", count);
    }

    private void firstDataUpdate(File targetFile) {
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\"?>\n" +
                "<currencyHistory>\n\t").append(appendNewData()).append("</currencyHistory>");
        writeAllDataToFile(targetFile, builder.toString());
    }

    private void writeAllDataToFile(File targetFile, String data) {
        try (PrintWriter writer = new PrintWriter(targetFile)) {
            targetFile.createNewFile();
            writer.write(data);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String appendNewData() {
        StringBuilder result = new StringBuilder();
        result.append(setLastUpdateCount().concat("\n\t"));
        result.append(String.format("<exchangeRate date=\"%s\">\n\t\t", rate.getUpdateDate().toString()));
        List<String> currencyList = (List<String>) context.getValue("currencyList");
        for (String currency : currencyList) {
            Double buy = rate.getRate().get(currency + "#buy");
            Double sale = rate.getRate().get(currency + "#sale");
            if (buy != null && sale != null)
                result.append("<").append(currency).append(" buy=\"").
                        append(String.format("%.3f", buy)).append(String.format("\" sale=\"%.3f",sale)).append("\"/>\n\t\t");
        }
        result.append("</exchangeRate>\n");
//        result.delete(result.length() - 1, result.length());
        return result.toString();
    }
}
