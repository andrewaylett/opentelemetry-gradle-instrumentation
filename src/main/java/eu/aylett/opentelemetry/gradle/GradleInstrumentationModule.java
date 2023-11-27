package eu.aylett.opentelemetry.gradle;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.List;

@AutoService(InstrumentationModule.class)
class GradleInstrumentationModule extends InstrumentationModule {
  public GradleInstrumentationModule() {
    super("gradle");
  }
  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    return List.of(new GradleInstrumentation());
  }

  @Override
  public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
    return AgentElementMatchers.hasClassesNamed("org.gradle.execution.plan.LocalTaskNode");
  }
}
