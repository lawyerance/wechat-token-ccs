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

  implementation("io.netty:netty-all:4.1.108.Final")
  implementation("io.netty:netty-resolver-dns-native-macos:4.1.108.Final")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")


}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "17"


val mainVerticleName = "com.honing.ccs.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "${projectDir}/src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

tasks.withType<Jar> {
  manifest {
    attributes["Main-Class"] = "io.vertx.core.Launcher" //改为Launcher
    attributes["Main-Verticle"] = "io.example.MainVerticle" //新增Main Verticle属性，对应MainVerticle类
  }

}

application {
  mainClass.set(launcherClassName)
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}


tasks.compileJava {
  options.encoding = "UTF-8"
}

tasks.withType<JavaExec> {
  args = listOf(
    "run",
    mainVerticleName,
    "--redeploy=$watchForChange",
    "--launcher-class=$launcherClassName",
    "--on-redeploy=$doOnChange"
  )
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
