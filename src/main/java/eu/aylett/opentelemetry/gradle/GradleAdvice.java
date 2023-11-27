package eu.aylett.opentelemetry.gradle;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge;
import net.bytebuddy.asm.Advice;
import org.gradle.execution.plan.LocalTaskNode;
import org.gradle.execution.plan.Node;

import javax.annotation.Nullable;

import static eu.aylett.opentelemetry.gradle.GradleInstrumenter.INSTRUMENTER;

@SuppressWarnings({"ReassignedVariable", "ParameterCanBeLocal"})
public class GradleAdvice {
  @Advice.OnMethodEnter(suppress = Throwable.class)
  public static void onEnter(@Advice.Argument(value = 0) Node node,
    @Advice.Local("otelContext") @Nullable Context context,
    @Advice.Local("otelScope") @Nullable Scope scope) {
    if (node instanceof LocalTaskNode n) {
      var parentContext = Java8BytecodeBridge.currentContext();
      if (!INSTRUMENTER.shouldStart(parentContext, n.getTask())) {
        return;
      }

      context = INSTRUMENTER.start(parentContext, ((LocalTaskNode) node).getTask());
      //noinspection UnusedAssignment
      scope = context.makeCurrent();
    }
  }

  @Advice.OnMethodExit(suppress = Throwable.class)
  public static void onExit(@Advice.Local("otelScope") @Nullable Scope scope) {
    if (scope != null) {
        scope.close();
    }
  }
}
