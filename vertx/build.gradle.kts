import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  repositories {
    mavenLocal()
    maven {
      url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    maven {
      url = uri("https://plugins.gradle.org/m2/")
    }
    maven {
      url = uri("https://maven.aliyun.com/repository/public")
    }
    mavenCentral()
  }
}

plugins {
  application
  id("com.github.johnrengelman.shadow") version "7.1.2"
  kotlin("jvm") version "1.7.21"
//  kotlin("jvm").version("1.8.21")
}

group = "com.honing.css"
version = "1.0.0-SNAPSHOT"

repositories {

  mavenLocal()
  maven {
    url = uri("https://maven.aliyun.com/repository/public")
  }
  mavenCentral()
}

val vertxVersion = "4.5.7"
val junitJupiterVersion = "5.9.1"

val mainVerticleName = "com.honing.ccs.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-web-client")
  implementation("io.vertx:vertx-json-schema")
  implementation("io.vertx:vertx-lang-kotlin")
  implementation(kotlin("stdlib-jdk8"))
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")

  // https://mvnrepository.com/artifact/com.github.ben-manes.caffeine/caffeine
  implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

  // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
  implementation("ch.qos.logback:logback-classic:1.5.6")

}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "17"


tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}


tasks.compileJava {
  options.encoding = "UTF-8"
}
