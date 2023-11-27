@file:Suppress("UnstableApiUsage")

plugins {
  `java-library`
  `jvm-test-suite`
  id("eu.aylett.conventions") version "0.3.0"
  id("eu.aylett.plugins.version") version "0.3.0"
}

group = "eu.aylett"
version = aylett.versions.gitVersion()

repositories {
  mavenCentral()
}

dependencies {
  implementation(platform("io.opentelemetry:opentelemetry-bom:1.32.0"))
  implementation(platform("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:1.32.0"))
  implementation(platform("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom-alpha:1.32.0-alpha"))

  implementation("com.google.guava:guava:32.1.3-jre")
  implementation("io.opentelemetry:opentelemetry-api:1.32.0")
  implementation("io.opentelemetry.semconv:opentelemetry-semconv:1.23.1-alpha")
  implementation("org.slf4j:slf4j-api:2.0.9")

  compileOnly("net.bytebuddy:byte-buddy:1.14.10")

  compileOnly("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure-spi")
  compileOnly("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api")
  compileOnly("io.opentelemetry.javaagent:opentelemetry-javaagent-extension-api") {
    exclude(group = "net.bytebuddy")
  }
  compileOnly(gradleApi())

  //Provides @AutoService annotation that makes registration of our SPI implementations much easier
  compileOnly("com.google.auto.service:auto-service:1.1.1")
  annotationProcessor("com.google.auto.service:auto-service:1.1.1")

//  testImplementation(kotlin("test"))
}

testing {
  suites {
    withType<JvmTestSuite>().configureEach {
      useJUnitJupiter("5.10.0")
      dependencies {
        implementation("org.hamcrest:hamcrest:2.2")
      }
    }
  }
}

tasks.named<Jar>("jar") {
  manifest {
    attributes("Automatic-Module-Name" to "eu.aylett.opentelemetry.gradle")
  }
}
