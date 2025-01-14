package stallitium;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.*;

public class LOG {
    private static final Logger logger = Logger.getLogger(LOG.class.getName());

    static {
        try {
            //ファイルハンドラ
            FileHandler fileHandler = new FileHandler("server.log",true);
            fileHandler.setFormatter(new CustomFormatter());
            logger.addHandler(fileHandler);

            //コンソールハンドラ
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new CustomFormatter());
            logger.addHandler(consoleHandler);

            //重複するログを防止
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    private static class CustomFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();
            sb.append(new java.text.SimpleDateFormat("yyyy/MM/dd").format(new java.util.Date(record.getMillis())))
                    .append(" ")
                    .append(record.getLevel().getName())
                    .append(": ")
                    .append(formatMessage(record))
                    .append("\n");
            return sb.toString();
        }
    }


    //スタックトレース専用
    public static void logStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();

        for (String line : stackTrace.split(System.lineSeparator())) {
            logger.warning(line);
        }
    }

}
