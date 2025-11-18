import com.diffplug.spotless.LineEnding
import org.vineflower.unpick.parser.retrofit.RetrofitTask

plugins {
    id("java")
    id("checkstyle")
    id("com.diffplug.spotless") version "7.0.2"
}

group = "org.vineflower"
version = "1.0-SNAPSHOT"

tasks.withType<JavaCompile> {
    options.release.set(21)
}

tasks.withType<Javadoc> {
    options.source = "21"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.code-intelligence:jazzer-junit:0.22.1")
}

tasks.test {
    useJUnitPlatform()
}

checkstyle {
    toolVersion = "10.21.1"
    configFile = file("checkstyle.xml")
    isIgnoreFailures = false
}

spotless {
    lineEndings = LineEnding.UNIX

    java {
        removeUnusedImports()
        importOrder("java", "javax", "", "org.vineflower.unpick.parser")
        leadingTabsToSpaces(4)
        trimTrailingWhitespace()
        endWithNewline()
    }
}

val retrofitJar by tasks.creating(RetrofitTask::class) {
    group = "build"
    inputFile.set(tasks.jar.flatMap { it.archiveFile })
    outputFile.set(layout.buildDirectory.file("libs/${base.archivesName.get()}-retrofitted-${version}.jar"))
}
