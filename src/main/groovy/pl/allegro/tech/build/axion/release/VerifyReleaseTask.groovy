package pl.allegro.tech.build.axion.release

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import pl.allegro.tech.build.axion.release.domain.ChecksResolver
import pl.allegro.tech.build.axion.release.domain.LocalOnlyResolver
import pl.allegro.tech.build.axion.release.domain.VersionConfig
import pl.allegro.tech.build.axion.release.domain.scm.ScmRepository
import pl.allegro.tech.build.axion.release.infrastructure.ComponentFactory

import javax.inject.Inject

class VerifyReleaseTask extends DefaultTask {

    private final ScmRepository repository

    @Inject
    public VerifyReleaseTask() {
        this.repository = ComponentFactory.scmRepository(project, project.extensions.getByType(VersionConfig))
    }

    @TaskAction
    void prepare() {
        VersionConfig config = project.extensions.getByType(VersionConfig)
        boolean dryRun = config.dryRun
        ChecksResolver resolver = new ChecksResolver(config.checks, project)
        LocalOnlyResolver localOnlyResolver = new LocalOnlyResolver(config, project)

        if(resolver.checkUncommitedChanges()) {
            boolean uncommitedChanges = repository.checkUncommitedChanges()
            project.logger.quiet("Looking for uncommited changes.. ${uncommitedChanges ? 'FAILED' : ''}")
            if (uncommitedChanges && !dryRun) {
                throw new IllegalStateException("There are uncommited files in your repository - can't release.")
            }
        }
        else {
            project.logger.quiet('Skipping uncommited changes check')
        }

        boolean remoteAttached = repository.remoteAttached(config.remote)
        if(resolver.checkAheadOfRemote() && !localOnlyResolver.localOnly(remoteAttached)) {
            boolean aheadOfRemote = repository.checkAheadOfRemote()
            project.logger.quiet("Checking if branch is ahead of remote.. ${aheadOfRemote ? 'FAILED' : ''}")
            if (aheadOfRemote && !dryRun) {
                throw new IllegalStateException("Current branch is ahead of remote counterpart - can't release.")
            }
        }
        else {
            project.logger.quiet("Skipping ahead of remote check")
        }
    }
}
