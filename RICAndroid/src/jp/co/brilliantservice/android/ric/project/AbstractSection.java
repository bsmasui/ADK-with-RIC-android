/*
 * Copyright (C) 2011 BRILLIANTSERVICE Co., Ltd. & RT Corporation
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
package jp.co.brilliantservice.android.ric.project;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.co.brilliantservice.android.ric.converter.Converter;
import jp.co.brilliantservice.android.ric.converter.FloatConverter;
import jp.co.brilliantservice.android.ric.converter.IntegerConverter;
import jp.co.brilliantservice.android.ric.converter.StringConverter;

/**
 * @author masui@brilliantservice.co.jp
 * 
 */
public abstract class AbstractSection implements Section {

  private Map<String, Entry<?>> entries = new LinkedHashMap<String, Entry<?>>();

  public String[] getEntries() {
    return null;
  }

  public void parseLine(String name, String value) {
    Entry<?> e = entries.get(name);
    if (e == null)
      return;
    // throw new IllegalArgumentException(name + "=" + value);

    e.apply(value);
    // System.out.println(name + "=" + value);
  }

  public void onSectionStart() {
  }

  public void onSectionEnd() {
  }

  protected Entry<String> newStringEntry(String name) {
    Entry<String> e = new SingleEntry<String>(name, new StringConverter());
    entries.put(name, e);
    return e;
  }

  protected Entry<Integer> newIntEntry(String name) {
    Entry<Integer> e = new SingleEntry<Integer>(name, new IntegerConverter());
    entries.put(name, e);
    return e;
  }

  protected Entry<Float> newFloatEntry(String name) {
    Entry<Float> e = new SingleEntry<Float>(name, new FloatConverter());
    entries.put(name, e);
    return e;
  }

  protected <T> Entry<List<T>> newMultipleEntry(String name,
      Converter<T> converter) {
    Entry<List<T>> e = new MultipleEntry<T>(name, converter);
    entries.put(name, e);
    return e;
  }

  protected <T> Entry<T> newCustomeEntry(String name, Converter<T> converter) {
    throw new UnsupportedOperationException("not implemented");
  }
}
