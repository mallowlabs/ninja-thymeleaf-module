package ninja.thymeleaf.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * A general exception handler for exceptions. Outputs a readable error in test
 * dev mode. - Outputs a error message with relevant status code in production.
 * @author ra, sojin
 */
@Singleton
public class NinjaExceptionHandler {

    private final NinjaProperties ninjaProperties;
    private final Logger logger;

    @Inject
    public NinjaExceptionHandler(Logger logger, NinjaProperties ninjaProperties) {
        this.logger = logger;
        this.ninjaProperties = ninjaProperties;
    }

    public void handleException(Exception te, String response, ResponseStreams outStream) {
        try {
            Writer out = outStream.getWriter();
            PrintWriter pw = (out instanceof PrintWriter) ? (PrintWriter) out : new PrintWriter(out);

            // TODO render with proper http status code.
            if (ninjaProperties.isProd()) {

                if (response.endsWith("html")) {
                    response = "Server error!";
                }

                pw.println(response);

                logger.log(Level.SEVERE, "Templating error. This should not happen in production", te);
            } else {
                // print out full stacktrace if we are in test or dev mode
                pw.println("<!-- Thymeleaf Template ERROR MESSAGE STARTS HERE -->"
                    + "<script language=javascript>//\"></script>"
                    + "<script language=javascript>//\'></script>"
                    + "<script language=javascript>//\"></script>"
                    + "<script language=javascript>//\'></script>"
                    + "</title></xmp></script></noscript></style></object>"
                    + "</head></pre></table>"
                    + "</form></table></table></table></a></u></i></b>"
                    + "<div align=left "
                    + "style='background-color:#FFFF00; color:#FF0000; "
                    + "display:block; border-top:double; padding:2pt; "
                    + "font-size:medium; font-family:Arial,sans-serif; "
                    + "font-style: normal; font-variant: normal; "
                    + "font-weight: normal; text-decoration: none; "
                    + "text-transform: none'>"
                    + "<b style='font-size:medium'>Thymeleaf template error!</b>"
                    + "<pre><xmp>");
                te.printStackTrace(pw);
                pw.println("</xmp></pre></div></html>");
                logger.log(Level.SEVERE, "Templating error.", te);
            }

            pw.flush();
            pw.close();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while handling error.", e);
        }
    }
}