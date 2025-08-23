plugins {
    java
    id("org.springframework.boot") version "3.3.13"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jooq.jooq-codegen-gradle") version "3.19.15"
    id("org.flywaydb.flyway") version "10.21.0"
    id("org.springdoc.openapi-gradle-plugin") version "1.8.0"
}

group = "com.adaptivemedia"
version = "0.0.1-SNAPSHOT"
description = "AdaptiveMedia"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    jooqCodegen
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.cloud:spring-cloud-starter")
    implementation("org.springframework.cloud:spring-cloud-context")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    implementation("org.springdoc:springdoc-openapi-starter-common:2.3.0")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    compileOnly("org.projectlombok:lombok")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("com.h2database:h2")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")

    jooqCodegen("org.postgresql:postgresql")
    jooqCodegen("org.flywaydb:flyway-core")
    jooqCodegen("org.flywaydb:flyway-database-postgresql")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.3")
    }
}

buildscript {
    dependencies {
        classpath("org.postgresql:postgresql:42.7.3")
        classpath("org.flywaydb:flyway-database-postgresql:10.21.0")
    }
}

openApi {
    apiDocsUrl.set("http://localhost:8080/v3/api-docs")
    outputDir.set(file("$layout.buildDirectory/docs"))
    outputFileName.set("openapi.json")
    waitTimeInSeconds.set(30)
}

flyway {
    url = "jdbc:postgresql://localhost:5432/adaptivemedia"
    user = "adaptivemedia"
    password = "adaptivemedia"
}

jooq {
    configuration {
        jdbc {
            driver = "org.postgresql.Driver"
            url = "jdbc:postgresql://localhost:5432/adaptivemedia"
            user = "adaptivemedia"
            password = "adaptivemedia"
        }
        generator {
            database {
                name = "org.jooq.meta.postgres.PostgresDatabase"
                inputSchema = "public"
                includes = ".*"
                excludes = "flyway_schema_history"
            }
            target {
                packageName = "com.adaptivemedia.assignment.jooq"
                directory = "src/main/java/generated"
            }
            generate {
                isRecords = true
                isDaos = true
                isPojos = true
                isSpringAnnotations = true
                isJavaTimeTypes = true
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {
    enabled = false
}

tasks.bootJar {
    archiveFileName.set("${project.name}-${project.version}.jar")
}

tasks.named("jooqCodegen") {
    dependsOn("flywayMigrate")
}

tasks.register("updateSchema") {
    group = "development"
    description = "Run Flyway migrations and generate JOOQ code"
    dependsOn("flywayMigrate")
    finalizedBy("jooqCodegen")
}