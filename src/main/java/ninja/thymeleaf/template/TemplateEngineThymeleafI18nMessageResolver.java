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

import java.util.Locale;

import ninja.i18n.Messages;

import org.thymeleaf.Arguments;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.messageresolver.MessageResolution;

import com.google.common.base.Optional;

/**
 * I18n message resolver for Thymeleaf template
 */
public class TemplateEngineThymeleafI18nMessageResolver extends AbstractMessageResolver {

    final Messages messages;

    public TemplateEngineThymeleafI18nMessageResolver(Messages messages) {
        this.messages = messages;
    }

    @Override
    public MessageResolution resolveMessage(Arguments arguments, String key, Object[] messageParameters) {
        Locale locale = null;
        if (arguments != null) {
            locale = arguments.getContext().getLocale();
        }

        Optional<String> lang = Optional.absent();
        if (locale != null) {
            // to conform to rfc5646 and BCP 47
            lang = Optional.of(locale.toString().replace('_', '-'));
        }

        Optional<String> i18nMessage = Optional.absent();
        i18nMessage = messages.get(key, lang, messageParameters);

        if (!i18nMessage.isPresent()) {
            return null;
        }

        return new MessageResolution(i18nMessage.or(""));
    }

}
