/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dubbo.gateway.provider;

import org.apache.dubbo.common.deploy.ApplicationDeployListener;
import org.apache.dubbo.rpc.model.ApplicationModel;

import static org.apache.dubbo.common.constants.CommonConstants.SystemProperty.SYSTEM_BYTE_ACCESSOR_KEY;

public class ConfigDeployListener implements ApplicationDeployListener {

    @Override
    public void onInitialize(ApplicationModel scopeModel) {
        System.setProperty(SYSTEM_BYTE_ACCESSOR_KEY, "snf");
    }

    @Override
    public void onStarting(ApplicationModel scopeModel) {

    }

    @Override
    public void onStarted(ApplicationModel scopeModel) {
    }

    @Override
    public void onStopping(ApplicationModel scopeModel) {

    }

    @Override
    public void onStopped(ApplicationModel scopeModel) {

    }

    @Override
    public void onFailure(ApplicationModel scopeModel, Throwable cause) {

    }
}
