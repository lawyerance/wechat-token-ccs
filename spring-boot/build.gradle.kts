import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.run.BootRun

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
  id("org.springframework.boot") version "3.2.5"
  id("io.spring.dependency-management") version "1.1.4"

  id("java-library")
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


val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"



dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-autoconfigure-processor")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  implementation("org.springframework.boot:spring-boot-starter-cache")
  implementation("org.springframework.retry:spring-retry")

  implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

  implementation("org.springframework.boot:spring-boot-starter-data-redis")

//    implementation("org.apache.httpcomponents.client5:httpclient5:5.3.1")

}


val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "17"

tasks.withType<BootRun> {
//  systemProperty("spring.config.additional-location", "file://${projectDir}/src/main/resources/application-cluster.yaml")
  args("--spring.config.additional-location=file://${projectDir}/src/main/resources/application-standalone.yaml")

}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<Jar> {
  manifest {
    attributes["Implementation-Title"] = project.name
    attributes["Implementation-Version"] = project.version
  }

  // Remove `plain` postfix from jar file name
  archiveClassifier.set("")

  exclude("*.yaml", "*.xml")
}

tasks.compileJava {
  options.encoding = "UTF-8"
}

tasks.register<Zip>("release") {
  dependsOn("jar")
  //压缩包名称
  archiveFileName = "${project.name}.zip"

  into("bin") {
    from("${projectDir}/scripts")
    setFileMode(755)
  }
  into("libs") {
    from(configurations.runtimeClasspath)

    from(layout.getBuildDirectory().dir("libs"))
  }

  into("config") {
    from("${projectDir}/src/main/resources/")
    include("*.yaml", "*.xml")
  }

}
