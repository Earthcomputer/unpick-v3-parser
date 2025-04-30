plugins {
    id("java")
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
