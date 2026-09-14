plugins {
    `version-catalog`
}



catalog {
    versionCatalog {
        version("events", project.version.toString())
    }
}