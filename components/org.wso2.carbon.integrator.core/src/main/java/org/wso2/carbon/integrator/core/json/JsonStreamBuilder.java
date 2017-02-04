/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.integrator.core.json;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.builder.Builder;
import org.apache.axis2.context.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.integrator.core.Utils;

import java.io.InputStream;
import java.lang.reflect.Method;

public class JsonStreamBuilder implements Builder {

    private Method synapseBuilder_processDocumentMethod;
    private Method axis2GsonBuilder_processDocumentMethod;
    private Object synapseBuilder;
    private Object axis2GsonBuilder;

    private static final Log logger = LogFactory.getLog(JsonStreamBuilder.class.getName());

    public JsonStreamBuilder()
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        Class<?> synapseBuilderClass = JsonStreamBuilder.class.getClassLoader().loadClass(Utils.getPassThroughJsonBuilder());
        this.synapseBuilder = synapseBuilderClass.newInstance();
        this.synapseBuilder_processDocumentMethod = synapseBuilderClass.getMethod("processDocument", InputStream.class, String.class, MessageContext.class);
        Class<?> axis2GsonBuilderClass = JsonStreamBuilder.class.getClassLoader().loadClass(Utils.getDSSJsonBuilder());
        this.axis2GsonBuilder = axis2GsonBuilderClass.newInstance();
        this.axis2GsonBuilder_processDocumentMethod = axis2GsonBuilderClass.getMethod("processDocument", InputStream.class, String.class, MessageContext.class);
    }

    public OMElement processDocument(InputStream inputStream, String s, MessageContext messageContext)
            throws AxisFault {
        try {
            if (Utils.isDataService(messageContext)) {
                return (OMElement) axis2GsonBuilder_processDocumentMethod.invoke(axis2GsonBuilder, inputStream, s, messageContext);
            } else {
                return (OMElement) synapseBuilder_processDocumentMethod.invoke(synapseBuilder, inputStream, s, messageContext);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new AxisFault(e.getMessage());
        }
    }
}
