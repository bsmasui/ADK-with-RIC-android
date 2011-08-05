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

import java.util.ArrayList;
import java.util.List;

import jp.co.brilliantservice.android.ric.converter.Converter;

/**
 * @author masui@brilliantservice.co.jp
 * 
 */
public class MultipleEntry<T> implements Entry<List<T>> {

  private String name;
  private List<T> value;
  private Converter<T> converter;

  public MultipleEntry(String name, Converter<T> converter) {
    this.name = name;
    this.converter = converter;
    this.value = new ArrayList<T>();
  }

  public MultipleEntry(String name, List<T> defaultValue, Converter<T> converter) {
    this.name = name;
    this.value = defaultValue;
    this.converter = converter;
    this.value = new ArrayList<T>();
  }

  public void apply(String value) {
    this.value.add(converter.convert(value));
  }

  /*
   * (non-Javadoc)
   * 
   * @see jp.co.brilliantservice.android.robot.project.Entry#getName()
   */
  public String getName() {
    return name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see jp.co.brilliantservice.android.robot.project.Entry#val()
   */
  public List<T> val() {
    return value;
  }
}
