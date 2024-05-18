subprojects {
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

  apply(plugin = ("java-library"))

}
