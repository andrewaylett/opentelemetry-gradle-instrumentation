package eu.aylett.opentelemetry.gradle;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import org.gradle.api.Task;
import org.jetbrains.annotations.Nullable;

public class GradleInstrumenter {
  public static final Instrumenter<Task, Task> INSTRUMENTER = Instrumenter
    .<Task, Task>builder(GlobalOpenTelemetry.get(), "gradle", Task::getName)
    .setSpanStatusExtractor((spanStatusBuilder, request, response, error) -> {
      var state = request.getState();
      var failure = state.getFailure();
      if (error != null) {
        spanStatusBuilder.setStatus(StatusCode.ERROR, "Unhandled Exception: ${error.message}");
      } else if (failure != null) {
        spanStatusBuilder.setStatus(StatusCode.ERROR, "Failed: ${failure.message}");
      } else if (state.getSkipped()) {
        spanStatusBuilder.setStatus(StatusCode.OK, "Skipped: ${state.skipMessage}");
      } else if (state.getNoSource()) {
        spanStatusBuilder.setStatus(StatusCode.OK, "No Source");
      } else if (state.getUpToDate()) {
        spanStatusBuilder.setStatus(StatusCode.OK, "Up To Date");
      } else if

      (!state.getDidWork()) {
        spanStatusBuilder.setStatus(StatusCode.OK, "Did no work");
      } else {
        spanStatusBuilder.setStatus(StatusCode.OK);
      }
    })
    .addAttributesExtractor(new AttributesExtractor<>() {
      @Override
      public void onStart(AttributesBuilder attributes, Context parentContext, Task request) {
        if (request.getDescription() != null) {
          attributes.put(DESCRIPTION, request.getDescription());
        }
      }

      @Override
      public void onEnd(AttributesBuilder attributes,
        Context context,
        Task request,
        @Nullable Task o,
        @Nullable Throwable error) {
        // empty
      }
    })
    .buildInstrumenter();


  public static final AttributeKey<String> DESCRIPTION = AttributeKey.stringKey("description");
}
