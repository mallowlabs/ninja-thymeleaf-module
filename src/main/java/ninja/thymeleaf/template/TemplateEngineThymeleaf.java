/**
 * Copyright (C) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.thymeleaf.template;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Lang;
import ninja.i18n.Messages;
import ninja.template.TemplateEngineManager;
import ninja.thymeleaf.exception.NinjaExceptionHandler;
import ninja.thymeleaf.util.ThymeleafConstant;
import ninja.thymeleaf.util.ThymeleafHelper;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;

import org.slf4j.Logger;
import org.thymeleaf.TemplateEngine;

import com.google.common.base.Optional;
import com.google.inject.Inject;

/**
 * Render Ninja with Thymeleaf template engine (http://www.thymeleaf.org/).
 */

public class TemplateEngineThymeleaf implements ninja.template.TemplateEngine {

    private final String FILE_SUFFIX = ".html";

    private final Messages messages;

    private final Lang lang;

    private final ThymeleafHelper thymeleafHelper;

    private final NinjaExceptionHandler exceptionHandler;

    private final Logger logger;

    private final TemplateEngine engine;

    @Inject
    public TemplateEngineThymeleaf(Messages messages, Lang lang, Logger ninjaLogger, NinjaExceptionHandler exceptionHandler, ThymeleafHelper thymeleafHelper,
            TemplateEngineManager templateEngineManager, NinjaProperties ninjaProperties, TemplateEngine engine) throws Exception {

        this.messages = messages;
        this.lang = lang;
        this.logger = ninjaLogger;
        this.exceptionHandler = exceptionHandler;
        this.thymeleafHelper = thymeleafHelper;
        this.engine = engine;
    }

    @Override
    public void invoke(Context context, Result result) {
        ResponseStreams responseStreams = context.finalizeHeaders(result);

        // set language from framework. You can access
        // it in the templates as @lang
        Optional<String> language = lang.getLanguage(context, Optional.of(result));

        Map<String, Object> renderArgs = getDefaultRenderArgs(context, result, language);

        String templateName = thymeleafHelper.getThymeleafTemplateForResult(context.getRoute(), result, FILE_SUFFIX);

        // Specify the data source where the template files come from.
        // Here I set a file directory for it:
        try {
            Locale locale = Locale.getDefault();

            if (language.isPresent()) {
                locale = new Locale(language.get());
            }

            org.thymeleaf.context.Context ctx = new org.thymeleaf.context.Context(locale, renderArgs);
            PrintWriter writer = new PrintWriter(responseStreams.getWriter());
            engine.process(templateName, ctx, writer);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            logger.error("failed to render Thymeleaf", e);
            handleServerError(context, e);
        }
    }

    private Map<String, Object> getDefaultRenderArgs(Context context, Result result, Optional<String> language) {
        Map<String, Object> renderArgs = new HashMap<String, Object>();
        if (language.isPresent()) {

            renderArgs.put("lang", language.get());
        }

        // put all entries of the session cookie to the map.
        // You can access the values by their key in the cookie
        // For eg: @session.get("key")
        if (!context.getSession().isEmpty()) {
            renderArgs.put("session", context.getSession().getData());
        }

        renderArgs.put("contextPath", context.getContextPath());

        // /////////////////////////////////////////////////////////////////////
        // Convenience method to translate possible flash scope keys.
        // !!! If you want to set messages with placeholders please do that
        // !!! in your controller. We only can set simple messages.
        // Eg. A message like "errorMessage=my name is: {0}" => translate in
        // controller and pass directly.
        // A message like " errorMessage=An error occurred" => use that as
        // errorMessage.
        //
        // get flash values like @flash.get("key")
        // ////////////////////////////////////////////////////////////////////

        Map<String, String> flash = new HashMap<String, String>();

        for (Entry<String, String> entry : context.getFlashScope().getCurrentFlashCookieData().entrySet()) {

            String messageValue = null;

            Optional<String> messageValueOptional = messages.get(entry.getValue(), context, Optional.of(result));

            if (!messageValueOptional.isPresent()) {
                messageValue = entry.getValue();
            } else {
                messageValue = messageValueOptional.get();
            }

            flash.put(entry.getKey(), messageValue);
        }
        renderArgs.put("flash", flash);
        return renderArgs;
    }

    private void handleServerError(Context context, Exception e) {
        ResponseStreams outStream = context.finalizeHeaders(Results.internalServerError());
        String html = engine.process(ThymeleafConstant.LOCATION_VIEW_HTML_INTERNAL_SERVER_ERROR, new org.thymeleaf.context.Context());
        exceptionHandler.handleException(e, html, outStream);
    }

    @Override
    public String getContentType() {
        return "text/html";
    }

    @Override
    public String getSuffixOfTemplatingEngine() {
        return FILE_SUFFIX;
    }
}
