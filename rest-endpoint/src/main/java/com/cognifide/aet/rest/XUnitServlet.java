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
package com.cognifide.aet.rest;

import com.google.common.base.Charsets;
import com.google.common.net.MediaType;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.exceptions.RestServiceException;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.StorageException;
import com.cognifide.aet.xunit.MetadataToXUnitConverter;
import com.cognifide.aet.xunit.model.Testsuites;

import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

@Service
@Component(label = "XUnitServlet", description = "Provides xunit test result", immediate = true)
public class XUnitServlet extends BasicDataServlet {

  private static final Logger LOGGER = LoggerFactory.getLogger(XUnitServlet.class);

  private static final long serialVersionUID = 2459345654081429533L;

  private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n";

  @Reference
  private MetadataDAO metadataDAO;

  @Activate
  public void start() {
    register(Helper.getXUnitPath());
  }

  @Deactivate
  public void stop() {
    unregister(Helper.getXUnitPath());
  }


  @Override
  protected void process(DBKey dbKey, HttpServletRequest request, HttpServletResponse response) throws IOException {
    final String correlationId = request.getParameter(Helper.CORRELATION_ID_PARAM);
    final String suiteName = request.getParameter(Helper.SUITE_PARAM);
    response.setCharacterEncoding(Charsets.UTF_8.toString());

    try {
      final Suite suite = getSuite(dbKey, correlationId, suiteName);
      generateXUnitAndRespondWithIt(dbKey, response, suite);
    } catch (RestServiceException e) {
      LOGGER.error("Failed to process request!", e);
      response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
      response.getWriter().write(responseAsJson(e.getMessage()));
    }
  }

  private Suite getSuite(DBKey dbKey, String correlationId, String suiteName) throws RestServiceException {
    final Suite suite;
    try {
      if (isValidCorrelationId(correlationId)) {
        suite = metadataDAO.getSuite(dbKey, correlationId);
      } else if (isValidName(suiteName)) {
        suite = metadataDAO.getLatestRun(dbKey, suiteName);
      } else {
        throw new RestServiceException("Neither valid correlationId or suite param was specified.");
      }
    } catch (StorageException e) {
      throw new RestServiceException(String.format("Failed to get suite: %s", e.getMessage()));
    }
    return suite;
  }

  private void generateXUnitAndRespondWithIt(DBKey dbKey, HttpServletResponse response, Suite suite) throws IOException {
    final MetadataToXUnitConverter converter = new MetadataToXUnitConverter(suite);

    try (InputStream result = generateXML(converter.convert())) {
      response.setContentType(MediaType.APPLICATION_XML_UTF_8.toString());
      response.getWriter().write(IOUtils.toString(result));
    } catch (IOException | JAXBException e) {
      LOGGER.error("Fatal exception while generating xUnit xml", e);
      response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
      response.getWriter().write(responseAsJson("Unable to get xUnit for %s", dbKey.toString()));
    }
  }

  private InputStream generateXML(Testsuites xUnitModel) throws JAXBException, IOException {
    Writer writer = new StringWriter();

    writer.write(XML_HEADER);
    prepareJaxbMarshaller().marshal(xUnitModel, writer);
    return IOUtils.toInputStream(writer.toString(), Charsets.UTF_8);
  }

  private Marshaller prepareJaxbMarshaller() throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(Testsuites.class);
    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
    return jaxbMarshaller;
  }
}
