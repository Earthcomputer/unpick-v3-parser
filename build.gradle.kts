import com.diffplug.spotless.LineEnding

plugins {
    id("java")
    id("checkstyle")
    id("com.diffplug.spotless") version "7.0.2"
}

group = "net.earthcomputer"
version = "1.0-SNAPSHOT"

tasks.withType<JavaCompile> {
    options.release.set(17)
}

tasks.withType<Javadoc> {
    options.source = "17"
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
        importOrder("java", "javax", "", "net.earthcomputer.unpickv3parser")
        leadingTabsToSpaces(4)
        trimTrailingWhitespace()
        endWithNewline()
    }
}
