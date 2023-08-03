plugins {
    id("java")
    id("maven-publish")
}

group = "com.nickcoblentz.montoya.turborails"
version = "0.1"

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/ncoblentz/BurpMontoyaUtilities")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GHUSERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GHTOKEN")
        }
    }
}


dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("net.portswigger.burp.extensions:montoya-api:+")
    implementation("com.nickcoblentz.montoya.libraries:burpmontoyautilities:+")
    implementation("org.json:json:+")
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    val fatJar = register<Jar>("fatJar") {

        dependsOn.add("build")
        archiveClassifier.set("fatjar") // Naming the jar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
                .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ncoblentz/burpmontoyaturborails")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            artifact(tasks.findByPath("fatJar"))
        }

    }
}