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

  id("java-library")
  kotlin("jvm") version "1.7.21"
//  kotlin("jvm").version("1.8.21")
}
