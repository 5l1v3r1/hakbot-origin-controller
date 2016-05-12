/*
 * This file is part of Hakbot Origin Controller.
 *
 * Hakbot Origin Controller is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Hakbot Origin Controller is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Hakbot Origin Controller. If not, see http://www.gnu.org/licenses/.
 */
package io.hakbot.publishers.threadfix;

import com.denimgroup.threadfix.data.entities.Scan;
import com.denimgroup.threadfix.remote.ThreadFixRestClientImpl;
import com.denimgroup.threadfix.remote.response.RestResponse;
import io.hakbot.controller.logging.Logger;
import io.hakbot.controller.model.Job;
import io.hakbot.controller.plugin.RemoteInstance;
import io.hakbot.controller.plugin.RemoteInstanceAutoConfig;
import io.hakbot.providers.Provider;
import io.hakbot.publishers.BasePublisher;
import io.hakbot.util.PayloadUtil;
import org.apache.commons.collections.MapUtils;
import java.io.File;
import java.util.Map;

public class ThreadFixPublisher extends BasePublisher {

    // Setup logging
    private static final Logger logger = Logger.getLogger(ThreadFixPublisher.class);

    private static Map<String, RemoteInstance> instanceMap = new RemoteInstanceAutoConfig().createMap(Type.PROVIDER, "threadfix");

    private RemoteInstance remoteInstance;
    private String appId;

    @Override
    public boolean initialize(Job job, Provider provider) {
        super.initialize(job, provider);

        Map<String, String> params = PayloadUtil.toParameters(job.getPublisherPayload());
        if (!PayloadUtil.requiredParams(params, "appId")) {
            job.addMessage("Invalid request. Expected parameter: [appId]");
            return false;
        }
        remoteInstance = instanceMap.get(MapUtils.getString(params, "instance"));
        if (remoteInstance == null) {
            return false;
        }
        appId = MapUtils.getString(params, "appId");
        return true;
    }

    public boolean publish(Job job) {
        File report = getReport(new File(System.getProperty("java.io.tmpdir")));
        if (report == null) {
            return false;
        }
        final ThreadFixRestClientImpl client = new ThreadFixRestClientImpl(remoteInstance.getUrl(), remoteInstance.getApiKey());
        final RestResponse<Scan> uploadResponse = client.uploadScan(appId, report.getAbsolutePath());
        return uploadResponse.success;
    }

    public String getName() {
        return "ThreadFix";
    }

    public String getDescription() {
        return "Publishes results to ThreadFix Community or Enterprise edition for vulnerability aggregation and normalization.";
    }

}