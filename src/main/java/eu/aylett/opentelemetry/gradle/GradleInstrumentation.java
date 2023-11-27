package eu.aylett.opentelemetry.gradle;

import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.gradle.execution.plan.Node;

import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;


class GradleInstrumentation implements TypeInstrumentation {
  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return AgentElementMatchers.hasSuperType(named("org.gradle.execution.plan.LocalTaskNodeExecutor"));
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer
      .applyAdviceToMethod(
        named("execute")
          .and(isPublic())
          .and(takesArgument(0, Node.class))
          .and(takesArguments(2)),
        GradleAdvice.class.getCanonicalName());
  }
}
