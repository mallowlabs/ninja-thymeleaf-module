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

import static org.mockito.Mockito.when;
import ninja.i18n.Messages;
import ninja.thymeleaf.template.TemplateEngineThymeleafI18nMessageResolver;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Optional;

@RunWith(MockitoJUnitRunner.class)
public class TemplateEngineThymeleafI18nMessageResolverTest {

    @Mock
    Messages messages;

    @Test
    public void testResolveMessage() {
        TemplateEngineThymeleafI18nMessageResolver resolver = new TemplateEngineThymeleafI18nMessageResolver(messages);

        Optional<String> lang = Optional.absent();
        when(messages.get(Mockito.eq("key"), Mockito.eq(lang), Mockito.eq("arg1"))).thenReturn(Optional.of("i18n-Message"));
        Assert.assertEquals("i18n-Message", resolver.resolveMessage(null, "key", new Object[] { "arg1" }).getResolvedMessage());
    }
}
