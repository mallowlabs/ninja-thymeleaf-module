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

package ninja.thymeleaf;

import ninja.i18n.Messages;
import ninja.thymeleaf.template.TemplateEngineThymeleafI18nMessageResolver;
import ninja.utils.NinjaProperties;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.StandardTemplateModeHandlers;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Instance provider for TemplateEngine class.
 */
public class ThymeleafEngineProvider implements Provider<TemplateEngine> {

    private final Messages messages;
    private final NinjaProperties ninjaProperties;

    @Inject
    public ThymeleafEngineProvider(Messages messages, NinjaProperties ninjaProperties) {
        this.messages = messages;
        this.ninjaProperties = ninjaProperties;
    }

    @Override
    public TemplateEngine get() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(StandardTemplateModeHandlers.HTML5.getTemplateModeName());
        templateResolver.setCacheable(ninjaProperties.isProd());
        templateResolver.setCacheTTLMs(60000L);
        templateResolver.setCharacterEncoding("utf-8");

        TemplateEngine engine = new TemplateEngine();
        engine.setMessageResolver(new TemplateEngineThymeleafI18nMessageResolver(messages));
        engine.setTemplateResolver(templateResolver);
        return engine;
    }
}
