package ninja.thymeleaf.util;

import ninja.Result;
import ninja.Route;

/**
 * Helper methods for Thymeleaf engines
 */
public class ThymeleafHelper {

    public String getThymeleafTemplateForResult(Route route, Result result, String suffix) {
        if (result.getTemplate() == null) {
            Class<?> controller = route.getControllerClass();

            // and the final path of the controller will be something like:
            // /some/package/submoduleName/ControllerName/templateName.html
            return String.format("/%s/%s%s", controller.getSimpleName(), route.getControllerMethod().getName(), suffix);
        } else {
            return result.getTemplate();
        }
    }

}