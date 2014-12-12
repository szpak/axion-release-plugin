package pl.allegro.tech.build.axion.release.domain

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import pl.allegro.tech.build.axion.release.ReleasePlugin
import pl.allegro.tech.build.axion.release.infrastructure.dry.DryRepository
import spock.lang.Ignore
import spock.lang.Specification

class DryRunReleaseTest extends Specification {

    private final Project project = ProjectBuilder.builder().build()

    private final VersionConfig config = new VersionConfig(project)

    private final LocalOnlyResolver resolver = new LocalOnlyResolver(config, project)

    @Ignore
    def "should resolve to localOnly when project release.localOnly property present"() {
        given:
        project.apply(plugin: "axion-release")
        project.scmVersion.dryRun = true

        expect:
            (project.getTasksByName(ReleasePlugin.RELEASE_TASK, false)[0]).releaser.repository instanceof DryRepository
    }
}