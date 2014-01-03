package org.fenixedu.bennu.scheduler.rest.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.rest.json.DomainObjectViewer;
import org.fenixedu.bennu.io.domain.FileSupport;
import org.fenixedu.bennu.io.domain.LocalFileSystemStorage;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;
import org.fenixedu.commons.json.JsonBuilder;
import org.fenixedu.commons.json.JsonViewer;

import com.google.common.collect.FluentIterable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(SchedulerSystem.class)
public class SchedulerSystemAdapter implements JsonViewer<SchedulerSystem> {

    @Override
    public JsonElement view(SchedulerSystem obj, JsonBuilder ctx) {
        final JsonObject json = new JsonObject();
        json.addProperty("running", SchedulerSystem.isRunning());
        json.add("loggingStorage", ctx.view(obj.getLoggingStorage(), LocalFileSystemStorage.class, DomainObjectViewer.class));
        json.add(
                "availableStorages",
                ctx.view(FluentIterable.from(FileSupport.getInstance().getFileStorageSet()).filter(LocalFileSystemStorage.class)
                        .toSet()));
        return json;
    }
}
