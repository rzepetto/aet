/**
 * Automated Exploratory Tests
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.job.common.datafilters.extractelement;

import com.cognifide.aet.job.api.datafilter.DataFilterFactory;
import com.cognifide.aet.job.api.datafilter.DataFilterJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

import java.util.Map;

/**
 * @author magdalena.biala
 *
 */
@Component
@Service
public class ExtractElementDataFilterFactory implements DataFilterFactory {

  @Override
  public String getName() {
    return ExtractElementDataModifier.NAME;
  }

  @Override
  public DataFilterJob<String> createInstance(Map<String, String> params) throws ParametersException {
    ExtractElementDataModifier dm = new ExtractElementDataModifier();
    dm.setParameters(params);
    return dm;
  }
}
