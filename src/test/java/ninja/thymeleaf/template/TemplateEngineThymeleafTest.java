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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Properties;

import ninja.Context;
import ninja.Result;
import ninja.Route;
import ninja.i18n.Lang;
import ninja.i18n.Messages;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.template.TemplateEngineManager;
import ninja.thymeleaf.ThymeleafEngineProvider;
import ninja.thymeleaf.exception.NinjaExceptionHandler;
import ninja.thymeleaf.util.ThymeleafHelper;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.thymeleaf.TemplateEngine;

import com.google.common.base.Optional;

@RunWith(MockitoJUnitRunner.class)
public class TemplateEngineThymeleafTest {

    @Mock
    Context contextRenerable;

    @Mock
    ResponseStreams responseStreams;

    @Mock
    NinjaProperties ninjaProperties;

    @Mock
    Messages messages;

    @Mock
    Lang lang;

    @Mock
    Logger ninjaLogger;

    @Mock
    NinjaExceptionHandler exceptionHandler;

    @Mock
    TemplateEngineManager templateEngineManager;

    @Mock
    ThymeleafHelper thymeleafHelper;

    @Mock
    Result result;

    @Mock
    Route route;

    @Mock
    TemplateEngine engine;

    @Mock
    Session cookie;

    @Mock
    FlashScope flashCookie;

    @Test
    public void testInvoke() throws Exception {
        Properties p = new Properties();
        p.setProperty("key", "value");
        when(ninjaProperties.getAllCurrentNinjaProperties()).thenReturn(p);

        TemplateEngine engine = new ThymeleafEngineProvider(messages, ninjaProperties).get();
        TemplateEngineThymeleaf thymeleaf = new TemplateEngineThymeleaf(messages, lang, ninjaLogger, exceptionHandler, thymeleafHelper, templateEngineManager, ninjaProperties, engine);

        when(contextRenerable.finalizeHeaders(Mockito.eq(result))).thenReturn(responseStreams);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(byteArrayOutputStream);
        when(responseStreams.getWriter()).thenReturn(writer);

        when(cookie.isEmpty()).thenReturn(true);
        when(contextRenerable.getSession()).thenReturn(cookie);

        when(flashCookie.getCurrentFlashCookieData()).thenReturn(new HashMap<String, String>());
        when(contextRenerable.getFlashScope()).thenReturn(flashCookie);
        when(contextRenerable.getRoute()).thenReturn(route);

        when(thymeleafHelper.getThymeleafTemplateForResult(Mockito.eq(route), Mockito.eq(result), Mockito.eq(".html"))).thenReturn("TemplateName");

        Optional<String> language = Optional.absent();
        when(lang.getLanguage(Mockito.eq(contextRenerable), Mockito.eq(Optional.of(result)))).thenReturn(language);

        thymeleaf.invoke(contextRenerable, result);

        assertEquals("Hellow world from Thymeleaf", byteArrayOutputStream.toString());
    }
}
